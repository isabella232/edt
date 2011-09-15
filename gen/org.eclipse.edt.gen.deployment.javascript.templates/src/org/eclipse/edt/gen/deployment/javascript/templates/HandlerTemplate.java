package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.egl.Handler;

public class HandlerTemplate extends JavaScriptTemplate {	
	public void genDependentPart(Handler handler, Context ctx, LinkedHashSet dependentFiles) {
		ctx.invoke(genOutputFileName, handler, ctx, dependentFiles);
		ctx.invoke(genDependentParts, handler, ctx, dependentFiles);
	}
}
