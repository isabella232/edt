package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Element;


public class EClassTemplate extends EGLDocTemplate {

	public void preGenContent(EClass eClass, Context ctx) {
		String theName = eClass.getName();
		String packageName = eClass.getPackageName();
		String fullPartName = theName + "." + packageName;
		
		ctx.put(Constants.PARTNAME, theName);
		ctx.put(Constants.PACKAGENAME, packageName);
		ctx.put(Constants.FULLPARTNAME, fullPartName);
		ctx.invokeSuper(this, preGenContent, eClass, ctx);
		// System.out.println("in EClass template, theName = " + theName);
		
	}

	/*
	public void genClassContent(EClass eClass, Context ctx, TabbedWriter out) {

		ctx.invoke(genStereotypeName, eClass, ctx, out);

	}
    */

}
