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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.common.security.User;
import tidify.tidify.service.BookmarkService;

@Api(tags = "북마크")
@RestController
@RequiredArgsConstructor
@RequestMapping("app/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    @Operation(summary="북마크 조회", description="유저의 북마크를 조회")
    private Page<BookmarkResponse> getBookmarks(Pageable pageable) {
        User user = null;
        return bookmarkService.getAllBookmarks(user, pageable);
    }

    @GetMapping("/search")
    @Operation(summary="북마크 검색", description="북마크를 자연어로 검색")
    private Page<BookmarkResponse> searchBookmarks(
        @RequestParam String keyword, Pageable pageable
    ) {
        User user = null;

        return bookmarkService.searchBookmarks(user, keyword, pageable);
    }

    @PostMapping
    @Operation(summary="북마크 생성", description="북마크를 생성")
    private ResponseEntity<BookmarkResponse> createBookmark(@RequestBody BookmarkRequest request) {
        User user = null;
        BookmarkResponse bookmarks = bookmarkService.createBookmark(request, user);
        return ResponseEntity.created(URI.create("/bookmark")).body(bookmarks);
    }

    @PatchMapping("/{bookmarkId}")
    @Operation(summary="북마크 수정", description="북마크 정보(이름, 라벨, URL) 수정")
    private ResponseEntity<BookmarkResponse.BookmarkModifyResponse> modifyBookmark(
        @PathVariable("bookmarkId") Long bookmarkId,
        @RequestBody BookmarkRequest request
    ) {
        User user = null;
        BookmarkResponse.BookmarkModifyResponse bookmarks = bookmarkService.modifyBookmark(bookmarkId, user, request);
        return ResponseEntity.ok().body(bookmarks);
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary="북마크 삭제")
    private ResponseEntity<Void> deleteBookmark(
        @PathVariable("bookmarkId") Long bookmarkId
    ) {
        User user = null;
        bookmarkService.deleteBookmark(bookmarkId, user);
        return ResponseEntity.noContent().build();
    }
}
