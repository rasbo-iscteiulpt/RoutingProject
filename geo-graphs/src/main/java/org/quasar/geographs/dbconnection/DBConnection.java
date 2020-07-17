package org.quasar.geographs.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

import org.quasar.geographs.algortihm.PointOfInterest;

public class DBConnection {

	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/crowding";

	// database credentials
	final String USER = "root";
	final String PASS = "";

	private Connection conn = null;
	private Statement stmt = null;
	
	private HashMap<Integer,Integer> visitTimes = new HashMap<Integer,Integer>();

	public void start() {

		try {
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("connecting to the database");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("creating a statement..");
			stmt = conn.createStatement();

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void close() {

		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}

		System.out.println("database closed");
	}

	public LinkedList<PointOfInterest> getPOI() {
		LinkedList<PointOfInterest> result = new LinkedList<PointOfInterest>();

		String sql = "SELECT point_id,longitude,latitude,altitude,sustainability,opens_hours,closes_hours,category_id,price"
				+ " from point_of_interest;";
		try {

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt("point_id");
				double latitude = rs.getDouble("latitude");
				double altitude = rs.getDouble("altitude");
				double longitude = rs.getDouble("longitude");
				int sustainability = rs.getInt("sustainability");
				int openHour = rs.getInt("opens_hours");
				int closeHour = rs.getInt("closes_hours");
				int category = rs.getInt("category_id");
				double price = rs.getDouble("price");

				// displaying values:
				// System.out.println("id " + id);

				PointOfInterest poi = new PointOfInterest(id, latitude, longitude, altitude, sustainability, openHour,
						closeHour, category, price);
				
				//int visitTime = visitTimes.get(category);
				//poi.setVisitTime(visitTime);
				result.add(poi);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public void getVisitTime() {

		String sql = "SELECT category_id, visit_time FROM category";

		try {

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int category_id= rs.getInt("category_id");
				int visit_time = rs.getInt("visit_time");
				
				visitTimes.put(category_id,visit_time);
				
//				System.out.println("category_id " + category_id);
//				System.out.println("visit_time: " + visit_time);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public HashMap<Integer,Integer> visitTimesHashMap(){
		return visitTimes;
	}
	
	public static void main(String[] args) {
		DBConnection db = new DBConnection();
		db.start();
		System.out.println(db.getPOI().size());
		
		db.close();
	}
}
