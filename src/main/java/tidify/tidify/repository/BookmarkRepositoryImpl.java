package tidify.tidify.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.QBookmark;
import tidify.tidify.domain.QFolder;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.QBookmarkResponse;

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFolder qFolder = QFolder.folder;
    private final QBookmark qBookmark = QBookmark.bookmark;

    @Override
    public List<BookmarkResponse> findBookmarksWithFolderId(Long userId) {

        return query.select(new QBookmarkResponse(qBookmark, qFolder.id))
            .from(qBookmark).where(qBookmark.userId.eq(userId))
            .leftJoin(qFolder)
            .on(qFolder.eq(qBookmark.folder))
            .distinct()
            .fetch();
    }
}
