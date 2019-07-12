package com.example.BootcampProject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class DBRequestsMonthly {

	ArrayList<Date> dateListMonthly = new ArrayList<Date>();
	List<Integer> stepsListMonthly = new ArrayList<Integer>();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DBRequestsMonthly req = new DBRequestsMonthly();
		req.sendPost();
		req.insertJSONtoDB();

	}
	public String sendPost() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",
				"Bearer ya29.GltDB493chE4BSKHTeUNGASsOQdPfIoRBRo6-1pXsvTuQ4zn9cJHekT61ONlZSacGJtznRYdPzwoGgMWju2d2OepOp7cNAlJC0QuZm4VGZXvhH2YErex_K54sSKo");
		// GET Today's date
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Request parameters and other properties.
		httppost.setEntity(new StringEntity(" {\r\n" + "  \"aggregateBy\": [{\r\n" + "    \"dataSourceId\":\r\n"
				+ "      \"derived:com.google.step_count.delta:com.google.android.gms:estimated_steps\"\r\n"
				+ "  }],\r\n" + "  \"bucketByTime\": { \"durationMillis\": 86400000 },\r\n" + "  \"startTimeMillis\": "
				+ "1559336400000" + ",\r\n" + "  \"endTimeMillis\": " + "1561928400000" + "\r\n" + "}"));

		// Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			try (InputStream instream = entity.getContent()) {
				// do something useful
				StringWriter writer = new StringWriter();
				Scanner s = new Scanner(instream).useDelimiter("\\A");
				result = s.hasNext() ? s.next() : "";

			}
			System.out.println(result);
			FileWriter fw = new FileWriter(
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsonMonthly.json");
			fw.write(result);
			System.out.println("Written json result to file");
			fw.close();
		}

		return result;
	}

	public void insertJSONtoDB() throws Exception {

		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_json", "root", "Student007");
		
		try {
			PreparedStatement stmt = con.prepareStatement("insert into  stepsdetails values ( 0, ?, ? )");
			// InputStream file = new
			// FileInputStream("C:\\Users\\Git\\BootcampProject\\BootcampJson.json");

			InputStream file = new FileInputStream(
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsonMonthly.json");
			JsonReader reader = Json.createReader(file);
			JsonObject jsonObj = reader.readObject();
			System.out.println("Json object is: " + jsonObj);
			reader.close();
			JsonArray jsonArrayBucket = jsonObj.getJsonArray("bucket");
			// System.out.println("Json Array dataset: "+jsonArrayBucket);

			for (Object projectObj : jsonArrayBucket.toArray()) {
				JsonObject project = (JsonObject) projectObj;
				JsonArray jsonArrayDataset = (JsonArray) project.get("dataset");

				// Get Date from json
				long timeInMillis = Long.parseLong(project.getString("startTimeMillis"));
				Date mydate = new Date(timeInMillis);
				java.sql.Date date = new java.sql.Date(mydate.getTime());

				// java.sql.Timestamp date = new java.sql.Timestamp(timeInMillis);
				for (Object issueObj : jsonArrayDataset.toArray()) {
					JsonObject datasetObj = (JsonObject) issueObj;
					// System.out.println("dataset object is : "+datasetObj);
					JsonArray jsonArraypoint = (JsonArray) datasetObj.get("point");

					for (Object issueObj1 : jsonArraypoint.toArray()) {
						JsonObject pointObj = (JsonObject) issueObj1;
						// System.out.println("point object is : "+pointObj);
						JsonArray jsonArrayvalue = (JsonArray) pointObj.get("value");
						// index1++;
						for (Object issueObj2 : jsonArrayvalue.toArray()) {
							JsonObject valueObj = (JsonObject) issueObj2;
							System.out.println(date + " --> Steps Count is: " + valueObj.getInt("intVal"));
							// Retrieved data and inserting into the database
							dateListMonthly.add(date);
							int x=valueObj.getInt("intVal");
							stepsListMonthly.add(x);
							stmt.executeUpdate("INSERT INTO stepsdetails VALUES (0 , '" + date + "','"
									+ valueObj.getInt("intVal") + "')");
							System.out.println("Data inserted");

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			System.out.println("Date list is : "+dateListMonthly);
			System.out.println("Steps list is : "+stepsListMonthly);
			
			try {
				if (con != null) {
					con.close();

				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

}
