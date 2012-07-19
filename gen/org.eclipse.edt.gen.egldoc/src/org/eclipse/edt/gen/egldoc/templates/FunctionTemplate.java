package org.eclipse.edt.gen.egldoc.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;

public class FunctionTemplate extends EGLDocTemplate {

	public void genSyntax(Function function, Context ctx, TabbedWriter out) {
		// process the function
		out.println(function.getId());
		out.println("Syntax");
		List<FunctionParameter> parameters = function.getParameters();
		for (FunctionParameter functionParameter : parameters) {
			out.println(functionParameter.getName());
		}
		
		String blockComment = Util.findBlockComment(ctx, Util.getLine(function));
		
		if(blockComment != null){
			Map<String, String> blockCommentMap = Util.parseCommentBlock(blockComment);
			for (Iterator<String> iterator = blockCommentMap.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				out.print(key);
				out.print(" ");
				out.println((String)blockCommentMap.get(key));
			}
		}
	}

	public void genDeclaration(Function function, org.eclipse.edt.gen.egldoc.Context ctx, TabbedWriter out) {
		ctx.invoke(genSyntax, function, ctx, out);
		//ctx.invoke(genFunctionExampleUse, function, ctx, out);
		//ctx.invoke(genComments()
		//ctx.invoke(genExample()
		//ctx.invoke(genCompatibility))
	}
}
