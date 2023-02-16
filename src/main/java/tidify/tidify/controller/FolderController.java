package tidify.tidify.controller;


import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.security.User;
import tidify.tidify.service.FolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/folders")
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/folder")
    private ResponseEntity<List<FolderResponse>> getFolders(@RequestParam String email) {
        List<FolderResponse> folders = folderService.getFolders(email);
        return ResponseEntity.ok(folders);
    }

    @PostMapping
    private ResponseEntity<FolderResponse> createFolders(@RequestBody FolderRequest request) {
        User user = null;
        FolderResponse response = folderService.createFolder(request, user);
        return ResponseEntity.created(URI.create("/folder")).body(response);
    }
}
