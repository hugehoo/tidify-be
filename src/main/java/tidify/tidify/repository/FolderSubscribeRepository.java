package tidify.tidify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.FolderSubscribe;

@Repository
public interface FolderSubscribeRepository extends JpaRepository<FolderSubscribe, Long> {

}

