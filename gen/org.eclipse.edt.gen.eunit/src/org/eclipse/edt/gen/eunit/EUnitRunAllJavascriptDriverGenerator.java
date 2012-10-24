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
package org.eclipse.edt.gen.eunit;

import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EUnitRunAllJavascriptDriverGenerator extends
		EUnitRunAllDriverGenerator {

	public EUnitRunAllJavascriptDriverGenerator(
			AbstractGeneratorCommand processor,
			IGenerationMessageRequestor msgReq, String driverPartNameAppend, IEUnitGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq, driverPartNameAppend, eckGenerationNotifier);
	}

	public void generateRunAllDriver(List<String> listOfGenedLibs, TestCounter totalCnts){	
		genPackageDeclaration();
		
		genImports();
		
		String genedPartName = RunAllTest + fDriverPartNameAppend;		
		out.println("Handler " + genedPartName + " type RUIHandler {initialUI = [], includefile = \"rununit.html\", onConstructionFunction = start, title=\"" + genedPartName + "\"} ");
		out.pushIndent();
		out.println("function start()");
		out.pushIndent();
		out.println("startTS timestamp(\"yyyyMMddHHmmssffffff\");");
		for(String genLibName : listOfGenedLibs){
			out.println(genLibName + "." + CommonUtilities.exeTestMethodName + "();");				
		}
		out.println("TestExecutionLib.writeResultSummary(" + totalCnts.getCount() + ", startTS);");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
		out.close();
	}	
}
