package in.andonsystem.dozerconverter;

import in.andonsystem.v1.entity.Designation;
import org.dozer.DozerConverter;

/**
 * Created by mdzahidraza on 10/06/17.
 */
public class DesignationLongConverter extends DozerConverter<Designation, Long> {

    public DesignationLongConverter() {
        super(Designation.class, Long.class);
    }

    @Override
    public Long convertTo(Designation designation, Long s) {
        if (designation == null) return null;
        return designation.getId();
    }

    @Override
    public Designation convertFrom(Long s, Designation designation) {
        if (s == null) return  null;
        designation = new Designation();
        designation.setId(s);
        return designation;
    }
}
