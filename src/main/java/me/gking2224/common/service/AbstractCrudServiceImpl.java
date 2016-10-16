package me.gking2224.common.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import me.gking2224.common.db.dao.CrudDao;

@Transactional(readOnly=true)
public abstract class AbstractCrudServiceImpl<T, K extends Serializable> implements CrudService<T, K> {

    protected abstract CrudDao<T, K> getDao();
    
    @Override
    @Transactional(readOnly=false)
    public T save(T t) {
        return getDao().save(t);
    }

    @Override
    public List<T> findAll() {
        return getDao().findAll();
    }

    @Override
    @Transactional(readOnly=false)
    public void delete(K id) {
        getDao().delete(id);
    }

    @Override
    public T findById(K id) {
        return getDao().findById(id);
    }

}
