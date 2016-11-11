package me.gking2224.common.model;

import java.io.Serializable;

import me.gking2224.common.client.AbstractEntityBean;

public abstract class AbstractEntity<K extends Serializable, B extends AbstractEntityBean> {

    public abstract B getBean();
    
    public abstract K getId();
    
    public abstract void setId(K id);
}
