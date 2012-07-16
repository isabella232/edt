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
		String stereoTypeName = "How Do You Get the Stereotype?";
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
				
		/** TODO:  retrieve comments from the IR */
		String mainComment = "CustomerType identifies the attributes of a customer. \n And what a customer! \n Yeah!";
		
		String mainComments[] = mainComment.split("\n", 2);
		
		
		// public void putAttribute(Object key, String value, Object entry) {
		ctx.putAttribute(this, "docType", docType);
		ctx.putAttribute(this, "stereoTypeName", stereoTypeName);
		ctx.putAttribute(this, "partName", partName);
		ctx.putAttribute(this, "fullPartName", fullPartName);
		ctx.putAttribute(this, "firstPara", mainComments[0].trim());
		ctx.putAttribute(this, "postFirstPara", mainComments[1].trim());
	}
		
	 
	public void genPart(Element part, Context ctx, TabbedWriter out) {
		ctx.invoke(genTop, part, ctx, out);
		ctx.invoke(genHead, part, ctx, out);
		ctx.invoke(genBody, part, ctx, out);
		out.println("</html>");
	}
	
	/** TODO:  must make each language code an external value */
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
		String fullPartName = (String) ctx.getAttribute(this, "fullPartName");
		String firstPara = (String) ctx.getAttribute(this, "firstPara");		
		
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></meta>");
		out.println("<meta name=\"DC.Type\" content=\"topic\"></meta>");
		out.println("<meta name=\"DC.Title\" content=\"" 
		            + partName + " " + docType + "\"></meta>");
		out.println("<meta name=\"abstract\" content=\"" + firstPara + "\"></meta>");
		out.println("<meta name=\"description\" content=\"" + firstPara + "\"></meta>");
		out.println("<meta name=\"DC.subject\" content=\"" + partName +"\"></meta>");
		out.println("<meta name=\"keywords\" content=\"" + partName +"\"></meta>");
		
		/** TODO:  must do something about the copyright and owner */
		out.println("<meta name=\"copyright\" content=\"" + "\"(C) Copyright 2011, 2012\" type=\"primary\"></meta>");
		out.println("<meta name=\"DC.Rights.Owner\" content=\"" + "\"(C) Copyright 2011, 2012\" type=\"primary\"></meta>");		
		
		out.println("<meta name=\"DC.Format\" content=\"XHTML\"></meta>");
		out.println("<meta name=\"DC.Identifier\" content=\"" + fullPartName + "\"></meta>");
		
		/** TODO:  must make language an external value */
		out.println("<meta name=\"DC.Language\" content=\"en-us\"></meta>");
		
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/commonltr.css\"></link>");
		out.println("<title>" + partName + " " + docType + "</title>");
		out.println("</head>");
	}
	
	
	
	
	public void genBody(Element part, Context ctx, TabbedWriter out) {
		String docType = (String) ctx.getAttribute(this, "docType");
		String stereoTypeName = (String) ctx.getAttribute(this, "stereoTypeName");
		String partName = (String) ctx.getAttribute(this, "partName");
		
		String firstPara = (String) ctx.getAttribute(this, "firstPara");
		
		// the topmost detail, with introductory paragraph		
		out.println("<body>");
		out.println("<h1 class=\"title topictitle1\">" + partName + " " + docType + "</h1>");
		out.println("<div class=\"body\" id=\"body\">");
		out.println("<p class=\"shortdesc\">" + firstPara + "</p>");
		
		// the list
		out.println("<dl class=\"dl\" id=\"main\">");
		
		// stereotype detail
		out.println("<dt class=\"dt dlterm\"><a name=\"typestereo\"</a>Type stereotype</dt>");
        
		if (stereoTypeName == ""){
		   out.println("<dd> <p class=\"p\">None.</p>");	
		}
		else {
			out.println("<dd> <p class=\"p\">" + stereoTypeName + "</p>");	
		}
		out.println("<p class=\"p\"></p></dd></dt>");
		
		
		
		
		
		out.println("</div></dl>");
		ctx.invoke(genPartContent, part, ctx, out);
		out.println("<anchor id=\"related_links\"></anchor>");
		out.println("</body>");
	}
}
