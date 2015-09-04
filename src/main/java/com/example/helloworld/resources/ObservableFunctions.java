package com.example.helloworld.resources;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.amazonaws.services.sqs.model.Message;


public class ObservableFunctions {
	
	private static List<String> dunsList = Arrays.asList("001005003", "200443976");
	
	Func1<String, Response> getFamilyTreeFunction = new Func1<String, Response>(){
		public Response call(String duns) {
			printFileAction.call("Inside Family Tree MAP Function");
			return OSB.getFamilyTree(duns);
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
				writer.append("---------------\n");

				writer.append("## Output ## " 			+ response + "\n");
				writer.append("Time : " 		+ LocalDateTime.now().getHour() + "-" + LocalDateTime.now().getMinute() + "-" + LocalDateTime.now().getSecond() + "     ;     ");
				writer.append("Thread Name : " 	+ Thread.currentThread().getName() + "\n");
				
				writer.append("---------------\n\n");

			} catch (Throwable e) {
				e.printStackTrace();
			} finally{
				try {
					writer.close();
				} catch (IOException e) {
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
			printFileAction.call("----COMPLETED----");
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
		
		printFileAction.call(new Date());
		
		dunsObservable
					//.observeOn(Schedulers.newThread())
					.map(getFamilyTreeFunction)
					//.observeOn(Schedulers.computation())
					.map((Response res) -> {
						sleepInMilliseconds(2000l);
						printFileAction.call("Inside 2nd Map Function");
						return res;
					})
					//.observeOn(Schedulers.newThread())
					.map((Response res) -> {
						sleepInMilliseconds(1000l);
						printFileAction.call("Inside 3nd Map Function");
						return res;
					})
					//.observeOn(Schedulers.newThread())
					.subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
	}
	
	/**
	 * 
	 */
	public void flatMap_Function(){
		Observable<String> dunsObservable = Observable.just("IBM", "Apple", "Google", "Microsoft");
		
		printFileAction.call("START");
		
		dunsObservable
					//.observeOn(Schedulers.newThread())
					.flatMap((String search) -> {
						//OSB.getFamilyTree("200443976");
						sleepInMilliseconds(2000l);
						printFileAction.call("Inside flatMap:" + search);
						Observable<String> companiesObservable = Observable.from(Arrays.asList(search + "-DUNS-1", search + "-DUNS-2"));
						return companiesObservable;
					})
					//.observeOn(Schedulers.newThread())
					.map((String duns) -> {
						//Observable.just("001005003").subscribeOn(Schedulers.newThread()).map(getFamilyTreeFunction).subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
						sleepInMilliseconds(2000l);
						printFileAction.call("Inside Map:" + duns);
						return "Final Response " + duns;
					})
					//.observeOn(Schedulers.newThread())
					.subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
	}
	
	/**
	 * 
	 */
	public void eventHandlingFunction(){
		OnSubscribe<String> onSubscribe = new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				while (true) {
					Message message = Helper.readMessageFromQueue();
					if(message != null) {
						subscriber.onNext(message.getBody());
						
						Helper.deleteMessageFromQueue(message.getReceiptHandle());
					}
				}
			}
		};
		
		printFileAction.call("START");
		Observable.create(onSubscribe)
					.observeOn(Schedulers.newThread())
					.flatMap((String search) -> {
						//OSB.getFamilyTree("200443976");
						sleepInMilliseconds(2000l);
						printFileAction.call("Inside flatMap:" + search);
						Observable<String> companiesObservable = Observable.from(Arrays.asList(search + "-DUNS-1", search + "-DUNS-2"));
						return companiesObservable;
					})
					.observeOn(Schedulers.newThread())
					.map((String duns) -> {
						//Observable.just("001005003").subscribeOn(Schedulers.newThread()).map(getFamilyTreeFunction).subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
						sleepInMilliseconds(2000l);
						printFileAction.call("Inside Map:" + duns);
						return "Final Response " + duns;
						
					})
					.observeOn(Schedulers.newThread())
					.subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
	}

	public void take_Function(){
		Observable<String> dunsObservable = Observable.just("IBM", "Google");
		
		printFileAction.call("START");
		
		Observable<String> dunsList = dunsObservable
					.flatMap((String search) -> {
						//OSB.getFamilyTree("200443976");
						sleepInMilliseconds(1000l);
						printFileAction.call("Inside flatMap" + search);
						return Observable.from(Arrays.asList("DUNS-1", "DUNS-2", "DUNS-3"));
					});
		
		dunsList.take(2)
					.observeOn(Schedulers.newThread())
					.map((String duns) -> {
						/*if(duns == "DUNS-1" || duns == "DUNS-3" ) 
							OSB.getFamilyTree("0001005003");
						else 
							OSB.getFamilyTree("200443976");*/
						sleepInMilliseconds(2000l);
						return "IBM - " + duns;
					}).subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
					
		
		dunsList.takeLast(1)
				.observeOn(Schedulers.newThread())
				.map((String duns) -> {
					/*if(duns == "DUNS-1" || duns == "DUNS-3" ) 
						OSB.getFamilyTree("0001005003");
					else 
						OSB.getFamilyTree("200443976");*/
					sleepInMilliseconds(2000l);
					return "IBM - " + duns;
				}).subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
		
	}

	/**
	 *  Search and Match - Aggregation can be done here.
	 */
	//@Test
	public void zip() {
		Observable<String> duns1 = Observable.just("001005003");
		Observable<String> duns2 = Observable.just("200443976");

		Observable<Response> observable1 = duns1.map(getFamilyTreeFunction).subscribeOn(Schedulers.computation());
		Observable<Response> observable2 = duns2.map(getFamilyTreeFunction).subscribeOn(Schedulers.computation());
		Func2<Response, Response, String> zipFunc = new Func2<Response, Response, String>() {
			public String call(Response t1, Response t2) {
				return t1.readEntity(String.class) + t2.readEntity(String.class);
			}
		};
		
		Observable.zip(observable1, observable2, zipFunc)
						.timeInterval()
						.subscribe(printFileAction, errorHandlingFunc, onCompleteFunc);
	}
	
	/**
	 * Prints out time elapsed since last emission. This would give us some statistics on time.
	 */
	//@Test
	public void timestamp_Function() {
		Observable<String> dunsObservable = Observable.from(Arrays.asList("001005003", "200443976"));

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