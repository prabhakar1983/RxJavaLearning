package com.examples.helloworld.session;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

import com.amazonaws.services.sqs.model.Message;
import com.example.helloworld.resources.Helper;

public class Observable1Way {

	@Test
	public void testObservables(){
		// TODO: create a Observable 
		Observable<String> initalObservable = Observable.create(
				
				new OnSubscribe<String>() {
					public void call(Subscriber<? super String> subsriber) {
						while(!subsriber.isUnsubscribed()){
							Message message = Helper.readMessageFromQueue();
							if(message !=  null) {
								String payload = message.getBody();
								subsriber.onNext(payload);
								
								Helper.deleteMessageFromQueue(message.getReceiptHandle());
								
								if(payload.equals("Stop the Thread")) {
									subsriber.unsubscribe();
								}
							}
						}
						
						
					}
		});
		
		// TODO: Create Observer funcitons - OnNext, OnError, OnCompleted
		Action1<String> onNextFunction = (String response) -> 	{
			Helper.persist(response);
		};
		
		Action1<Throwable> onErrorFunction = new Action1<Throwable>(){
			@Override
			public void call(Throwable e) {
				System.out.println(e + " ERRROR");
			}
		};
		
		Action0 onCompleted = new Action0(){
			@Override
			public void call() {
				Helper.persist("ALL WENT WELL");
			}
		};
		
		
		// TOD: Hook the Observblae to the Observor
		Observable<String> transofomedObservable = initalObservable.map(GetCompanyDetailFunc.GETCOMPANY_FUNC);
		transofomedObservable.subscribe(onNextFunction, onErrorFunction, onCompleted);
	}
}
