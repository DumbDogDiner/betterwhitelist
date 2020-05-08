package com.dumbdogdiner.betterwhitelist.utils;

import com.zaxxer.hikari.HikariConfigMXBean;

/**
 * Hikari Pool MBean Monitoring / Management
 * 
 * @link https://github.com/brettwooldridge/HikariCP/wiki/MBean-(JMX)-Monitoring-and-Management
 */
public class ConfigMXBean {
	
	private HikariConfigMXBean pool;
	
	public ConfigMXBean(HikariConfigMXBean pool) {
		this.pool = pool;
	}
	
	public String getCatalog() {
		return pool.getCatalog();
	}
	
	public String getPoolName() {
		return pool.getPoolName();
	}
	
	public long getConnectionTimeout() {
		return pool.getConnectionTimeout();
	}
	
	public long getIdleTimeout() {
		return pool.getIdleTimeout();
	}
	
	public long getLeakDetectionThreshold() {
		return pool.getLeakDetectionThreshold();
	}
	
	public long getMaxLifetime() {
		return pool.getMaxLifetime();
	}
	
	public long getMinimumIdle() {
		return pool.getMinimumIdle();
	}
	
	public long getValidationTimeout() {
		return pool.getValidationTimeout();
	}
	
	public int getMaximumPoolSize() {
		return pool.getMaximumPoolSize();
	}
}