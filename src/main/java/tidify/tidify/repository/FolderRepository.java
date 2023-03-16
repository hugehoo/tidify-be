package tidify.tidify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Folder;
import tidify.tidify.domain.User;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {

    Optional<Folder> findFolderByIdAndUser(Long id, User user);

    @Modifying
    @Query(value = "UPDATE bookmark b set b.folder_id = null where b.folder_id = :folderId and b.user_id = :userId", nativeQuery = true)
    void updateBookmarksAsNoneFolder(@Param("userId") Long userId, @Param("folderId") Long folderId);
}
