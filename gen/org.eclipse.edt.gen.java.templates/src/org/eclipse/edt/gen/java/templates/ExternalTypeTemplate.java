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

import java.util.List;

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
		if ( annot != null )
		{
			String shortName = part.getName();
			if ( ((String)annot.getValue("externalName")).length() > 0 )
			{
				shortName = (String)annot.getValue("externalName");
			}
			
			String fullName = shortName;
			if ( ((String)annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0 )
			{
				fullName = (String)annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
			}
			else if ( part.getPackageName().length() > 0 )
			{
				fullName = part.getPackageName() + '.' + fullName;
			}
			
			CommonUtilities.processImport(fullName, ctx);
		}
		else
		{
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
		// if this external type has an alias, then use it instead
		Annotation annot = part.getAnnotation("eglx.java.JavaObject");
		if ( annot != null )
		{
			String shortName = part.getName();
			if ( ((String)annot.getValue("externalName")).length() > 0 )
			{
				shortName = (String)annot.getValue("externalName");
			}
			
			String fullName = shortName;
			if ( ((String)annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0 )
			{
				fullName = (String)annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME) + '.' + fullName;
			}
			else if ( part.getPackageName().length() > 0 )
			{
				fullName = part.getPackageName() + '.' + fullName;
			}
			
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
		}
		else
		{
			ctx.invoke(genPartName, part, ctx, out);
		}
	}
}
