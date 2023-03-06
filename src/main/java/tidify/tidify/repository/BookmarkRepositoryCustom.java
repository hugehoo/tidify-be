package tidify.tidify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;

public interface BookmarkRepositoryCustom {
    Page<BookmarkResponse> findBookmarksWithFolderId(User user, Pageable pageable);
    Page<BookmarkResponse> searchBookmarks(User user, String searchKeyword, Pageable pageable);
}
