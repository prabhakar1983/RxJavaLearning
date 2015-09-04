import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;


public class CreateObservables {
	
	private List<String> dunsList = Arrays.asList("001005003", "200443976");
	
	
	/**
	 *  Create Observable from Classes that implement Iterable. i.e All Collections etc., 
	 *  
	 *  It Emits all the Items ONE by ONE to the subscriber
	 */
	//@Test
	public void Observable_from(){
		Observable<String> dunsObservable = Observable.from(dunsList);

		dunsObservable.subscribe(printAction);
	}
	
	
	/**
	 * Create Observable from One Entity
	 * 
	 * Emits its One Item
	 */
	//@Test
	public void Observable_just(){
		Observable<String> dunsObservable = Observable.just("001005003");

		dunsObservable.subscribe(printAction);
	}
	
	/**
	 * Create Observable from an external Entity -> Classic example for listening a Event Stream
	 * @throws InterruptedException 
	 * 
	 */
	//@Test
	public void Observable_create() throws InterruptedException{
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (true) {
					subscriber.onNext(readInputFromTerminal(subscriber));
				}
			}
		};
		
		Observable.create(onSubscribe).subscribe(printAction);
	}
	
	/**
	 * Create Observable from an external Entity -> Classic example for listening a Event Stream
	 * @throws InterruptedException 
	 * 
	 */
	//@Test
	public void Observable_create_unSubscribes() throws InterruptedException{
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (!subscriber.isUnsubscribed()) {
					subscriber.onNext(readInputFromTerminal(subscriber));
				}
			}
		};
		
		Subscription subscription = Observable
										.create(onSubscribe)
										.subscribe(printAction);
		
		Thread.sleep(10000);  // could be replaced with a Condition.
		subscription.unsubscribe();
	}
	
	//@Test
	public void multiple_Observers() throws InterruptedException{
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (!subscriber.isUnsubscribed()) {
					subscriber.onNext(readInputFromTerminal(subscriber));
				}
			}
		};
		
		Observable<String> observable = Observable.create(onSubscribe);
		ConnectableObservable<String> connectableObservable = observable.publish();
		
		connectableObservable.subscribe(printFileAction);
		connectableObservable.subscribe(printAction);
		
		connectableObservable.connect();
	}
	
	private static String readInputFromTerminal(Subscriber<? super String> subscriber) {
		System.out.println("Enter Text:");
		return new Scanner(System.in).next();
	}
	
	private Action1<String> printAction = (String duns) -> {
		System.out.println("Duns: " + duns);
	};
	
	Action1<Object> printFileAction = new Action1<Object>(){
		@Override
		public void call(Object response) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("/home/thatchinamoorthyp/Desktop/RxJava/Output", true));
				writer.append("\n");
				writer.append("Output: " + response);
				
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	

	
}