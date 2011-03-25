package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Interface;

public class InterfaceTemplate extends ClassTemplate {

	public void validateClassBody(Interface part, Context ctx, Object... args) {}

	public void genPart(Interface part, Context ctx, TabbedWriter out, Object... args) {}

	public void genClassBody(Interface part, Context ctx, TabbedWriter out, Object... args) {}

	public void genClassHeader(Interface part, Context ctx, TabbedWriter out, Object... args) {}

}
