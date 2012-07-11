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
		ctx.invoke(genTop, part, ctx, out);
		ctx.invoke(genHead, part, ctx, out);
		ctx.invoke(genBody, part, ctx, out);
		out.println("</html>");
	}
	
	
	
	
	public void genHead(EGLClass part, Context ctx, TabbedWriter out)
	{
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></meta>");
		out.println("<meta name=\"DC.Type\" content=\"topic\"></meta>");
		out.println("<meta name=\"DC.Title\" content=\"" 
		            + part.getName() + " " + part.getEClass().getName() + "\"></meta>");
				   
		
	   
	}
	
	
	
	
	
	public void genBody(EGLClass part, Context ctx, TabbedWriter out) {
		out.println("<body>");
		out.println("<b>" + part.getFullyQualifiedName() + "</b>");
		out.println("TODO");
	}
}
