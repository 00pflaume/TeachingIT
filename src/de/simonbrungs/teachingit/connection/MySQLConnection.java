package de.simonbrungs.teachingit.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;

public class MySQLConnection {
	private String user;
	private String password;
	private int port;
	private String database;
	private String databasePrefix;
	private String host;

	public MySQLConnection(String pUser, String pPassword, int pPort, String pHost, String pDatabasePrefix,
			String pDatabase) {
		user = pUser;
		password = pPassword;
		port = pPort;
		host = pHost;
		database = pDatabase;
		databasePrefix = pDatabasePrefix;
		importDatabase();
	}

	private void importDatabase() {
		Connection con = createConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + databasePrefix
					+ "users` ( `user` VARCHAR(128) NOT NULL ," + " `email` VARCHAR(254) NOT NULL ,"
					+ " `password` CHAR(32)," + " `id` INT NOT NULL AUTO_INCREMENT , "
					+ "`regestrationdate` INT(10) NULL ," + "`activated` TINYINT(1) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + databasePrefix
					+ "groups` ( `groupname` VARCHAR(128) NOT NULL ," + " `id` INT(5) AUTO_INCREMENT ,"
					+ " `supergroup` INT(5) NOT NULL ," + " `permissionheight` INT(9) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + databasePrefix
					+ "permissions` ( `id` INT(9) NOT NULL AUTO_INCREMENT ," + " `permission` VARCHAR(512) NOT NULL ,"
					+ " PRIMARY KEY (`id`))");
			preparedStatement.executeUpdate();
			preparedStatement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + databasePrefix
					+ "grouppermissions` ( `groupid` INT(5) NOT NULL , `permissionid` INT(9) NOT NULL ))");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(con);
	}

	public String getDatabase() {
		return database;
	}

	public String getTablePrefix() {
		return databasePrefix;
	}

	public Connection createConnection() {
		Connection con;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection("jdbc:mysql://" + host + ":" + port + "/?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e1) {
			System.out.println(TeachingIt.getInstance().PREFIX + "Fatal Error the system is now going to hold.");
			e1.printStackTrace();
			TeachingIt.getInstance().shutDown();
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
