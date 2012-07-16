package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;

public class EGLClassTemplate extends EGLDocTemplate {

	public void genClassContent(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstants, part, ctx, out);
		ctx.invoke(genFields, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
	}
	
	public void genConstants(EGLClass part, Context ctx, TabbedWriter out){
		List<ConstantField> constantFields = new ArrayList<ConstantField>();
		for (Field field : part.getFields()) {
			if(field instanceof ConstantField){
				constantFields.add((ConstantField)field);
			}
		}
		if(constantFields.size() > 0){
			out.println("Constants");
		}
		for (ConstantField constantField : constantFields) {
			ctx.invoke(genConstant, part, ctx, out, constantField);
		}
	}
	
	public void genConstant(EGLClass part, Context ctx, TabbedWriter out, ConstantField arg){
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out) {
		List<Field> nonConstantFields = new ArrayList<Field>();
		for (Field field : part.getFields()) {
			if(!(field instanceof ConstantField)){
				nonConstantFields.add((Field)field);
			}
		}
		
		if(nonConstantFields.size() > 0){
			out.println(part.getEClass().getName() + " fields");
		}
		for (Field field : nonConstantFields) {
			ctx.invoke(genField, part, ctx, out, field);
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out) {
		if(part.getFunctions().size() > 0){
			out.println("Functions");
		}
		for (Function function : part.getFunctions()) {
			ctx.invoke(genFunction, part, ctx, out, function);
		}
	}
	

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genDeclaration(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(part.getFullyQualifiedName());
	}

}
