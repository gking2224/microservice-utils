package me.gking2224.common.db.dao;

import java.io.Serializable;
import java.util.List;

public interface CrudDao<T, K extends Serializable> {

    T save(T thing);

    List<T> findAll();

    T update(T thing);

    void delete(K id);

    T findById(K id);
}
