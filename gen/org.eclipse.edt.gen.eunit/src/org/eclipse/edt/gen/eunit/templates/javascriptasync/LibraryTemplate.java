/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import org.eclipse.edt.gen.eunit.templates.EUnitTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;

public class LibraryTemplate extends EUnitTemplate {

	public void preGenClassBody(Library part, Context ctx, TestCounter counter) {
		preGenFunctions(part, ctx, counter);
	}

	public void preGenFunctions(Library part, Context ctx, TestCounter counter) {
		collectTestFunctions(part, ctx, counter);
	}
	
	public void genImports(Part part, Context ctx, TabbedWriter out) {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestAsyncLib;");
		generateImportStatement(part, ctx, out);
	}	
	
	public void genClassBody(Library part, Context ctx, TabbedWriter out, TestCounter counter) {
		out.println("library " + part.getName());
		out.pushIndent();
		out.println("function " + CommonUtilities.exeTestMethodName + "()");
		out.pushIndent();
		out.print("testMethods String[] = [");
		
		List<String> functions = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partFunctionsWanted);		
		boolean flag2 = false;
		for (String function : functions) {
			if (flag2)
				out.print(", ");
			out.print("\"" + function + "\"");
			flag2 = true;
		}
		out.println("];");
		
		out.println("testVariationCnt int = testMethods.getSize();");
		out.println(part.getFullyQualifiedName() + ".ms.expectedCnt = testVariationCnt;");
		out.println("LogResult.clearResults();");
		
		out.println("TestAsyncLib.runTestMtds runTestMethod[] = new runTestMethod[];");
		for(String function : functions){
			out.print("runTestMtds ::= ");
			out.println(part.getFullyQualifiedName() + "." + function + ";");
		}
		out.println("TestAsyncLib.runTestMtds ::= " + CommonUtilities.endTestMethodName);
		out.println();
		out.println("TestAsyncLib.runTestMtds[1]();");	
		out.popIndent();
		out.println("end");		
		 
		out.println();
		
		out.println("function " + CommonUtilities.endTestMethodName + "()");
		out.pushIndent();
		out.println("td TestDescription;");
		out.println("td.expCnt = " + counter.getCount() + ";");
		out.println("td.rootDir = \"\";");
		out.println("td.pkgName = \"" + part.getPackageName() + "\";");
		out.println("td.name = \"" + part.getName() + "\";");
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
		out.println("td.sources = \"" + part.getName() + ".egl\";");
		out.println("td.keywords = \"\";");
		
		out.println("TestExecutionLib.writeResults(td, " + part.getFullyQualifiedName() + ".ms, true);");						
		out.println("TestAsyncLib.nextTestLibrary();");		
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");
	}
	
	public void genLibDriverClassBody(Library part, Context ctx, TabbedWriter out, String driverPartNameAppend, TestCounter counter){
		String genedHandlerName = part.getName() + driverPartNameAppend;
		out.println("Handler " + genedHandlerName + " type RUIhandler {initialUI = [], includefile = \"rununit.html\", onConstructionFunction = start, title=\"" +genedHandlerName + "\"} ");
		
		out.pushIndent();	
		out.println("function start()");
		out.pushIndent();
		out.println("TestAsyncLib.LibraryStartTests = new runTestMethod[];");
		out.println("TestAsyncLib.LibraryStartTests ::= " + CommonUtilities.getECKGenPartFQName(part) + "." + CommonUtilities.exeTestMethodName + "();");
		out.println("TestAsyncLib.LibraryStartTests ::= " + CommonUtilities.endTestMethodName + ";");
		out.println("TestAsyncLib.LibraryStartTests[1]();");
		out.popIndent();
		out.println("end");
		out.println();
		
		out.println("function " + CommonUtilities.endTestMethodName + "()");
		out.pushIndent();
		out.println("TestExecutionLib.writeResultSummary(" + counter.getCount() + ");");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
	}
}
