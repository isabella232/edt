package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
				+ field.getName() + "</span>");
		
		// display, element, and list (if appropriate)
		ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(field.getType().getTypeSignature());			
	
		out.println("<a href=\"" + stringDetails.get(1) + "\">");
		out.println(stringDetails.get(0) + "</a>");
		
		if (stringDetails.get(2) != null) {
             out.println("<a href=\"" + stringDetails.get(2) + "\">");
             out.println(" [ ]</a>");
		}     
		
		out.println("</span></dt>");
		out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!"
				+ "</dd>");
		/*
		String typeDocLocation = field.getType().getTypeSignature(); 		
	    String displayedTypeName = Util.getEGLSimpleType(typeDocLocation);			
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">" + field.getName() + "</span>");
		out.println("<a href=" + typeDocLocation + ">" +  displayedTypeName + "</a></span></dt>");
        out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!" + "</dd>");
		*/
//		String blockComment = Util.findBlockComment(ctx, Util.getLine(field));
//		
//		if(blockComment != null){
//			Map<String, String> blockCommentMap = Util.parseCommentBlock(blockComment);
//			for (Iterator<String> iterator = blockCommentMap.keySet().iterator(); iterator.hasNext();) {
//				String key = (String) iterator.next();
//				out.print(key);
//				out.print(" ");
//				out.println((String)blockCommentMap.get(key));
//			}
//		}
	}
}
