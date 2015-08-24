import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.example.helloworld.resources.FamilyTreeResource;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;


public class Combinations {

private List<String> dunsList = Arrays.asList("001005003", "200443976");
	
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
				writer.append("response +++" + response);
				
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

	
	@Test
	public void zip() {
		Observable<String> duns1 = Observable.just("001005003");
		Observable<String> duns2 = Observable.just("200443976");

		Observable<Response> observable1 = duns1.map(getFamilyTreeFunction);
		Observable<Response> observable2 = duns2.map(getFamilyTreeFunction);
		Func2<Response, Response, String> zipFunc = new Func2<Response, Response, String>() {
			public String call(Response t1, Response t2) {
				return t1.readEntity(String.class) + t2.readEntity(String.class);
			}
		};
		
		Observable.zip(observable1, observable2, zipFunc)
						.timeInterval()
						.subscribe(printFileAction);
	}

	
	@Test
	public void combine_Latest() {
		Observable<String> duns1 = Observable.just("001005003");
		Observable<String> duns2 = Observable.just("200443976");

		Observable<Response> observable1 = duns1.map(getFamilyTreeFunction);
		Observable<Response> observable2 = duns2.map(getFamilyTreeFunction);
		Func2<Response, Response, String> combineLatestFunc = new Func2<Response, Response, String>() {
			public String call(Response t1, Response t2) {
				return t1.readEntity(String.class) + t2.readEntity(String.class);
			}
		};
		
		Observable.combineLatest(observable1, observable2, combineLatestFunc)
						.timeInterval()
						.subscribe(printFileAction);
	}

}
