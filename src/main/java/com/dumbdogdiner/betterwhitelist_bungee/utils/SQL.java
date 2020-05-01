package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BaseClass;
import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

/**
 * SQL wrapper for fetching/storing user whitelist data.
 */
public class SQL implements BaseClass {

    private String databaseUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true",
            getConfig().getString("mysql.host"), getConfig().getString("mysql.port"),
            getConfig().getString("mysql.database"));

    private Boolean enabled = getConfig().getBoolean("enableSql");

    private HikariDataSource ds;

    public SQL() {
        // Create and configure SQL configuration.
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(getConfig().getString("mysql.username"));
        config.setPassword(getConfig().getString("mysql.password"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        checkTable();
    }

    /**
     * Fetch a connection from the pool, and use it to create a new SQL statement.
     * 
     * @return
     * @throws Exception
     */
    private Statement createStatement() throws Exception {
        if (!enabled) {
            throw new Exception("SQL disabled.");
        }

        return ds.getConnection().createStatement();
    }

    /**
     * Handle and print SQL errors to console.
     */
    private void handleSQLError(Exception e) {
        getLogger().severe("Failed to execute SQL statement.");
        e.printStackTrace();

    }

    /**
     * Check that the SQL table storing player UUIDs is valid.
     */
    public void checkTable() {
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        getLogger().info("[sql] Checking the UUID table is valid...");

        try {
            Statement statement = createStatement();
            String checkTable = "CREATE TABLE IF NOT EXISTS `minecraft_whitelist` (`discordID` VARCHAR(20),`minecraft_uuid` VARCHAR(36));";
            statement.executeUpdate(checkTable);
            statement.getConnection().close();
        }

        catch (Exception e) {
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

        getLogger().info("[sql] Upgrading table...");

        try {
            Statement statement = createStatement();
            String update = "ALTER TABLE `minecraft_whitelist` RENAME COLUMN `discordID` TO `discord_id`";

            statement.executeUpdate(update);

            update = "";

        } catch (Exception e) {
            handleSQLError(e);
        }
    }

    /**
     * Checks if the table can be upgraded.
     */
    private boolean checkIfUpgradeable() {

        try {
            ResultSet result = createStatement().executeQuery(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = `minecraft_whitelist`");

            while (result.next()) {
                if (result.getString(1) == "discord_id") {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
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
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            Statement statement = createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT `discordID` FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");

            // Return the first result.
            while (result.next()) {
                String id = result.getString(1);
                statement.getConnection().close();
                return id;
            }

            // If not found, return null.
            return null;
        }

        catch (Exception e) {
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
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            Statement statement = createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT `minecraft_uuid` FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");

            if (result.next()) {
                String uuid = result.getString(1);
                statement.getConnection().close();

                // BetterWhitelistBungee.getInstance().getLogger().info("Got '" + uuid + "' for
                // ID '" + discordID + "'.");
                return uuid;
            }

            return null;
        }

        catch (Exception e) {
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
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            Statement statement = createStatement();
            statement.executeUpdate("INSERT IGNORE INTO `minecraft_whitelist` (`discordID`, `minecraft_uuid`) VALUES ('"
                    + discordID + "','" + uuid + "');");
            statement.getConnection().close();

            getLogger().info("Added whitelist entry: '" + discordID + "' => '" + uuid + "'");

            return true;
        }

        catch (Exception e) {
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
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        // Check to make sure entry exists to be removed.
        if (getUuidFromDiscordId(discordID) == null) {
            getLogger().warning("Request to remove non-existent whitelist entry for ID '" + discordID + "'.");
            return false;
        }

        try {
            Statement statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");
            statement.getConnection().close();

            getLogger().info("Removed whitelist entry: '" + discordID + "'");

            return true;
        }

        catch (Exception e) {
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
        if (!enabled) {
            getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            Statement statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");
            statement.getConnection().close();
            return true;
        }

        catch (Exception e) {
            handleSQLError(e);
            return false;
        }
    }
}
