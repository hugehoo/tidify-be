package tidify.tidify.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.domain.User;
import tidify.tidify.dto.ObjectResponseDto;
import tidify.tidify.dto.PageResponseDto;
import tidify.tidify.dto.ResponseDto;
import tidify.tidify.service.BookmarkService;

@Api(tags = "북마크")
@RestController
@RequiredArgsConstructor
@RequestMapping("app/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    @Operation(summary="북마크 조회", description="유저의 전체 북마크 조회")
    private ResponseDto getBookmarks(
        @AuthenticationPrincipal User user, Pageable pageable
    ) {
        return new PageResponseDto<>(bookmarkService.getAllBookmarks(user, pageable));
    }

    @GetMapping("/custom")
    @Operation(summary="Custom Dto Test 용")
    private ResponseDto getBookmarksCustom(@AuthenticationPrincipal User user, Pageable pageable) {
        return new PageResponseDto<>(bookmarkService.getAllBookmarks(user, pageable));
    }

    @GetMapping("/search")
    @Operation(summary="북마크 검색", description="북마크를 자연어로 검색")
    private ResponseDto searchBookmarks(
        @AuthenticationPrincipal User user,
        @RequestParam String keyword, Pageable pageable
    ) {
        return new PageResponseDto<>(bookmarkService.searchBookmarks(user, keyword, pageable));
    }

    @PostMapping
    @Operation(summary="북마크 생성", description="북마크를 생성")
    private ResponseDto createBookmark(
        @AuthenticationPrincipal User user,
        @RequestBody BookmarkRequest request
    ) {
        return new ObjectResponseDto<>(bookmarkService.createBookmark(request, user));
    }

    @PatchMapping("/{bookmarkId}")
    @Operation(summary="북마크 수정", description="북마크 정보(이름, 라벨, URL) 수정")
    private ResponseDto modifyBookmark(
        @AuthenticationPrincipal User user,
        @PathVariable("bookmarkId") Long bookmarkId,
        @RequestBody BookmarkRequest request
    ) {
        return new ObjectResponseDto<>(bookmarkService.modifyBookmark(bookmarkId, user, request));
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary="북마크 삭제")
    private ResponseEntity<Void> deleteBookmark(
        @AuthenticationPrincipal User user,
        @PathVariable("bookmarkId") Long bookmarkId
    ) {
        bookmarkService.deleteBookmark(bookmarkId, user);
        return ResponseEntity.noContent().build();
    }
}
