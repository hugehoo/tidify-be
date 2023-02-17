package tidify.tidify.repository;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {

    // Optional<Folder> findFolderById(Long id);

    Optional<Folder> findFolderByIdAndUserId(Long id, Long userId);

}
