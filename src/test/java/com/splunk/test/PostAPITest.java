package com.splunk.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splunk.base.TestBase;
import com.splunk.client.RestClient;
import com.splunk.data.Movies;

public class PostAPITest extends TestBase {
	TestBase testBase;
	String postUrl;
	RestClient restClient;
	CloseableHttpResponse closebaleHttpResponse;
	
	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException{
		testBase = new TestBase();
		postUrl = prop.getProperty("postURL");
	}
	
	
	@Test
	public void postAPITest() throws JsonGenerationException, IOException{
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/json");
		
		//Jackson API
		ObjectMapper mapper = new ObjectMapper();
		Movies movie = new Movies("HarryPotter", "Best movie ever made!!");
		
		//Object to JSON file
		mapper.writeValue(new File("/Users/nikhilsethi/Documents/workspace/restAPI/src/main/java/com/splunk/data/MoviesData.json"), movie);
		
		//java object to JSON in String:
		String movieJsonString = mapper.writeValueAsString(movie);
		System.out.println(movieJsonString);
		
		closebaleHttpResponse = restClient.postMovies(postUrl, movieJsonString, headerMap); //call the API
		
		//validate response from API:
		//status code
		int statusCode = closebaleHttpResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(statusCode, testBase.RESPONSE_STATUS_CODE_201, "Status code is not 201");
		
		//JsonString
		String responseString = EntityUtils.toString(closebaleHttpResponse.getEntity(), "UTF-8");
		
		JSONObject responseJson = new JSONObject(responseString);
		System.out.println("The response from API is:"+ responseJson);
		
		//JSON to java object:
		Movies moviesResObj = mapper.readValue(responseString, Movies.class);
		System.out.println(moviesResObj);
		
		Assert.assertTrue(movie.getName().equals(moviesResObj.getName()));
		Assert.assertTrue(movie.getDescription().equals(moviesResObj.getDescription()));
		
		System.out.println(moviesResObj.getId());
		System.out.println(moviesResObj.getTitle());
 }
}