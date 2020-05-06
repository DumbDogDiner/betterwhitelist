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
        
        config.setMaximumPoolSize(32);

        ds = new HikariDataSource(config);
        
        checkTable();
    }
    
    private void createAndExecUpdate(String request) throws SQLException {
    	PreparedStatement pt = ds.getConnection().prepareStatement(request);
    	
    	pt.executeUpdate();
    	
    	pt.close();
    	pt.getConnection().close();
    }
    
    private ResultSet createAndExecQuery(String request) throws SQLException {
    	PreparedStatement pt = ds.getConnection().prepareStatement(request);
    	
    	ResultSet result = pt.executeQuery();
    	
    	pt.close();
    	pt.getConnection().close();
    	
    	return result;
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
        	ResultSet result = createAndExecQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = `minecraft_whitelist`");

            while (result.next()) {
                if (result.getString(1) == "discord_id") {
                    return true;
                }
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
        	ResultSet res = createAndExecQuery("SELECT `discordID` FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");

            // Return the first result.
            while (res.next()) {
                String id = res.getString(1);
                return id;
            }

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
        	ResultSet res = createAndExecQuery("SELECT `minecraft_uuid` FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");
        	
            if (res.next()) {
                String uuid = res.getString(1);

                // BetterWhitelistBungee.getInstance().getLogger().info("Got '" + uuid + "' for
                // ID '" + discordID + "'.");
                return uuid;
            }

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
