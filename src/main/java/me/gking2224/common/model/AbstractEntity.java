package me.gking2224.common.model;

import me.gking2224.common.client.AbstractEntityBean;

public abstract class AbstractEntity<B extends AbstractEntityBean> {

    public abstract B getBean();
}
