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

import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
public class ElementTemplate extends EGLDocTemplate {
	
	public void preGenPart(Element part, Context ctx) {
		
		String docType = part.getEClass().getName(); 
		String partName =  part.eGet("Name").toString();
		String fullPartName = part.eGet("PackageName") + "." + partName;
		
		if (docType.endsWith("Type")) {			
			int endPosition = docType.lastIndexOf("Type");
			StringBuffer docType02 = new StringBuffer();
			docType = 
				 docType02.append(Character.toLowerCase(docType.charAt(0)))
			              .append(docType.substring(1, endPosition))
			              .toString();
		}
		
		// public void putAttribute(Object key, String value, Object entry) {
		ctx.putAttribute(this, "docType", docType);
		ctx.putAttribute(this, "partName", partName);
		ctx.putAttribute(this, "fullPartName", fullPartName);
	}
		
	
	public void genPart(Element part, Context ctx, TabbedWriter out) {
		ctx.invoke(genTop, part, ctx, out);
		ctx.invoke(genHead, part, ctx, out);
		ctx.invoke(genBody, part, ctx, out);
		out.println("</html>");
	}
	
	// Is for English.  Language should be made external.
	public void genTop(Element part, Context ctx, TabbedWriter out)
		{
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" + 
			     "http://www.w3.org/TR/html4/loose.dtd\">");
			out.println ("<html xml:lang=\"en-us\" lang=\"en-us\">");        
		}
		
	public void genHead(Element part, Context ctx, TabbedWriter out)
	{
		
		// public Object getAttribute(Object key, String value)
		String docType = (String) ctx.getAttribute(this, "docType");
		String partName = (String) ctx.getAttribute(this, "partName");
		
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></meta>");
		out.println("<meta name=\"DC.Type\" content=\"topic\"></meta>");
		out.println("<meta name=\"DC.Title\" content=\"" 
		            + partName + " " + docType + "\"></meta>");
	}
	
	
	
	
	
	public void genBody(Element part, Context ctx, TabbedWriter out) {
		String fullPartName = (String) ctx.getAttribute(this, "fullPartName");
		
		out.println("<body>");
		out.println("<b>" + fullPartName + "</b>");
		out.println("TODO");
	}
}
