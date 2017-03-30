package in.andonsystem.v2.respository;

import in.andonsystem.v2.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by razamd on 3/30/2017.
 */
public interface IssueRepository extends JpaRepository<Issue, Long> {

    public List<Issue> findByLastModifiedGreaterThan(Date date);

}
