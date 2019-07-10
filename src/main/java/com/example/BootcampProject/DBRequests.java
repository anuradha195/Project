package com.example.BootcampProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;

@Controller
public class DBRequests {
	/*
	 * TODO: -create new request for Google Fit -parse data received from Google
	 */
	// URL("https://www.googleapis.com/fitness/v1/users/me/dataSources") for Get
	// request parameter, 
	// for post request: 
	// https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate

	public static void main(String[] args) throws Exception {
		DBRequests http = new DBRequests();
		System.out.println("\nTesting 2 - Send Http POST request");
		http.sendPost();
		http.insertJSONtoDB();
	}

	public String sendPost() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",
				"Bearer ya29.GltABwTkfp-nwNhDOSUKWlA8JukyF3uguqcTMcw0vdV1jbUPojpjDYYvCIHPm52kjLIWZSiOtgleAx6A482bX3ihm3uoZKoTfA9C8OZr9V_LKGjPAPxN4Gd8SxNe");
		// GET Today's date
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		long DAY_IN_MS = 1000 * 60 * 60 * 24;

		Calendar endcal = Calendar.getInstance();
		endcal.set(Calendar.HOUR, 0);
		endcal.set(Calendar.MINUTE, 0);
		endcal.set(Calendar.SECOND, 0);
		// System.out.println("Format Time Now: "+simpleformat.format(now.getTime()));
		endcal.set(Calendar.HOUR_OF_DAY, 0);
		long endtime = endcal.getTimeInMillis();
		Date startdate = new Date(endtime - (7 * DAY_IN_MS));
		long starttime = startdate.getTime();

		// Request parameters and other properties.
		httppost.setEntity(new StringEntity(" {\r\n" + "  \"aggregateBy\": [{\r\n" + "    \"dataSourceId\":\r\n"
				+ "      \"derived:com.google.step_count.delta:com.google.android.gms:estimated_steps\"\r\n"
				+ "  }],\r\n" + "  \"bucketByTime\": { \"durationMillis\": 86400000 },\r\n" + "  \"startTimeMillis\": "
				+ starttime + ",\r\n" + "  \"endTimeMillis\": " + endtime + "\r\n" + "}"));

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
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsontestout.json");
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
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsontestout.json");
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

			//final String SQL = "SELECT * FROM stepsdetails";
			final String SQL = "SELECT CHAR(date),steps FROM database_json.stepsdetails";
			final CategoryDataset dataset = new JDBCCategoryDataset(con, SQL);
			JFreeChart chart = ChartFactory.createBarChart("Bar Chart Example", "date", "steps", dataset,
					PlotOrientation.VERTICAL,true,true, false);
			CategoryPlot catplot = chart.getCategoryPlot();
			catplot.setRangeGridlinePaint(Color.BLACK);
			ChartPanel chartpanel = new ChartPanel(chart);
			chartpanel.setVisible(true); 
			Container jPanel1 = null;
			jPanel1.removeAll();
			jPanel1.add(chartpanel); //, BorderLayout.CENTER);
			jPanel1.validate();

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
