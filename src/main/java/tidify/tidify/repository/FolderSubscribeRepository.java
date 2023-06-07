package tidify.tidify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Folder;
import tidify.tidify.domain.FolderSubscribe;
import tidify.tidify.domain.User;

@Repository
public interface FolderSubscribeRepository extends JpaRepository<FolderSubscribe, Long> {

    boolean existsByUserAndFolder(User user, Folder folder);

}

