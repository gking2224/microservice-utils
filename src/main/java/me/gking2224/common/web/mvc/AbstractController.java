package me.gking2224.common.web.mvc;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import me.gking2224.common.client.AbstractEntityBean;
import me.gking2224.common.model.AbstractEntity;

public abstract class AbstractController {

    protected <B extends AbstractEntityBean, E extends AbstractEntity<? extends Serializable, B>> List<B> toBeans(final List<E> entities) {
        
        return entities.stream().map(e -> e.getBean()).collect(Collectors.toList());
    }
}
