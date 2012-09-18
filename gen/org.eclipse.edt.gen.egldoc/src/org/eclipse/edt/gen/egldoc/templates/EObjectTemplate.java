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
import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.jface.text.DefaultLineTracker;

public class EObjectTemplate extends EGLDocTemplate {
	

    /* preGen */	
	public void preGenDocContent(EObject eObject, Context ctx) {
		
		/* control flows to the method in the type-specific template; 
		 * not to the local preGenContent method.
		 */
		ctx.invoke(preGenContent, eObject, ctx);
	}	
	
	public void preGenContent(EObject eObject, Context ctx) {		
		IFile eglFile = (IFile)ctx.getAttribute(ctx.getClass(), "eglFile");
		try {
			String fileContents = Util.getFileContents(eglFile);
			ctx.put(Constants.FILECONTENTS, fileContents);
			ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(new BufferedReader(new StringReader(fileContents))), ErrorCorrectingParser.RETURN_BLOCK_COMMENT | ErrorCorrectingParser.RETURN_LINE_COMMENT);
			File fileAST =  (File)parser.parse().value;
			ctx.put(Constants.BLOCKCOMMENTS, fileAST.getBlockComments());
			ctx.put(Constants.LINECOMMENTS, fileAST.getLineComments());
			DefaultLineTracker tracker = new DefaultLineTracker();
			tracker.set(fileContents);
			ctx.put(Constants.LINETRACKER, tracker);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
	   String blockComment = org.eclipse.edt.gen.egldoc.Util.findBlockComment(ctx, org.eclipse.edt.gen.egldoc.Util.getLine(eObject));
		
		if (blockComment != null){
			Map<String, String> blockCommentMap = org.eclipse.edt.gen.egldoc.Util.parseCommentBlock(blockComment);
			ctx.put(Constants.FIRSTPARA, blockCommentMap.get(Constants.FIRSTPARA));
			ctx.put(Constants.POSTFIRSTPARA, blockCommentMap.get(Constants.POSTFIRSTPARA));
		}
	}

	
    /* gen */
	public void genDocContent(EObject eObject, Context ctx, TabbedWriter out) {
		
		/* 
		 * at this writing, the next invoked methods are local
		 */
		ctx.invoke(genTop, eObject, ctx, out);
		ctx.invoke(genHead, eObject, ctx, out);
		ctx.invoke(genBodyStart, eObject, ctx, out);		
		ctx.invoke(genPackage, eObject, ctx, out);
		
		/* control flows to the genContent method 
		 * in the type-specific template.
		 */
		ctx.invoke(genContent, eObject, ctx, out);
		
		/* 
		 * at this writing, the rest of the code is local
		 */		
		ctx.invoke(genBodyEnd, eObject, ctx, out);
		ctx.invoke(genLastComments, eObject, ctx, out);
		ctx.invoke(genBodyEnd, eObject, ctx, out);
		out.println("</html>");
	}	
	
	
	/********************* the remaining functions are in alphabetical order ************************/
	public void genBodyStart(EObject eObject, Context ctx, TabbedWriter out) {
	
		String docType = (String) ctx.get(Constants.DOCTYPE);
		String partName = (String) ctx.get(Constants.PARTNAME);
		String firstPara = (String) ctx.get(Constants.FIRSTPARA);
		
		// the topmost detail, with introductory paragraph
		out.println("<body>");
		out.println("<h1 class=\"title topictitle1\">" + partName + " "
				+ docType + "</h1>");
		out.println("<div class=\"body\" id=\"body\">");
		out.println("<p class=\"shortdesc\">" + firstPara + "</p>");
		
		// the list
		out.println("<dl class=\"dl\" id=\"main\">");	}
	
	public void genBodyEnd(EObject eObject, Context ctx, TabbedWriter out) {
	
		out.println("<p class=\"p\"></p></dd></dt>");
		out.println("</div></dl>");
		out.println("<anchor id=\"related_links\"></anchor>");
		out.println("</body>");
	}
	
		
	public void genDeclaration(EObject eObject, Context ctx, TabbedWriter out) {
		out.println(eObject.toString());
	}
	public void genDeclaration(String eObject, Context ctx, TabbedWriter out) {
		out.println(eObject);
	}

	public void genHead(EObject eObject, Context ctx, TabbedWriter out) {
		
		String docType = (String) ctx.get(Constants.DOCTYPE);
		String partName = (String) ctx.get(Constants.PARTNAME);
		String fullPartName = (String) ctx.get(Constants.FULLPARTNAME);
		String firstPara = (String) ctx.get(Constants.FIRSTPARA);		
		String packageName = (String)ctx.get(Constants.PACKAGENAME);
		
		
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
		out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\""); 
		
		if (packageName != null) {
		   out.print(org.eclipse.edt.gen.egldoc.Util.createRelativePath(packageName));
		}
		
		out.println("../css/commonltr.css\"></link>");
		out.println("<title>" + partName + " " + docType + "</title>");
		out.println("</head>");
	}

	public void genLastComments(EObject eObject, Context ctx, TabbedWriter out) {
		   String postFirstPara = (String) ctx.get("postFirstPara");
		   if(postFirstPara != null){
			   postFirstPara.replaceAll("\n", "<p class=\"p\"></p>");
			   out.println("<dt class=\"dt dlterm\"><a name=\"comments\"</a>Comments</dt>");		    
			   out.println("<dd class=\"dd\">" + postFirstPara + "</dd>");
		   } 
		}
	

	public void genPackage(EObject eObject, Context ctx, TabbedWriter out) {

		String packageName = (String) ctx.get(Constants.PACKAGENAME);

		
		out.println("<dt class=\"dt dlterm\"><a name=\"package\"</a>EGL package name</dt>");

			
		if (packageName == null) {
			out.println("<dd> <p class=\"p\">The default package is in use.</p>");
		} else {
			out.println("<dd> <p class=\"p\">" + packageName + "</p>");
		}
		out.println("<p class=\"p\"></p></dd></dt>");
	}
	
	/** TODO: must make each language code an external value */
	public void genTop(EObject eObject, Context ctx, TabbedWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
				+ "http://www.w3.org/TR/html4/loose.dtd\">");
		out.println("<html xml:lang=\"en-us\" lang=\"en-us\">");
	}
}
