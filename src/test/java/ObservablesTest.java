import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.resources.FamilyTreeResource;


public class ObservablesTest {
	
	@Test
	@Timed
	public void test_Scenarios(){
		List<String> fruits = new ArrayList<String>();
		fruits.add("Applie");
		
		OnSubscribe<String> source = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				try{
						// it could be a Message Reader where the duns numbers (Events) may come async.
						/*subscriber.onNext("001005003");
						subscriber.onNext("200443976");*/
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
						// it could be a Message Reader where the duns numbers (Events) may come async.
						while (true) {
							subscriber.onNext(bufferedReader.readLine());
						}
				} catch (Throwable t) {
					subscriber.onError(t);
				}
				subscriber.onCompleted();
			}
		};
		
		Action1<Throwable> errorHandlingFunc = new Action1<Throwable>(){
			@Override
			public void call(Throwable throwable) {
				System.out.println(throwable);
			}
		};
		Action0 onCompleteFunc= new Action0(){
			@Override
			public void call() {
				System.out.println("We have finished---");
			}
		};
		
		Func1<String, Response> getFamilyTreeFunction = new Func1<String, Response>(){
			@Override
			public Response call(String duns) {
		    	return FamilyTreeResource.getFamilyTree(duns);
			}
		};
		
		Action1<Response> action = new Action1<Response>(){
			@Override
			public void call(Response response) {
				PrintWriter out = null;
				try {
					out = new PrintWriter("/home/thatchinamoorthyp/Desktop/RxJava/Output");
					out.append("response +++" + response);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					out.close();
				}
			}
		};
		
		Observable.create(source)
					.map(getFamilyTreeFunction)
					.subscribeOn(Schedulers.io())
					.subscribe(action, errorHandlingFunc, onCompleteFunc);
					//.subscribe(response -> System.out.println("response: " + response));
		// TODO:
		// To only way to prove Event handling is - Read messages from Queue and append data and push it back.
		
	}
	
	

}
