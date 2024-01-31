 package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;



public class DbConnection {
	
	private static  String HOST = "Localhost";
	private static  String PASSWORD = "projects";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";

	
	public static Connection getConnection() {
		
		String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false", HOST, PORT, SCHEMA, USER, PASSWORD );
		
		System.out.println("Connection with url=" + uri);
		
		try {
			Connection conn = DriverManager.getConnection(uri);
			System.out.println("Successfully optained connection!");
			return conn;
			
		} catch (SQLException e) {
			System.out.println("Error getting a connection");
			throw new DbException(e);
		}
		
	}

}
