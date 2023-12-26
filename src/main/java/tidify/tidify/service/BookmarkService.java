package tidify.tidify.service;

import java.io.IOException;
import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Bookmark;
import tidify.tidify.domain.Folder;
import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.CustomPage;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.exception.ResourceNotFoundException;
import tidify.tidify.repository.BookmarkRepository;
import tidify.tidify.repository.FolderRepository;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final FolderRepository folderRepository;
    private final BookmarkRepository bookmarkRepository;

    @Description("folder 지정되지 않은 북마크도 모두 가져옴")
    @Transactional(readOnly = true)
    public CustomPage getAllBookmarks(User user, Pageable pageable) {
        Page<BookmarkResponse> bookmarks = bookmarkRepository.findBookmarksWithFolderId(user, pageable);
        return CustomPage.of(bookmarks);
    }

    @Transactional(readOnly = true)
    public CustomPage searchBookmarks(User user, String keyword, Pageable pageable) {
        Page<BookmarkResponse> bookmarkResponses = bookmarkRepository.searchBookmarks(user, keyword, pageable);
        return CustomPage.of(bookmarkResponses);
    }

    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request, User user) {
        Bookmark bookmark = buildBookmark(request, user);
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.of(bookmark, request.getFolderId());
    }

    @Transactional
    public BookmarkResponse.BookmarkModifyResponse modifyBookmark(Long id, User user, BookmarkRequest request) {

        Bookmark bookmark = getBookmark(id, user);

        String url = request.getUrl();
        String name = getNameByOption(request, url);

        Folder folder = getFolder(request.getFolderId(), user);
        bookmark.modify(url, name, folder);
        return new BookmarkResponse.BookmarkModifyResponse(bookmark);
    }

    @Transactional // TODO : 동시성 이슈 피하려면(Optimistic Lock)?
    public BookmarkResponse enrollFavorite(User user, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
            .orElseThrow();
        bookmark.toggleStar();

        // TODO 줠라 맘에 안들군!
        Folder folder = bookmark.getFolder();
        Long folderId = folder == null ? null : folder.getId();
        return BookmarkResponse.of(bookmark, folderId);
    }

    @Transactional
    public void deleteBookmark(Long id, User user) {
        Bookmark bookmark = getBookmark(id, user);
        bookmark.delete();
    }

    private Bookmark getBookmark(Long id, User user) {
        return bookmarkRepository.findBookmarkByIdAndUserAndDelFalse(id, user)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.BOOKMARK_NOT_FOUND, id));
    }

    private Folder getFolder(Long folderId, User user) {
        if (folderId == 0L) {
            return null;
        }

        return folderRepository.findFolderByIdAndUser(folderId, user)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, folderId));
    }

    private Bookmark buildBookmark(BookmarkRequest request, User user) {

        Long folderId = request.getFolderId();
        Folder folder = getFolder(folderId, user);

        String url = request.getUrl();
        String name = getNameByOption(request, url);

        return Bookmark.create()
            .name(name)
            .url(url)
            .folder(folder)
            .user(user)
            .build();
    }

    private String getNameByOption(BookmarkRequest request, String url) {
        String name = request.getName();
        if (name == null || name.isBlank()) {
            return url;
        }
        return name;
    }

    @Transactional(readOnly = true)
    public CustomPage getStarBookmarks(User user, Pageable pageable) {
        Page<BookmarkResponse> bookmarks = bookmarkRepository.findStarBookmarks(user, pageable);
        return CustomPage.of(bookmarks);
    }

}
