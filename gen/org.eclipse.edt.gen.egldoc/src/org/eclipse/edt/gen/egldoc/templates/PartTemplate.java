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
import org.eclipse.edt.mof.egl.Part;

public class PartTemplate extends EGLDocTemplate {
	
	
	
//	public void genPart(Part part, Context ctx, TabbedWriter out) {
//		ctx.invoke(genTop, part, ctx, out);
//		ctx.invoke(genHead, part, ctx, out);
//		ctx.invoke(genBody, part, ctx, out);
//		out.println("</html>");
//	}
	
	
	// Is for English.  Language should be made external.
	public void genTop(Part part, Context ctx, TabbedWriter out)
	{
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" + 
		     "http://www.w3.org/TR/html4/loose.dtd\">");
		out.println ("<html xml:lang=\"en-us\" lang=\"en-us\">");        
	}
}
