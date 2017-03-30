package in.andonsystem.v2.dozer.converter;

import in.andonsystem.v2.entity.Buyer;
import org.dozer.DozerConverter;

/**
 * Created by razamd on 3/30/2017.
 */
public class LongBuyerConverter extends DozerConverter<Long, Buyer> {

    public LongBuyerConverter() {
        super(Long.class,Buyer.class);
    }

    @Override
    public Buyer convertTo(Long aLong, Buyer buyer) {
        if (aLong == null || aLong == 0L){
            return null;
        }
        return new Buyer(aLong);
    }

    @Override
    public Long convertFrom(Buyer buyer, Long aLong) {
        if (buyer == null) return null;
        return buyer.getId();
    }
}
