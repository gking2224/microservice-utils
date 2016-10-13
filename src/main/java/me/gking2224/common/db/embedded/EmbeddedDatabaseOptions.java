package me.gking2224.common.db.embedded;

import java.util.Map;

public interface EmbeddedDatabaseOptions {

    String[] getScripts();

    Map<String, String> getDatabaseOptions();

    Map<String, String> getSessionVariables();
}
