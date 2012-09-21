package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;

public class ConstantFieldTemplate extends EGLDocTemplate {
	
	public void genDeclaration(ConstantField field, Context ctx, TabbedWriter out) {
		out.println(field.getCaseSensitiveName());
	}
	
}
