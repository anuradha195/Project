package com.example.BootcampProject;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.fitness.FitnessScopes;
import com.google.gson.Gson;

import com.google.gson.Gson;

@Controller @EnableWebMvc
public class bootCampController {
	 
	private static HttpTransport HTTP_TRANSPORT =new NetHttpTransport();
	private static JsonFactory JSON_FACTORY =JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(FitnessScopes.FITNESS_ACTIVITY_READ);
	
	//private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String  USER_IDENTIFIER_KEY="MY_DUMMY_USER";
	 
	@Value("${google.oauth.callback.uri}")
	private String CALLBACK_URI;
	
	@Value("${google.secret.key.path}")
	private Resource gdSecretKeys;
	
	@Value("${google.credentials.folder.path}")
	private Resource credentialsFolder;
	
	private GoogleAuthorizationCodeFlow flow;
	
	@PostConstruct
	public void init() throws Exception {
		
		
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(gdSecretKeys.getInputStream()));
		flow=new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
	}
	
	@GetMapping(value= {"/"})
	public String showHomePage() throws Exception {
		boolean isUserAuthenticated = false;
		
		Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
		if(credential!=null) {
			boolean tokenValid = credential.refreshToken();
			if(tokenValid) {
				isUserAuthenticated = true;
			}	
		}
		return isUserAuthenticated?"new1.html":"index";
	}
	
	@GetMapping(value = {"/googlesignin"})
	public void doGoogleSignIn(HttpServletResponse response) throws Exception{
		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
		response.sendRedirect(redirectURL);	
	}
	
	@GetMapping(value = {"/redirect"})
	public String saveAuthorizationCode(HttpServletRequest request) throws Exception{
		String code = request.getParameter("code");
		if(code!=null) {
			saveToken(code);
			return "Project";
		}
		return "index";
	}
	@GetMapping(value = {"/data"})
	//@ResponseBody
	public Chartdata chartData (HttpServletRequest request) throws Exception{
		/* String code = request.getParameter("code");
		if(code!=null) {
			saveToken(code);
			return "dashboard";
		}
		return "index"; */
		DBRequests req = new DBRequests();
		
		Chartdata data = new Chartdata();
		data.animationEnabled=true;
		data.exportEnabled=true;
		
		ChartTitle title = new ChartTitle();
		title.text ="Steps data";
		ChartArray array = new ChartArray();
		array.type="bar";
		//array.datapoints=req.stepsList;
		data.data=new ChartArray[1];
		data.data[0]=array;
		data.title=title;
		System.out.println("Returning: "+data);
		
		return data;
	}
	@GetMapping(value = {"/home"})
	@ResponseStatus(value = HttpStatus.OK)
	public String home(HttpServletRequest request) throws Exception{
		//return "Project";
		 DBtoJson json = new DBtoJson();
		System.out.println("Json is: "+json.DBtoJsonMethod());
		return json.DBtoJsonMethod();
		//return  new Gson().toJson(json.DBtoJsonMethod());
		//DBRequests req = new DBRequests();
		//return req.sendPost();
		/* Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_json", "root", "Student007");
		String sql = "SELECT * FROM database_json.stepsdetails";
		return DBtoJson.resultSetToJson(con, sql);  */
		}
		
	@RequestMapping(value = {"/Chart"})
	@ResponseBody
	public String getWeeklyGraph() throws Exception{
		
		DBRequests req = new DBRequests();
		req.insertJSONtoDB();
		System.out.println("StepsList is : "+req.stepsListWeekly);
		System.out.println("DateList is : "+req.dateListWeekly);
		
		String str="<!DOCTYPE html>\n"
				+ "<html>\r\n" + 
				"  <head>\r\n" + 
				"<style>"+
				"body {\r\n" +
				"-webkit-animation: colorchange 60s infinite;"+ 
				"animation: colorchange 60s infinite;"+
				"}"+
				"@-webkit-keyframes colorchange {"+
				"0%  {background: #33FFF3;}"+
				"25%  {background: #78281F;}"+
				"50%  {background: #117A65;}"+
				"75%  {background: #DC7633;}"+
				"100% {background: #9B59B6;}"+
				"}"+
				"@keyframes colorchange {"+
				"0%  {background: #33FFF3;}"+
				"25%  {background: #78281F;}"+
				"50%  {background: #117A65;}"+
				"75%  {background: #DC7633;}"+
				"100% {background: #9B59B6;}"+
				"}"+   

				"</style>"+
				"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\r\n" + 
				"    <script type=\"text/javascript\">\r\n" + 
				"      google.charts.load('current', {'packages':['corechart']});\r\n" + 
				"      google.charts.setOnLoadCallback(drawChart);\r\n" + 
				"\r\n" + 
				"      function drawChart() {\r\n" + 
				"        var data = google.visualization.arrayToDataTable([\n" +
				 "['Date', 'Steps', {type: 'number', role: 'annotation'}],\n";
		
		for(int i=0; i < req.stepsListWeekly.size(); i ++) {
			str=str+"['"+req.dateListWeekly.get(i)+"'"+", "+req.stepsListWeekly.get(i)+", "+req.stepsListWeekly.get(i)+"],\n";
		}
				
		str=str+"]);\r\n" + 
						"\r\n" + 
						"        var options = {\r\n" + 
						"          title: 'Weekly Steps Count',\r\n" +
						"          curveType: 'function',\r\n" + 
						"          legend: { position: 'bottom' }\r\n" + 
						"        };\r\n" + 
						"\r\n" + 
						"        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));\r\n" + 
						"\r\n" + 
						"        chart.draw(data, options);\r\n" + 
						"      }\r\n" + 
						"    </script>\r\n" + 
						"  </head>\r\n" + 
						"  <body>\r\n" + 
						"    <div id=\"curve_chart\" style=\"width: 900px; height: 500px\"></div>\r\n" + 
						"  </body>\r\n" + 
						"</html>";
		return str;
		
	}
	
	@RequestMapping(value = {"/MonthlyChart"})
	@ResponseBody
	public String getMonthlyGraph() throws Exception{
		
		DBRequestsMonthly req = new DBRequestsMonthly();
		req.insertJSONtoDB();
		System.out.println("StepsList is : "+req.stepsListMonthly);
		System.out.println("DateList is : "+req.dateListMonthly);
		
		String str="<!DOCTYPE html>\n"
				+ "<html>\r\n" + 
				"  <head>\r\n" + 
				"<style>"+
				"body {\r\n" +
				"-webkit-animation: colorchange 60s infinite;"+ 
				"animation: colorchange 60s infinite;"+
				"}"+
				"@-webkit-keyframes colorchange {"+
				"0%  {background: #33FFF3;}"+
				"25%  {background: #78281F;}"+
				"50%  {background: #117A65;}"+
				"75%  {background: #DC7633;}"+
				"100% {background: #9B59B6;}"+
				"}"+
				"@keyframes colorchange {"+
				"0%  {background: #33FFF3;}"+
				"25%  {background: #78281F;}"+
				"50%  {background: #117A65;}"+
				"75%  {background: #DC7633;}"+
				"100% {background: #9B59B6;}"+
				"}"+   

				"</style>"+
				"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\r\n" + 
				"    <script type=\"text/javascript\">\r\n" + 
				"      google.charts.load('current', {'packages':['corechart']});\r\n" + 
				"      google.charts.setOnLoadCallback(drawChart);\r\n" + 
				"\r\n" + 
				"      function drawChart() {\r\n" + 
				"        var data = google.visualization.arrayToDataTable([\n" +
				 "['Date', 'Steps'],\n";
		
		for(int i=0; i < req.stepsListMonthly.size(); i ++) {
			str=str+"['"+req.dateListMonthly.get(i)+"'"+", "+req.stepsListMonthly.get(i)+"],\n";
		}
				
		str=str+"]);\r\n" + 
						"\r\n" + 
						"        var options = {\r\n" + 
						"          title: 'Weekly Steps Count',\r\n" +
						"          curveType: 'function',\r\n" + 
						"          legend: { position: 'bottom' }\r\n" + 
						"        };\r\n" + 
						"\r\n" + 
						"        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));\r\n" + 
						"\r\n" + 
						"        chart.draw(data, options);\r\n" + 
						"      }\r\n" + 
						"    </script>\r\n" + 
						"  </head>\r\n" + 
						"  <body>\r\n" + 
						"    <div id=\"curve_chart\" style=\"width: 900px; height: 500px\"></div>\r\n" + 
						"  </body>\r\n" + 
						"</html>";
		return str;
		
	}
	
	
	@PostMapping(path="/api/json")
	public String update(@RequestBody DBRequests request) throws Exception {
	    return request.sendPost();
	}
	
	public void saveToken(String code) throws Exception {
		GoogleTokenResponse response =flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
		flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
	}
	
	
}


