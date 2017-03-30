package in.andonsystem.v2.respository;

import in.andonsystem.v2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface UserRespository extends JpaRepository<User, Long>{
    
    public User findByEmail(String email);
    
    public User findByName(String name);

    public List<User> findByLastModifiedGreaterThan(Date date);
}
