package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.EObject;

public class EObjectTemplate extends EGLDocTemplate {
	
	public void genDeclaration(EObject field, Context ctx, TabbedWriter out) {
		out.println(field.toString());
	}
	public void genDeclaration(String field, Context ctx, TabbedWriter out) {
		out.println(field);
	}
	
}
