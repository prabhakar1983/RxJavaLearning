package com.examples.helloworld.session;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.example.helloworld.resources.Helper;
import com.example.helloworld.resources.OSB;

public class TraditionalWay {
	
		@Test
		public void traditionalWay() {
			//  PULL_COMPANY_INFO -->  Log to Database
			Helper.persist("START  --> Traditional Way");
			
			List<String> dunsList = Arrays.asList("001005003", "200100500");
			try {
				for (String duns : dunsList) {
					String companyInfo = OSB.getBasicCompanyInfo(duns); 	// 2 secs each
					
					Helper.persist(companyInfo); 								// 10 milli secs for a database call
				}
			} catch (Throwable t) {
				Helper.persist("An error occured");
				// TODO: Error Handling Code goes here.
			}

			Helper.persist("EMAIL or Take an appropriate action for Success Scenario"); 
		}

}
