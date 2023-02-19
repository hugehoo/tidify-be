package tidify.tidify.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tidify.tidify.dto.FolderResponse;

public interface FolderRepositoryCustom {
    Page<FolderResponse> findFoldersWithCount(Long userId, Pageable pageable);

}
