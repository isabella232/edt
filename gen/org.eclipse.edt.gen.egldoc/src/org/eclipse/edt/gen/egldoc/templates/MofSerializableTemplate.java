package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class MofSerializableTemplate extends EGLDocTemplate {

	public void genDeclaration(MofSerializable mofSerializable, Context ctx, TabbedWriter out) {
		out.println(mofSerializable.getEClass().getETypeSignature());
	}
}
