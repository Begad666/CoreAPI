package begad.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
	private HikariDataSource dataSource;
	private HikariConfig config;
	private boolean jdbc;

	private Database(HikariConfig config, boolean isJdbc) {
		this.config = config;
		this.jdbc = isJdbc;
	}

	/**
	 * Sets the username and password
	 *
	 * @param username The username to set
	 * @param password The password to set
	 */
	public void set(String username, String password) {
		config.setUsername(username);
		config.setPassword(password);
	}

	/**
	 * Adds a property to the datasource or the jdbc url
	 *
	 * @param property The property name
	 * @param value    The value to set the property to
	 * @throws NullPointerException If using datasource and value is null
	 */
	public void addProperty(String property, Object value) throws NullPointerException {
		if (!jdbc) {
			if (value == null) throw new NullPointerException("value cannot be null");
			config.addDataSourceProperty(property, value);
		} else {
			String jdbcUrl = config.getJdbcUrl();
			String toAdd = (jdbcUrl.contains("?") ? '&' : '?') + property + (value != null ? ("=" + value) : "");
			config.setJdbcUrl(jdbcUrl + toAdd);
		}
	}

	/**
	 * Starts the datasource if not started
	 */
	public void startDataSource() {
		if (dataSource == null) {
			dataSource = new HikariDataSource(config);
		}
	}

	/**
	 * Returns a new connection from the pool if datasource is started
	 *
	 * @return Connection from the pool
	 */
	public Connection getConnection() throws SQLException {
		if (dataSource != null && !dataSource.isClosed()) {
			return dataSource.getConnection();
		} else {
			return null;
		}
	}

	/**
	 * Stops the datasource if started
	 */
	public void stopDataSource() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
			dataSource = null;
		}
	}

	/**
	 * True if started otherwise false
	 */
	public boolean isStarted() {
		return dataSource != null && !dataSource.isClosed();
	}

	/**
	 * Class used to make {@link Database} instances
	 */
	public static class Factory {
		/**
		 * Creates a new Database using a datasource class
		 *
		 * @param dataSourceClassName The class name for the datasource <a href="https://github.com/brettwooldridge/HikariCP#popular-datasource-class-names">Click here for a list of popular libraries datasource names</a>
		 * @param serverName          The host to connect to (e.g. localhost, 127.0.0.1)
		 * @param port                The port to connect to
		 * @param databaseName        the name of the database (e.g. minecraft, server)
		 * @param poolName            Your pool name shown in the console
		 * @param maxPoolSize         Max connections in the pool
		 */
		public static Database setupWithDataSource(String dataSourceClassName,
		                                           String serverName,
		                                           int port,
		                                           String databaseName,
		                                           String poolName,
		                                           int maxPoolSize) {
			HikariConfig config = new HikariConfig();
			config.setDataSourceClassName(dataSourceClassName);
			config.addDataSourceProperty("serverName", serverName);
			config.addDataSourceProperty("port", port);
			config.addDataSourceProperty("databaseName", databaseName);
			config.setPoolName(poolName);
			config.setMaximumPoolSize(maxPoolSize);
			config.setLeakDetectionThreshold(15000);
			config.setConnectionTimeout(30000);
			return new Database(config, false);
		}

		/**
		 * Creates a new Database using a jdbc url
		 *
		 * @param jdbcUrl         The url
		 * @param driverClassName The class name of the driver (Optional, add only if hikari throws a exception about it cannot find the class name)
		 * @param poolName        Your pool name shown in the console
		 * @param maxPoolSize     Max connections in the pool
		 */
		public static Database setupWithJdbcUrl(String jdbcUrl,
		                                        String driverClassName,
		                                        String poolName,
		                                        int maxPoolSize) {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(jdbcUrl);
			if (driverClassName != null) {
				config.setDriverClassName(driverClassName);
			}
			config.setPoolName(poolName);
			config.setMaximumPoolSize(maxPoolSize);
			config.setLeakDetectionThreshold(15000);
			config.setConnectionTimeout(30000);
			return new Database(config, true);
		}

		/**
		 * Use this if you use {@link Database}'s isStarted() method to determine if the database is connected
		 */
		public static Database setupNothing() {
			return new Database(null, false);
		}
	}
}
