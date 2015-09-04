package com.example.helloworld.resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.JerseyClientBuilder;

import rx.Subscriber;
import rx.functions.Action1;

public class OSB {
	
	private static final Client httpClient = new JerseyClientBuilder().build();
    private static final String maxcvUserName = "cirrustest@dnb.com";
    private static final String maxcvPassword = "password";
	
    public static Response getFamilyTree(@QueryParam("duns") String duns) {
    	String maxcvAuthToken = getMaxcvAuthToken();
			
		return httpClient.target("https://maxcvservices-stg.dnb.com/V3.1/organizations/" + duns+ "/products/LNK_UPF").request()
											.header("Authorization", maxcvAuthToken)
											.build(HttpMethod.GET)
											.invoke();
    }
    
	public static List<String> searchCompany(String searchTerm) {
		Helper.sleep(2000l);
		
		return Arrays.asList(searchTerm + "-DUNS-1",
							searchTerm + "-DUNS-2",
							searchTerm + "-DUNS-3");
	}

	public static String getBasicCompanyInfo(String duns) {
		Helper.sleep(2000l);
		
		return "{\'Response\' : \'PCM_Response-" + duns + "\'}";
	}
	
	public static String getFamilyTreeData(String duns) {
		Helper.sleep(2000l);
		
		return "FamilyTree_Response-" + duns;
	}
	
	private static String getMaxcvAuthToken() {
		Response response = httpClient.target("https://maxcvservices-stg.dnb.com/rest/Authentication").request()
					        		  .header("x-dnb-user", maxcvUserName)
					        		  .header("x-dnb-pwd", maxcvPassword)
					        		  .build(HttpMethod.POST)
					        		  .invoke();
		 if(response.getStatus() == Status.NO_CONTENT.getStatusCode()) 
	        	return response.getHeaderString("Authorization");
		 else 	
			 	return null;
	}

}
