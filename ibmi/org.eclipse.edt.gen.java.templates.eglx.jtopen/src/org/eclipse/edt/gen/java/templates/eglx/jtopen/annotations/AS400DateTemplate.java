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

public class AS400DateTemplate extends JavaTemplate implements Constants{
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		ctx.invokeSuper(this, genJavaAnnotation, (Type)aType, ctx, out, annot, member);
	}
	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		Expression ibmiFormat = (Expression)annot.getValue(subKey_ibmiFormat);
		boolean addComma = false;
		if(ibmiFormat != null){
			out.print("ibmiFormat=");
			ctx.invoke(genExpression, ibmiFormat, ctx, out);
			addComma = true;
		}
		String ibmiSeperator = (String)annot.getValue(subKey_ibmiSeperator);
		if(ibmiSeperator == null){
			if(addComma){
				out.print(", ");
			}
			out.print("ibmiSeperator=\"null\"");
		}
		else if(!ibmiSeperator.isEmpty()){
			if(addComma){
				out.print(", ");
			}
			out.print("ibmiSeperator=\"");
			ctx.invoke(genExpression, ibmiFormat, ctx, out);
			out.print("\"");
		}
	}
}
