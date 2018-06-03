package com.splunk.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class RestClient {
   
	/*Get Movies method
	public static void getMovies(String url) throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url); //http get request
		httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		CloseableHttpResponse response = httpClient.execute(httpGet); //hit the get url 
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode);
		String contentString = EntityUtils.toString(response.getEntity(), "UTF-8");
		JSONObject responseJson = new JSONObject(contentString);
		System.out.println(responseJson + ",\n");
		
	}
	
	public static void postMovies(String url) throws ClientProtocolException, IOException{
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(url);
	    String json = "{'name':'Black Panther','description':'Nice Movie'}";
	    StringEntity entity = new StringEntity(json);
	    httpPost.setEntity(entity);
	    httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
	    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
	    CloseableHttpResponse response = client.execute(httpPost);
	    int statusCode = response.getStatusLine().getStatusCode();
	    if(statusCode == 200){
	    	System.out.println("Movie posted successfully!!");
	    }
	}*/
	
	//POST Method
	public CloseableHttpResponse postMovies(String url, String entityString, HashMap<String, String> headerMap) throws ClientProtocolException, IOException{
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new StringEntity(entityString));
			
			//for headers
			for(Map.Entry<String,String> entry : headerMap.entrySet()){
				httppost.addHeader(entry.getKey(), entry.getValue());
			}
			
			CloseableHttpResponse closebaleHttpResponse = httpClient.execute(httppost);
			return closebaleHttpResponse;
	}
}
