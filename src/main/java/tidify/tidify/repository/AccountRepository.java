package tidify.tidify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}
