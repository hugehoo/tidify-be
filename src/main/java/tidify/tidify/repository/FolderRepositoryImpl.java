package tidify.tidify.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.QBookmark;
import tidify.tidify.domain.QFolder;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.dto.QFolderResponse;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFolder qFolder = QFolder.folder;
    private final QBookmark qBookmark = QBookmark.bookmark;

    @Override
    public Page<FolderResponse> findFoldersWithCount(Long userId, Pageable pageable) {
        BooleanExpression whereClause = qFolder.userId.eq(userId)
            .and(qFolder.del.isFalse());

        List<FolderResponse> fetch = query.select(new QFolderResponse(qFolder, qBookmark.count()))
            .from(qFolder)
            .where(whereClause)
            .leftJoin(qBookmark).on(qBookmark.folder.eq(qFolder))
            .groupBy(qFolder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = query.select(qFolder.count())
            .from(qFolder)
            .where(whereClause);

        assert (count != null);
        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne); // fetchCount 가 deprecated 된다고 하니.
        // 왜 deprecated 되지 fetchCount?
        // return new PageImpl<>(fetch, pageable, count.fetchOne());
    }

}
