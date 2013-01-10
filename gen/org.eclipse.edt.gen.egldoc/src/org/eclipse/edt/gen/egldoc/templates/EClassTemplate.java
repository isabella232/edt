/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Element;


public class EClassTemplate extends EGLDocTemplate {

	public void preGenContent(EClass eClass, Context ctx) {
		String theName = eClass.getName();
		String packageName = eClass.getPackageName();
		String fullPartName = theName + "." + packageName;
		
		ctx.put(Constants.PARTNAME, theName);
		ctx.put(Constants.PACKAGENAME, packageName);
		ctx.put(Constants.FULLPARTNAME, fullPartName);
		ctx.invokeSuper(this, preGenContent, eClass, ctx);
		// System.out.println("in EClass template, theName = " + theName);
		
	}

	/*
	public void genClassContent(EClass eClass, Context ctx, TabbedWriter out) {

		ctx.invoke(genStereotypeName, eClass, ctx, out);

	}
    */

}
