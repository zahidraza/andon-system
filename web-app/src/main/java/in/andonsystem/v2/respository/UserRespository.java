package in.andonsystem.v2.respository;

import in.andonsystem.v2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface UserRespository extends JpaRepository<User, Long>{
    
    User findByEmail(String email);
    
    User findByName(String name);

    List<User> findByLastModifiedGreaterThan(Date date);
}
