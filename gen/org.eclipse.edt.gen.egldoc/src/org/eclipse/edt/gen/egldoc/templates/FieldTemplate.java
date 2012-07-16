package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		out.print(field.getName());
		out.print(" ");
		out.println(field.getType().getTypeSignature());
	}
}
