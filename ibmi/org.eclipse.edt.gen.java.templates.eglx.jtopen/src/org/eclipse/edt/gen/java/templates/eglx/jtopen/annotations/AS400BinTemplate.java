package org.eclipse.edt.gen.java.templates.eglx.jtopen.annotations;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class AS400BinTemplate extends JavaTemplate{
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		ctx.invokeSuper(this, genJavaAnnotation, (Type)aType, ctx, out, annot, member);
	}
	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
	}
}
