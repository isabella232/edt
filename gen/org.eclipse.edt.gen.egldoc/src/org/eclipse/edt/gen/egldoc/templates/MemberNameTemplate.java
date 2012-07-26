package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.MemberName;

public class MemberNameTemplate extends EGLDocTemplate {
	
	public void genDeclaration(MemberName field, Context ctx, TabbedWriter out) {
		out.println(field.getNamedElement().getName());
		
	}
	
}
