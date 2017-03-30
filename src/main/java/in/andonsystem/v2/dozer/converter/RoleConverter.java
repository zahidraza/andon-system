package in.andonsystem.v2.dozer.converter;

import in.andonsystem.v2.enums.UserType;
import org.dozer.DozerConverter;

public class RoleConverter extends DozerConverter<String, UserType>{

    public RoleConverter() {
        super(String.class, UserType.class);
    }
    
    @Override
    public UserType convertTo(String source, UserType destination) {
        if(source == null) return null;
        return UserType.parse("ROLE_" + source);
    }

    @Override
    public String convertFrom(UserType source, String destination) {
        if(source == null) return null;
        return source.name();
    }
    
}
