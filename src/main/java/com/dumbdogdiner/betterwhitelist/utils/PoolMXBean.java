package com.dumbdogdiner.betterwhitelist.utils;

import com.zaxxer.hikari.HikariPoolMXBean;

/**
 * Hikari Pool MBean Monitoring / Management
 * 
 * @link https://github.com/brettwooldridge/HikariCP/wiki/MBean-(JMX)-Monitoring-and-Management
 */
public class PoolMXBean {
	
	private HikariPoolMXBean pool;
	
	public PoolMXBean(HikariPoolMXBean pool) {
		this.pool = pool;
	}
	
	public int getActiveConnections() {
		return pool.getActiveConnections();
	}
	
	public int getIdleConnections() {
		return pool.getIdleConnections();
	}
	
	public int getTotalConnections() {
		return pool.getTotalConnections();
	}
	
	public int getThreadsAwaitingConnection() {
		return pool.getThreadsAwaitingConnection();
	}
}