package tidify.tidify.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tidify.tidify.dto.BookmarkResponse;

public interface BookmarkRepositoryCustom {
    Page<BookmarkResponse> findBookmarksWithFolderId(Long userId, Pageable pageable);
    Page<BookmarkResponse> searchBookmarks(Long userId, String searchKeyword, Pageable pageable);
}
