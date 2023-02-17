package tidify.tidify.repository;

import java.util.Arrays;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
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

    @Override
    public List<BookmarkResponse> searchBookmarks(Long userId, String searchKeyword) {
        // 이거 매번 생성될 건데 성능고려해서 개선할 수 없나?
        BooleanBuilder builder = searchKeywords(searchKeyword, new BooleanBuilder());

        return query.select(new QBookmarkResponse(qBookmark, qFolder.id))
            .from(qBookmark).where(qBookmark.userId.eq(userId))
            .leftJoin(qFolder)
            .on(qFolder.eq(qBookmark.folder))
            .where(builder)
            .distinct()
            .fetch();
    }

    private BooleanBuilder searchKeywords(String searchKeyword, BooleanBuilder builder) {
        if (searchKeyword.length() == 1) {
            builder.and(qBookmark.name.startsWith(searchKeyword));
        } else {
            Arrays.stream(searchKeyword.split(" "))
                .forEach(word -> builder.and(qBookmark.name.contains(word))); // and 냐 or 이냐는 의도에 따라 다를듯
        }
        return builder;
    }

}
