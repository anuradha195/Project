package com.example.BootcampProject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
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
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.fitness.FitnessScopes;


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
		return isUserAuthenticated?"dashboard":"index";
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
			return "dashboard";
		}
		return "index";
	}
	@GetMapping(value = {"/data"})
	@ResponseBody
	public Chartdata chartData (HttpServletRequest request) throws Exception{
		/* String code = request.getParameter("code");
		if(code!=null) {
			saveToken(code);
			return "dashboard";
		}
		return "index"; */
		Chartdata data = new Chartdata();
		data.animationEnabled=true;
		data.exportEnabled=true;
		
		ChartTitle title = new ChartTitle();
		title.text ="Steps data";
		ChartArray array = new ChartArray();
		array.type="bar";
		array.datapoints=Arrays.asList(100,200,300);
		data.data=new ChartArray[1];
		data.data[0]=array;
		data.title=title;
		System.out.println("Returning: "+data);
		
		return data;
	}
	@GetMapping(value = {"/home"})
	
	public String home(HttpServletRequest request) throws Exception{
		return "Project";
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


