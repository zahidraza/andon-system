package in.andonsystem.v2.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by razamd on 3/30/2017.
 */
public class FixedValidator implements ConstraintValidator<Fixed, String> {

    private Set<String> teams = new HashSet<>();

    @Override
    public void initialize(Fixed teams) {
        Class<? extends FixedValue> c = teams.fixClass();
        String[] values = null;
        try {
            Method method = c.getMethod("getFixedValues");
            Object obj=  method.invoke(c.getConstructor().newInstance());
            values = (String[])obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String t: values){
            this.teams.add(t.trim());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) return true;
        if (!teams.contains(value.trim())) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            teams.forEach(v -> builder.append(v + ","));
            builder.setLength(builder.length()-1);
            builder.append("].");

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Accepted values are " + builder.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
