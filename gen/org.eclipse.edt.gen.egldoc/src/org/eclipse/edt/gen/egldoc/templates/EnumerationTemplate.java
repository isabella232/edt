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

import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Member;

public class EnumerationTemplate extends EGLDocTemplate {

    public void preGenContent(Enumeration enumeration, Context ctx) {
		
		String docType = (String)ctx.get(Constants.DOCTYPE);
		/*
		if (docType == null) {
			ctx.put(Constants.DOCTYPE, "");
			ctx.put(Constants.FIELDCONTAINERTYPE, "");
		}
		*/
		ctx.invokeSuper(this, preGenContent, enumeration, ctx);					
    }
	
	

	
	public void genContent(Enumeration enumeration, Context ctx, TabbedWriter out) {

		//ctx.invoke(genExampleUse, annotationType, ctx, out);
		//ctx.invoke(genTargets, enumeration, ctx, out);
		
		//ctx.invoke(genMemberAnnotations, (Part)stereotypeType, ctx, out);
		// does an annotation type have a default super type?  method is now in stereotype type 
		//     ctx.invoke(genDefaultSuperType, (Part)annotationType, ctx, out);
		ctx.invoke(genFields, (Element)enumeration, ctx, out);
		//ctx.invoke(genComments, annotationType, ctx, out);
		//ctx.invoke(genCompatibility, annotationType, ctx, out);
		//now returns to the same method:  ctx.invokeSuper(this, genContent, enumeration, ctx);
		
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
/*	
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
*/ 
/*	
	public void genTarget(AnnotationType annotationType, Context ctx, TabbedWriter out, ElementKind arg){
        
		  
		out.print(arg.name());
		/* String argClassName = arg.getClass().getName();
		   out.println("<a href=\"" + argClassName + "\">");
		   out.println(arg + "</a>"); 
		*/
/*	
	}
*/	
	public void genFields(Enumeration enumeration, Context ctx, TabbedWriter out) {
	    String fieldContainerType = (String) ctx.get("fieldContainerType");

		out.println ("<dt class=\"dt dlterm\"><a name=\"annofields\"></a>" + fieldContainerType + " fields</dt>");
		
		if(enumeration.getMembers().size() > 0){
			// out.println(annotationType.getEClass().getName() + " fields");
			out.println ("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");
		}
		else {
			out.println ("<dd class=\"dd\"> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");			
		}
		
		for (Member member : enumeration.getMembers()) {
			ctx.invoke(genMember, (Element)enumeration, ctx, out, member);
		}
	}

	public void genMember(Enumeration enumeration, Context ctx, TabbedWriter out, Member arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
		out.println("<p class=\"p\"></p>");
	}
	
	public void genDeclaration(Enumeration enumeration, Context ctx, TabbedWriter out) {
        out.println(enumeration.getFullyQualifiedName());	
    }
	
}
