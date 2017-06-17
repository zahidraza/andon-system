package in.andonsystem.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;


@Documented
@Constraint(validatedBy = StringEnumValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, CONSTRUCTOR })
@Retention(RUNTIME)
public @interface StringEnum {

  String message() default "{com.xxx.bean.validation.constraints.StringEnumeration.message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  Class<? extends Enum<?>> enumClass();

}