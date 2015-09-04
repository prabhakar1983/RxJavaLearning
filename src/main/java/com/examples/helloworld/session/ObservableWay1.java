package com.examples.helloworld.session;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import com.example.helloworld.resources.Helper;
import com.example.helloworld.resources.OSB;

public class ObservableWay1 {

	@Test
	public void ObservableWay() {
		// SEARCH_COMPANY --> PULL_COMPANY_INFO -->  Log to Database
		Helper.persist("START  --> Observable Way");
		
		List<String> dunsList = Arrays.asList("001005003", "200100500");
		
		Observable<String> dunsObservable = Observable.from(dunsList);
		
		Func1<String, String> getBasicCompanyInfo = new Func1<String, String>() {
			public String call(String duns) {
				return OSB.getBasicCompanyInfo(duns);
			}
		};
		Observable<String> observable = dunsObservable.map(getBasicCompanyInfo);
		
		Subscriber<String> subscriber = new Subscriber<String>() {
															public void onNext(String pcmData) {
																Helper.persist(pcmData); 			// 10 milli secs for a database call
															}
															public void onError(Throwable e) {
																// 	Handle Error Here
																Helper.persist("An error occured");
															}
															public void onCompleted() {
																// Logging, Email
																Helper.persist("EMAIL or Take an appropriate action for Success Scenario"); 
															}
														};
		observable.subscribe(subscriber);
	}

}
