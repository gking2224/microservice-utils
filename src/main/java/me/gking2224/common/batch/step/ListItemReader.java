package me.gking2224.common.batch.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;

public class ListItemReader<I> extends AbstractItemCountingItemStreamItemReader<I> {
    
    private static Logger logger = LoggerFactory.getLogger(ListItemReader.class);

    List<I> list;

    public ListItemReader(String name, List<I> list) {
        super();
        super.setName(name);
        this.list = list;
    }

    @Override
    protected I doRead() throws Exception {
        int idx = super.getCurrentItemCount() -1;
        if (idx >= list.size()) return null;
        I item = list.get(idx);
        logger.debug("Read item from list: {}", item);
        return item;
    }

    @Override
    protected void doOpen() throws Exception {}
    @Override
    protected void doClose() throws Exception {}
}