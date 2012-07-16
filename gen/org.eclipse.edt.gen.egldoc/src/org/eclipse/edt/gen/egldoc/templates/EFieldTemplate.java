package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class EFieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(EField field, Context ctx, TabbedWriter out) {
		out.print(field.getName());
		out.print(" ");
		out.println(field.getTypeSignature());
	}
}
