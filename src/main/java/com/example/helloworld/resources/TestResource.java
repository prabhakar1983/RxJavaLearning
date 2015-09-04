package com.example.helloworld.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {
	
	@Path("/map_Function")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response map_Function() {
    	ObservableFunctions functions = new ObservableFunctions();
    	functions.map_Function();
    	
    	return Response.ok().entity("--------DONE-----").build();
    }
   
	@Path("/flatMap_Function")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response flatMap_Function() {
    	ObservableFunctions functions = new ObservableFunctions();
    	functions.flatMap_Function();
    	
    	return Response.ok().entity("--------DONE-----").build();
    }
   
	@Path("/eventHandlingFunction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response eventHandlingFunction() {
    	ObservableFunctions functions = new ObservableFunctions();
    	functions.eventHandlingFunction();
    	
    	return Response.ok().entity("--------DONE-----").build();
    }
    
	@Path("/zip")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response zip() {
    	ObservableFunctions functions = new ObservableFunctions();
    	functions.zip();
    	
    	return Response.ok().entity("--------DONE-----").build();
    }
	
	@Path("/take_Function")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response take_Function() {
    	ObservableFunctions functions = new ObservableFunctions();
    	functions.take_Function();
    	
    	return Response.ok().entity("--------DONE-----").build();
    }
    
   
}