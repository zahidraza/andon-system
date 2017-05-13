package in.andonsystem.v2.page.converter;

import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.entity.User;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<User, UserDto>{

    @Autowired Mapper mapper;
    
    @Override
    public UserDto convert(User source) {
        return mapper.map(source, UserDto.class);
    }

    
}
