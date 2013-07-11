package d13.dao.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


/**
 * Hibernate UserType definition for org.joda.time.DateTime,
 * Handles persistence of DateTime fields in the database.
 */
public class DateTimeType implements UserType {

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
        DateTime result = null;
        long dateAsInt = resultSet.getLong(names[0]);
        if (!resultSet.wasNull()) {
          result = new DateTime(dateAsInt, DateTimeZone.UTC);
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor s)
            throws HibernateException, SQLException {
          if (value == null) {
                statement.setNull(index, java.sql.Types.BIGINT);
              } else {
                long dateAsInteger = 
                    ((DateTime) value).getMillis();
                statement.setLong(index, dateAsInteger);
              }

    }

    @Override
    public Object replace(Object arg0, Object arg1, Object arg2)
            throws HibernateException {
        return arg0;
    }

    @Override
    public Class<?> returnedClass() {
            return DateTime.class;
    }

    private static final int SQL_TYPES[] = { Types.BIGINT };
    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

}
