package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Type;

public class AnyDelegateTypeTemplate extends JavaTemplate {

	public void genRuntimeClassTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("org.eclipse.edt.javart.Delegate");
		out.print(".class");
	}

}
