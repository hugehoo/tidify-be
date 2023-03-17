package tidify.tidify.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    Optional<Bookmark> findBookmarkByIdAndUserIdAndDelFalse(Long id, Long userId);

    Bookmark findBookmarkByIdAndDel(Long bookmarkId, Boolean bool);

    @Modifying
    @Query(value = "DELETE from bookmark b where b.user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);

}

