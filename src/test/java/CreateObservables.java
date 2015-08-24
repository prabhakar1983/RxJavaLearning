import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.ws.rs.core.Response;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Observable.OnSubscribe;
import rx.Subscription;
import rx.functions.Action1;

import com.example.helloworld.resources.FamilyTreeResource;


public class CreateObservables {
	
	private List<String> dunsList = Arrays.asList("001005003", "200443976");
	
	private Action1<String> action = (String duns) -> {
		System.out.println("Duns: " + duns);
	};
	
	Consumer<Object> printFunction = (Object duns) -> {
		PrintWriter out = null;
		try {
			out = new PrintWriter("/home/thatchinamoorthyp/Desktop/RxJava/Output");
			out.append("" + duns);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			out.close();
		}
	};
	
	private Action1<Integer> print = (Integer duns) -> {
		System.out.println("Duns: " + duns);
		printFunction.accept(duns);
	};
	
	/**
	 *  Create Observable from Classes that implement Iterable. i.e All Collections etc., 
	 *  
	 *  It Emits all the Items ONE by ONE to the subscriber
	 */
	//@Test
	public void Observable_from(){
		Observable<String> dunsObservable = Observable.from(dunsList);

		dunsObservable.subscribe(action);
	}
	
	
	/**
	 * Create Observable from One Entity
	 * 
	 * Emits its One Item
	 */
	//@Test
	public void Observable_just(){
		Observable<String> dunsObservable = Observable.just("001005003");

		dunsObservable.subscribe(action);
	}
	
	/**
	 * Create Observable from an external Entity -> Classic example for listening a Event Stream
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void Observable_create() throws InterruptedException{
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (true) {
					subscriber.onNext(readInputFromTerminal(subscriber));
				}
			}
		};
		
		Observable.create(onSubscribe).subscribe(action);
	}
	
	/**
	 * Create Observable from an external Entity -> Classic example for listening a Event Stream
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void Observable_create_unSubscribes() throws InterruptedException{
		System.out.println("==> Observable.create");
		
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (subscriber.isUnsubscribed()) {
					subscriber.onNext(readInputFromTerminal(subscriber));
				}
			}
		};
		
		Subscription subscribtion = Observable.create(onSubscribe).subscribe(action);
		
		Thread.sleep(10000);
		subscribtion.unsubscribe();
	}
	
	
	
	
	private static String readInputFromTerminal(Subscriber<? super String> subscriber) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Nothing";
	}

	
	
}

