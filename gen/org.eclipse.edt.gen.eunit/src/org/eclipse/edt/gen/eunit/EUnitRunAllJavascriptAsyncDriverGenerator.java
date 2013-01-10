/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit;

import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EUnitRunAllJavascriptAsyncDriverGenerator extends
		EUnitRunAllDriverGenerator {

	public EUnitRunAllJavascriptAsyncDriverGenerator(
			AbstractGeneratorCommand processor,
			IGenerationMessageRequestor msgReq, String driverPartNameAppend,
			IEUnitGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq, driverPartNameAppend, eckGenerationNotifier);
	}
	
	@Override
	protected void genImports() {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".runTestMethod;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".ServiceBindingType;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME +".TestListMgr;");
		super.genImports();
	}

	@Override
	public void generateRunAllDriver(List<String> listOfGenedLibs,
			TestCounter totalCnts) {
		genPackageDeclaration();
		
		genImports();

		String genedPartName = RunAllTest + fDriverPartNameAppend;		
		out.println("Handler " + genedPartName + " type RUIHandler {initialUI = [], includefile = \"rununit.html\", onConstructionFunction = start, title=\"" + genedPartName + "\"} ");
		out.pushIndent();		
		out.println("startTS timestamp?;");
		out.println();
		out.println("function start()");
		out.pushIndent();
		out.println("ts timestamp(\"yyyyMMddHHmmssffffff\");");
		out.println("startTS = ts;");
		out.println("TestListMgr.bindingType = ServiceBindingType.DEDICATED;");
		out.println("TestListMgr.LibraryStartTests = new runTestMethod[];");
		for(String genLibName : listOfGenedLibs){
			out.print("TestListMgr.LibraryStartTests ::= ");
			out.println(genLibName + "." + CommonUtilities.exeTestMethodName + ";");				
		}
		out.println("TestListMgr.LibraryStartTests ::= " + CommonUtilities.endTestMethodName + ";");
		out.println("TestListMgr.LibraryStartTests[1]();");
		out.popIndent();
		out.println("end");
		
		out.println();
		out.println("function " + CommonUtilities.endTestMethodName + "()");
		out.pushIndent();		
		out.println("TestExecutionLib.writeResultSummary(" + totalCnts.getCount() + ", startTS);");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
		out.close();
		
	}	

}
