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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.security.User;
import tidify.tidify.service.BookmarkService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    private ResponseEntity<List<BookmarkResponse>> getBookmarks(@RequestParam("userId") Long userId) {
        List<BookmarkResponse> bookmarks = bookmarkService.getAllBookmarks(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    private ResponseEntity<BookmarkResponse> createBookmark(@RequestBody BookmarkRequest request) {
        User user = null;
        BookmarkResponse bookmarks = bookmarkService.createBookmark(request, user);
        return ResponseEntity.created(URI.create("/bookmark")).body(bookmarks);
    }

    @PatchMapping("/{bookmarkId}")
    private ResponseEntity<BookmarkResponse.BookmarkModifyResponse> modifyBookmark(
        @PathVariable("bookmarkId") Long bookmarkId,
        @RequestBody BookmarkRequest request
    ) {
        User user = null;
        BookmarkResponse.BookmarkModifyResponse bookmarks = bookmarkService.patchBookmark(bookmarkId, user, request);
        return ResponseEntity.ok().body(bookmarks);
    }


    @DeleteMapping("/{bookmarkId}")
    private ResponseEntity<Void> deleteBookmark(
        @PathVariable("bookmarkId") Long bookmarkId
    ) {
        User user = null;
        bookmarkService.deleteBookmark(bookmarkId, user);
        return ResponseEntity.noContent().build();
    }
}
