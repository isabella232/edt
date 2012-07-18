package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class EFieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(EField field, Context ctx, TabbedWriter out) {
		// out.print(field.getName());
		// out.print(" ");
		// out.println(field.getTypeSignature());
		

		String lastTypeQualifier = field.getTypeSignature();
		
		/** TODO.  Identify the url for the field-type doc*/
		String typeDocLocation = "unknownEField";
				
		int lastPeriod = lastTypeQualifier.lastIndexOf('.');
		
		if (lastPeriod != 0) {
           lastTypeQualifier = lastTypeQualifier.substring(lastPeriod + 1);
		}
		
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">" + field.getName() + "</span>");
		out.println("<a href=" + typeDocLocation + ">" +  lastTypeQualifier + "</a></span></dt>");
        out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!" + "</dd>");

	
	
	}
}
