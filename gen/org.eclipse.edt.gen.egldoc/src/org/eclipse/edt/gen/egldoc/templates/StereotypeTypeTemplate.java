package org.eclipse.edt.gen.egldoc.templates;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.EClassProxy;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StereotypeType;

public class StereotypeTypeTemplate extends EGLDocTemplate {

	public void preGenContent(StereotypeType stereotypeType, Context ctx) {

		String docType = (String) ctx.get(Constants.DOCTYPE);

		if (docType == null) {
			ctx.put(Constants.DOCTYPE, "stereotype type");
			ctx.put(Constants.FIELDCONTAINERTYPE, "Stereotype");
		}

		ctx.invokeSuper(this, preGenContent, (EClass) stereotypeType, ctx);
	}

	public void genContent(StereotypeType stereotypeType, Context ctx, TabbedWriter out) {

		// ctx.invoke(genExampleUse, stereotypeType, ctx, out);

		ctx.invoke(genTargets, (EClass) stereotypeType, ctx, out);
		// no:  ctx.invoke(genMemberAnnotations, (Part) stereotypeType, ctx, out);
		ctx.invoke(genDefaultSuperType, (EClass) stereotypeType, ctx, out);
		ctx.invoke(genFields, (EClass) stereotypeType, ctx, out);

		// ctx.invoke(genCompatibility, stereotypeType, ctx, out);

	}

	public void genDefaultSuperType(StereotypeType stereotypeType, Context ctx, TabbedWriter out) {
		if (stereotypeType.getDefaultSuperType() != null) {
			out.println("Default Super Type");
			// out.println(stereotypeType.getDefaultSuperType().getMofSerializationKey());
			ctx.invoke(genMofSerializable, stereotypeType.getDefaultSuperType(), ctx, out);
		}
	}

	/*  As of 1 September 2012, the language is not using member annotations
	public void genMemberAnnotations(StereotypeType stereotypeType, Context ctx, TabbedWriter out) {
		
		int numberOfAnnos = stereotypeType.getMemberAnnotations().size();
		
		if (numberOfAnnos > 0) {
			int count = 0;
			
			out.println("<dt class=\"dt dlterm\"><a name=\"memberannos\"</a>Member annotations</dt>");
			out.println("<dd class=\"dd\"><p class=\"p\">");

			// TODO: What is supposed to happen here?
			List<AnnotationType> memberAnnotations = stereotypeType.getMemberAnnotations();
			for (Iterator<AnnotationType> iterator = memberAnnotations.iterator(); iterator.hasNext();) {
				EObject annotationType = (EObject) iterator.next();
				if (annotationType instanceof ProxyEObject) {
					
					if (count > 0 && count < numberOfAnnos){
						out.print(", ");						
					}	
					count++;
					
					ctx.invoke(genMemberAnnotation, (EClass) stereotypeType, ctx, out, annotationType);
				}
			}
			out.flush();
		}
		// for (AnnotationType memberAnnotation :
		// stereotypeType.getMemberAnnotations()) {
		// ctx.invoke(genMemberAnnotation, (Part)stereotypeType, ctx, out,
		// memberAnnotation);
		// }
	}
    

	// TODO:  What is supposed to happen here?
	public void genMemberAnnotation(StereotypeType stereotypeType, Context ctx, TabbedWriter out, ProxyEObject arg) {
		out.print(arg.toString());
	}
    */
}

/*
 * public void genTarget(StereotypeType stereotypeType, Context ctx,
 * TabbedWriter out, ElementKind arg){
 * out.println(stereotypeType.getFullyQualifiedName()); }
 */

