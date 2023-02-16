package tidify.tidify.repository;

import java.util.List;

import tidify.tidify.dto.FolderResponse;

public interface FolderRepositoryCustom {
    List<FolderResponse> findFoldersWithCount(Long userId);

}
