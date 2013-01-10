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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.NamedElementComparator;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;

public class EGLClassTemplate extends EGLDocTemplate {

	NamedElementComparator comparator = new NamedElementComparator();

	public void genContent(EGLClass eglClass, Context ctx, TabbedWriter out) {
		ctx.invokeSuper(this, genContent, eglClass, ctx, out);
		ctx.invoke(genAnnotations, eglClass, ctx, out);
		ctx.invoke(genConstants, eglClass, ctx, out);
		ctx.invoke(genFields, eglClass, ctx, out);
		ctx.invoke(genFunctions, eglClass, ctx, out);
	}

	public void genConstants(EGLClass eglClass, Context ctx, TabbedWriter out) {
		List<ConstantField> constantFields = new ArrayList<ConstantField>();
		for (Field field : eglClass.getFields()) {
			if (field instanceof ConstantField) {
				constantFields.add((ConstantField) field);
			}
		}
		if (constantFields.size() > 0) {
			out.println("Constants");
		} else {

			Collections.sort(constantFields, comparator);

			for (ConstantField constantField : constantFields) {
				ctx.invoke(genConstant, eglClass, ctx, out, constantField);
			}
		}
	}

	public void genConstant(EGLClass eglClass, Context ctx, TabbedWriter out, ConstantField arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genFields(EGLClass eglClass, Context ctx, TabbedWriter out) {

		String fieldContainerType = (String) ctx.get("fieldContainerType");

		List<Field> nonConstantFields = new ArrayList<Field>();
		for (Field field : eglClass.getFields()) {
			if (!(field instanceof ConstantField)) {
				nonConstantFields.add((Field) field);
			}
		}

		/*
		 * SBM. show section in any case if(nonConstantFields.size() > 0){
		 * out.println(part.getEClass().getName() + " fields"); }
		 */
		// part.getEClass().getName()
		out.println("<dt class=\"dt dlterm\"><a name=\"recordfields\"></a>" + fieldContainerType + " fields</dt>");
		if (nonConstantFields.size() == 0) {

			out.println("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");
			/*
			 * out.println("<dd class=\"dd\">"); out.println("None.");
			 * out.println("<p class=\"p\"></p>"); out.println("</dd>");
			 */
		} else {
			// FieldComparator comparator = new FieldComparator();
			Collections.sort(nonConstantFields, comparator);

			out.println("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");

			for (Field field : nonConstantFields) {
				ctx.invoke(genField, eglClass, ctx, out, field);
				out.println("<p class=\"p\"></p>");
			}
			out.println("<p class=\"p\"></p></dd>");
		}

	}

	public void genField(EGLClass eglClass, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);

	}

	public void genFunctions(EGLClass eglClass, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt dlterm\"><a name=\"functions\"></a>Functions</dt>");
		out.println("<dd class=\"dd\"><p class=\"p\"></p>");

		if (eglClass.getFunctions().size() == 0) {
			out.println("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");
		} else {
			out.println("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");
			out.println("<div class=\"tablenoborder\">");
			out.println("<table cellpadding=\"4\" cellspacing=\"0\" id=\"function_table\"");
			out.println("class=\"table\" frame=\"border\" border=\"1\" rules=\"all\">");
			out.println("<thead class=\"thead\" align=\"left\">");
			out.println("<tr class=\"row\" valign=\"bottom\">");
			// out.println("<th class=\"entry\" valign=\"bottom\">Name</th>");
			// out.println("<th class=\"entry\" valign=\"bottom\"
			// width=\"70%\">Description</th>)
			out.println("</tr></thead>");
			out.println("<tbody class=\"tbody\">");

			List<Function> theFunctions = new ArrayList<Function>();
			for (Function theFunction : eglClass.getFunctions()) {
				theFunctions.add((Function) theFunction);
			}

			// FunctionComparator comparator = new FunctionComparator();
			Collections.sort(theFunctions, comparator);

			for (Function function : theFunctions) {
				ctx.invoke(genFunction, eglClass, ctx, out, function);
			}

			out.println("</tbody></table></div></dd></dl></div>");
		}

	}

	public void genFunction(EGLClass eglClass, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genAnnotations(EGLClass eglClass, Context ctx, TabbedWriter out) {

		List<Annotation> annoList = eglClass.getAnnotations();
		int numberOfAnnos = annoList.size();

		
		out.println("<dt class=\"dt dlterm\"><a name=\"annos\"></a>Annotations</dt>");
		out.println("<dd class=\"dd\"><p class=\"p\"></p>");

		/* ignore EGL_LOCATION */
		if (numberOfAnnos == 1) {
			out.println("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");
		} else {

			Iterator<Annotation> annoIterator = annoList.iterator();
			int counter = 0;
			
			while (annoIterator.hasNext()) {
				ctx.invoke(genAnnotation, annoIterator.next(), ctx, out);
				counter = counter + 1;
				
				if (counter < numberOfAnnos-1) {
					out.print(", ");
				}
			}
			
			
		}
	}
}
