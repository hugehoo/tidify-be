package tidify.tidify.controller;

import jakarta.validation.Valid;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.User;
import tidify.tidify.dto.CustomPage;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.dto.ObjectResponseDto;
import tidify.tidify.dto.PageResponseDto;
import tidify.tidify.dto.ResponseDto;
import tidify.tidify.security.JwtTokenProvider;
import tidify.tidify.service.FolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/folders")
public class FolderController {

    private final FolderService folderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    private ResponseDto getFolders(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        CustomPage folders = folderService.getFolders(user, pageable);
        return new PageResponseDto<>(folders);
    }

    @GetMapping("/subscribed")
    private ResponseDto getSubscribedFolders(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        CustomPage folders = folderService.getSubscribed(user, pageable);
        return new PageResponseDto<>(folders);
    }

    @GetMapping("/subscribing")
    private ResponseDto getSubscribingFolders(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        CustomPage folders = folderService.getSubscribing(user, pageable);
        return new PageResponseDto<>(folders);
    }

    @GetMapping("/{folderId}")
    private ResponseDto getFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId
    ) {
        FolderResponse folder = folderService.getFolderById(user, folderId);
        return new ObjectResponseDto<>(folder);
    }

    @PostMapping("/star/{folderId}")
    private ResponseDto enrollFavorite(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId
    ) {
        FolderResponse folder = folderService.enrollFavorite(user, folderId);
        return new ObjectResponseDto<>(folder);
    }

    @GetMapping("/{folderId}/bookmarks")
    private ResponseDto getFolderWithBookmarks(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId,
        Pageable pageable
    ) {
        CustomPage folderWithBookmarks = folderService.getFolderWithBookmarks(user, folderId,
            pageable);
        return new PageResponseDto<>(folderWithBookmarks);
    }

    @PatchMapping("/{folderId}")
    private ResponseDto modifyFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId,
        @Valid @RequestBody FolderRequest request) {
        FolderResponse response = folderService.modifyFolder(folderId, request, user);
        return new ObjectResponseDto<>(response);
    }

    @DeleteMapping("/{folderId}")
    private ResponseDto deleteFolders(
        @AuthenticationPrincipal User user,
        @PathVariable("folderId") Long folderId) {
        folderService.deleteFolder(folderId, user);
        return ResponseDto.ofDeleteApi();
    }

    @GetMapping("/verify")
    private ResponseDto verifyFolderOwner(
        @AuthenticationPrincipal User user,
        @RequestParam("id") Long folderId
    ) {
        boolean myFolder = folderService.isMyFolder(folderId, user);
        return new ObjectResponseDto<>(myFolder);
    }

    @PostMapping
    private ResponseDto createFolders(
        @AuthenticationPrincipal User user, @Valid @RequestBody FolderRequest request) {
        FolderResponse response = folderService.createFolder(request, user);
        return new ObjectResponseDto<>(response);
    }


    @Description("폴더 구독")
    @PostMapping("/subscribed/{id}")
    private ResponseDto subscribeFolder(
        @AuthenticationPrincipal User user,
        // @Valid @RequestBody AuthKey request,
        @PathVariable("id") Long folderId
    ) {

        // String userEmail = jwtTokenProvider.getUserPk(request.getAuthKey(), false);
        folderService.subscribeFolder(folderId, user.getUserEmail());
        return new ObjectResponseDto<>(null);
    }


    @Description("폴더 구독 취소")
    @PostMapping("/un-subscribed/{id}")
    private ResponseDto unSubscribeFolder(
        @AuthenticationPrincipal User user,
        // @RequestBody AuthKey request,
        @PathVariable("id") Long folderId
    ) {
        // String userEmail = jwtTokenProvider.getUserPk(request.getAuthKey(), false);
        folderService.unSubscribeFolder(folderId, user.getUserEmail());
        return new ObjectResponseDto<>(null);
    }

    @Description("공유자 입장 - 유저 폴더 공유 중지")
    @PostMapping("/{folderId}/share-suspending")
    private ResponseDto suspendSharing(
        @AuthenticationPrincipal User user,
        @PathVariable(value = "folderId") Long folderId) {

        boolean result = folderService.suspendSharing(user, folderId);
        return new ObjectResponseDto<>(result);
    }
}
