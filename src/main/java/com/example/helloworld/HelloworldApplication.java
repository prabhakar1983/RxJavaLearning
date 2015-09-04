package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.example.helloworld.resources.TestResource;


public class HelloworldApplication extends Application<HelloworldConfiguration> {
	
    public static void main(String[] args) throws Exception {
        HelloworldApplication helloworldApplication = new HelloworldApplication();
        helloworldApplication.run(args);
    }

    @Override
    public String getName() {
        return "hello-world applicaiton";
    }

    @Override
    public void initialize(Bootstrap<HelloworldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloworldConfiguration configuration, Environment environment) {
    	//environment.jersey().register(new FamilyTreeResource());
    	environment.jersey().register(new TestResource());
    }

}