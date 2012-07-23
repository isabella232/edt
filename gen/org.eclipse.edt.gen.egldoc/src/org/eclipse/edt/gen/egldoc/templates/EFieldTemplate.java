package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class EFieldTemplate extends EGLDocTemplate {

	public void genDeclaration(EField field, Context ctx, TabbedWriter out) {

		/** TODO. Identify the url for the field-type doc */
		String typeDocLocation = field.getTypeSignature();
		String displayedTypeName = Util.getEGLSimpleType(typeDocLocation);

		int lastPeriod = displayedTypeName.lastIndexOf('.');

		if (lastPeriod != 0) {
			displayedTypeName = displayedTypeName.substring(lastPeriod + 1);
		}

		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
				+ field.getName() + "</span>");
		out.println("<a href=" + typeDocLocation + ">" + displayedTypeName
				+ "</a></span></dt>");
		out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!"
				+ "</dd>");
	}
}
