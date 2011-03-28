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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.StructuredRecord;

public class StructuredRecordTemplate extends StructTemplate {

	/**
	 * Declare items and nested records.
	 */
	public void genFields(StructuredRecord part, TemplateContext ctx, TabbedWriter out, Object... args) {
		for (StructuredField field : part.getStructuredFields()) {
			genField(field, ctx, out, args);
		}
	}

	public void genField(StructuredField field, TemplateContext ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDeclaration, field, ctx, out, args);
	}

	/**
	 * Generate the initialize method if the record or structured record has initializers or set value blocks defined.
	 */
	public void genInitializeMethod(StructuredRecord part, Context ctx, TabbedWriter out, Object... args) {
		if (!needToInitialize(part)) {
			return;
		}

		out.println("public void initialize" + "( org.eclipse.edt.javart.resources.Program ezeProgram ) " + "throws org.eclipse.edt.javart.JavartException");
		out.println("{");

		// If any fields are redefined records, call their redefine methods.
		List<StructuredField> fields = part.getStructuredFields();
		for (StructuredField field : fields) {
			Annotation redef = field.getAnnotation(IEGLConstants.PROPERTY_REDEFINES);
			if (redef != null) {
				Member redefMember = ((MemberName) redef.getValue()).getMember();
				if (!(redefMember instanceof StructuredField)) {
					ctx.gen(genName, field, ctx, out, args);
					out.print(".redefine( ");
					ctx.gen(genName, redefMember, ctx, out, args);
					out.println(" );");
				}
			}
		}

		out.println("}");
	}

	/**
	 * Length in bytes of all leaf items that are in an overlay (i.e. not at the top level)
	 */
	public void genInitialBufferSize(StructuredRecord type, Context ctx, TabbedWriter out, Object... args) {
		out.print(type.getSizeInBytes());
	}

	public void validateClassBody(Part part, Context ctx, Object... args) {
		// TODO Auto-generated method stub
	}

	public void genClassBody(Part part, Context ctx, TabbedWriter out, Object... args) {
		// TODO Auto-generated method stub
	}

	public void genClassHeader(Part part, Context ctx, TabbedWriter out, Object... args) {
		// TODO Auto-generated method stub
	}
}
