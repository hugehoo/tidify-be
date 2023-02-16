package tidify.tidify.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    Bookmark findBookmarkByIdAndUserIdAndDelFalse(Long id, Long userId);
}

