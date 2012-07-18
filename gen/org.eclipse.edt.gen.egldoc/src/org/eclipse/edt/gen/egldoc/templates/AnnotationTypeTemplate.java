package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Part;

public class AnnotationTypeTemplate extends EGLDocTemplate {

	public void genClassContent(AnnotationType annotation, Context ctx, TabbedWriter out) {
		//ctx.invoke(genExampleUse, annotation, ctx, out);
		ctx.invoke(genTargets, (Part)annotation, ctx, out);
		ctx.invoke(genFields, (Part)annotation, ctx, out);
		//ctx.invoke(genComments, annotation, ctx, out);
		//ctx.invoke(genCompatibility, annotation, ctx, out);
		
	}
	public void genTargets(AnnotationType annotationType, Context ctx, TabbedWriter out){
		if(annotationType.getTargets().size() > 0){
			out.println("Targets");
		}
		for (ElementKind target : annotationType.getTargets()) {
			ctx.invoke(genTarget, (Part)annotationType, ctx, out, target);
		}
	}
	
	public void genTarget(AnnotationType annotationType, Context ctx, TabbedWriter out, ElementKind arg){
		ctx.invoke(genDeclaration, arg, ctx, out);
	}
	
	public void genFields(AnnotationType annotationType, Context ctx, TabbedWriter out) {
	    String fieldContainerType = (String) ctx.get("fieldContainerType");

		out.println ("<dt class=\"dt dlterm\"><a name=\"annofields\"></a>" + fieldContainerType + " fields</dt>");
		
		if(annotationType.getEFields().size() > 0){
			// out.println(annotationType.getEClass().getName() + " fields");
			out.println ("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");
		}
		else {
			out.println ("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");			
		}
		
		for (EField efield : annotationType.getEFields()) {
			ctx.invoke(genField, (Part)annotationType, ctx, out, efield);
		}
	}

	public void genField(AnnotationType annotationType, Context ctx, TabbedWriter out, EField arg) {
		
		
		
		ctx.invoke(genDeclaration, arg, ctx, out);
	}
	
	public void genDeclaration(AnnotationType annotationType, Context ctx, TabbedWriter out) {
		out.println("in AssociationTypeTemplate.genDeclaration " + annotationType.getFullyQualifiedName());
		
		
		
		
		
		
		
		
	}
}
