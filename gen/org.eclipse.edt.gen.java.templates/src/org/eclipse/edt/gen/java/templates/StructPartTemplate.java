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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.StructPart;

public class StructPartTemplate extends JavaTemplate {

	public void genAnnotations(StructPart part, Context ctx, TabbedWriter out, Field field) {
		for(Annotation annot : field.getAnnotations()){
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}

	public static String getInterfaces(StructPart part, Context ctx){
		TabbedWriter out = ctx.getTabbedWriter();
		List<StructPart> extndsAry = part.getSuperTypes();
		boolean appendComma = false;
		if (extndsAry != null) {
			for (StructPart extend : extndsAry) {
				if(extend instanceof Interface){
					if(appendComma){
						out.print(", ");
					}
					else{
						appendComma = true;
					}
					String pkg = CommonUtilities.packageName(extend);
					if(pkg != null && !pkg.isEmpty()){
						out.print(pkg);
						out.print(".");
					}
					ctx.invoke(genClassName, extend, ctx, out);
				}
			}
		}
		out.close();
		return out.getWriter().toString();
	}
}
