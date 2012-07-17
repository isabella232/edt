package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {

		// name and type.  revise type to remove the initial "E" from type names such as "EString"
		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">" + field.getName() + "</span>");
		out.println("<a href=eglx.lang.EString>" + field.getType().getTypeSignature() + "</a></span></dt>");
        out.println("<dd class=\"dd pd\">" + "This is the purpose!" + "</dd>");
		
		// out.print(field.getName());
		// out.print(" ");
		// out.println(field.getType().getTypeSignature());
	}
}
