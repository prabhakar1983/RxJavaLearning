package com.examples.helloworld.session;

import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

import com.example.helloworld.resources.Helper;

public class MultipleObservors4 {

		@Test
		public void multiple_Observers() throws InterruptedException{
			OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
				@Override
				public void call(Subscriber<? super String> subscriber) {
					while (!subscriber.isUnsubscribed()) {
						subscriber.onNext("001005003");
					}
				}
			};
			
			Observable<String> observable = Observable.create(onSubscribe);
			ConnectableObservable<String> connectableObservable = observable.publish();
			
			connectableObservable.subscribe(Helper.printToFileAction);
			connectableObservable.subscribe(Helper.printToConsoleAction);
			
			connectableObservable.connect();
		}
}
