package tidify.tidify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tidify.tidify.domain.User;
import tidify.tidify.dto.FolderResponse;

public interface FolderRepositoryCustom {
    Page<FolderResponse> findFoldersWithCount(User user, Pageable pageable);

}
