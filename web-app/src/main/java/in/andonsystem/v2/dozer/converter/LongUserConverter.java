package in.andonsystem.v2.dozer.converter;

import in.andonsystem.v2.entity.User;
import org.dozer.DozerConverter;

/**
 * Created by razamd on 3/30/2017.
 */
public class LongUserConverter extends DozerConverter<Long, User> {

    public LongUserConverter() {
        super(Long.class, User.class);
    }

    @Override
    public User convertTo(Long aLong, User user) {
        if(aLong == null){
            return null;
        }
        return new User(aLong);
    }

    @Override
    public Long convertFrom(User user, Long aLong) {
        if (user == null) return null;
        return user.getId();
    }
}
