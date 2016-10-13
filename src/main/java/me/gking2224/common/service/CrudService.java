package me.gking2224.common.service;

import java.io.Serializable;
import java.util.List;

public interface CrudService<T, K extends Serializable> {


    T save(T t);

    List<T> findAll();

    T update(T t);

    void delete(K id);

    T findById(K id);
}
