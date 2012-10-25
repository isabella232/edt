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
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.StructPart;

public class StructPartTemplate extends JavaTemplate {

	public static String getInterfaces(StructPart part, Context ctx){
		TabbedWriter out = ctx.getTabbedWriter();
		boolean appendComma = false;
		for (Interface iface : part.getInterfaces()) {
			if(appendComma){
				out.print(", ");
			}
			else{
				appendComma = true;
			}
			String pkg = CommonUtilities.packageName(iface);
			if(pkg != null && !pkg.isEmpty()){
				out.print(pkg);
				out.print(".");
			}
			ctx.invoke(genClassName, iface, ctx, out);
		}
		out.close();
		return out.getWriter().toString();
	}
}
