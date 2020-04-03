package nu.granskogen.spela.BanName;

import java.sql.*;
import java.util.concurrent.Executors;

public class DataBaseManager {
	private BanName pl = BanName.getInstance();
	private Connection connection = null;
	private String host, database, username, password;
	private int port;
	private String url;
	private int timeout = 15000;
	// private String table;

	public DataBaseManager() {
	}
	
	public boolean setup() {
		try {
			if (connection != null && !connection.isClosed()) {
				return true;
			}
			
			Class.forName("com.mysql.jdbc.Driver");
			this.host = pl.cfgm.getConfig().getString("database.host");
			this.database = pl.cfgm.getConfig().getString("database.database");
			this.username = pl.cfgm.getConfig().getString("database.username");
			this.password = pl.cfgm.getConfig().getString("database.password");
			port = 3306;

			this.url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=false";
			
			setConnection(DriverManager.getConnection(this.url, this.username, this.password));
			getConnection().prepareStatement(SQLQuery.CREATE_TABLE_BANNED_NAMES.toString()).executeUpdate();
			getConnection().prepareStatement(SQLQuery.CREATE_TABLE_BANNED_NAMES_LOG.toString()).executeUpdate();
			closeConnection();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed() || !connection.isValid(timeout)) {
				setConnection(DriverManager.getConnection(this.url, this.username, this.password));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		try {
			this.connection = connection;
			this.connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), timeout);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			if(!this.connection.isClosed())
				this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}

	public int getPort() {
		return port;
	}

	public String getUrl() {
		return url;
	}

	public int getTimeout() {
		return timeout;
	}
}
