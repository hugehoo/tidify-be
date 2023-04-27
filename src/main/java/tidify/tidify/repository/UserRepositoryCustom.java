package tidify.tidify.repository;

import org.springframework.stereotype.Repository;

import tidify.tidify.domain.User;

@Repository
public interface UserRepositoryCustom {

    void deleteUserRelateInfo(User user);

}

