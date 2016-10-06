package me.gking2224.common.db.embedded;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mysql.management.MysqldResource;

public class EmbeddedMySQLDatabase extends DriverManagerDataSource {
    private final Logger logger = LoggerFactory.getLogger(EmbeddedMySQLDatabase.class);
    private final MysqldResource mysqldResource;
    private String databaseName;
    private boolean persistent = false;

    public EmbeddedMySQLDatabase(MysqldResource mysqldResource) {
        this.mysqldResource = mysqldResource;
    }

    public void shutdown() {
        if (mysqldResource != null) {
            mysqldResource.shutdown();
            if (!mysqldResource.isRunning() && !persistent) {
                logger.info("Deleting MySQL baseDir: {}", mysqldResource.getBaseDir());
                try {
                    FileUtils.forceDelete(mysqldResource.getBaseDir());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setPersistent(boolean persistent) {
        this.persistent= persistent;
    }
}