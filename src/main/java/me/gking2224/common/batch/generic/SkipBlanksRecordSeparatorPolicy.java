package me.gking2224.common.batch.generic;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.util.StringUtils;

public class SkipBlanksRecordSeparatorPolicy extends SimpleRecordSeparatorPolicy {

    @Override
    public boolean isEndOfRecord(String line) {
        if (!StringUtils.hasLength(line)) {
            return false;
        }
        return super.isEndOfRecord(line);
    }

    @Override
    public String postProcess(String record) {
        if (!StringUtils.hasLength(record)) {
            return null;
        }
        return super.postProcess(record);
    }

}
