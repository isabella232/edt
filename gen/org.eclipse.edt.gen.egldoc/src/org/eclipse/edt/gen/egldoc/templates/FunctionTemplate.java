package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Type;

public class FunctionTemplate extends EGLDocTemplate {

	public void genSyntax(Function function, Context ctx, TabbedWriter out) {
		// process the function

		List<FunctionParameter> parameters = function.getParameters();
		String blockComment = Util.findBlockComment(ctx, Util.getLine(function));		
		String functionName = function.getCaseSensitiveName();
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
			String parmName = functionParameter.getCaseSensitiveName().trim();
			String parmType = functionParameter.getType().getTypeSignature()
					.toString();

			
			// display, element, and list (if appropriate)
			ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(functionParameter.getType().getTypeSignature());			
		
			out.println(parmName + "<a href=\"" + stringDetails.get(1) + "\">");
			out.println(stringDetails.get(0) + "</a>");
			
			if (stringDetails.get(2) != null) {
	             out.println("<a href=\"" + stringDetails.get(2) + "\">");
	             out.println(" [ ]</a>");
			}     
	/*		
			out.println("</span></dt>");
			out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!"
					+ "</dd>");
	*/		
			
			
			
			
			
			/*
			String typeDocLocation = "unknownParmType";

			int lastPeriod = parmType.lastIndexOf('.');

			if (lastPeriod != 0) {
				parmType = parmType.substring(lastPeriod + 1);
			}

			out.println(parmName + " <a href=\"" + typeDocLocation + "\">"
					+ parmType + "</a>");
*/
			if (numberOfParmsLeft > 0) {

				out.println(", ");
				numberOfParmsLeft--;
			}
			
		}
		out.println(")");
	
		Type theReturnType = function.getReturnType();		
		
		if (theReturnType != null){
			
			// String theReturnSpec = "something";
			// gives the long name for EList:  
			//           String theReturnSpec = theReturnType.getTypeSignature().toString();
			//    or     String theReturnSpec = theReturnType.getClassifier().getTypeSignature();
			// the next gives this:
			/*
			   Instance of: org.eclipse.edt.mof.EClass - org.eclipse.edt.mof.egl.ArrayType eClass 
			-> Instance of: org.eclipse.edt.mof.EClass - org.eclipse.edt.mof.EClass metadata 
			-> [] name -> ArrayType packageName -> org.eclipse.edt.mof.egl eTypeParameters 
			-> [] superTypes 
			-> [Instance of: org.eclipse.edt.mof.EClass - org.eclipse.edt.mof.egl.GenericType] isAbstract 
			-> false isInterface 
			-> false eFields 
			-> [initialSize : org.eclipse.edt.mof.egl.Expression, elementsNullable : org.eclipse.edt.mof.EBoolean] eFunctions 
			-> [Instance of: org.eclipse.edt.mof.EFunction, 
			Instance of: org.eclipse.edt.mof.EFunction, Instance of: org.eclipse.edt.mof.EFunction] 
			*/
			// String theReturnSpec = theReturnType.getEClass().toString();
			// the next returns this:  org.eclipse.edt.mof.egl.impl.ArrayTypeImpl 
			// String theReturnSpec = theReturnType.getClass().getCanonicalName().toString();
			// the next returns ArrayTypeImpl
			// String theReturnSpec = theReturnType.getClass().getSimpleName();
			// the next gives this:  egl:eglx.lang.elist 
			// String theReturnSpec = theReturnType.getMofSerializationKey();
			

			// display, element, and list (if appropriate)
			ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(theReturnType.getTypeSignature());			
			out.println(" returns <a href=\"" + stringDetails.get(1) + "\">");
			out.println(stringDetails.get(0) + "</a>");
			
			if (stringDetails.get(2) != null) {
				 //  <a href="eglx.lang.EInt">EInt</a> <a href="eglx.lang.EList"> [ ]</a>
                 out.println("<a href=\"" + stringDetails.get(2) + "\">");
                 out.println(" [ ]</a>");
			}     
		}
     	
		
		out.println("</span><p class=\"p\"></p>");		
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

		// out.println("in FunctionTemplate.genDeclaration");
		
		ctx.invoke(genSyntax, function, ctx, out);

		// ctx.invoke(genFunctionExampleUse, function, ctx, out);
		// ctx.invoke(genComments()
		// ctx.invoke(genExample()
		// ctx.invoke(genCompatibility))
	}
}
