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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;

public class ProgramTemplate extends JavaTemplate {

	public void preGen(Program program, Context ctx) {
		// process anything else the superclass needs to do
		ctx.invokeSuper(this, preGen, program, ctx);
		// when we get here, we need to add the program parameter list to the table
		for (ProgramParameter programParameter : program.getParameters()) {
			CommonUtilities.generateSmapExtension(programParameter, ctx);
		}
	}

	public void genSuperClass(Program program, Context ctx, TabbedWriter out) {
		out.print("ProgramBase");
	}

	public void genConstructor(Program program, Context ctx, TabbedWriter out) {
		String packageName = "";
		if (CommonUtilities.packageName(program) != null && CommonUtilities.packageName(program).length() > 0)
			packageName = CommonUtilities.packageName(program).replace(".", "/") + "/";
		out.println("");
		out.println("public static void main(String... ezeargs) throws Exception {");
		out.print("StartupInfo info = new StartupInfo( \"");
		ctx.invoke(genClassName, program, ctx, out);
		out.print("\", \"" + packageName);
		ctx.invoke(genClassName, program, ctx, out);
		out.println(".properties\", ezeargs );");
		out.println("RunUnit ru = new JSERunUnit( info );");
		out.println("org.eclipse.edt.javart.Runtime.setStaticRunUnit( ru );");
		out.print("ru.start( new ");
		ctx.invoke(genClassName, program, ctx, out);
		out.println("() );");
		out.println("ru.exit();");
		out.println("}");

		out.print("public ");
		ctx.invoke(genClassName, program, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, program, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, program, ctx, out);
		out.println(");");
		out.println("}");

		out.println("{");
		out.println("if(org.eclipse.edt.javart.Runtime.getRunUnit().getActiveExecutable() == null)");
		out.println("{");
		out.println("org.eclipse.edt.javart.Runtime.getRunUnit().setActiveExecutable(this);");
		out.println("}");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genRuntimeTypeName(Program program, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, program, ctx, out);
	}
}
