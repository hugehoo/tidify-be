package tidify.tidify.repository.qdsl;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.QBookmark;
import tidify.tidify.domain.QFolder;
import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.QBookmarkResponse;
import tidify.tidify.repository.BookmarkRepositoryCustom;

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFolder qFolder = QFolder.folder;
    private final QBookmark qBookmark = QBookmark.bookmark;

    @Override
    public Page<BookmarkResponse> findBookmarksWithFolderId(User user, Pageable pageable) {

        List<BookmarkResponse> fetch = query
            .select(new QBookmarkResponse(qBookmark, qFolder.id))
            .from(qBookmark)
            .where(condition(user))
            .leftJoin(qFolder)
            .on(qFolder.eq(qBookmark.folder))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qBookmark.createTimestamp.desc())
            .fetch();

        JPAQuery<Long> count = query.select(qBookmark.count())
            .from(qBookmark)
            .where(condition(user));

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    @Override
    public Page<BookmarkResponse> searchBookmarks(User user, String searchKeyword, Pageable pageable) {
        // 이거 매번 생성될 건데 성능고려해서 개선할 수 없나?
        BooleanBuilder keywordBuilder = searchKeywords(searchKeyword, new BooleanBuilder());

        List<BookmarkResponse> fetch = query.select(new QBookmarkResponse(qBookmark, qFolder.id))
            .from(qBookmark)
            .where(condition(user), keywordBuilder)
            .leftJoin(qFolder)
            .on(qFolder.eq(qBookmark.folder))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qBookmark.createTimestamp.desc())
            .distinct()
            .fetch();

        JPAQuery<Long> count = query.select(qBookmark.count())
            .from(qBookmark)
            .where(condition(user), keywordBuilder);

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    private BooleanExpression condition(User user) {
        return qBookmark.user.eq(user).and(qBookmark.del.isFalse());
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
