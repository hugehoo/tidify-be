package tidify.tidify.controller;


import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.domain.User;
import tidify.tidify.service.FolderService;

@Api(tags = "폴더")
@RestController
@RequiredArgsConstructor
@RequestMapping("app/folders")
public class FolderController {

    private final FolderService folderService;

    @Operation(summary = "폴더 조회", description = "유저의 폴더 조회")
    @GetMapping("/folder")
    private Page<FolderResponse> getFolders(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        return folderService.getFolders(user, pageable);
    }

    @Operation(summary = "개별 폴더 조회", description = "유저의 개별 폴더 조회")
    @GetMapping("/folder/{folderId}")
    private ResponseEntity<FolderResponse> getFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId
    ) {
        FolderResponse folder = folderService.getFolderById(user, folderId);
        return ResponseEntity.ok().body(folder);
    }

    @Operation(summary="폴더 생성")
    @PostMapping
    private ResponseEntity<FolderResponse> createFolders(
        @AuthenticationPrincipal User user, @RequestBody FolderRequest request) {
        FolderResponse response = folderService.createFolder(request, user);
        return ResponseEntity.created(URI.create("/folder")).body(response);
    }


    @Operation(summary="폴더 수정", description="폴더 정보(이름, 라벨) 수정")
    @PatchMapping("/{folderId}")
    private ResponseEntity<FolderResponse> modifyFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {
        FolderResponse response = folderService.modifyFolder(folderId, request, user);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary="폴더 삭제")
    @DeleteMapping("/{folderId}")
    private ResponseEntity<Void> deleteFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {
        folderService.deleteFolder(folderId, user);
        return ResponseEntity.noContent().build();
    }
}
