package org.eclipse.edt.gen.java.templates.eglx.jtopen.annotations;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class AS400DecTemplate extends JavaTemplate implements Constants{
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		ctx.invokeSuper(this, genJavaAnnotation, (Type)aType, ctx, out, annot, member);
	}
	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		out.print("decimals=");
		ctx.invoke(genDecimals, member.getType(), ctx, out, annot);
		out.print(", length=");
		ctx.invoke(genLength, member.getType(), ctx, out, annot);
	}

}
