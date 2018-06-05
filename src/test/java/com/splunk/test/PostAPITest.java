package com.splunk.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.splunk.base.TestBase;
import com.splunk.client.RestClient;

public class PostAPITest extends TestBase {
	TestBase testBase;
	String postUrl;
	RestClient restClient;
	CloseableHttpResponse closebaleHttpResponse;
	public final static String DATA_PATH = "./src/test/resources/data";
	
	//Constuctor
	public PostAPITest() throws FileNotFoundException {
		super();
	}
	
	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException{
		testBase = new TestBase();
		postUrl = testSuiteProperties.getProperty("postURL");
	}
	
	// Post name and description parameters to the movie api
	@Test
	public void postAPITest() throws JsonGenerationException, IOException{
		CloseableHttpClient client = HttpClients.createDefault();
		
	    HttpPost httpPost = new HttpPost(postUrl);
	    
	    String json = "{'name':'Black Panther','description':'Best Movie Made'}";
	    
	    StringEntity entity = new StringEntity(json);
	    httpPost.setEntity(entity);
	    
	    httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
	    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
	    
	    CloseableHttpResponse response = client.execute(httpPost);
	    
	    int statusCode = response.getStatusLine().getStatusCode();
	    
	    if(statusCode == 200){
	    	System.out.println("Success...Movie posted to catalog");
	    }
	    
	    Assert.assertEquals(statusCode, testBase.RESPONSE_STATUS_CODE_200, "Status code is not 200");

		BufferedReader rd = new BufferedReader(
		        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
	}
}