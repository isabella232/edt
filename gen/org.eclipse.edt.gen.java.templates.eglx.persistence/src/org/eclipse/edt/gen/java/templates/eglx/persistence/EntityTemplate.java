package org.eclipse.edt.gen.java.templates.eglx.persistence;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;

public class EntityTemplate extends JavaTemplate {
	
	public void genSuperClass(EGLClass part, Context ctx, TabbedWriter out) {
		out.print("org.eclipse.edt.runtime.java.eglx.lang.AnyValue");
	}
}
