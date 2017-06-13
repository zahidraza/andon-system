package in.andonsystem.v1.repository;
import in.andonsystem.v1.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created by Md Jawed Akhtar on 09-04-2017.
 */

public interface DesignationRepository extends JpaRepository<Designation,Long> {

    Designation findByName(String name);

}
