package com.splunk.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.splunk.base.TestBase;
import com.splunk.client.RestClient;

public class GetAPITest extends TestBase{
	TestBase testBase;
	String getUrl;
	RestClient restClient;
	CloseableHttpResponse closeableHttpResponse;
	
	//Constuctor
	public GetAPITest() throws FileNotFoundException {
		super();
	}
	
	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException{
		testBase = new TestBase();
		getUrl = testSuiteProperties.getProperty("getURL");
	}
	
	//Gets the response from the movie api
	@Test
	public void getAPITest() throws ClientProtocolException, IOException{
		restClient = new RestClient();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(getUrl); //http get request
		httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		CloseableHttpResponse response = httpClient.execute(httpGet); //hit the get url 
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode);
		
		Assert.assertEquals(statusCode, RESPONSE_STATUS_CODE_200, "Status code is not 200");
		
		String contentString = EntityUtils.toString(response.getEntity(), "UTF-8");
		
		JSONObject responseJson = new JSONObject(contentString);
		System.out.println(responseJson + ",\n");
	}
}
