package tidify.tidify.repository.qdsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.QBookmark;
import tidify.tidify.domain.QFolder;
import tidify.tidify.domain.QFolderSubscribe;
import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.dto.QBookmarkResponse;
import tidify.tidify.dto.QFolderResponse;
import tidify.tidify.repository.FolderRepositoryCustom;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFolder qFolder = QFolder.folder;
    private final QBookmark qBookmark = QBookmark.bookmark;
    private final QFolderSubscribe qFolderSubscribe = QFolderSubscribe.folderSubscribe;

    @Override
    public Page<FolderResponse> findFoldersWithCount(User user, Pageable pageable) {
        BooleanExpression whereClause = qFolder.user.eq(user)
            .and(qFolder.del.isFalse());

        return queryFolderResponses(pageable, whereClause);
    }

    @Override
    public Page<BookmarkResponse> findBookmarksByFolder(User user, Long folderId, Pageable pageable) {

        BooleanExpression whereClause = qBookmark.user.eq(user)
            .and(qBookmark.folder.id.eq(folderId))
            .and(qBookmark.del.isFalse());

        List<BookmarkResponse> fetch = query.select(new QBookmarkResponse(qBookmark, qFolder.id))
            .from(qBookmark)
            .where(whereClause)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = query.select(qBookmark.count())
            .from(qBookmark)
            .where(whereClause);

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    @Override
    public Page<FolderResponse> findSubscribedFolders(User user, Pageable pageable) {

        BooleanExpression whereClause = qFolderSubscribe.user.eq(user)
            .and(qFolder.del.isFalse())
            .and(qFolderSubscribe.del.isFalse());

        List<FolderResponse> fetch = query.select(new QFolderResponse(qFolder, qBookmark.count()))
            .from(qFolder)
            .where(whereClause)
            .innerJoin(qFolderSubscribe).on(qFolderSubscribe.folder.eq(qFolder))
            .leftJoin(qBookmark).on(qBookmark.folder.eq(qFolder))
            .groupBy(qFolder) // key point?
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qFolder.createTimestamp.desc())
            .fetch();

        JPAQuery<Long> count = query.select(qFolderSubscribe.count())
            .from(qFolder)
            .where(whereClause);

        assert (count != null);
        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    @Override
    public Page<FolderResponse> findSubscribingFolders(User user, Pageable pageable) {

        BooleanExpression whereClause = qFolder.user.eq(user)
            .and(qFolder.isShared.isTrue())
            .and(qFolder.del.isFalse());

        return queryFolderResponses(pageable, whereClause);
    }

    private Page<FolderResponse> queryFolderResponses(Pageable pageable, BooleanExpression whereClause) {
        List<FolderResponse> fetch = getFolderResponse(pageable, whereClause);

        JPAQuery<Long> count = query.select(qFolder.count())
            .from(qFolder)
            .where(whereClause);

        assert (count != null);
        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    private List<FolderResponse> getFolderResponse(Pageable pageable, BooleanExpression whereClause) {
        return query.select(new QFolderResponse(qFolder, qBookmark.count()))
            .from(qFolder)
            .where(whereClause)
            .leftJoin(qBookmark).on(qBookmark.folder.eq(qFolder))
            .groupBy(qFolder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qFolder.createTimestamp.desc())
            .fetch();
    }

    // @Override
    public void updateBookmarksAsNoneFolder(Long folderId) {
        query.update(qBookmark)
            .set(qBookmark.folder.id, 1L)
            .where(qBookmark.folder.id.eq(folderId))
            .execute();
    }
}
