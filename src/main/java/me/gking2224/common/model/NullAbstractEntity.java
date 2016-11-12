package me.gking2224.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class NullAbstractEntity<K extends Serializable> extends AbstractEntity<K, NullEntityBean>
implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -9178571050108131441L;

    @Override
    @JsonIgnore
    public final NullEntityBean getBean() {
        return NullEntityBean.INSTANCE;
    }
}
