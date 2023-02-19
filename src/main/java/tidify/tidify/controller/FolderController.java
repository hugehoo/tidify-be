package tidify.tidify.controller;


import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import tidify.tidify.common.security.User;
import tidify.tidify.service.FolderService;

@Api(tags = "폴더")
@RestController
@RequiredArgsConstructor
@RequestMapping("app/folders")
public class FolderController {

    private final FolderService folderService;

    @Operation(summary="폴더 조회", description="유저의 폴더 조회")
    @GetMapping("/folder")
    private Page<FolderResponse> getFolders(User user, Pageable pageable) {
        return folderService.getFolders(user, pageable);
    }

    @Operation(summary="폴더 생성")
    @PostMapping
    private ResponseEntity<FolderResponse> createFolders(@RequestBody FolderRequest request) {
        User user = null;
        FolderResponse response = folderService.createFolder(request, user);
        return ResponseEntity.created(URI.create("/folder")).body(response);
    }


    @Operation(summary="폴더 수정", description="폴더 정보(이름, 라벨) 수정")
    @PatchMapping("/{folderId}")
    private ResponseEntity<FolderResponse> modifyFolders(
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {

        User user = null;
        FolderResponse response = folderService.modifyFolder(folderId, request, user);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary="폴더 삭제")
    @DeleteMapping("/{folderId}")
    private ResponseEntity<Void> deleteFolders(
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {

        User user = null;
        folderService.deleteFolder(folderId, user);
        return ResponseEntity.noContent().build();
    }
}
