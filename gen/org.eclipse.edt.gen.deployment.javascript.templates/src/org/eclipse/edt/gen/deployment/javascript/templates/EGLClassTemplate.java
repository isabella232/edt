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
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class EGLClassTemplate extends JavaScriptTemplate {
	public void genDependentPart(EGLClass part, Context ctx, TabbedWriter out, Boolean addComma) {}
	public void genDependentParts(EGLClass part, Context ctx, TabbedWriter out, Boolean addComma) {		
		List<Type> processedParts = (List<Type>)ctx.get(genDependentParts);
		if(processedParts == null){
			processedParts = new ArrayList<Type>();
			ctx.put(genDependentParts, processedParts);
		}
		processedParts.add(part);
		try {
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			for(Part refPart:refParts){
				if(!processedParts.contains(refPart)){
					processedParts.add(refPart);
					if(!refPart.getFullyQualifiedName().startsWith("egl") && 
							!refPart.getFullyQualifiedName().startsWith("eglx")){
						ctx.invoke(genDependentPart, refPart, ctx, out, addComma);
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void genCSSFiles(EGLClass part, TabbedWriter out, LinkedHashSet cssFiles){}
	public void genDependentCSSs(EGLClass part, Context ctx, TabbedWriter out, LinkedHashSet cssFiles) {}
	
	public void genPropFiles(EGLClass part, TabbedWriter out, LinkedHashSet propFiles){}
	public void genDependentProps(EGLClass part, Context ctx, TabbedWriter out, LinkedHashSet propFiles){
		try {
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(!refPart.getFullyQualifiedName().startsWith("egl") && 
						!refPart.getFullyQualifiedName().startsWith("eglx")){
					ctx.invoke(genPropFiles, refPart, out, propFiles);
				}
			}
			for(Part refPart:refParts){
				if(!refPart.getFullyQualifiedName().startsWith("egl") && 
						!refPart.getFullyQualifiedName().startsWith("eglx")){
					ctx.invoke(genDependentProps, refPart, ctx, out, propFiles);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void genOutputFileName(Part part, Context ctx, TabbedWriter out, Boolean addComma) {
		StringBuilder buf = new StringBuilder(50);
		if(addComma){
			buf.append(", ");
		}
		
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append(".js\"");
		
		out.print(buf.toString());
	}		
}
