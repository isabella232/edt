package org.eclipse.edt.gen.java.templates.eglx.jtopen.annotations;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class AS400TimestampTemplate extends JavaTemplate implements Constants{
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		ctx.invokeSuper(this, genJavaAnnotation, (Type)aType, ctx, out, annot, member);
	}
	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		boolean addComma = false;
		Expression ibmiFormat = (Expression)annot.getValue(subKey_ibmiFormat);
		if(ibmiFormat != null){
			out.print("ibmiFormat=");
			ctx.invoke(genExpression, ibmiFormat, ctx, out);
			addComma = true;
		}
		if(addComma){
			out.print(", ");
		}
		out.print("eglPattern=\"");
		ctx.invoke(genPattern, member.getType(), ctx, out, annot);
		out.print("\"");
	}
}
