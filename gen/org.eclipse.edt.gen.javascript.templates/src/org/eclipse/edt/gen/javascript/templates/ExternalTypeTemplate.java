/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate.TypeNameKind;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ExternalTypeTemplate extends JavaScriptTemplate {

	public void preGenClassBody(ExternalType part, Context ctx) {}

	public void genPart(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genAccessor(ExternalType part, Context ctx, TabbedWriter out) {
		ctx.invoke(genPartName, part, ctx, out);
	}

	public void genRuntimeTypeName(ExternalType part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else{
			Annotation annotation = part.getAnnotation(Constants.Annotation_JavaScriptObject);
			if(annotation != null){
				String packageName = (String) annotation.getValue(InternUtil.intern("relativePath"));
				if(packageName != null && !(packageName.isEmpty())){
					packageName = packageName.replace('/', '.');
					packageName = packageName.replace('\\', '.');
					packageName += ".";
				}else{
					packageName = "";
				}
				String partName = (String) annotation.getValue(InternUtil.intern("externalName"));
				if(partName == null || partName.isEmpty()){
					partName = part.getName();
				}
				out.print(eglnamespace + packageName.toLowerCase() + partName);
			}
		}
	}

	public void genQualifier(ExternalType part, Context ctx, TabbedWriter out, NamedElement arg) {
		// out.print("this.");
	}
	
	public Boolean supportsConversion(ExternalType part, Context ctx) {
		return Boolean.FALSE;
	}
}
