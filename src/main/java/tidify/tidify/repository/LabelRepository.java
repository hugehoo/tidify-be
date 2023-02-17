package tidify.tidify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

}
