package app.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;


public class LiquibaseConfig {
    private final YmlReader ymlReader;

    @Autowired
    public LiquibaseConfig(YmlReader ymlReader) {
        this.ymlReader = ymlReader;

        try (Connection connection = DriverManager.getConnection(
                ymlReader.getUrl(),
                ymlReader.getUsername(),
                ymlReader.getPassword())) {
            System.out.println("starting migration");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase(ymlReader.getChangelog(), new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("migration finished successfully");
        } catch (Exception e) {
            System.out.println("Got SQL Exception " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
