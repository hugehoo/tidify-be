package tidify.tidify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.FolderResponse;

public interface FolderRepositoryCustom {
    Page<FolderResponse> findFoldersWithCount(User user, Pageable pageable);

    Page<BookmarkResponse> findBookmarksByFolder(User user, Long folderId, Pageable pageable);

    Page<FolderResponse> findSubscribedFolders(User user, Pageable pageable);

    Page<FolderResponse> findSubscribingFolders(User user, Pageable pageable);

    void subscribeFolder(User user, Long folderId);
}
