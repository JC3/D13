package d13.dao.types;

import d13.dao.Location;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;


/**
 * Hibernate UserType definition for Location
 */
public class LocationType implements UserType {

    @Override
    public Object assemble(Serializable arg0, Object arg1)
            throws HibernateException {
        return arg0;
    }

    @Override
    public Object deepCopy(Object arg0) throws HibernateException {
        return arg0;
    }

    @Override
    public Serializable disassemble(Object arg0) throws HibernateException {
        return (Serializable)arg0;
    }

    @Override
    public boolean equals(Object arg0, Object arg1) throws HibernateException {
        if (arg0 == arg1)
            return true;
        else if (arg0 == null || arg1 == null)
            return false;
        else
            return arg0.equals(arg1);
    }

    @Override
    public int hashCode(Object arg0) throws HibernateException {
        return arg0.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor s, Object owner)
            throws HibernateException, SQLException {
        Location result = null;
        Integer locationAsInt = resultSet.getInt(names[0]);
        if (!resultSet.wasNull()) {
            result = Location.fromDBId(locationAsInt);
            if (result == null)
                throw new HibernateException("Unknown location value '" + locationAsInt + "' in database.");
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor s)
            throws HibernateException, SQLException {
          if (value == null) {
                statement.setNull(index, java.sql.Types.INTEGER);
              } else {
                  int locationAsInt = ((Location)value).toDBId();
                  statement.setInt(index, locationAsInt);
              }

    }

    @Override
    public Object replace(Object arg0, Object arg1, Object arg2)
            throws HibernateException {
        return arg0;
    }

    @Override
    public Class<?> returnedClass() {
            return Location.class;
    }

    private static final int SQL_TYPES[] = { Types.INTEGER };
    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

}
