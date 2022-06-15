package nu.granskogen.spela.BanName;

public enum SQLQuery {
	CREATE_TABLE_BANNED_NAMES("CREATE TABLE IF NOT EXISTS banned_names (" + 
			"	name varchar(255) PRIMARY KEY," + 
			"    operator varchar(255) NOT NULL," + 
			"    time_banned datetime NOT NULL," + 
			"    time_changed datetime," +
			"    isBanned boolean NOT NULL" +
			");"),
	CREATE_TABLE_BANNED_NAMES_LOG("CREATE TABLE IF NOT EXISTS banned_names_log (" + 
			"	id int(11) AUTO_INCREMENT PRIMARY KEY NOT NULL," + 
			"    type text NOT NULL," + 
			"    time datetime NOT NULL," + 
			"    parameters longtext NOT NULL" +
			");"),
	SELECT_NAME("SELECT * FROM banned_names WHERE name=?;"),
	SET_BANNED_NAME_TRUE("UPDATE banned_names SET isBanned = true, operator = ?, time_changed = NOW() WHERE name=?;"),
	SET_BANNED_NAME_FALSE("UPDATE banned_names SET isBanned = false, operator = ?, time_changed = NOW() WHERE name=?;"),
	INSERT_NAME("INSERT INTO banned_names (name, operator, time_banned, isBanned) VALUES (?, ?, NOW(), ?);"),
	INSERT_INTO_LOG("INSERT INTO banned_names_log (type, time, parameters) VALUES (?, NOW(), ?);");
	private String mysql;
	
	SQLQuery(String mysql) {
		this.mysql = mysql;
	}
	
	@Override
    public String toString() {
        return mysql;
    }
}
