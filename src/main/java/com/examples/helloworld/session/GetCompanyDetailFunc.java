package com.examples.helloworld.session;

import rx.functions.Func1;

import com.example.helloworld.resources.OSB;

public enum GetCompanyDetailFunc implements Func1<String, String>{

	GETCOMPANY_FUNC;
	
	@Override
	public String call(String duns) {
		return OSB.getBasicCompanyInfo(duns);
	}

}
