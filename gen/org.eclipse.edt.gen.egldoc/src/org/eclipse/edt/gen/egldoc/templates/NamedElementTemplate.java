package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends EGLDocTemplate {
	
	public void genDeclaration(NamedElement field, Context ctx, TabbedWriter out) {
		out.println(field.getCaseSensitiveName());
	}
	public void genDeclaration(String field, Context ctx, TabbedWriter out) {
		out.println(field);
	}
	
}
