package com.examples.helloworld.session;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;

import com.example.helloworld.resources.Helper;
import com.example.helloworld.resources.OSB;

public class LessCodeExample2 {
	
	@Test
	public void ObservableWay_LessBoilerPlateCode() {
		// SEARCH_COMPANY --> PULL_COMPANY_INFO -->  Log to Database
		Helper.persist("START  --> Observable Way");
		
		List<String> dunsList = Arrays.asList("001005003", "200100500");
		
		Observable<String> dunsObservable = Observable.from(dunsList);
		
		dunsObservable
					  .map((String duns) -> OSB.getBasicCompanyInfo(duns))
    				  .subscribe((String pcmData) -> {
										Helper.persist(pcmData);
									},
									(Throwable e) -> {
										// 	Handle Error Here
										Helper.persist("An error occured");
									},
									() -> {
										// Logging, Email
										Helper.persist("EMAIL or Take an appropriate action for Success Scenario"); 
									});
	}


}
