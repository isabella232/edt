package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends EGLDocTemplate {

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
				+ field.getCaseSensitiveName() + "</span>");

		// display, element, and list (if appropriate)
		ArrayList<String> stringDetails;
		stringDetails = (ArrayList<String>) Util.getEGLSimpleType(field.getType().getTypeSignature());

		out.println("<a href=\"" + stringDetails.get(1) + "\">");
		out.println(stringDetails.get(0) + "</a>");

		if (stringDetails.get(2) != null) {
			out.println("<a href=\"" + stringDetails.get(2) + "\">");
			out.println(" [ ]</a>");
		}

		out.println("</span></dt>");
		out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!");

		// get field annotations such as ExternalName
		List<Annotation> theList = field.getAnnotations();

		/* EGL_LOCATION is always present, so test for > 1 ... */
		if (theList.size() > 1) {

			out.println("<dl class=\"dl\">");
			out.println("<dt class=\"dt pt dlterm\">");
			out.println("<dl class=\"dl\">");
			out.println("<dt class=\"dt pt dlterm\">");
			out.println("<dd class=\"dd pd\">" + "<p></p>");
			out.println("Field annotations");

			Iterator<Annotation> iterator = theList.iterator();		
			
			while (iterator.hasNext()) {

				// out.println(iterator.next().toString()); gives this:
				/*
				 * Instance of: eglx.lang.ExternalName eClass -> Instance of:
				 * org.eclipse.edt.mof.egl.AnnotationType -
				 * eglx.lang.ExternalName annotations -> [] metadata -> [] value
				 * -> hello Instance of: EGL_Location eClass -> Instance of:
				 * org.eclipse.edt.mof.EClass - EGL_Location annotations -> []
				 * metadata -> [] len -> 41 off -> 182 line -> 18
				 */

				
				ctx.invoke(genAnnotation, iterator.next(), ctx, out); 
				}

			out.println("</dd></dl>");

		}
		out.println("</dd>");
	}
}

	/*
	 * String typeDocLocation = field.getType().getTypeSignature(); String
	 * displayedTypeName = Util.getEGLSimpleType(typeDocLocation); out.println(
	 * "<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
	 * + field.getName() + "</span>"); out.println("<a href=" + typeDocLocation
	 * + ">" + displayedTypeName + "</a></span></dt>");
	 * out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!" +
	 * "</dd>");
	 */
	// String blockComment = Util.findBlockComment(ctx, Util.getLine(field));
	//
	// if(blockComment != null){
	// Map<String, String> blockCommentMap =
	// Util.parseCommentBlock(blockComment);
	// for (Iterator<String> iterator = blockCommentMap.keySet().iterator();
	// iterator.hasNext();) {
	// String key = (String) iterator.next();
	// out.print(key);
	// out.print(" ");
	// out.println((String)blockCommentMap.get(key));
	// }
	// }
