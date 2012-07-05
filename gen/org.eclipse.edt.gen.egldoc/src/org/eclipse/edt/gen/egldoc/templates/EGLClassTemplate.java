/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;

public class EGLClassTemplate extends EGLDocTemplate {
	
	public void preGenPart(EGLClass part, Context ctx) {
		//TODO any required analysis or validation
	}
	
	public void genPart(EGLClass part, Context ctx, TabbedWriter out) {
		out.println("<html>");
		out.println("<body>");
		ctx.invoke(genBody, part, ctx, out);
		out.println("</body>");
		out.println("</html>");
	}
	
	public void genBody(EGLClass part, Context ctx, TabbedWriter out) {
		out.println("<b>" + part.getFullyQualifiedName() + "</b>");
		out.println("TODO - actually generate egldoc");
	}
}
