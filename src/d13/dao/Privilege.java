package d13.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to mark privilege getters in Role to facilitate creation of view_roles.jsp.
 * @author Jason
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Privilege {

}
