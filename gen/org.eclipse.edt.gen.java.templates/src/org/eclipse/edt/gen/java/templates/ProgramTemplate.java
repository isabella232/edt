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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Program;

public class ProgramTemplate extends JavaTemplate {

	public void genSuperClass(Program program, Context ctx, TabbedWriter out, Object... args) {
		out.print("ProgramBase");
	}

	public void genConstructor(Program program, Context ctx, TabbedWriter out, Object... args) {
		String packageName = "";
		if (CommonUtilities.packageName(program) != null && CommonUtilities.packageName(program).length() > 0)
			packageName = CommonUtilities.packageName(program).replace(".", "/") + "/";
		out.println("");
		out.println("public static StartupInfo _startupInfo() {");
		out.print("\treturn new StartupInfo( \"");
		ctx.gen(genClassName, program, ctx, out, args);
		out.print("\", \"" + packageName);
		ctx.gen(genClassName, program, ctx, out, args);
		out.println(".properties\", false );");
		out.println("}");
		out.println("public static void main(String... args) throws Exception {");
		out.println("\t\tStartupInfo info = _startupInfo();");
		out.println("\t\tinfo.setArgs( args );");
		out.println("\t\tRunUnit ru = new RunUnitBase( info );");
		out.print("\t\tru.start( new ");
		ctx.gen(genClassName, program, ctx, out, args);
		out.println("( ru ), args );");
		out.println("\t\tru.exit();");
		out.println("}");

		// Generate RunUnit constructor
		out.print("public ");
		ctx.gen(genClassName, program, ctx, out, args);
		out.print("( RunUnit ru");
		ctx.gen(genAdditionalConstructorParams, program, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		ctx.gen(genAdditionalSuperConstructorArgs, program, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genRuntimeTypeName(Program program, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, program, ctx, out, args);
	}
}
