package d13.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataView {

    Class<? extends DataConverter> value() default DefaultDataConverter.class;
    int i() default -1;
    String n() default "";
    boolean longtext() default false;
    
}
