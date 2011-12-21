package org.eclipse.edt.gen.java.templates.eglx.jtopen.annotations;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class AS400ArrayTemplate extends JavaTemplate implements Constants{
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		ctx.invokeSuper(this, genJavaAnnotation, (Type)aType, ctx, out, annot, member);
		Annotation elementTypeAnnotation = (Annotation)annot.getValue(subKey_elementTypeAS400Annotation);
		if(elementTypeAnnotation != null){
			ctx.invoke(genAnnotation, elementTypeAnnotation.getEClass(), ctx, out, elementTypeAnnotation, member);
		}
	}
	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		if(annot != null){
			Integer elementCount = (Integer)annot.getValue(subKey_elementCount);
			if(elementCount != null){
				out.print("elementCount=");
				out.print(elementCount.toString());
			}
		}
	}
}
