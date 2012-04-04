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

import java.util.List;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Stereotype;

public class HandlerTemplate extends JavaScriptTemplate {
	public static final String FieldName_OnConstructionFunction = "onConstructionFunction";
	
	public void genSuperClass(Handler type, Context ctx, TabbedWriter out) {
		out.print("ExecutableBase");
	}

	public void genClassHeader(Handler handler, Context ctx, TabbedWriter out) {
		if (CommonUtilities.isRUIWidget(handler)) {
			out.print("egl.defineRUIWidget(");
		}
		else{
			out.print("egl.defineClass(");
		}

		out.print(singleQuoted(handler.getPackageName().toLowerCase()));
		out.print(", ");
		out.print(singleQuoted(handler.getName()));
		out.println(", ");
		out.println("{");
		out.print("'eze$$fileName': ");
		out.print(singleQuoted(handler.getFileName()));
		out.println(", ");
		out.print("'eze$$runtimePropertiesFile': ");
		out.print(singleQuoted(handler.getFullyQualifiedName()));
		out.println(", ");
	}

	public void genConstructor(Handler handler, Context ctx, TabbedWriter out) {
		out.print(quoted("constructor"));
		out.println(": function() {");

		// instantiate each user part
		List<Part> usedParts = handler.getUsedParts();
		for (Part part : usedParts) {
			ctx.invoke(genInstantiation, part, ctx, out);
			out.println(";");
		}
		// instantiate each library
		ctx.invoke(genLibraries, handler, ctx, out);
		ctx.invoke(genFields, handler, ctx, out);
		out.println("this.eze$$setInitial();");
		
		Stereotype stereotype = handler.getStereotype();
		if ((stereotype != null) && ("RUIWidget".equals(stereotype.getEClass().getName()))){
			MemberName onConstruction = (MemberName) stereotype.getValue(FieldName_OnConstructionFunction);
			if (onConstruction != null) {
				out.print("this.");
				ctx.invoke(genName, onConstruction.getMember(), ctx, out);
				out.println("();");
			}
		}
		
		out.println("}");
	}

	public void genContainerBasedAccessor(Handler type, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genName, arg, ctx, out);
	}

	public void genConstructorOptions(Handler type, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(Handler type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}

	public void genClassFooter(Handler handler, Context ctx, TabbedWriter out) {
		if (CommonUtilities.isRUIWidget(handler)) {
			Annotation a = handler.getAnnotation("eglx.ui.rui.RUIWidget"); // TODO sbg Need constant
			String tagName = (String) a.getValue("tagName");// TODO sbg Need constant
			if ((tagName != null) && (tagName.trim().length() > 0)) {
				out.println(", '" + tagName + "'");
			}
		}
	}
	public void genCloneMethod(Handler handler, Context ctx, TabbedWriter out) {
		out.print(".eze$$clone()");
	}
	
	public void genCSSFile(Handler handler, TabbedWriter out){
		Annotation a = handler.getAnnotation( CommonUtilities.isRUIHandler( handler ) ? Constants.RUI_HANDLER : Constants.RUI_WIDGET);
		if ( a != null ){
			String fileName = (String)a.getValue( "cssFile" );
			if ( fileName != null && fileName.length() > 0 ){
				out.println("egl.loadCSS(\"" + fileName + "\");"); 
			}
		}
	}
}
