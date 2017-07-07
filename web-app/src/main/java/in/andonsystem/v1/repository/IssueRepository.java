package in.andonsystem.v1.repository;

import in.andonsystem.v1.entity.Issue1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by mdzahidraza on 16/06/17.
 */
@Repository("issueRepository1")
public interface IssueRepository extends JpaRepository<Issue1, Long> {

    List<Issue1> findByLastModifiedGreaterThan(Date date);

    List<Issue1> findByLastModifiedBetweenOrderByRaisedAtDesc(Date start, Date end);

    List<Issue1> findByProcessingAtLessThanAndRaisedAtGreaterThan(Integer processingAt, Date date);

    List<Issue1> findByRaisedAtGreaterThanAndDeleted(Date raisedAt, Boolean deleted);
}
