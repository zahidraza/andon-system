package in.andonsystem.v1.repository;

import in.andonsystem.v1.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 09-04-2017.
 */
public interface ProblemRepository extends JpaRepository<Problem,Long>{

    List<Problem> findByDepartment(String department);

}
