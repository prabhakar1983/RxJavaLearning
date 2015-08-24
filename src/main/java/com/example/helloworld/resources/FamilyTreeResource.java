package com.example.helloworld.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.JerseyClientBuilder;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.annotation.Timed;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
public class FamilyTreeResource {
    private static final Client httpClient = new JerseyClientBuilder().build();
    private static final String maxcvUserName = "cirrustest@dnb.com";
    private static final String maxcvPassword = "password";
    private Response returnedResponse;
    
    private static final String AUTHORIZATION = "Authorization";
    
    // http://localhost:8080/services/call-Serial?duns2=001005003&duns1=200443976
    @GET
    @Path("/call-Serial")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response pullOSBProductInSequeuence(@QueryParam("duns1") String duns1, @QueryParam("duns2") String duns2) {
        	
			Func1<String, Response> osbCallFunc = duns -> getFamilyTree(duns);
			
			Func2<Response, Response, Response> mergeFunc = new Func2<Response, Response,Response>(){
				@Override
				public Response call(Response linkageProductResponse1, Response linkageProductResponse2) {
					System.out.println("Current Thread Name:" + Thread.currentThread().getName());
					
					if(isResponseStatusOK(linkageProductResponse1) && isResponseStatusOK(linkageProductResponse2)) {
			    		return Response.status(Status.CREATED)
			    					.entity(linkageProductResponse1.getEntity())
			    					.entity(linkageProductResponse2.getEntity())
			    					.build();
			    	} else if(isResponseStatusOK(linkageProductResponse1)) {
			    		return buildResponse(linkageProductResponse1);
			    	} else if(isResponseStatusOK(linkageProductResponse2)) {
			    		return buildResponse(linkageProductResponse2);
			    	} else {
			    		return Response.status(Status.SERVICE_UNAVAILABLE).entity("Not Available").build();
			    	}
				}

				private Response buildResponse(Response linkageProductResponse1) {
					return Response.status(Status.CREATED).entity(linkageProductResponse1.getEntity()).build();
				}
				private boolean isResponseStatusOK(Response linkageProductResponse1) {
					return linkageProductResponse1.getStatus() == Status.OK.getStatusCode();
				}
				
			};
			
			Observable.just(duns1, duns2)
						.map(osbCallFunc)
			        	.reduce(mergeFunc)
			        	.subscribeOn(Schedulers.io())
			        	.subscribe(response -> returnedResponse = response);
			
			return returnedResponse;
    }

    // http://localhost:8080/services/familyTree?duns=001005003
    @GET
    @Path("/familyTree")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public static Response getFamilyTree(@QueryParam("duns") String duns) {
    	String maxcvAuthToken = getMaxcvAuthToken();
       	//System.out.println("Auth Token : " + maxcvAuthToken);
			
		return httpClient.target("https://maxcvservices-stg.dnb.com/V3.1/organizations/" + duns+ "/products/LNK_UPF").request()
											.header(AUTHORIZATION, maxcvAuthToken)
											.build(HttpMethod.GET)
											.invoke();
    }
    
	private static String getMaxcvAuthToken() {
		Response response = httpClient.target("https://maxcvservices-stg.dnb.com/rest/Authentication").request()
					        		  .header("x-dnb-user", maxcvUserName)
					        		  .header("x-dnb-pwd", maxcvPassword)
					        		  .build(HttpMethod.POST)
					        		  .invoke();
		 if(response.getStatus() == Status.NO_CONTENT.getStatusCode()) 
	        	return response.getHeaderString(AUTHORIZATION);
		 else 	
			 	return null;
	    
	}

}