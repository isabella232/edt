package org.eclipse.edt.ide.eunit.ui.testresult;

public class ConstantUtil {

	public static final int SPASSED = 0;		//green
	public static final int SFAILED = 1;		//red
	public static final int SERROR = 2;			//orange
	public static final int SNOT_RUN = 3;		//yellow
	public static final int SBAD = 4;

	final static String[] STEXTS  = {"Passed.", "Failed.", "Error.", "Not run.", "Bad."};
	
//Record ResultSummaryRoot
//	expCnt int;	{@XMLAttribute{}};					//expected total number of test variation count	
//	trSum ResultSummary[]{};
//end	
	public static final String ELEM_trSummary = "trSum";
	
	
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
