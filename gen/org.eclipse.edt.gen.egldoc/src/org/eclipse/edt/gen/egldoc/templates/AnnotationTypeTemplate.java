/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egldoc.templates;

import java.util.List;

import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.EClass;

public class AnnotationTypeTemplate extends EGLDocTemplate {

    public void preGenContent(AnnotationType annotationType, Context ctx) {
		
		String docType = (String)ctx.get(Constants.DOCTYPE);
		
		if (docType == null) {
			ctx.put(Constants.DOCTYPE, "stereotype type");
			ctx.put(Constants.FIELDCONTAINERTYPE, "Stereotype");
		}
		ctx.invokeSuper(this, preGenContent, (EClass)annotationType, ctx);					
    }
	
	

	
	public void genContent(AnnotationType annotationType, Context ctx, TabbedWriter out) {

		//ctx.invoke(genExampleUse, annotationType, ctx, out);
		ctx.invoke(genTargets, (EClass)annotationType, ctx, out);
		
		//ctx.invoke(genMemberAnnotations, (Part)stereotypeType, ctx, out);
		// does an annotation type have a default super type?  method is now in stereotype type 
		//     ctx.invoke(genDefaultSuperType, (Part)annotationType, ctx, out);
		ctx.invoke(genFields, (Part)annotationType, ctx, out);
		//ctx.invoke(genComments, annotationType, ctx, out);
		//ctx.invoke(genCompatibility, annotationType, ctx, out);
		
	}
	
    
    
    
	/*
	 *  public void genClassContent(AnnotationType annotationType, Context ctx, TabbedWriter out) {
	 
		//ctx.invoke(genExampleUse, annotation, ctx, out);
		ctx.invoke(genTargets, (Part)annotationType, ctx, out);
		ctx.invoke(genFields, (Part)annotationType, ctx, out);
		//ctx.invoke(genComments, annotation, ctx, out);
		//ctx.invoke(genCompatibility, annotation, ctx, out);
		
	}
	*/
	
	public void genTargets(AnnotationType annotationType, Context ctx, TabbedWriter out){
        int numberOfTargets = annotationType.getTargets().size();
		
		if (numberOfTargets > 0) {
			int count = 0;
			out.println("<dt class=\"dt dlterm\"><a name=\"targets\"</a>Targets</dt>");		    
			out.println("<dd class=\"dd\"><p class=\"p\">");
			
			for (ElementKind target : annotationType.getTargets()) {
				
				if (count > 0 && count < numberOfTargets){
					out.print(", ");
				}
				count++;
				
				ctx.invoke(genTarget, (EClass)annotationType, ctx, out, target);				
			}			
			out.println("</p><p class=\"p\"></p></dd></dt>");
		}		
	}
	
	public void genTarget(AnnotationType annotationType, Context ctx, TabbedWriter out, ElementKind arg){
        
		  
		out.print(arg.name());
		/* String argClassName = arg.getClass().getName();
		   out.println("<a href=\"" + argClassName + "\">");
		   out.println(arg + "</a>"); 
		*/						
	}
	
	public void genFields(AnnotationType annotationType, Context ctx, TabbedWriter out) {
	    String fieldContainerType = (String) ctx.get("fieldContainerType");

		out.println ("<dt class=\"dt dlterm\"><a name=\"annofields\"></a>" + fieldContainerType + " fields</dt>");
		
		if(annotationType.getEFields().size() > 0){
			// out.println(annotationType.getEClass().getName() + " fields");
			out.println ("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");
		}
		else {
			out.println ("<dd class=\"dd\"> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");			
		}
		
		for (EField efield : annotationType.getEFields()) {
			ctx.invoke(genField, (EClass)annotationType, ctx, out, efield);
		}
	}

	public void genField(AnnotationType annotationType, Context ctx, TabbedWriter out, EField arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
		out.println("<p class=\"p\"></p>");
	}
	
	public void genDeclaration(AnnotationType annotationType, Context ctx, TabbedWriter out) {
        out.println(annotationType.getFullyQualifiedName());	
    }
	
}
