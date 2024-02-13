package tidify.tidify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Folder;
import tidify.tidify.domain.FolderSubscribe;
import tidify.tidify.domain.User;

@Repository
public interface FolderSubscribeRepository extends JpaRepository<FolderSubscribe, Long> {

    boolean existsByUserAndFolderAndDel(User user, Folder folder, Boolean isDel);

    Optional<FolderSubscribe> findByUserAndFolderAndDel(User user, Folder folder, Boolean isDel);

    List<FolderSubscribe> findByFolder(Folder folder);
}

