package in.andonsystem.dozerconverter;

import org.dozer.DozerConverter;

import java.util.Date;

/**
 * Created by razamd on 3/30/2017.
 */
public class LongDateConverter extends DozerConverter<Long,Date> {

    public LongDateConverter() {
        super(Long.class,Date.class);
    }

    @Override
    public Date convertTo(Long aLong, Date date) {
        if(aLong == null || aLong == 0L){
            return null;
        }

        return new Date(aLong);
    }

    @Override
    public Long convertFrom(Date date, Long aLong) {
        if(date == null) {
            return null;
        }
        return date.getTime();
    }
}
