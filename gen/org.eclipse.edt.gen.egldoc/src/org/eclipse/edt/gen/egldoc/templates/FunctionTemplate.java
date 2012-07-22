package org.eclipse.edt.gen.egldoc.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;

public class FunctionTemplate extends EGLDocTemplate {

	public void genSyntax(Function function, Context ctx, TabbedWriter out) {
		// process the function

		List<FunctionParameter> parameters = function.getParameters();
		String blockComment = Util.findBlockComment(ctx, Util.getLine(function));		
		String functionName = function.getName();
		String functionFirstPara = "";

		/* from svihovec
		if (blockComment != null) {
			Map<String, String> blockCommentMap = Util
					.parseCommentBlock(blockComment);
			for (Iterator<String> iterator = blockCommentMap.keySet()
					.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				out.print(key);
				out.print(" ");
				out.println((String) blockCommentMap.get(key));
			}
		}
		*/

		if (blockComment != null) {
					
			Map<String, String> blockCommentMap = Util.parseCommentBlock(blockComment);
			
            Iterator<String> iterator = blockCommentMap.keySet().iterator();			
            
			while (iterator.hasNext()){
				String key = (String) iterator.next();
				if (key == "firstPara") {
				    functionFirstPara = blockCommentMap.get(key);					
				}
		
							
				
			}
			
            
			
		    // functionFirstPara = blockComment;
            
		}
		
		/*		
		out.println("<tr id=\"");
		out.println(functionName + "\">");
		
		if (true) {
			out.println("<a href=\"#" + functionName + "\">" + functionName + "</a>" + "class=\"row\">");   
		}
		*/
		
		out.println("<td class=\"entry\" valign=\"top\">");
		
		
		out.println("<span class=\"ph synph\"><span class=\"keyword kwd\">"); 
		
		// TODO.  Create a new function topic under some circumstances; and reference it as appropriate.
		if (true) {
		   out.println("<a href=\"#" + functionName + "\">" + functionName + "</a>" + " (");
		}

		int numberOfParmsLeft = parameters.size() - 1;

		for (FunctionParameter functionParameter : parameters) {
			String parmName = functionParameter.getName().trim();
			String parmType = functionParameter.getType().getTypeSignature()
					.toString();

			/** TODO. Identify the url for the field-type doc */
			String typeDocLocation = "unknownParmType";

			int lastPeriod = parmType.lastIndexOf('.');

			if (lastPeriod != 0) {
				parmType = parmType.substring(lastPeriod + 1);
			}

			out.println(parmName + " <a href=\"" + typeDocLocation + "\">"
					+ parmType + "</a>");

			if (numberOfParmsLeft > 0) {

				out.println(", ");
				numberOfParmsLeft--;
			}
		}

		
		out.println(")</span><p class=\"p\"></p>");		
		out.println(functionFirstPara + "</td></tr>");

		/*
		 * from svihovec new stuff
		 * 
		 * 
		 * out.println(function.getId()); out.println("Syntax");
		 * List<FunctionParameter> parameters = function.getParameters(); for
		 * (FunctionParameter functionParameter : parameters) {
		 * out.println(functionParameter.getName()); }
		 */

	}

	public void genDeclaration(Function function,
			org.eclipse.edt.gen.egldoc.Context ctx, TabbedWriter out) {

		ctx.invoke(genSyntax, function, ctx, out);

		// ctx.invoke(genFunctionExampleUse, function, ctx, out);
		// ctx.invoke(genComments()
		// ctx.invoke(genExample()
		// ctx.invoke(genCompatibility))
	}
}
