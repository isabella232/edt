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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;

public class ExternalTypeTemplate extends JavaTemplate {

	static final String DELEGATE_IN_INNER_CLASS = "org.eclipse.edt.gen.java.templates.ExternalTypeTemplate.DELEGATE_IN_INNER_CLASS";

	public void preGenClassBody(ExternalType part, Context ctx) {}

	public void preGen(ExternalType part, Context ctx) {
		// ignore adding this entry to the list, if it is the part we are currently generating
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated)).getFullyQualifiedName().equalsIgnoreCase(
			part.getFullyQualifiedName()))
			return;

		// if this external type has an alias, then use it instead
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if (annot != null) {
			String shortName = part.getName();
			if (annot.getValue("externalName") != null && ((String) annot.getValue("externalName")).length() > 0) {
				shortName = (String) annot.getValue("externalName");
			}
			String fullName = shortName;
			if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0) {
				fullName = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
			} else if (part.getPackageName().length() > 0) {
				fullName = part.getPackageName() + '.' + fullName;
			}
			CommonUtilities.processImport(fullName, ctx);
		} else {
			annot = part.getAnnotation("eglx.java.RootJavaObject");
			if (annot != null) {
				String fullName = part.getName();
				if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0) {
					fullName = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
				} else if (part.getPackageName().length() > 0) {
					fullName = part.getPackageName() + '.' + fullName;
				}
				CommonUtilities.processImport(fullName, ctx);
			} else {
				// process anything else the superclass needs to do
				ctx.invokeSuper(this, preGen, part, ctx);
			}
		}
	}

	public void genPart(ExternalType part, Context ctx, TabbedWriter out) {
		if (part.getAnnotation("eglx.lang.NativeType") != null) {
			// While there's no part to generate, we still want the SMAP.
			ctx.putAttribute(ctx.getClass(), Constants.SubKey_forceWriteSMAP, Boolean.TRUE);
			
			// Save functions for last so all globals come first. vars after functions are local.
			List<Function> functions = new ArrayList<Function>();
			for (Member m : part.getAllMembers()) {
				if (m instanceof Field) {
					CommonUtilities.generateSmapExtension( m, ctx );
				}
				else if (m instanceof Function) {
					functions.add((Function)m);
				}
			}
			for (Function f : functions) {
				List<FunctionParameter> parms = f.getParameters();
				if (parms.size() > 0) {
					CommonUtilities.generateSmapExtension( f, ctx );
					for (FunctionParameter parm : parms) {
						CommonUtilities.generateSmapExtension(parm, ctx);
					}
				}
			}
		}
	}

	public void genClassBody(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genAccessor(ExternalType part, Context ctx, TabbedWriter out) {
		genRuntimeTypeName(part, ctx, out, TypeNameKind.JavaImplementation);
	}

	@SuppressWarnings("unchecked")
	public void genRuntimeTypeName(ExternalType part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		// if this external type has an alias, then use it instead
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if (annot != null) {
			String shortName = part.getName();
			if (annot.getValue("externalName") != null && ((String) annot.getValue("externalName")).length() > 0)
				shortName = (String) annot.getValue("externalName");
			String fullName = shortName;
			if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0)
				fullName = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
			else if (part.getPackageName().length() > 0)
				fullName = part.getPackageName() + '.' + fullName;
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partTypesImported);
			for (String imported : typesImported) {
				if (fullName.equalsIgnoreCase(imported)) {
					// it was in the table, so use the short name
					out.print(shortName);
					return;
				}
			}
			out.print(fullName);
		} else {
			annot = part.getAnnotation("eglx.java.RootJavaObject");
			if (annot != null) {
				String fullName = part.getName();
				if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0)
					fullName = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
				else if (part.getPackageName().length() > 0)
					fullName = part.getPackageName() + '.' + fullName;
				out.print(fullName);
			} else
				ctx.invoke(genPartName, part, ctx, out);
		}
	}

	public void genContainerBasedAssignment(ExternalType part, Context ctx, TabbedWriter out, Assignment assignment, Field field) {
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if (annot == null)
			annot = part.getAnnotation("eglx.java.RootJavaObject");
		if (annot != null) {
			Annotation eventListener = field.getAnnotation("eglx.lang.EventListener");
			if (eventListener != null) {
				ctx.invoke(genExpression, assignment.getLHS().getQualifier(), ctx, out);
				out.print('.');
				out.print(eventListener.getValue("addMethod").toString());
				out.print("( new ");
				out.print(eventListener.getValue("listenerType").toString());
				out.print("() { public void ");
				out.print(eventListener.getValue("method").toString());
				out.print("( ");
				ctx.invoke(genRuntimeTypeName, ((Delegate) field.getType()).getParameters().get(0).getType(), ctx, out);
				out.print(" ezeEvent ) { ");
				ctx.put(DELEGATE_IN_INNER_CLASS, true);
				ctx.invoke(genExpression, assignment.getRHS(), ctx, out);
				ctx.remove(DELEGATE_IN_INNER_CLASS);
				out.print(".invoke( ezeEvent );");
				out.print(" } } )");
				return;
			}
		}
		ctx.invokeSuper(this, genContainerBasedAssignment, part, ctx, out, assignment, field);
	}
}
