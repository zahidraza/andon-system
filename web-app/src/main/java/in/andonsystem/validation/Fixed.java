package in.andonsystem.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;


@Documented
@Constraint(validatedBy = FixedValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, CONSTRUCTOR })
@Retention(RUNTIME)
public @interface Fixed {

    Class<? extends FixedValue> fixClass();

    String message() default "{com.xxx.bean.validation.constraints.StringEnumeration.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
