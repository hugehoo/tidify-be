package tidify.tidify.repository;

import java.util.List;

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
    public List<FolderResponse> findFoldersWithCount(Long userId) {
        return query.select(new QFolderResponse(qFolder, qBookmark.count()))
            .from(qFolder)
            .where(qFolder.userId.eq(userId))
            .leftJoin(qBookmark).on(qBookmark.folder.eq(qFolder))
            .groupBy(qFolder)
            .fetch();
    }

}
