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
package org.eclipse.edt.gen.eunit.templates.javascript;

import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.gen.eunit.TestCounter;
import org.eclipse.edt.gen.eunit.templates.EUnitTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Library;

public class LibraryTemplate extends EUnitTemplate {

	public void preGenClassBody(Library part, Context ctx, TestCounter counter) {
		preGenFunctions(part, ctx, counter);
	}

	public void preGenFunctions(Library part, Context ctx, TestCounter counter) {
		collectTestFunctions(part, ctx, counter);
	}

	public void genClassBody(Library part, Context ctx, TabbedWriter out, TestCounter counter) {
		generateDriverLibraryBody(part, ctx, out, counter);
	}
	
	public void genLibDriverClassBody(Library part, Context ctx, TabbedWriter out, String driverPartNameAppend, TestCounter counter){
		String genedHandlerName = part.getName() + driverPartNameAppend;
		out.println("Handler " + genedHandlerName + " type RUIhandler {initialUI = [], includefile = \"rununit.html\", onConstructionFunction = start, title=\"" +genedHandlerName + "\"} ");
		
		out.pushIndent();
		//out.println("use " + part.getFullyQualifiedName() + ";");		
		out.println("function start()");
		out.pushIndent();
		out.print(part.getName() + "." + CommonUtilities.exeTestMethodName + "()");
		out.println(";");
		out.println("TestExecutionLib.writeResultSummary(" + counter.getCount() + ");");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
	}

}
