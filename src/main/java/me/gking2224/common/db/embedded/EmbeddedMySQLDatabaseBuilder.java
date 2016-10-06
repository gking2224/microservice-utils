package me.gking2224.common.db.embedded;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;

public class EmbeddedMySQLDatabaseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedMySQLDatabaseBuilder.class);

    public static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String PASSWORD = "";

    private final String baseDatabaseDir = System.getProperty("java.io.tmpdir");
    private String databaseName = "test_db_" + System.nanoTime();
    private int port = new Random().nextInt(10000) + 3306;
    private final String username = "root";
    private boolean foreignKeyCheck = true;
    private boolean persistent = false;

    private final ResourceLoader resourceLoader;
    private final ResourceDatabasePopulator databasePopulator;

    public EmbeddedMySQLDatabaseBuilder() {
        resourceLoader = new DefaultResourceLoader();
        databasePopulator = new ResourceDatabasePopulator();
        foreignKeyCheck = true;
    }

    private EmbeddedMySQLDatabase createDatabase(MysqldResource mysqldResource) {
        if (!mysqldResource.isRunning()) {
            logger.error("MySQL instance not found... Terminating");
            throw new RuntimeException("Cannot get Datasource, MySQL instance not started");
        }
        EmbeddedMySQLDatabase database = new EmbeddedMySQLDatabase(mysqldResource);
        database.setDriverClassName(DRIVER_CLASS_NAME);
        database.setUsername(username);
        database.setPassword(PASSWORD);
        database.setDatabaseName(databaseName);
        String url = "jdbc:mysql://localhost:" + port + "/" + databaseName + "?" + "createDatabaseIfNotExist=true";

        if (!foreignKeyCheck) {
            url += "&sessionVariables=FOREIGN_KEY_CHECKS=0";
        }
        logger.debug("database url: {}", url);
        database.setUrl(url);
        database.setPersistent(persistent);
        return database;
    }

    private MysqldResource createMysqldResource() {
        if (logger.isDebugEnabled()) {
            logger.debug("=============== Starting Embedded MySQL using these parameters ===============");
            logger.debug("baseDatabaseDir : " + baseDatabaseDir);
            logger.debug("databaseName : " + databaseName);
            logger.debug("host : localhost (hardcoded)");
            logger.debug("port : " + port);
            logger.debug("username : root (hardcoded)");
            logger.debug("password : (no password)");
            logger.debug("=============================================================================");
        }

        Map<String, String> databaseOptions = new HashMap<String, String>();
        databaseOptions.put(MysqldResourceI.PORT, Integer.toString(port));

        MysqldResource MysqldResource = new MysqldResource(new File(baseDatabaseDir, databaseName));
        MysqldResource.start("embedded-mysql-thread-" + System.currentTimeMillis(), databaseOptions);

        if (!MysqldResource.isRunning()) {
            throw new RuntimeException("MySQL did not start");
        }

        logger.info("MySQL started successfully");
        return MysqldResource;
    }

    private void populateScripts(EmbeddedMySQLDatabase database) {
        try {
            DatabasePopulatorUtils.execute(databasePopulator, database);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            database.shutdown();
        }
    }

    public EmbeddedMySQLDatabaseBuilder addScript(String script) {
        databasePopulator.addScript(resourceLoader.getResource(script));
        return this;
    }

    public EmbeddedMySQLDatabaseBuilder addScripts(String... scripts) {
        Stream.of(scripts).forEach((s) -> addScript(s));
        return this;
    }
    public EmbeddedMySQLDatabaseBuilder addDefaultScripts() {
        return addScripts("schema.sql", "data.sql");
    }
    public EmbeddedMySQLDatabaseBuilder persistent() {
        this.persistent = true;
        return this;
    }

    /**
     * whether to enable MySql foreign key check
     *
     * @param foreignKeyCheck
     */
    public EmbeddedMySQLDatabaseBuilder setForeignKeyCheck(boolean foreignKeyCheck) {
        this.foreignKeyCheck = foreignKeyCheck;
        return this;
    }

    /**
     * @param databaseName
     *            the databaseName to set
     */
    public final EmbeddedMySQLDatabaseBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public EmbeddedMySQLDatabase build() {
        MysqldResource MysqldResource = createMysqldResource();
        EmbeddedMySQLDatabase database = createDatabase(MysqldResource);
        populateScripts(database);
        return database;
    }

    public EmbeddedMySQLDatabaseBuilder setPort(int i) {
        this.port = i;
        return this;
    }
}
