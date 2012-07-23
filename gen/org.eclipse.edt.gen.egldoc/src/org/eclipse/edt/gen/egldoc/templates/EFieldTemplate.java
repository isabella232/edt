package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class EFieldTemplate extends EGLDocTemplate {

	public void genDeclaration(EField field, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
				+ field.getName() + "</span>");
		
		// display, element, and list (if appropriate)
		ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(field.getTypeSignature());			
	
		out.println("<a href=\"" + stringDetails.get(1) + "\">");
		out.println(stringDetails.get(0) + "</a>");
		
		if (stringDetails.get(2) != null) {
             out.println("<a href=\"" + stringDetails.get(2) + "\">");
             out.println(" [ ]</a>");
		}     
		
		out.println("</span></dt>");
		out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!"
				+ "</dd>");
	}
}
