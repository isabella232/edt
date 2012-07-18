package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {


		String lastTypeQualifier = field.getType().getTypeSignature();
		int lastPeriod = lastTypeQualifier.lastIndexOf('.');
		
		if (lastPeriod != 0) {
           lastTypeQualifier = lastTypeQualifier.substring(lastPeriod + 1);
		}
		
		/** TODO.  Identify the url for the doc that's specified for the field type */
		String typeDocLocation = "unknown";
		
		
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">" + field.getName() + "</span>");
		out.println("<a href=" + typeDocLocation + ">" +  lastTypeQualifier + "</a></span></dt>");
        out.println("<dd class=\"dd pd\">" + "This is the purpose!" + "</dd>");
		
		// out.print(field.getName());
		// out.print(" ");
		// out.println(field.getType().getTypeSignature());
	}
}
