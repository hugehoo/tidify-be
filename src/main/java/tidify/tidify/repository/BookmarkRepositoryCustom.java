package tidify.tidify.repository;

import java.util.List;

import tidify.tidify.dto.BookmarkResponse;

public interface BookmarkRepositoryCustom {
    List<BookmarkResponse> findBookmarksWithFolderId(Long userId);

}
