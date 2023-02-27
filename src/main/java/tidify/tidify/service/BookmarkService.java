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
import tidify.tidify.common.exception.ErrorTypes;
import tidify.tidify.common.exception.ResourceNotFoundException;
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
        return bookmarkRepository.findBookmarksWithFolderId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> searchBookmarks(User user, String keyword, Pageable pageable) {
        return bookmarkRepository.searchBookmarks(user.getId(), keyword, pageable);
    }

    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request, User user) {

        Bookmark bookmark = buildBookmark(request, user.getId());
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.of(bookmark, request.getFolderId());
    }

    @Transactional
    public BookmarkResponse.BookmarkModifyResponse modifyBookmark(Long id, User user, BookmarkRequest request) {

        Long userId = user.getId();
        Long folderId = request.getFolderId();
        Folder folder = getFolder(folderId, userId);
        Bookmark bookmark = getBookmark(id, userId);

        bookmark.moidfy(request.getUrl(), request.getName(), folder);
        return new BookmarkResponse.BookmarkModifyResponse(request);
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

    private Folder getFolder(Long folderId, Long userId) {
        return folderRepository.findFolderByIdAndUserId(folderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, folderId));
    }

    private Bookmark buildBookmark(BookmarkRequest request, Long userId) {

        Long folderId = request.getFolderId();
        Folder folder = null;

        // 븅신 같은.. many to one 관계에서 부모 엔티티가 생성되지 않은 상태에서, 자식 엔티티를 저장할 때 발생하는 에러.
        // save the transient instance before flushing

        long NO_FOLDER_ID = 0L;
        if (!folderId.equals(NO_FOLDER_ID)) {
            folder = getFolder(folderId, userId);
        }
        return Bookmark.create()
            .name(request.getName())
            .url(request.getUrl())
            .folder(folder)
            .userId(userId)
            .build();
    }

}
