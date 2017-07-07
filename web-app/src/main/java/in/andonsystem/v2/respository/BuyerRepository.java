package in.andonsystem.v2.respository;

import in.andonsystem.v2.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by razamd on 3/30/2017.
 */
public interface BuyerRepository extends JpaRepository<Buyer, Long>{

    List<Buyer> findByTeam(String team);

}
