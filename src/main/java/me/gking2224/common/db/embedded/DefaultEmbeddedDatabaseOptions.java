package me.gking2224.common.db.embedded;

import java.util.Collections;
import java.util.Map;

public class DefaultEmbeddedDatabaseOptions implements EmbeddedDatabaseOptions {

    @Override
    public String[] getScripts() {
        return new String[] {"db/model/01_model.sql"};
    }

    @Override
    public Map<String, String> getDatabaseOptions() {
        return Collections.singletonMap("tinyInt1isBit", "false");
    }

    @Override
    public Map<String, String> getSessionVariables() {
        return Collections.emptyMap();
    }

}
