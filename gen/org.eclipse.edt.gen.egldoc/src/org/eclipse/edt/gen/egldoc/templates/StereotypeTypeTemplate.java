package org.eclipse.edt.gen.egldoc.templates;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StereotypeType;

public class StereotypeTypeTemplate extends EGLDocTemplate {

	public void genClassContent(StereotypeType stereotypeType, Context ctx, TabbedWriter out) {
		//ctx.invoke(genExampleUse, stereotypeType, ctx, out);
		ctx.invoke(genTargets, (Part)stereotypeType, ctx, out);
		//ctx.invoke(genMemberAnnotations, (Part)stereotypeType, ctx, out);
		ctx.invoke(genDefaultSuperType, (Part)stereotypeType, ctx, out);
		ctx.invoke(genFields, (Part)stereotypeType, ctx, out);
		//ctx.invoke(genComments, stereotypeType, ctx, out);
		//ctx.invoke(genCompatibility, stereotypeType, ctx, out);
		
	}
	
	public void genDefaultSuperType(StereotypeType stereotypeType, Context ctx, TabbedWriter out){
		if(stereotypeType.getDefaultSuperType() != null){
			out.println("Default Super Type");
			ctx.invoke(genDeclaration, stereotypeType.getDefaultSuperType(), ctx, out);
		}
	}
	
	public void genMemberAnnotations(StereotypeType stereotypeType, Context ctx, TabbedWriter out){
		if(stereotypeType.getMemberAnnotations().size() > 0){
			out.println("Member Annotations");
		}
		//TODO: Temporary workaround?
		List<AnnotationType> memberAnnotations = stereotypeType.getMemberAnnotations();
		for (Iterator iterator = memberAnnotations.iterator(); iterator.hasNext();) {
			EObject annotationType = (EObject) iterator.next();
			if(annotationType instanceof AnnotationType){
				ctx.invoke(genMemberAnnotation, (Part)stereotypeType, ctx, out, annotationType);
			}
		}
//		for (AnnotationType memberAnnotation : stereotypeType.getMemberAnnotations()) {
//			ctx.invoke(genMemberAnnotation, (Part)stereotypeType, ctx, out, memberAnnotation);
//		}
	}
	
	public void genMemberAnnotation(StereotypeType stereotypeType, Context ctx, TabbedWriter out, AnnotationType arg){
		ctx.invoke(genDeclaration, (Part)arg, ctx, out);
	}
	
	
}
