package org.eclipse.edt.gen.egldoc.templates;

import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Stereotype;

public class PartTemplate extends EGLDocTemplate {

	public void preGenPart(Part part, Context ctx) {
		ctx.put("partName", part.getName());
		ctx.put("packageName", part.getPackageName());
		ctx.put("fullPartName", part.getFullyQualifiedName());
		ctx.invokeSuper(this, preGenPart, part, ctx);
	}
	
	public void genPartContent(Part part, Context ctx, TabbedWriter out) {

		ctx.invoke(genPackage, part, ctx, out);
        
		ctx.invoke(genClassContent, part, ctx, out);
		
		ctx.invoke(genLastComments, part, ctx, out);
	
		// ctx.invoke(genCompatibility, part, ctx, out);
	}

	public void genClassContent(Part part, Context ctx, TabbedWriter out) {

		ctx.invoke(genStereotypeName, part, ctx, out);

	}

	public void genLastComments(Element part, Context ctx, TabbedWriter out) {
		   String theRest = (String) ctx.get("postFirstPara");
		   if(theRest != null){
			   theRest.replaceAll("\n", "<p class=\"p\"></p>");
		   } 
		   out.println("<dt class=\"dt dlterm\"><a name=\"comments\"</a>Comments</dt>");
		   
		  // <dd class="dd">This statement is a comment.</dd>
		}
	public void genPackage(Part part, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt dlterm\"><a name=\"package\"</a>EGL package name</dt>");

		if (part.getPackageName() == "") {
			out.println("<dd> <p class=\"p\">The default package is in use.</p>");
		} else {
			out.println("<dd> <p class=\"p\">" + part.getPackageName() + "</p>");
		}
		out.println("<p class=\"p\"></p></dd></dt>");
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
			String stereoString = part.getStereotype().getEClass().getName();
			out.println("<dd> <p class=\"p\">" + stereoString + "</p>");
		}
		out.println("<p class=\"p\"></p></dd></dt>");
	}

}
