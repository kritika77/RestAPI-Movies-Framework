package com.splunk.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestBase {
	
	public int RESPONSE_STATUS_CODE_200 = 200;
	public int RESPONSE_STATUS_CODE_500 = 500;
	public int RESPONSE_STATUS_CODE_400 = 400;
	public int RESPONSE_STATUS_CODE_401 = 401;
	public int RESPONSE_STATUS_CODE_201 = 201;
    
	public Properties testSuiteProperties;
	public final static String DATA_PATH = "./src/test/resources/properties";
      
    public TestBase() throws FileNotFoundException{
    	    testSuiteProperties = new Properties();

			FileInputStream fFileInputStream = new FileInputStream(DATA_PATH + "//config.properties");
			try {
				testSuiteProperties.load(fFileInputStream);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    }
}
