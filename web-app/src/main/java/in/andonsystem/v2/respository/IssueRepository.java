package in.andonsystem.v2.respository;

import in.andonsystem.v2.entity.Issue2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by razamd on 3/30/2017.
 */
@Repository("issueRepository2")
public interface IssueRepository extends JpaRepository<Issue2, Long> {

    List<Issue2> findByLastModifiedGreaterThan(Date date);

    List<Issue2> findByRaisedAtGreaterThanAndDeleted(Date date, Boolean deleted);

    List<Issue2> findByLastModifiedBetweenOrderByRaisedAtDesc(Date start, Date end);

    List<Issue2> findByProcessingAtLessThanAndRaisedAtLessThanAndDeleted(Integer processingAt, Date raisedat, Boolean deleted);
}
