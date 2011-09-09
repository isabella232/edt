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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class EGLClassTemplate extends JavaScriptTemplate {
	public void genDependentPart(EGLClass part, Context ctx, LinkedHashSet dependentFiles) {}
	public void genDependentParts(EGLClass part, Context ctx, LinkedHashSet dependentFiles) {		
		LinkedHashSet processedParts = (LinkedHashSet)ctx.get(genDependentParts);
		if(processedParts == null){
			processedParts = new LinkedHashSet();
			ctx.put(genDependentParts, processedParts);
		}
		try {
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			for(Part refPart:refParts){
				if(!processedParts.contains(refPart.getFullyQualifiedName()) && refPart instanceof EGLClass){
					processedParts.add(refPart.getFullyQualifiedName());
					if(CommonUtilities.isRUIWidget(refPart)){
						ctx.invoke(genDependentPart, refPart, ctx, dependentFiles);
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void genOutputFileName(Part part, Context ctx, LinkedHashSet dependentFiles) {
		StringBuilder buf = new StringBuilder(50);
		
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append(".js\"");
		dependentFiles.add(buf.toString());
	}
	
	public void genCSSFiles(EGLClass part, TabbedWriter out, LinkedHashSet cssFiles){}
	public void genDependentCSSs(EGLClass part, Context ctx, TabbedWriter out, LinkedHashSet cssFiles, LinkedHashSet handledParts) {}
	
	public void genPropFiles(EGLClass part, TabbedWriter out, LinkedHashSet propFiles){}
	public void genDependentProps(EGLClass part, Context ctx, TabbedWriter out, LinkedHashSet propFiles, LinkedHashSet handledParts){
		if ( handledParts.contains( part.getFullyQualifiedName() ) ) {
			return;
		}
		try {
			handledParts.add( part.getFullyQualifiedName() );
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genPropFiles, refPart, out, propFiles);
				}
			}
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genDependentProps, refPart, ctx, out, propFiles, handledParts);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void genIncludeFiles(Part part, TabbedWriter out, LinkedHashSet includeFiles){}
	public void genDependentIncludeFiles(Part part, Context ctx, TabbedWriter out, LinkedHashSet includeFiles, LinkedHashSet handledParts){}
}
