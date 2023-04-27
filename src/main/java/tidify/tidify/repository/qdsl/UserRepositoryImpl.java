package tidify.tidify.repository.qdsl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.QBookmark;
import tidify.tidify.domain.QFolder;
import tidify.tidify.domain.QUser;
import tidify.tidify.domain.User;
import tidify.tidify.repository.UserRepositoryCustom;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFolder folder = QFolder.folder;
    private final QBookmark bookmark = QBookmark.bookmark;
    private final QUser user = QUser.user;

    @Override
    public void deleteUserRelateInfo(User user) {
        query
            .delete(bookmark)
            .where(bookmark.user.eq(user))
            .execute();

        query
            .delete(folder)
            .where(folder.user.eq(user))
            .execute();

        query.delete(this.user)
            .where(this.user.eq(user));
    }
}
