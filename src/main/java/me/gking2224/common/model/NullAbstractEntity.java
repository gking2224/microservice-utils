package me.gking2224.common.model;

import java.io.Serializable;

public abstract class NullAbstractEntity<K extends Serializable> extends AbstractEntity<K, NullEntityBean> {

    @Override
    public final NullEntityBean getBean() {
        return NullEntityBean.INSTANCE;
    }
}
