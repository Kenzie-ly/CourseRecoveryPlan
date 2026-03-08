package repository;
import java.sql.Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        
        // 1. Essential Settings
        config.setJdbcUrl("jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:6543/postgres?prepareThreshold=0");
        config.setUsername("postgres.zcolpwxxdlhgawzrwkxm");
        config.setPassword("#Kenletken123");

        // 2. Performance & Sizing
        config.setMaximumPoolSize(60);
        config.setIdleTimeout(10000); // 5 minutes
        config.setMinimumIdle(50);
        
        // 3. Recommended optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }
}