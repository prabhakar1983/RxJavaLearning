package com.example.helloworld.resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import rx.functions.Action1;

public class Helper {
	
	
	
	public static Action1<Object> printToFileAction = new Action1<Object>(){
		@Override
		public void call(Object response) {
			persist(response);
		}
	};
	
	public static Action1<Object> printToConsoleAction = new Action1<Object>(){
		@Override
		public void call(Object response) {
			System.out.println(response);
		}
	};
	
	public static void persist(Object response) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("/home/thatchinamoorthyp/Desktop/RxJava/Output", true));
			writer.append("---------------\n");

			writer.append("## Output ## " 			+ response + "\n");
			writer.append("Time : " 		+ LocalDateTime.now().getMinute() + "-" + LocalDateTime.now().getSecond() + "     ;     ");
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
	
	public static void sleep(long time) {
		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String readInputFromTerminal() {
		System.out.println("Enter Text:");
		return new Scanner(System.in).next();
	}
	
	public static String readInputFromFile() {
		try {
			return new Scanner(new File("/home/thatchinamoorthyp/Desktop/RxJava/Input")).next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Message readMessageFromQueue() {
		AmazonSQSClient amazonSQSClient = new AmazonSQSClient(new BasicAWSCredentials("AKIAJENLAO2AAZNM3V4A", "qefLN1jitQhAuNVjRKyOApWXAmUasnObMhgXyss5"));
		ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage("https://sqs.us-east-1.amazonaws.com/992971877959/RxJava");
		if (receiveMessageResult.getMessages() != null && receiveMessageResult.getMessages().size() > 0) {
			return receiveMessageResult.getMessages().get(0);
		} else 
			return null;
		
	}
	
	public static void deleteMessageFromQueue(String handle) {
		AmazonSQSClient amazonSQSClient = new AmazonSQSClient(new BasicAWSCredentials("AKIAJENLAO2AAZNM3V4A", "qefLN1jitQhAuNVjRKyOApWXAmUasnObMhgXyss5"));
		amazonSQSClient.deleteMessage("https://sqs.us-east-1.amazonaws.com/992971877959/RxJava", handle);
	}
	
}
