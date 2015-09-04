package com.example.helloworld.resources;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import com.amazonaws.services.sqs.model.Message;

public class BasicObservable {
	
	//@Test
	public void traditionalWay() {
		//  PULL_COMPANY_INFO -->  Log to Database
		Helper.persist("START  --> Traditional Way");
		
		List<String> dunsList = Arrays.asList("001005003", "200100500");
		try {
			for (String duns : dunsList) {
				String companyInfo = OSB.getBasicCompanyInfo(duns); 	// 2 secs each
				
				Helper.persist(companyInfo); 								// 10 milli secs for a database call
			}
		} catch (Throwable t) {
			Helper.persist("An error occured");
			// TODO: Error Handling Code goes here.
		}

		Helper.persist("EMAIL or Take an appropriate action for Success Scenario"); 
	}
	
	
	//@Test
	public void functional_Interface(){
		Func1<String, String> getBasicCompanyInfo = new Func1<String, String>() {
			public String call(String duns) {
				return OSB.getBasicCompanyInfo(duns);
			}
		};
		
		String response = getBasicCompanyInfo.call("001005003");
		
		System.out.println(response);
	}

	
	
	//@Test
	public void ObservableWay_Event_Handling() {
		// SEARCH_COMPANY --> PULL_COMPANY_INFO -->  Log to Database
		Helper.persist("START  --> Observable Way");
		
		Observable<String> observable = Observable.create((Subscriber<? super String> subscriber) -> {
				while (!subscriber.isUnsubscribed()) {
					Message message = Helper.readMessageFromQueue();
					if(message != null) {
						subscriber.onNext(message.getBody());
						
						Helper.deleteMessageFromQueue(message.getReceiptHandle());
						
						if(message.getBody().equals("Stop")){
							subscriber.onCompleted();
							subscriber.unsubscribe();
						}
					}
				}
		});

		
		observable.map((String duns) -> {
							return OSB.getBasicCompanyInfo(duns);
						})
					.subscribe(
							(String pcmData) -> {
								Helper.persist(pcmData);
							},
							(Throwable e) -> {
								// 	Handle Error Here
								Helper.persist("An error occured");
							},
							() -> {
								// Logging, Email
								Helper.persist("EMAIL Success"); 
							});
	}
	
}