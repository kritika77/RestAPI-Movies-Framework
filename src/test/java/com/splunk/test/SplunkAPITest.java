package com.splunk.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.splunk.base.TestBase;
import com.splunk.client.RestClient;

public class SplunkAPITest extends TestBase{

	TestBase testBase;
	boolean flag;
	RestClient restClient;
	CloseableHttpResponse closeableHttpResponse;
	String getUrl = testSuiteProperties.getProperty("getURL");
	
	//Constructor
	public SplunkAPITest() throws FileNotFoundException {
		super();
	}


	//SPL-001: No two movies should have the same image
	@Test(priority=1)
	public void isSameMovieImage(){
		flag = true;
		try {
			JSONObject responseImage = getAPIResponse();
			HashMap<String, Integer> hmap = new HashMap<String, Integer>();
			JSONArray array = responseImage.getJSONArray("results");
			for(int i = 0 ; i < array.length() ; i++){
			 if(!array.getJSONObject(i).isNull("poster_path")){
				 if(!hmap.containsKey(array.getJSONObject(i).getString("poster_path"))){
					 hmap.put(array.getJSONObject(i).getString("poster_path"), 1);
				 }else{
					 flag = false;
					 Assert.assertEquals(flag, false);
				 }
			  }
			 }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(flag, true);
	}
	
	//SPL-002: All poster_path links must be valid. poster_path link of null is also acceptable
	@Test(priority=2)
	public void isValidLink(){
		HttpURLConnection httpUrlConn;
		flag = true;
        try {
        JSONObject responsePosterPath = getAPIResponse();
		JSONArray array = responsePosterPath.getJSONArray("results");
		for(int i = 0 ; i < array.length() ; i++){
		 if(!array.getJSONObject(i).isNull("poster_path")){
		    String targetUrl = array.getJSONObject(i).getString("poster_path");
		    
            httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();
 
            httpUrlConn.setRequestMethod("HEAD");
 
            // Set timeouts in milliseconds
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);
 
            if (httpUrlConn.getResponseCode() != 200){
            	flag = false; 
            	Assert.assertEquals(flag, false);
            }
          }
		 }
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Assert.assertEquals(flag, false);
        }
        
        Assert.assertEquals(flag, true);
	}
	
	//Gets API Response and returns the JSON response data
	private JSONObject getAPIResponse() throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(getUrl); //http get request
		httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		CloseableHttpResponse response = httpClient.execute(httpGet); //hit the get url 
		//int statusCode = response.getStatusLine().getStatusCode();
		String contentString = EntityUtils.toString(response.getEntity(), "UTF-8");
		JSONObject responseJson = new JSONObject(contentString);
		return responseJson;
	}
		
    //Check if the genre ids are sorted
	private boolean isSorted() throws ClientProtocolException, IOException{
		JSONObject responsePosterPath = getAPIResponse();
		JSONArray array = responsePosterPath.getJSONArray("results");
		int flag=0;
		int prev_id=Integer.MIN_VALUE;
		for(int i = 0 ; i < array.length() ; i++){
			
			 if(!array.getJSONObject(i).isNull("genre_ids") && flag==0){
				 flag=1; 
	          }
			 else if(flag==1 && array.getJSONObject(i).isNull("genre_ids") || array.getJSONObject(i).getJSONArray("genre_ids").length()==0){
				 return false;
			 }
			 else if(prev_id > array.getJSONObject(i).getInt("id")){
				 return false;
			 }
			 prev_id=array.getJSONObject(i).getInt("id");
		 }
        
		return true;
	}
    
	//SPL-003: Sorting requirement. Rule #1 Movies with genre_ids == null should be first in response. 
	//Rule #2, if multiple movies have genre_ids == null, then sort by id (ascending). 
	//For movies that have non-null genre_ids, results should be sorted by id (ascending)
	@Test(priority=3)
    public void sortedGenreIds() throws ClientProtocolException, IOException{
    	boolean value = isSorted();
    	Assert.assertEquals(value, false);
    }
    
	//SPL-004: The number of movies whose sum of "genre_ids" > 400 should be no more than 7. 
	//Another way of saying this is: there at most 7 movies such that their sum of genre_ids is great than 400
	@Test(priority=4)
    public void isAtMostSeven() throws ClientProtocolException, IOException{
		JSONObject responsePosterPath = getAPIResponse();
		flag = true;
		JSONArray array = responsePosterPath.getJSONArray("results");
		int count = 0;
		for(int i = 0 ; i < array.length() ; i++){
		 int sum=0;
		 if(!array.getJSONObject(i).isNull("genre_ids")){
			 JSONArray genreArray = array.getJSONObject(i).getJSONArray("genre_ids");
			 for(int j=0; j < genreArray.length(); j++){
				 sum = sum + genreArray.getInt(j);
			 }
			 if(sum > 400){
				 count++;
			 }
          }
		}
		
		if(count > 7){
			flag = false;
			Assert.assertEquals(flag, false);
		}
		 
		Assert.assertEquals(flag, true);
	}
   
	//SPL-005: There is at least one movie in the database whose title has a palindrome in it. 
	//Example: "title": "Batman: Return of the Kayak Crusaders". The title contains ‘kayak’ which is a palindrome
   @Test(priority=5)
   public void hasPalindrome() throws ClientProtocolException, IOException{
	    JSONObject responsePosterPath = getAPIResponse();
		JSONArray array = responsePosterPath.getJSONArray("results");
		int count = 0;
		for(int i = 0 ; i < array.length() ; i++){
		 if(!array.getJSONObject(i).isNull("title")){
			 String title = array.getJSONObject(i).getString("title");
			 String[] words = title.split(" ");
			 for(String word : words){
			   if(isPalindrome(word)){
				  count++;
			   }
			 }
          }
		}
		
		if(count < 1){
			flag = false;
			Assert.assertEquals(flag, false);
		}
		 
		Assert.assertEquals(flag, true);
	}
   
   //SPL-006: There are at least two movies in the database whose title contain the title of another movie. 
   //Example: movie id: 287757 (Scooby-Doo Meets Dante), movie id: 404463 (Dante). 
   //This example shows one such set. The business requirement is that there are at least two such occurrences.
   @Test(priority=6)
   public void titleContainsTitle() throws ClientProtocolException, IOException{
	    JSONObject responsePosterPath = getAPIResponse();
		JSONArray array = responsePosterPath.getJSONArray("results");
		int count = 0;
		for(int i = 0 ; i < array.length() ; i++){
			for(int j = i+1; j < array.length(); j++){
		       if(!array.getJSONObject(i).isNull("title")){
				 String title1 = array.getJSONObject(i).getString("title");
				 String title2 = array.getJSONObject(j).getString("title");
				 if(title1.toLowerCase().contains(title2.toLowerCase())){
					 count++;
				 }
		       }
            }
		}
		
		if(count < 2){
			flag = false;
			Assert.assertEquals(flag, false);
		}
		 
		Assert.assertEquals(flag, true);
	}
   
   //Check if the title is palindrome and returns boolean value
   private boolean isPalindrome(String title){
	   if(title == null || title.length() == 0) 
           return true;
       
       for(int i=0, j=title.length()-1; i<=title.length()/2; i++, j--){
           if(!Character.isLetterOrDigit(title.charAt(i))){
               j++;
               continue;
           }else if(!Character.isLetterOrDigit(title.charAt(j))){
               i--;
               continue;
           }else if(Character.toLowerCase(title.charAt(i))!= Character.toLowerCase(title.charAt(j))){
               return false;
           }
       }
		 
       return true;
	}
}
