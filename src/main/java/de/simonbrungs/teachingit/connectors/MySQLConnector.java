package de.simonbrungs.teachingit.connectors;

import de.simonbrungs.teachingit.TeachingIt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.logging.Level;

public class MySQLConnector {
	private static MySQLConnector instance;
	private final String user;
	private final String password;
	private final int port;
	private final String database;
	private final String tablePrefix;
	private final String host;

	public MySQLConnector(String pUser, String pPassword, int pPort, String pHost, String ptablePrefix,
	                      String pDatabase) {
		instance = this;
		user = pUser;
		password = pPassword;
		port = pPort;
		host = pHost;
		database = pDatabase;
		tablePrefix = ptablePrefix;
		importDatabase();
	}

	public static MySQLConnector getInstance() {
		return instance;
	}

	private void importDatabase() {
		Connection con = createConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "users` ( `user` VARCHAR(128) NOT NULL ," + " `email` VARCHAR(254) NOT NULL ,"
					+ " `password` CHAR(40)," + " `id` INT(6) NOT NULL AUTO_INCREMENT , "
					+ "`regestrationdate` INT(10) NULL ," + "`activated` TINYINT(1) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "groups` ( `groupname` VARCHAR(128) NOT NULL ," + " `id` INT(5) AUTO_INCREMENT ,"
					+ " `supergroup` INT(5) NOT NULL ," + " `permissionheight` INT(9) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "permissions` ( `id` INT(9) NOT NULL AUTO_INCREMENT ," + " `permission` VARCHAR(512) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "grouppermissions` ( `groupid` INT(4) NOT NULL , `permissionid` INT(9) NOT NULL )");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "groupsusers` ( `userid` INT(6) NOT NULL , `groupid` INT(5) NOT NULL )");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + tablePrefix
					+ "usermeta` ( `metakey` VARCHAR(128) NOT NULL ," + " `id` INT(9) NOT NULL ,"
					+ " `userid` INT NOT NULL ," + " `metavalue` VARCHAR(256) NOT NULL )");
			preparedStatement.executeUpdate();
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id from `" + getDatabase() + "`.`" + getTablePrefix() + "groups` LIMIT 1");
			if (!resultSet.next()) {
				preparedStatement = con.prepareStatement(
						"insert into  `" + getDatabase() + "`.`" + getTablePrefix() + "groups` values (?, ?, ?, ?)");
				preparedStatement.setString(1, "Users");
				preparedStatement.setNull(2, 2);
				preparedStatement.setInt(3, -1);
				preparedStatement.setInt(4, 10);
				preparedStatement.executeUpdate();
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		closeConnection(con);
	}

	public String getDatabase() {
		return database;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public Connection createConnection() {
		Connection con;
		try {
			String add = "";
			if (TeachingIt.getInstance().getConfig().getProperty("MySQLUseSSL").equalsIgnoreCase("false")) {
				add = "&allowPublicKeyRetrieval=true&useSSL=false";
			}
			con = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/?user=" + user + "&password=" + password + "&serverTimezone=" + TeachingIt.getInstance().getConfig().getProperty("ServerTimeZone") + add);
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		}
		return con;

	}

	public void closeConnection(Connection pConnection) {
		try {
			pConnection.close();
		} catch (SQLException e) {
		}
	}

}
