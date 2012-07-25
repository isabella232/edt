package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;

public class AnnotationTemplate extends EGLDocTemplate {


	
	public void genAnnotation(Annotation annotation, Context ctx, TabbedWriter out) {
				
		String annotationName = annotation.getEClass().getETypeSignature();

		// gives display value, element type, and list type (if appropriate)
		ArrayList<String>stringDetails = (ArrayList<String>) Util.getEGLSimpleType(annotationName);
		if (stringDetails.get(0).equals("EGL_Location")) {
			return;
		}
		ctx.invoke(genDeclaration, annotation, ctx, out, stringDetails);
        	
		// "eglx.lang.ExternalName EGL_Location":
		// out.println(iterator.next().getEClass().getETypeSignature());
		// annotationName =
		// iterator.next().getEClass().getETypeSignature();

		// display, element, and list (if appropriate); but assume no
		// list
		// stringDetails = (ArrayList<String>)
		// Util.getEGLSimpleType(annotationName);
		// out.println("<a href=\"" + stringDetails.get(1) + "\">");
		// out.println(stringDetails.get(0) + "</a>");

		// "hello 41":
		// out.println(iterator.next().getValue().toString());		
	}

		
	public void genDeclaration(Annotation annotation, Context ctx, TabbedWriter out, ArrayList<String> stringList) {

		
		out.println("<a href=\"" + stringList.get(1) + "\">");
		out.println(stringList.get(0) + "</a>");
		
		if (stringList.get(2) != null) {
			out.println("<a href=\"" + stringList.get(2) + "\">");
			out.println(" [ ]</a>");
		}		
	}
}
