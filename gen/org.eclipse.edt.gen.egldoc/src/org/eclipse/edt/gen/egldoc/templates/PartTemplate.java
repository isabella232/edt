package org.eclipse.edt.gen.egldoc.templates;

import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.Stereotype;

public class PartTemplate extends EGLDocTemplate {

	public void preGenContent(Part part, Context ctx) {
		ctx.put(Constants.PARTNAME, part.getCaseSensitiveName());
		ctx.put(Constants.PACKAGENAME, part.getCaseSensitivePackageName());
		ctx.put(Constants.FULLPARTNAME, part.getFullyQualifiedName());
		
		ctx.invokeSuper(this, preGenContent, part, ctx);
	}
	
	
	public void genContent(Part part, Context ctx, TabbedWriter out) {

		ctx.invoke(genStereotypeName, part, ctx, out);
		// loops back to the current method:  ctx.invokeSuper(this, genContent, part, ctx, out);

	}

	public void genStereotypeName(Part part, Context ctx, TabbedWriter out) {

		// List<Stereotype> stereotypeType = part.getStereotypes();
		Stereotype stereotypeType = part.getStereotype();
        // String stereoString = stereotypeType.toString();
        // int firstPeriod = stereoString.indexOf(':');
        // stereoString = stereoString.substring(firstPeriod);
        
		out.println("<dt class=\"dt dlterm\"><a name=\"typestereo\"</a>Type stereotype</dt>");

		/*
		 * List<Annotation> theAnnoList = part.getAnnotations();
		 * Iterator<Annotation> iterator = theAnnoList.iterator(); while (
		 * iterator.hasNext() ) { Annotation annotationType = iterator.next();
		 * 
		 * 
		 * String theAnnotationName = annotationType.toString();
		 * out.println(theAnnotationName);
		 * 
		 * if(annotationType instanceof AnnotationType){
		 * ctx.invoke(genMemberAnnotation, (Part)stereotypeType, ctx, out,
		 * annotationType); } }
		 */

		if (stereotypeType == null) {
			out.println("<dd> <p class=\"p\">None.</p>");
		} else {
			String stereoString = part.getStereotype().getEClass().getCaseSensitiveName();
			out.println("<dd> <p class=\"p\">" + stereoString + "</p>");
		}
		out.println("<p class=\"p\"></p></dd></dt>");
	}

}
