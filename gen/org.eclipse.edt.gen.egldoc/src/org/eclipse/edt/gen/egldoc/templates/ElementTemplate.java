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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.jface.text.DefaultLineTracker;

public class ElementTemplate extends EGLDocTemplate {

	public void preGenPart(Element part, Context ctx) {
		
		String docType = part.getEClass().getCaseSensitiveName(); 
		String fieldContainerType = new String(docType);
		
		// control display of the identifiers used
		// in doc title (docType) and a section heading (fieldContainerType)
		if (docType.endsWith("Type")) {

			int endPosition = docType.lastIndexOf("Type");
			StringBuffer docType02 = new StringBuffer();
			docType = docType02
					.append(Character.toLowerCase(docType.charAt(0)))
					.append(docType.substring(1, endPosition))
					.append(" type").toString();
			
			if (fieldContainerType.equals("ExternalType")){
				fieldContainerType = "External type";				
			}
			else
			{
			   StringBuffer fieldContainerType02 = new StringBuffer();
			   fieldContainerType = fieldContainerType02
						.append(fieldContainerType.substring(0, endPosition))
						.toString();
			}
			
			
			/*
			 * fieldContainerType = fieldContainerType02
             *		.append(Character.toLowerCase(fieldContainerType.charAt(0)))
			 *      .append(docType.substring(1, endPosition)).toString();
			 *
			 *				.append(fieldContainerType.substring(0, endPosition))
			 *				.append(" type").toString();
			 */
		}
		
		processComments(part, ctx);		
		
		String blockComment = org.eclipse.edt.gen.egldoc.Util.findBlockComment(ctx, org.eclipse.edt.gen.egldoc.Util.getLine(part));
		
		if(blockComment != null){
			Map<String, String> blockCommentMap = org.eclipse.edt.gen.egldoc.Util.parseCommentBlock(blockComment);
			ctx.put("firstPara", blockCommentMap.get("firstPara"));
			ctx.put("postFirstPara", blockCommentMap.get("postFirstPara"));
		}
		
		ctx.put("docType", docType);
		ctx.put("fieldContainerType", fieldContainerType);
	}


	private void processComments(Element part, Context ctx) {
		IFile eglFile = (IFile)ctx.getAttribute(ctx.getClass(), "eglFile");
		try {
			String fileContents = Util.getFileContents(eglFile);
			ctx.put("fileContents", fileContents);
			ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(new BufferedReader(new StringReader(fileContents))), ErrorCorrectingParser.RETURN_BLOCK_COMMENT | ErrorCorrectingParser.RETURN_LINE_COMMENT);
			File fileAST =  (File)parser.parse().value;
			ctx.put("blockComments", fileAST.getBlockComments());
			ctx.put("lineComments", fileAST.getLineComments());
			DefaultLineTracker tracker = new DefaultLineTracker();
			tracker.set(fileContents);
			ctx.put("lineTracker", tracker);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
		
	 
	public void genPart(Element part, Context ctx, TabbedWriter out) {
		ctx.invoke(genTop, part, ctx, out);
		ctx.invoke(genHead, part, ctx, out);
		ctx.invoke(genBody, part, ctx, out);
		out.println("</html>");
	}

	/********************* the remaining functions are in alphabetical order ************************/
	

	public void genBody(Element part, Context ctx, TabbedWriter out) {
		String docType = (String) ctx.get("docType");
		String stereoTypeName = (String) ctx.get("stereoTypeName");
		String partName = (String) ctx.get("partName");
		String firstPara = (String) ctx.get("firstPara");
		
		// the topmost detail, with introductory paragraph
		out.println("<body>");
		out.println("<h1 class=\"title topictitle1\">" + partName + " "
				+ docType + "</h1>");
		out.println("<div class=\"body\" id=\"body\">");
		out.println("<p class=\"shortdesc\">" + firstPara + "</p>");
		
		// the list
		out.println("<dl class=\"dl\" id=\"main\">");
				
		/*
		 *  stereotype detail
		 
		out.println("<dt class=\"dt dlterm\"><a name=\"typestereo\"</a>Type stereotype</dt>");

		if (stereoTypeName == null) {
			out.println("<dd> <p class=\"p\">None.</p>");
		} else {
			out.println("<dd> <p class=\"p\">" + stereoTypeName + "</p>");
		}
		out.println("<p class=\"p\"></p></dd></dt>");

		out.println("</div></dl>");
		*/
		ctx.invoke(genPartContent, part, ctx, out);
		out.println("<anchor id=\"related_links\"></anchor>");
		out.println("</body>");
	}

	public void genHead(Element part, Context ctx, TabbedWriter out) {
		
		String docType = (String) ctx.get("docType");
		String partName = (String) ctx.get("partName");
		String fullPartName = (String) ctx.get("fullPartName");
		String firstPara = (String) ctx.get("firstPara");		
		
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></meta>");
		out.println("<meta name=\"DC.Type\" content=\"topic\"></meta>");
		out.println("<meta name=\"DC.Title\" content=\"" + partName + " "
				+ docType + "\"></meta>");
		out.println("<meta name=\"abstract\" content=\"" + firstPara
				+ "\"></meta>");
		out.println("<meta name=\"description\" content=\"" + firstPara
				+ "\"></meta>");
		out.println("<meta name=\"DC.subject\" content=\"" + partName
				+ "\"></meta>");
		out.println("<meta name=\"keywords\" content=\"" + partName
				+ "\"></meta>");
		
		/** TODO:  must do something about the copyright and owner */
		out.println("<meta name=\"copyright\" content=\""
				+ "\"(C) Copyright 2011, 2012\" type=\"primary\"></meta>");
		out.println("<meta name=\"DC.Rights.Owner\" content=\""
				+ "\"(C) Copyright 2011, 2012\" type=\"primary\"></meta>");
		
		out.println("<meta name=\"DC.Format\" content=\"XHTML\"></meta>");
		out.println("<meta name=\"DC.Identifier\" content=\"" + fullPartName
				+ "\"></meta>");
		
		/** TODO:  must make language an external value */
		out.println("<meta name=\"DC.Language\" content=\"en-us\"></meta>");
		
		/** TODO: must calculate the folder level for the CSS file and then prepend "../" as many times */
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + org.eclipse.edt.gen.egldoc.Util.createRelativePath((String)ctx.get("packageName")) + "../css/commonltr.css\"></link>");
		out.println("<title>" + partName + " " + docType + "</title>");
		out.println("</head>");
	}

	
	/** TODO: must make each language code an external value */
	public void genTop(Element part, Context ctx, TabbedWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
				+ "http://www.w3.org/TR/html4/loose.dtd\">");
		out.println("<html xml:lang=\"en-us\" lang=\"en-us\">");
	}
	
	}
