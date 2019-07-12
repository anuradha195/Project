package com.example.BootcampProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import com.google.gson.Gson;

public class DBtoJson {
	
	JsonObjectFromDB jsonObj = new JsonObjectFromDB();
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		DBtoJson jsonList1 = new DBtoJson();
		String str = jsonList1.DBtoJsonMethod();
		//List<JsonArray> final_list = jsonList1.DBtoJsonMethod();
		System.out.println("Final list is : " + str);
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_json", "root", "Student007");
		String sql = "SELECT * FROM database_json.stepsdetails";
		String result = resultSetToJson(con,sql);
		//System.out.println("Result is " + result);
		
	}

	public String DBtoJsonMethod() throws Exception {
		
		//ArrayList<JsonObjectFromDB> resultObj = new ArrayList<JsonObjectFromDB>();
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_json", "root", "Student007");
		String sql = "SELECT * FROM database_json.stepsdetails";
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		//System.out.println("Result set is : " + rs);
		JsonArray jsonArray = null;
		ArrayList<JsonObject> jsonList = new ArrayList<>();
		JsonObject newObj = null;
		while (rs.next()) {
			
			int id = rs.getInt("id");
			Date date = rs.getDate("date");
			int steps = rs.getInt("steps");

			//getting data from SQL into Java file JsonObjectFroDB 
			
			jsonObj.setId(rs.getInt("id"));
			jsonObj.setDate(rs.getDate("date"));
			jsonObj.setSteps(rs.getInt("steps"));
			//resultObj.add(jsonObj);
			

			System.out.println("id is : " + id);
			System.out.println("Date is : " + date);
			System.out.println("Steps is : " + steps);
			String date_str = date.toString();

			System.out.println("*----------------------------------------*");

			JsonObject obj = Json.createObjectBuilder().add("date", date_str).add("steps", steps).build();
			jsonArray = Json.createArrayBuilder().add(obj).build();
			jsonList.add(obj);
			

		}
		//System.out.println("Json Array is " + jsonArray);
		//System.out.println("Json List is:  " + jsonList);
		//String json = new Gson().toJson(jsonList);
		System.out.println("Json String is:  " + jsonList);
		return jsonList.toString();
	} // end of method : DBtoJsonMethod

	public static String resultSetToJson(Connection connection, String query) {
		List<Map<String, Object>> listOfMaps = null;
		try {
			QueryRunner queryRunner = new QueryRunner();
			listOfMaps = queryRunner.query(connection, query, new MapListHandler());
		} catch (SQLException se) {
			throw new RuntimeException("Couldn't query the database.", se);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		return new Gson().toJson(listOfMaps);
	}

}
