package com.dumbdogdiner.betterwhitelist.utils;

import com.dumbdogdiner.betterwhitelist.BaseClass;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL wrapper for fetching/storing user whitelist data.
 */
public class SQL implements BaseClass {

    private String databaseUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true",
            getConfig().getString("mysql.host"), getConfig().getString("mysql.port"),
            getConfig().getString("mysql.database"));

    public final HikariDataSource ds;
    
    private final ConfigMXBean configMx;
    private final PoolMXBean poolMx;
    
    public ConfigMXBean getConfigMXBean() {
    	return configMx;
    }
    
    public PoolMXBean getPoolMXBean() {
    	return poolMx;
    }

    public SQL() {
        // Create and configure SQL configuration.
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(getConfig().getString("mysql.username"));
        config.setPassword(getConfig().getString("mysql.password"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("userServerPrepStmts", "true");
        
        // Enable MBeans for JMX Monitoring / Management
        config.setRegisterMbeans(true);
        
        // Set a pool name.
        config.setPoolName("BetterwhitelistBungeePool-1");
        
        config.setMaximumPoolSize(32);

        ds = new HikariDataSource(config);
        
        configMx = new ConfigMXBean(ds.getHikariConfigMXBean());
        poolMx = new PoolMXBean(ds.getHikariPoolMXBean());
        
        checkTable();
    }
    
    private void createAndExecUpdate(String request) throws SQLException {
    	PreparedStatement pt = ds.getConnection().prepareStatement(request);
    	
    	pt.executeUpdate();
    	
    	pt.getConnection().close(); // close the DB connection
    	pt.close(); // close the statement (removes ResultSet if there)
    }
    
    private QueryResponse createAndExecQuery(String request) throws SQLException {
    	PreparedStatement pt = ds.getConnection().prepareStatement(request);
    	
    	ResultSet result = pt.executeQuery();
    	
    	// remember to exec QueryResponse.closeAll() once all ResultSet operations are done! :)
    	return new QueryResponse(result, pt);
    }
    
    private class QueryResponse {
    	public ResultSet result;
		public PreparedStatement statement;

    	private QueryResponse (ResultSet result, PreparedStatement statement) {
    		this.result = result;
    		this.statement = statement;
    	}
    	
    	private void closeAll() throws SQLException {
    		statement.getConnection().close(); // close the DB connection
    		statement.close() ;// close the statement (removes ResultSet if there)
    	}
    }

    /**
     * Handle and print SQL errors to console.
     */
    private void handleSQLError(Exception e) {
        getLogger().severe(getConfig().getString("lang.console.sql.handleSqlError"));
        e.printStackTrace();

    }

    /**
     * Check that the SQL table storing player UUIDs is valid.
     */
    public void checkTable() {
        getLogger().info("[SQL] " + getConfig().getString("lang.console.sql.checkTable"));

        try {
        	createAndExecUpdate("CREATE TABLE IF NOT EXISTS `minecraft_whitelist` (`discordID` VARCHAR(20),`minecraft_uuid` VARCHAR(36));");
        }

        catch (SQLException e) {
            handleSQLError(e);
        }
    }

    /**
     * Check, and upgrade the SQL table - for later.
     */
    private void upgradeTable() {
        if (!checkIfUpgradeable()) {
            return;
        }

        getLogger().info("[SQL] " + getConfig().getString("lang.console.sql.upgradingTable"));

        try {
        	createAndExecUpdate("ALTER TABLE `minecraft_whitelist` RENAME COLUMN `discordID` TO `discord_id`");
        } catch (SQLException e) {
            handleSQLError(e);
        }
    }

    /**
     * Checks if the table can be upgraded.
     */
    private boolean checkIfUpgradeable() {

        try {
        	QueryResponse res = createAndExecQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = `minecraft_whitelist`");

            while (res.result.next()) {
                if (res.result.getString(1) == "discord_id") {
                	res.closeAll();
                	
                    return true;
                }
                
                res.closeAll();
            }

            return false;
        } catch (SQLException e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Fetch a user's Discord ID from their Minecraft UUID.
     * 
     * @param uuid
     * @return
     */
    public String getDiscordIDFromMinecraft(String uuid) {
        try {
        	QueryResponse res = createAndExecQuery("SELECT `discordID` FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");

            // Return the first result.
            while (res.result.next()) {
                String id = res.result.getString(1);
                
                res.closeAll();
                
                return id;
            }
            
            res.closeAll();

            // If not found, return null.
            return null;
        }

        catch (SQLException e) {
            handleSQLError(e);
            return null;
        }
    }

    /**
     * Fetch a user's Minecraft UUID from their Discord ID.
     * 
     * @param discordID
     * @return
     */
    public String getUuidFromDiscordId(String discordID) {
        try {
        	QueryResponse res = createAndExecQuery("SELECT `minecraft_uuid` FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");
        	
            if (res.result.next()) {
                String uuid = res.result.getString(1);
                
                res.closeAll();

                // BetterWhitelistBungee.getInstance().getLogger().info("Got '" + uuid + "' for
                // ID '" + discordID + "'.");
                return uuid;
            }
            res.closeAll();

            return null;
        }

        catch (SQLException e) {
            handleSQLError(e);
            return null;
        }
    }

    /**
     * Add a user to the SQL database.
     * 
     * @param discordID
     * @param uuid
     */
    public boolean addEntry(String discordID, String uuid) {
        try {
        	createAndExecUpdate("INSERT IGNORE INTO `minecraft_whitelist` (`discordID`, `minecraft_uuid`) VALUES ('" + discordID + "','" + uuid + "');");

            getLogger().info("[SQL] " + String.format(getConfig().getString("lang.console.sql.addEntry"), discordID, uuid));

            return true;
        }

        catch (SQLException e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Discord ID.
     * 
     * @param discordID
     */
    public boolean removeEntry(String discordID) {
        // Check to make sure entry exists to be removed.
        if (getUuidFromDiscordId(discordID) == null) {
            getLogger().warning("[SQL] " + String.format(getConfig().getString("lang.console.sql.removeEntryPreWarning"), discordID));
            return false;
        }

        try {
        	createAndExecUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");

            getLogger().info("[SQL] " + String.format(getConfig().getString("lang.console.sql.removeEntryPostInfo"), discordID));

            return true;
        }

        catch (SQLException e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Minecraft UUID.
     * 
     * @param uuid
     */
    public boolean removeEntryUsingUuid(String uuid) {
        try {
        	createAndExecUpdate("DELETE FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");
            return true;
        }

        catch (SQLException e) {
            handleSQLError(e);
            return false;
        }
    }
}
