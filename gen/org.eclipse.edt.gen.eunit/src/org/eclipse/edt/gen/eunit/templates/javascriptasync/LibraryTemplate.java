/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit.templates.javascriptasync;

import java.util.List;

import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.Constants;
import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.gen.eunit.TestCounter;
import org.eclipse.edt.gen.eunit.TestDriverTargetLanguageKind;
import org.eclipse.edt.gen.eunit.templates.EUnitTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;

public class LibraryTemplate extends EUnitTemplate {

	public void preGenClassBody(Library part, Context ctx, TestCounter counter) {
		preGenFunctions(part, ctx, counter);
	}

	public void preGenFunctions(Library part, Context ctx, TestCounter counter) {
		collectTestFunctions(part, ctx, counter, TestDriverTargetLanguageKind.JAVASCRIPT);
	}
	
	public void genImports(Part part, Context ctx, TabbedWriter out) {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestListMgr;");
		generateImportStatement(part, ctx, out);
	}	
	
	public void genLibDriverImports(Library part, Context ctx, TabbedWriter out){
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestListMgr;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".runTestMethod;");
		generateLibDriverImportStatements(part, ctx, out);
	}
	
	public void genClassBody(Library part, Context ctx, TabbedWriter out, TestCounter counter) {
		out.println("library " + part.getCaseSensitiveName());
		out.pushIndent();
		out.println("function " + CommonUtilities.exeTestMethodName + "()");
		out.pushIndent();		
//		out.println("TestListMgr.bindingType = ServiceBindingType.DEDICATED;");
		out.println(CommonUtilities.runningTestMethodName + "();");
		out.popIndent();
		out.println("end");
		
		out.println();
		
		out.println("function " + CommonUtilities.runningTestMethodName + "()");
		out.pushIndent();
		out.println("//reset the test library name (TestDescription.name)");
		out.println("TestListMgr.testLibName = \"" + part.getCaseSensitiveName() + "\" + \"_\" + TestListMgr.getBindingTypeString(TestListMgr.bindingType);");
		
		List<String> functions = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partFunctionsWanted);
		out.println("//reset the list of test method names in this libary ");
		out.print("TestListMgr.testMethodNames = [");
		boolean flag2 = false;
		for (String function : functions) {
			if (flag2)
				out.print(", ");
			out.print("\"" + function + "\"");
			flag2 = true;
		}
		out.println("];");
		
		out.println("testVariationCnt int = TestListMgr.testMethodNames.getSize();");
		out.println();
		out.println("//reset the multiStatus in this library");
		out.println("TestListMgr.ms = new MultiStatus;");
		out.println("TestListMgr.ms.expectedCnt = testVariationCnt;");
		out.println();
		
		out.println("LogResult.clearResults();");
		
		out.println("//reset the list of tests in this libary ");
		out.println("TestListMgr.runTestMtds = new runTestMethod[];");
		for(String function : functions){
			out.print("TestListMgr.runTestMtds ::= ");
			out.println(part.getFullyQualifiedName() + "." + function + ";");
		}
		out.println("TestListMgr.runTestMtds ::= " + CommonUtilities.endTestMethodName + ";");
		out.println();
		
		out.println("//reset the index back to 1");
		out.println("TestListMgr.testIndex = 1;");		
		out.println("TestListMgr.runTestMtds[TestListMgr.testIndex]();");	
		out.popIndent();
		out.println("end");		
		 
		out.println();
		
		out.println("function " + CommonUtilities.endTestMethodName + "()");
		out.pushIndent();
//		
//		out.println("case(TestListMgr.bindingType)");
//		out.pushIndent();
//		out.println("when(ServiceBindingType.DEDICATED)");
//		out.pushIndent();
//		out.println("TestListMgr.bindingType = ServiceBindingType.DEVELOP;");
//		out.println(CommonUtilities.runningTestMethodName + "();");
//		out.popIndent();
//		out.println("when(ServiceBindingType.DEVELOP)");
//		out.pushIndent();
//		out.println("TestListMgr.bindingType = ServiceBindingType.DEPLOYED;");
//		out.println(CommonUtilities.runningTestMethodName + "();");
//		out.popIndent();
//		out.popIndent();
//		out.println("end");
		
		out.println("td TestDescription;");
		out.println("td.expCnt = " + counter.getCount() + ";");
		out.println("td.rootDir = \"\";");
		out.println("td.pkgName = \"" + part.getCaseSensitivePackageName() + "\";");
		out.println("td.name = \"" + part.getCaseSensitiveName() + "\";");
		out.println("td.title = \"\";");
		out.println("td.description = \"\";");
		out.print("td.testcases = \"");

		boolean flag1 = false;
		for (String function : functions) {
			if (flag1)
				out.print(" ");
			out.print(function);
			flag1 = true;
		}
		out.println("\";");
		out.println("td.sources = \"" + part.getCaseSensitiveName() + ".egl\";");
		out.println("td.keywords = \"\";");
		
		out.println("TestExecutionLib.writeResults(td, TestListMgr.ms, true);");						
		out.println("TestListMgr.nextTestLibrary();");		
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");
	}
	
	public void genLibDriverClassBody(Library part, Context ctx, TabbedWriter out, String driverPartNameAppend, TestCounter counter){
		String genedHandlerName = part.getCaseSensitiveName() + driverPartNameAppend;
		out.println("Handler " + genedHandlerName + " type RUIhandler {initialUI = [], includefile = \"rununit.html\", onConstructionFunction = start, title=\"" +genedHandlerName + "\"} ");
		
		out.pushIndent();	
		out.println("startTS timestamp?;");
		out.println();
		
		out.println("function start()");
		out.pushIndent();
		out.println("ts timestamp(\"yyyyMMddHHmmssffffff\");");
		out.println("startTS = ts;");
		out.println("TestListMgr.bindingType = ServiceBindingType.DEDICATED;");		
		out.println("TestListMgr.LibraryStartTests = new runTestMethod[];");
		out.println("TestListMgr.LibraryStartTests ::= " + CommonUtilities.getECKGenPartFQName(part) + "." + CommonUtilities.exeTestMethodName + ";");
		out.println("TestListMgr.LibraryStartTests ::= " + CommonUtilities.endTestMethodName + ";");
		out.println("TestListMgr.LibraryStartTests[1]();");
		out.popIndent();
		out.println("end");
		out.println();
		
		out.println("function " + CommonUtilities.endTestMethodName + "()");
		out.pushIndent();
		out.println("TestExecutionLib.writeResultSummary(" + counter.getCount() + ", startTS);");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
	}
}
