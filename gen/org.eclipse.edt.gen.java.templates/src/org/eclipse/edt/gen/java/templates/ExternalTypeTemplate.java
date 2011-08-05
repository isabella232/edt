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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;

public class ExternalTypeTemplate extends JavaTemplate {

	public void preGenClassBody(ExternalType part, Context ctx) {}

	public void preGen(ExternalType part, Context ctx) {
		// ignore adding this entry to the list, if it is the part we are currently generating
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated)).getFullyQualifiedName().equalsIgnoreCase(
			part.getFullyQualifiedName()))
			return;
		// if this external type has an alias, then use it instead
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if (annot != null && annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) != null && annot.getValue(IEGLConstants.PROPERTY_JAVANAME) != null)
			CommonUtilities.processImport(
				(String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + "." + (String) annot.getValue(IEGLConstants.PROPERTY_JAVANAME), ctx);
		else {
			// process anything else the superclass needs to do
			ctx.invokeSuper(this, preGen, part, ctx);
		}
	}

	public void genPart(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genAccessor(ExternalType part, Context ctx, TabbedWriter out) {
		ctx.invoke(genPartName, part, ctx, out);
	}

	public void genRuntimeTypeName(ExternalType part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if (annot != null && annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) != null && annot.getValue(IEGLConstants.PROPERTY_JAVANAME) != null)
			out.print((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + "." + (String) annot.getValue(IEGLConstants.PROPERTY_JAVANAME));
		else
			ctx.invoke(genPartName, part, ctx, out);
	}
}
