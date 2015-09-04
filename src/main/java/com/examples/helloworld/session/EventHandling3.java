package com.examples.helloworld.session;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;

import com.amazonaws.services.sqs.model.Message;
import com.example.helloworld.resources.Helper;
import com.example.helloworld.resources.OSB;

public class EventHandling3 {

		@Test
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
