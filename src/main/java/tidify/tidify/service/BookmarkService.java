package tidify.tidify.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Bookmark;
import tidify.tidify.domain.Folder;
import tidify.tidify.dto.BookmarkRequest;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.exception.BookmarkNotFoundException;
import tidify.tidify.exception.FolderNotFoundException;
import tidify.tidify.repository.BookmarkRepository;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.security.User;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final FolderRepository folderRepository;
    private final BookmarkRepository bookmarkRepository;

    private final long NO_FOLDER_ID = 0L;

    @Description("folder 지정되지 않은 북마크도 모두 가져옴")
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getAllBookmarks(Long userId) {
        return bookmarkRepository.findBookmarksWithFolderId(userId);
    }

    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request, User user) {

        Long userId = 24L;
        Bookmark bookmark = buildBookmark(request, userId);
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.of(bookmark, request.getFolderId());
    }

    @Transactional
    public BookmarkResponse.BookmarkModifyResponse patchBookmark(Long bookmarkId, User user, BookmarkRequest request) {

        Folder folder = folderRepository.findFolderById(request.getFolderId())
            .orElseThrow(FolderNotFoundException::new);

        Long userId = 24L;
        Bookmark bookmark = bookmarkRepository.findBookmarkByIdAndUserIdAndDelFalse(bookmarkId, userId);
        if (bookmark == null) {
            throw new BookmarkNotFoundException();
        }
        bookmark.moidfy(request.getUrl(), request.getName(), folder);
        return new BookmarkResponse.BookmarkModifyResponse(request.getUrl(), request.getName(), folder.getName());
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId, User user) {
        long userId = 24L;
        Bookmark bookmark = bookmarkRepository.findBookmarkByIdAndUserIdAndDelFalse(bookmarkId, userId);
        bookmark.delete();
    }

    private Bookmark buildBookmark(BookmarkRequest request, Long id) {

        Long folderId = request.getFolderId();
        Folder folder = null;

        // 븅신 같은.. many to one 관계에서 부모 엔티티가 생성되지 않은 상태에서, 자식 엔티티를 저장할 때 발생하는 에러.
        // save the transient instance before flushing

        if (!folderId.equals(NO_FOLDER_ID)) {
            folder = folderRepository.findFolderById(folderId)
                .orElseThrow(FolderNotFoundException::new);
        }
        return Bookmark.create()
            .name(request.getName())
            .url(request.getUrl())
            .folder(folder)
            .userId(id)
            .build();
    }
}
