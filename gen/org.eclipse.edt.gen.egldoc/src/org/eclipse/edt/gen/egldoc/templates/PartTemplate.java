package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Part;

public class PartTemplate extends EGLDocTemplate {

	public void genPartContent(Part part, Context ctx, TabbedWriter out) {
	
		ctx.invoke(genPackage, part, ctx, out);
		
		ctx.invoke(genClassContent, part, ctx, out);

//		ctx.invoke(genComments, part, ctx, out);
//		ctx.invoke(genCompatibility, part, ctx, out);
	}
	
	public void genClassContent(Part part, Context ctx, TabbedWriter out){
		
	}
	
	public void genPackage(Part part, Context ctx, TabbedWriter out){
		out.println("Package");
		out.println(part.getPackageName());
	}
}
