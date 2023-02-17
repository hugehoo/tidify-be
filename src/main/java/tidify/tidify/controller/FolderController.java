package tidify.tidify.controller;


import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.common.security.User;
import tidify.tidify.service.FolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/folders")
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/folder")
    private ResponseEntity<List<FolderResponse>> getFolders(User user) {
        List<FolderResponse> folders = folderService.getFolders(user);
        return ResponseEntity.ok(folders);
    }

    @PostMapping
    private ResponseEntity<FolderResponse> createFolders(@RequestBody FolderRequest request) {
        User user = null;
        FolderResponse response = folderService.createFolder(request, user);
        return ResponseEntity.created(URI.create("/folder")).body(response);
    }


    @PatchMapping("/{folderId}")
    private ResponseEntity<FolderResponse> modifyFolders(
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {

        User user = null;
        FolderResponse response = folderService.modifyFolder(folderId, request, user);
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping("/{folderId}")
    private ResponseEntity<Void> deleteFolders(
        @PathVariable("folderId") Long folderId,
        @RequestBody FolderRequest request) {

        User user = null;
        folderService.deleteFolder(folderId, user);
        return ResponseEntity.noContent().build();
    }
}
