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
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class EGLClassTemplate extends JavaScriptTemplate {
	
	public void genOutputFileName(Part part, LinkedHashSet dependentFiles) {
		if (part instanceof Interface || part instanceof Service){
			return;
		}
		StringBuilder buf = new StringBuilder(50);
		
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append(".js\"");
		CommonUtilities.addToDependentList(dependentFiles, buf.toString());
	}
	
	public void genDependentParts(EGLClass part, Context ctx, LinkedHashSet dependentFiles, LinkedHashSet handledParts) {		
		if (part instanceof Service || handledParts.contains(part.getFullyQualifiedName())) {
			return;
		}
		try {
			if(!(part instanceof ExternalType)){
				handledParts.add( part.getFullyQualifiedName() );	
			}					
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genOutputFileName, refPart, dependentFiles);
				}				
			}
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart) && (	part instanceof ExternalType || part instanceof Handler || 
						part instanceof Library || part instanceof Record)){
					ctx.invoke(genDependentParts, refPart, ctx, dependentFiles, handledParts);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void genCSSFiles(EGLClass part, LinkedHashSet cssFiles){}
	public void genDependentCSSs(EGLClass part, Context ctx, LinkedHashSet cssFiles, LinkedHashSet handledParts){
		if ( handledParts.contains( part.getFullyQualifiedName() ) ) {
			return;
		}
		try {
			handledParts.add( part.getFullyQualifiedName() );
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genCSSFiles, refPart, cssFiles);
				}				
			}
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genDependentCSSs, refPart, ctx, cssFiles, handledParts);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void genPropFiles(EGLClass part, LinkedHashSet propFiles){}
	public void genDependentProps(EGLClass part, Context ctx, LinkedHashSet propFiles, LinkedHashSet handledParts){
		if ( handledParts.contains( part.getFullyQualifiedName() ) ) {
			return;
		}
		try {
			handledParts.add( part.getFullyQualifiedName() );
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genPropFiles, refPart, propFiles);
				}
			}
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genDependentProps, refPart, ctx, propFiles, handledParts);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void genIncludeFiles(EGLClass part, LinkedHashSet includeFiles){}
	public void genDependentIncludeFiles(EGLClass part, Context ctx, LinkedHashSet includeFiles, LinkedHashSet handledParts){
		if ( handledParts.contains( part ) ) {
			return;
		}
		try {
			handledParts.add( part );
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genIncludeFiles, refPart, includeFiles);
				}				
			}
			for(Part refPart:refParts){
				if(CommonUtilities.isUserPart(refPart)){
					ctx.invoke(genDependentIncludeFiles, refPart, ctx, includeFiles, handledParts);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
