package in.andonsystem.dozerconverter;

import in.andonsystem.v1.entity.Designation;
import org.dozer.DozerConverter;

/**
 * Created by mdzahidraza on 10/06/17.
 */
public class DesignationStringConverter extends DozerConverter<Designation, String> {

    public DesignationStringConverter() {
        super(Designation.class, String.class);
    }

    @Override
    public String convertTo(Designation designation, String s) {
        if (designation == null) return null;
        return designation.getName();
    }

    @Override
    public Designation convertFrom(String s, Designation designation) {
        if (s == null) return  null;
        designation = new Designation();
        designation.setName(s);
        return designation;
    }
}
