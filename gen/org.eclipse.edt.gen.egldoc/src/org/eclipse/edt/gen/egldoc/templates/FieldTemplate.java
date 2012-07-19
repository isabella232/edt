package org.eclipse.edt.gen.egldoc.templates;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		
		String lastTypeQualifier = field.getType().getTypeSignature();
		
		/** TODO.  Identify the url for the field-type doc*/
		String typeDocLocation = "unknownField";
		
		
		int lastPeriod = lastTypeQualifier.lastIndexOf('.');
		
		if (lastPeriod != 0) {
           lastTypeQualifier = lastTypeQualifier.substring(lastPeriod + 1);
		}
		
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">" + field.getName() + "</span>");
		out.println("<a href=" + typeDocLocation + ">" +  lastTypeQualifier + "</a></span></dt>");
        out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!" + "</dd>");
		
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
