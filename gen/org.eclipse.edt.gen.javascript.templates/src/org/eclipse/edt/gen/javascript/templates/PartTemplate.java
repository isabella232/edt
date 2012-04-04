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
package org.eclipse.edt.gen.javascript.templates;

import java.util.ArrayList;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.DataTable;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class PartTemplate extends JavaScriptTemplate {

	IRUtils utils = new IRUtils();

	public void preGenPart(Part part, Context ctx) {
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partDataTablesUsed, new ArrayList<DataTable>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partFormsUsed, new ArrayList<Form>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed, new ArrayList<Library>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partRecordsUsed, new ArrayList<Record>());
		ctx.invoke(preGenClassBody, part, ctx);
		
		for (Annotation annot : part.getAnnotations()) {
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, part);
		}
	}

	public void genPart(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genClassHeader, part, ctx, out);
		out.pushIndent();
		ctx.invoke(genClassBody, part, ctx, out);
		out.println("}");
		out.popIndent();
		ctx.invoke(genClassFooter, part, ctx, out);
		out.println(");");
	}

	public void genClassFooter(Part part, Context ctx, TabbedWriter out) {
		// do nothing...
	}

	public void genPartName(Part part, Context ctx, TabbedWriter out) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else{
			// TODO Use Aliaser stuff from RBD
			String packageName = part.getPackageName().toLowerCase();
			if(packageName != null && packageName.length() > 0)
				out.print(eglnamespace + part.getPackageName().toLowerCase() + "." + part.getName()); 
			else
				out.print(eglnamespace +  part.getName());
		}
	}

	public void genClassName(Part part, Context ctx, TabbedWriter out) {
		out.print(JavaScriptAliaser.getAlias(part.getId()));
	}

	public void genSuperClass(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genSuperClass, part, ctx, out);
	}
	public void genFieldInfoTypeName(Part part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, part, ctx, out, arg);
	}
	public Boolean supportsConversion(Part part, Context ctx) {
		return Boolean.TRUE;
	}
}
