/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.ui.testresult;

public class ConstantUtil {

	public static final int PASSED = 0;		//green
	public static final int FAILED = 1;		//red
	public static final int EXCEPTION = 2;	//purple
	public static final int NOT_RUN = 3;	//orange

	final static String[] STEXTS  = {"Passed.", "Failed.", "Exception.", "Skipped."};
	

//Record ResultSummaryRoot {@xmlrootelement {}}
//	expCnt int{@XMLAttribute{}};					//expected total number of test variation count
//	startTS String{@XMLAttribute{}};				//starting timestamp
//	endRunTS String{@XMLAttribute{}};				//ending timestamp for running all the test cases
//	finalTS String{@XMLAttribute{}};				//after finish running the test case, it will write out result summary, 
//													//this is the timestamp it finishes everything including writing out result summary 		
//	trSum ResultSummary[]{};
//end	
	public static final String ELEM_trSummary = "trSum";
	public static final String ATTRIB_startTS = "startTS";
	public static final String ATTRIB_endRunTS = "endRunTS";
	public static final String ATTRIB_finalTS = "finalTS";	
	
//Record ResultSummary
//	pkgName String {@XMLAttribute{}};	//package name
//	name String{@XMLAttribute{}};		//test library part name
//	resultCode int{@XMLAttribute{}};	//test result code value
//	tCnt int{@XMLAttribute{}};
//	expCnt int{@XMLAttribute{}};		//expected test variation count for one test library	
//	passCnt int{@XMLAttribute{}};
//	failCnt int{@XMLAttribute{}};
//	errCnt int{@XMLAttribute{}};
//	badCnt int{@XMLAttribute{}};
//	skipCnt int{@XMLAttribute{}};	
//end		
	public static final String ATTRIB_pkgName = "pkgName";
	public static final String ATTRIB_name = "name";
	public static final String ATTRIB_resultCode = "code";	
	public static final String ATTRIB_testCnt = "tCnt";
	public static final String ATTRIB_expCnt = "expCnt";
	public static final String ATTRIB_passedCnt = "passCnt";
	public static final String ATTRIB_failedCnt = "failCnt";
	public static final String ATTRIB_errCnt = "errCnt";
	public static final String ATTRIB_badCnt = "badCnt";
	public static final String ATTRIB_notRunCnt = "skipCnt";
	
//
//Record TestResult
//	td TestDescription;
//	stat Status;
//	log Result;
//end		
	public static final String ELEM_td = "td";
	public static final String ELEM_stat = "stat";
	public static final String ELEM_log = "log";

//Record TestDescription
//	rootDir String;	
//	pkgName String;		//package name
//	name String;		//test library part name
//	title String;
//	description String;
//	testcases String;
//	sources String;  //list of the runTestMethod names, delimted by whitespace. 
//	keywords String; //list of tag names, delimited by ,
//end	
	public static final String ELEM_rootDir = "rootDir";
	public static final String ELEM_pkgName = "pkgName";
	public static final String ELEM_name = "name";
	public static final String ELEM_title = "title";
	public static final String ELEM_description = "description";
	public static final String ELEM_testcases = "testcases";
	public static final String ELEM_sources = "sources";
	public static final String ELEM_keywords = "keywords";
	
//Record Status
//	code int;
//	reason String;
//end
//	
	public static final String ELEM_code = "code";
	public static final String ELEM_reason = "reason";
	
//Record Result
//	msg String;
//end
//	
	public static final String ELEM_msg = "msg";
}
