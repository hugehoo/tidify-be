package tidify.tidify.controller;

import javax.validation.Valid;

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
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.ObjectResponseDto;
import tidify.tidify.dto.PageResponseDto;
import tidify.tidify.dto.ResponseDto;
import tidify.tidify.service.BookmarkService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    private ResponseDto getBookmarks(
        @AuthenticationPrincipal User user, Pageable pageable
    ) {
        return new PageResponseDto<>(bookmarkService.getAllBookmarks(user, pageable));
    }

    @GetMapping("/custom")
    private ResponseDto getBookmarksCustom(@AuthenticationPrincipal User user, Pageable pageable) {
        return new PageResponseDto<>(bookmarkService.getAllBookmarks(user, pageable));
    }

    @GetMapping("/search")
    private ResponseDto searchBookmarks(
        @AuthenticationPrincipal User user,
        @RequestParam String keyword, Pageable pageable
    ) {
        return new PageResponseDto<>(bookmarkService.searchBookmarks(user, keyword, pageable));
    }

    @PostMapping
    private ResponseDto createBookmark(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody BookmarkRequest request
    ) {
        return new ObjectResponseDto<>(bookmarkService.createBookmark(request, user));
    }

    @PatchMapping("/{bookmarkId}")
    private ResponseDto modifyBookmark(
        @AuthenticationPrincipal User user,
        @PathVariable("bookmarkId") Long bookmarkId,
        @Valid @RequestBody BookmarkRequest request
    ) {
        return new ObjectResponseDto<>(bookmarkService.modifyBookmark(bookmarkId, user, request));
    }

    @DeleteMapping("/{bookmarkId}")
    private ResponseDto deleteBookmark(
        @AuthenticationPrincipal User user,
        @PathVariable("bookmarkId") Long bookmarkId
    ) {
        bookmarkService.deleteBookmark(bookmarkId, user);
        return ResponseDto.ofDeleteApi();
    }
}
