package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ElementKind;

public class ElementKindTemplate extends EGLDocTemplate {

	public void genDeclaration(ElementKind elementKind, Context context, TabbedWriter out){
		out.println(elementKind.getDeclaringClass().getSimpleName());
	}
}
