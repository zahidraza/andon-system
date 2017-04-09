package in.andonsystem.v2.service;

import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.page.converter.UserConverter;
import in.andonsystem.v2.respository.UserRespository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.dozer.Mapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {
    
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRespository userRepository;
    
    @Autowired Mapper mapper;
    
    @Autowired UserConverter converter;

    public UserDto findOne(Long id) {
        logger.debug("findOne(): id = {}",id);
        return mapper.map(userRepository.findOne(id), UserDto.class);
    }

    public List<UserDto> findAllAfter(Long after) {
        logger.debug("findAll()");
        List<User> users = null;
        if(after > 0L){
            users = userRepository.findByLastModifiedGreaterThan(new Date(after));
        }else{
            users = userRepository.findAll();
        }
        users.forEach(user -> {
            Hibernate.initialize(user.getBuyers());
        });
        return users.stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
    
    public Page<UserDto> findAllByPage(Pageable pageable){
        logger.debug("findAllByPage()");
        return userRepository.findAll(pageable).map(converter);
    }

    public UserDto findByEmail(String email) {
        logger.debug("findByEmail(): email = {}",email);
        return mapper.map(userRepository.findByEmail(email), UserDto.class);
    }
    
    public UserDto findByUsername(String username) {
        logger.debug("findByUsername(): name = {}" , username);
        return mapper.map(userRepository.findByName(username), UserDto.class);
    }

    public Boolean exists(Long id) {
        logger.debug("exists(): id = {}",id);
        return userRepository.exists(id);
    }
    
    public Long count(){
        logger.debug("count()");
        return userRepository.count();
    }

    @Transactional
    public UserDto save(UserDto userDto) {
        logger.debug("save()");
        User user = mapper.map(userDto, User.class);
        user.setPassword(userDto.getMobile());
        user = userRepository.save(user);
        return mapper.map(user, UserDto.class);
    }

    @Transactional
    public UserDto update(UserDto userDto) {
        logger.debug("update()");
        User user = userRepository.findOne(userDto.getId());
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getMobile() != null) user.setMobile(userDto.getMobile());
        if (userDto.getLevel() != null) user.setLevel(userDto.getLevel());
        if (userDto.getRole() != null) user.setRole(userDto.getRole());
        if (userDto.getUserType() != null) user.setUserType(userDto.getUserType());
        if (userDto.getPassword() != null) user.setPassword(userDto.getPassword());
        if (userDto.getBuyers() != null){
            user.getBuyers().clear();
            userDto.getBuyers().forEach(buyer -> user.getBuyers().add(buyer));
        }
        return mapper.map(user, UserDto.class);
    }

    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword){
        User user = userRepository.findOne(userId);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
    
    @Transactional
    public void delete(Long id) {
        logger.debug("delete(): id = {}",id);
        userRepository.delete(id);
    }
    
}
