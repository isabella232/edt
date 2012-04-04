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
package org.eclipse.edt.gen.eunit.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.Constants;
import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.gen.eunit.TestCounter;
import org.eclipse.edt.gen.eunit.TestDriverTargetLanguageKind;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;

public abstract class EUnitTemplate extends AbstractTemplate {
	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genPart = "genPart";
	public static final String genImports = "genImports";
	public static final String genClassBody = "genClassBody";
	public static final String genClassHeader = "genClassHeader";

	public static final String genLibDriver = "genLibDriver";
	public static final String genLibDriverImports = "genLibDriverImports";
	public static final String genLibDriverClassBody = "genLibDriverClassBody";

	// these are used by the validation step. preGen is used to preGen individual items within the part being generated.
	// preGenPart is invoked by the generator and should not be overridden or used by extending logic
	public static final String preGen = "preGen";
	public static final String preGenPart = "preGenPart";
	public static final String preGenClassBody = "preGenClassBody";

	@SuppressWarnings("unchecked")
	protected void collectTestFunctions(Library part, Context ctx, TestCounter couter, TestDriverTargetLanguageKind driverTargetLang) {
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partFunctionsWanted, new ArrayList<String>());
		for (Function function : part.getFunctions()) {
			ctx.invoke(preGen, function, ctx, couter, driverTargetLang);
		}
		// if there aren't any functions in the partFunctionsWanted list, then I should skip the partGeneration
		List<String> wantedFunctions = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partFunctionsWanted);
		if (wantedFunctions.isEmpty()) {
			String[] details = new String[] { part.getFullyQualifiedName() };
			// then just issue an informational message that we did not find any test functions for this library, driver will
			// not be generated
			EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_VALIDATION_NOFUNCFOUND, part, details,
				org.eclipse.edt.gen.CommonUtilities.includeEndOffset(part.getAnnotation(IEGLConstants.EGL_LOCATION), ctx));
			ctx.getMessageRequestor().addMessage(message);
		}
	}

	@SuppressWarnings("unchecked")
	protected void generateDriverLibraryBody(Library part, Context ctx, TabbedWriter out, TestCounter counter) {
		out.println("library " + part.getName());
		out.pushIndent();
		// out.println("use " + part.getFullyQualifiedName() + ";");
		out.println("function " + CommonUtilities.exeTestMethodName + "()");
		out.pushIndent();
		out.println("exeLibTestMtd executeLibTestMethod = executeLibTest;");
		out.println("td TestDescription;");
		out.println("td.expCnt = " + counter.getCount() + ";");
		out.println("td.rootDir = \"\";");
		out.println("td.pkgName = \"" + part.getPackageName() + "\";");
		out.println("td.name = \"" + part.getName() + "\";");
		out.println("td.title = \"\";");
		out.println("td.description = \"\";");
		out.print("td.testcases = \"");
		List<String> functions = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partFunctionsWanted);
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
		out.println("TestExecutionLib.testMain(exeLibTestMtd, td, true);");
		out.popIndent();
		out.println("end");
		out.println("function executeLibTest(testName String in) returns (MultiStatus)");
		out.pushIndent();
		out.print("testMethods String[] = [");
		boolean flag2 = false;
		int functionCnt = 0;
		for (String function : functions) {
			if (flag2)
				out.print(", ");
			out.print("\"" + function + "\"");
			flag2 = true;
			functionCnt++;
		}
		out.println("];");
		
		out.println("runTestMtds runTestMethod[] = new runTestMethod[];");
		for(String function : functions){
			out.print("runTestMtds ::= ");
			out.println(part.getFullyQualifiedName() + "." + function + ";");
		}
		
//		out.print("runTestMtds runTestMethod[] = new runTestMethod[]{");
//		boolean flag3 = false;
//		for (String function : functions) {
//			if (flag3)
//				out.print(", ");
//			out.print(part.getFullyQualifiedName() + "." + function);
//			flag3 = true;
//		}
//		out.println("};");
		
		out.println("return (TestExecutionLib.runMultiTest(testMethods, runTestMtds, testName));");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");
	}

	protected void generateImportStatement(Part part, Context ctx, TabbedWriter out) {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".MultiStatus;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestDescription;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestExecutionLib;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".LogResult;");		
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".executeLibTestMethod;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".runTestMethod;");
		out.println();
	}	
	
	protected void generateLibDriverImportStatements(Part part, Context ctx, TabbedWriter out) {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestExecutionLib;");
		out.println();
	}	
}
