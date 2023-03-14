package tidify.tidify.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Bookmark;
import tidify.tidify.domain.Folder;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.exception.ResourceNotFoundException;
import tidify.tidify.repository.BookmarkRepository;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.domain.User;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final FolderRepository folderRepository;
    private final BookmarkRepository bookmarkRepository;

    @Description("folder 지정되지 않은 북마크도 모두 가져옴")
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getAllBookmarks(User user, Pageable pageable) {

        // user 값으로만 조회하구나. 중첩으로 만들어야겠다. embedded 가 맞다 linked 도 안됨.
        // linked 하려면 지금 구조에서 folder 를 먼저 조회하고, join 걸어서 해야하는데 비용을 생각해야함.
        return bookmarkRepository.findBookmarksWithFolderId(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> searchBookmarks(User user, String keyword, Pageable pageable) {
        return bookmarkRepository.searchBookmarks(user, keyword, pageable);
    }

    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request, User user) {

        Bookmark bookmark = buildBookmark(request, user);
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.of(bookmark, request.getFolderId());
    }

    @Transactional
    public BookmarkResponse.BookmarkModifyResponse modifyBookmark(Long id, User user, BookmarkRequest request) {

        Long userId = user.getId();
        Bookmark bookmark = getBookmark(id, userId);

        String url = request.getUrl();
        String name = getNameByOption(request, url);

        Folder folder = getFolder(request.getFolderId(), user);
        bookmark.moidfy(url, name, folder);

        return new BookmarkResponse.BookmarkModifyResponse(bookmark);
    }

    @Transactional
    public void deleteBookmark(Long id, User user) {
        Bookmark bookmark = getBookmark(id, user.getId());
        bookmark.delete();
    }

    private Bookmark getBookmark(Long id, Long userId) {
        return bookmarkRepository
            .findBookmarkByIdAndUserIdAndDelFalse(id, userId)
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
        if (name == null) {
            return url;
        }
        return name;
    }
}
