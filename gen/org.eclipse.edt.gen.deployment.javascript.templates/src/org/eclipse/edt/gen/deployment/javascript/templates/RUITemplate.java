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

import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class RUITemplate extends JavaScriptTemplate {
	
	public void genDependentPart(Handler handler, Context ctx, TabbedWriter out, Boolean addComma) {
		ctx.invoke(genOutputFileName, handler, ctx, out, addComma);
		ctx.invoke(genDependentParts, handler, ctx, out, Boolean.TRUE);
	}
	
	public void genCSSFiles(Handler handler, TabbedWriter out, LinkedHashSet cssFiles){
		Annotation a = handler.getAnnotation( CommonUtilities.isRUIHandler( handler ) ? Constants.RUI_HANDLER : Constants.RUI_WIDGET);
		if ( a != null ){
			String fileName = (String)a.getValue( "cssFile" );
			if ( fileName != null && fileName.length() > 0 ){
				cssFiles.add(fileName);
			}
		}
	}
	
	public void genDependentCSSs(Handler part, Context ctx, TabbedWriter out, LinkedHashSet cssFiles, LinkedHashSet handledParts){
		if ( handledParts.contains( part.getFullyQualifiedName() ) ) {
			return;
		}
		try {
			handledParts.add( part.getFullyQualifiedName() );
			Set<Part> refParts = IRUtils.getReferencedPartsFor(part);
			// BFS traverse
			for(Part refPart:refParts){
				if(!refPart.getFullyQualifiedName().startsWith("egl") && 
						!refPart.getFullyQualifiedName().startsWith("eglx") && refPart instanceof EGLClass){
					ctx.invoke(genCSSFiles, refPart, out, cssFiles);
				}				
			}
			for(Part refPart:refParts){
				if(!refPart.getFullyQualifiedName().startsWith("egl") && 
						!refPart.getFullyQualifiedName().startsWith("eglx") && refPart instanceof EGLClass){
					ctx.invoke(genDependentCSSs, refPart, ctx, out, cssFiles, handledParts);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
