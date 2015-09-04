import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.example.helloworld.resources.FamilyTreeResource;


public class Functions {
	
	private static List<String> dunsList = Arrays.asList("001005003", "200443976");
	
	Func1<String, Response> getFamilyTreeFunction = new Func1<String, Response>(){
		public Response call(String duns) {
	    	return FamilyTreeResource.getFamilyTree(duns);
		}
	};
	
	private Action1<Object> printAction = (Object response) -> {
		System.out.println("--->: " + response);
	};
	
	Action1<Object> printFileAction = new Action1<Object>(){
		@Override
		public void call(Object response) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("/home/thatchinamoorthyp/Desktop/RxJava/Output", true));
				writer.append("\n");
				writer.append("Output: " + response);
				writer.append("   :   " + new Date().toString());
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
	
	Action1<Throwable> errorHandlingFunc = new Action1<Throwable>(){
		@Override
		public void call(Throwable throwable) {
			System.out.println(throwable);
		}
	};
	Action0 onCompleteFunc= new Action0(){
		@Override
		public void call() {
			printFileAction.call(new Date());
		}
	};
	
	private Func1<? super Response, Boolean> filterGoodResults = new Func1<Response, Boolean>() {
		public Boolean call(Response response) {
			if(response.getStatus() == 200)
				return true;
			else 
				return false;
		}
	};

	private Func2<Response, Response, Response> joinAllResponses = new Func2<Response, Response, Response>() {
		public Response call(Response response1, Response response2) {
			return Response.status(201).entity(response1).entity(response2).build();
		}
	};
	
	
	/**
	 * Transform an emited item from one form to another
	 */
	//@Test
	public void map_Function(){
		Observable<String> dunsObservable = Observable.from(dunsList);

		dunsObservable
					.map(getFamilyTreeFunction)
					.subscribe(printAction);
	}
	
	/**
	 * AWESOMEness STARTs here.
	 */
	@Test
	public void flatMap_Function(){
		Observable<String> dunsObservable = Observable.just("IBM");
		printFileAction.call(new Date());
		dunsObservable
					.subscribeOn(Schedulers.computation())
					.flatMap((String search) -> {
						/* SEARCH API CALL */ sleepInMilliseconds(2000l);
						
						List<String> companies = new ArrayList<String>();
						companies.add("DUNS-1");
						companies.add("DUNS-2");
						companies.add("DUNS-3");
						return Observable.from(companies);
					}).map((String duns) -> {
						/* PCM  API CALL */ sleepInMilliseconds(2000l);
						return "IBM - " + duns;
					})
					.subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
	}

	/**
	 * Prints out time elapsed since last emission. This would give us some statistics on time.
	 */
	//@Test
	public void timestamp_Function() {
		Observable<String> dunsObservable = Observable.from(dunsList);

		dunsObservable
					.map(getFamilyTreeFunction)
					.timeInterval()
					.subscribe(printAction);
	}
	
	/**
	 * Filters out the Response that has Response STATUS=200
	 */
	//@Test
	public void filter_Function() {
		Observable<String> dunsObservable = Observable.from(Arrays.asList("0015003", "200443976"));

		dunsObservable
					.observeOn(Schedulers.io())
					.map(getFamilyTreeFunction)
					.filter(filterGoodResults)
					.subscribe(printFileAction);
	}
	
	/**
	 * Filters out the Response that has Response STATUS=200
	 */
	//@Test
	public void reduce_Function() {
		Observable<String> dunsObservable = Observable.from(Arrays.asList("001005003", "200443976"));

		dunsObservable
					.subscribeOn(Schedulers.io())
					.map(getFamilyTreeFunction)
					.filter(filterGoodResults)
					.reduce(joinAllResponses)
					.timeInterval()
					.subscribe(printFileAction);
	}
	
	private void sleepInMilliseconds(Long time) {
		try {
			Thread.currentThread().sleep(time);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}