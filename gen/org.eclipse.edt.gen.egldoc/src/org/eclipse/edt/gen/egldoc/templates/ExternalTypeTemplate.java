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

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StereotypeType;

public class ExternalTypeTemplate extends EGLDocTemplate {

	public void preGenContent(ExternalType externalType, Context ctx) {

		// String theName = externalType.getTypeSignature();
		// System.out.println(theName);
		// externalType.getEClass().getName();
		// new String(docType);

		ctx.put("docType", "external type");
		ctx.put("fieldContainerType", "External type");

		/*
		 * int endPosition = docType.lastIndexOf("Type");
		 * 
		 * StringBuffer docType02 = new StringBuffer();
		 * 
		 * docType = docType02
		 * .append(Character.toLowerCase(docType02.charAt(0)))
		 * .append(docType02.substring(1, endPosition))
		 * .append(" type").toString();
		 * 
		 * if (fieldContainerType.equals("ExternalType")){ fieldContainerType =
		 * "External type"; } else { StringBuffer fieldContainerType02 = new
		 * StringBuffer(); fieldContainerType = fieldContainerType02
		 * .append(fieldContainerType.substring(0, endPosition)) .toString(); }
		 */
		ctx.invokeSuper(this, preGenContent, externalType, ctx);
	}
/*
	public void genClassContent(ExternalType externalType, Context ctx, TabbedWriter out) {
		// ctx.invoke(genExampleUse, stereotypeType, ctx, out);
		// ctx.invoke(genTargets, externalType, ctx, out);
		// ctx.invoke(genMemberAnnotations, (Part)stereotypeType, ctx, out);
		// ctx.invoke(genDefaultSuperType, (Part) externalType, ctx, out);
		ctx.invoke(genFields, (Part) externalType, ctx, out);
		// ctx.invoke(genComments, stereotypeType, ctx, out);
		// ctx.invoke(genCompatibility, stereotypeType, ctx, out);

	}
*/
	/*
	 * public void genTargets(AnnotationType annotationType, Context ctx,
	 * TabbedWriter out){
	 * 
	 * if(annotationType.getTargets().size() > 0){ out.println("Targets"); } for
	 * (ElementKind target : annotationType.getTargets()) {
	 * ctx.invoke(genTarget, (Part)annotationType, ctx, out, target); } }
	 */
}
