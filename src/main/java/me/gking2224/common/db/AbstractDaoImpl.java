package me.gking2224.common.db;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.SqlReturnUpdateCount;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.client.AbstractEntityBean;
import me.gking2224.common.db.dao.CrudDao;
import me.gking2224.common.model.AbstractEntity;

/**
 * Abstract DAO class providing convenience access to database functions
 * @author gk
 *
 */
@Transactional(readOnly=true)
public abstract class AbstractDaoImpl<T extends AbstractEntity<K, ? extends AbstractEntityBean>, K extends Serializable> implements CrudDao<T, K> {

    private EntityManager entityManager;

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        entityManager = emf.createEntityManager();
    }
    
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    protected void flush() {
        entityManager.joinTransaction();
        entityManager.flush();
    }
    
    public interface ComplexFetchCallback<E> {

        E getComplexObject(Map<String, Object> call);

        void addAdditionalOutputParameters(List<SqlParameter> parameters);

    }

    enum CallType {
        UPDATE,
        FETCH_MANY,
        FETCH_ONE,
        FETCH_COMPLEX
    }
    
    private static final String UPDATE_COUNT = "update-count";

    private static final String RESULT_SET = "result-set";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    @SuppressWarnings("unchecked")
    public T fetchOne(final String sproc, final RowMapper<T> rowMapper, final Object... args) {
        logger.debug("fetchOne: {} with args", sproc, args);
        return (T)_call(CallType.FETCH_ONE, sproc, rowMapper, null, args);
    }
    
    @SuppressWarnings("unchecked")
    public List<T> fetchMany(final String sproc, final RowMapper<T> rowMapper, final Object... args) {
        logger.debug("fetchMany: {} with args {}", sproc, args);
        return (List<T>)_call(CallType.FETCH_MANY, sproc, rowMapper, null, args);
    }
    
    @SuppressWarnings("unchecked")
    public <E> E fetchComplex(final String sproc, final ComplexFetchCallback<E> callback, final Object... args) {
        logger.debug("fetchComplex: {} with args {}", sproc, args);
        return (E)_call(CallType.FETCH_COMPLEX, sproc, null, callback, args);
    }

    @Transactional(readOnly=false)
    public int update(final String sproc, final Object... args) {
        logger.debug("update: {} with args {}", sproc, args);
        return ((Integer)_call(CallType.UPDATE, sproc, null, null, args)).intValue();
    }
    
    public Object _call(
            final CallType type, final String sproc, final RowMapper<T> rowMapper,
            final ComplexFetchCallback<?> complexFetchCallback,
            final Object... args) {
        List<SqlParameter> parameters = getInputParameters(type, args);
        addStandardOutputParameters(parameters, type, rowMapper);
        if (complexFetchCallback != null) complexFetchCallback.addAdditionalOutputParameters(parameters);
        Map<String, Object> call = jdbcTemplate.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                CallableStatement cs = con.prepareCall(sproc);
                setConnectionArgs(cs, args);
                return cs;
            }
        }, parameters);

        if (type == CallType.UPDATE) {
            return getUpdateCount(call);
        }
        else if (type == CallType.FETCH_MANY) {
            return getResultSetAsList(call, RESULT_SET);
        }
        else if (type == CallType.FETCH_ONE) {
            return getResultSetAsObject(call, RESULT_SET);
        }
        else if (type == CallType.FETCH_COMPLEX) {
            return complexFetchCallback.getComplexObject(call);
        }
        else {
            throw new IllegalStateException("Unknown call type");
        }
    }

    @SuppressWarnings("unchecked")
    protected List<T> getResultSetAsList(final Map<String, Object> call, final String resultSet) {
        Object o = call.get(resultSet);
        if (o == null || !List.class.isAssignableFrom(o.getClass())) {
            throw new IllegalStateException(String.format("Unexpected value returned"));
        }
        return (List<T>)o;
    }

    protected T getResultSetAsObject(final Map<String, Object> call, final String resultSet) {

        return getSingleObject(getResultSetAsList(call, resultSet));
    }

    protected Integer getUpdateCount(Map<String, Object> call) {
        Object updateCount = call.get(UPDATE_COUNT);
        if (updateCount == null || !Integer.class.isAssignableFrom(updateCount.getClass())) {
            throw new IllegalStateException("Could not retrieve update count");
        }
        else {
            return (Integer)updateCount;
        }
    }

    private List<SqlParameter> getInputParameters(final CallType type, final Object[] args) {
        List<SqlParameter> rv = new ArrayList<SqlParameter>(args.length+1);
        int idx = 0;
        for (Object arg : args) {
            if (arg == null) {
                rv.add(idx++, new SqlParameterValue(Types.NULL, null));
            }
            else if (String.class.isAssignableFrom(arg.getClass())) {
                rv.add(new SqlParameterValue(Types.CHAR, arg));
            }
            else if (Integer.class.isAssignableFrom(arg.getClass())) {
                rv.add(new SqlParameterValue(Types.INTEGER, arg));
            }
            else if (Long.class.isAssignableFrom(arg.getClass())) {
                rv.add(new SqlParameterValue(Types.BIGINT, arg));
            }
            else if (Boolean.class.isAssignableFrom(arg.getClass())) {
                rv.add(new SqlParameterValue(Types.BOOLEAN, arg));
            }
        }
        return rv;
    }
    
    protected void addStandardOutputParameters(
            final List<SqlParameter> parameters, final CallType type, final RowMapper<T> rowMapper) {
        
        if (type == CallType.UPDATE) {
            parameters.add(new SqlReturnUpdateCount(UPDATE_COUNT));
        }
        else if (type != CallType.FETCH_COMPLEX) {
            parameters.add(new SqlReturnResultSet(RESULT_SET, rowMapper));
        }
    }

    protected void setConnectionArgs(CallableStatement cs, Object[] args) throws SQLException {
        int idx =1;
        for (Object arg : args) {
            if (arg == null) {
                cs.setNull(idx++, Types.NULL);
            }
            else if (String.class.isAssignableFrom(arg.getClass())) {
                try {
                    cs.setString(idx++, (String)arg);
                } catch (SQLException e) {
                    throw new UncategorizedScriptException(e.getMessage(), e);
                }
            }
            else if (Integer.class.isAssignableFrom(arg.getClass())) {
                try {
                    cs.setInt(idx++, (Integer)arg);
                } catch (SQLException e) {
                    throw new UncategorizedScriptException(e.getMessage(), e);
                }
            }
            else if (Long.class.isAssignableFrom(arg.getClass())) {
                try {
                    cs.setLong(idx++, (Long)arg);
                } catch (SQLException e) {
                    throw new UncategorizedScriptException(e.getMessage(), e);
                }
            }
            else if (Boolean.class.isAssignableFrom(arg.getClass())) {
                try {
                    cs.setBoolean(idx++, (Boolean)arg);
                } catch (SQLException e) {
                    throw new UncategorizedScriptException(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public List<T> findAll() {
        List<T> tt = getRepository().findAll();
        return tt;
    }

    @Transactional(readOnly=false)
    @Override
    public T save(T t) {
        T saved = getRepository().save(t);
        return saved;
    }

    @Override
    @Transactional(readOnly=false)
    public T saveOrUpdate(final T t) {
        
        if (t.getId() == null) {
            logger.debug("id is non-null, find existing");
            T existing = findExisting(t);
            logger.debug("got {}", existing);
            if (existing != null) {
                t.setId(existing.getId());
            }
        }
        T saved = save(t);
        return saved;
    }

    protected T findExisting(T t) {
        return null;
    }

    @Override
    @Transactional(readOnly=false)
    public void delete(K id) {
        getRepository().delete(id);
    }

    @Override
    public T findById(K id) {
        T t = getRepository().findOne(id);
        return t;
    }
    
    protected <E> E getSingleObject(final List<E> list) {
        
        if (list.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1);
        }
        if (list.size() == 0) {
            throw new EmptyResultDataAccessException(1);
        }
        return list.get(0);
    }

    protected abstract JpaRepository<T, K> getRepository();
}
