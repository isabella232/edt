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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.DataTable;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class PartTemplate extends JavaTemplate {

	IRUtils utils = new IRUtils();

	public void validatePart(Part part, Context ctx, Object... args) {
		ctx.putAttribute(ctx.getClass(), Constants.Annotation_partDataTablesUsed, new ArrayList<DataTable>());
		ctx.putAttribute(ctx.getClass(), Constants.Annotation_partFormsUsed, new ArrayList<Form>());
		ctx.putAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed, new ArrayList<Library>());
		ctx.putAttribute(ctx.getClass(), Constants.Annotation_partRecordsUsed, new ArrayList<Record>());
		ctx.putAttribute(ctx.getClass(), Constants.Annotation_partTypesImported, new ArrayList<String>());
		ctx.validate(validateClassBody, part, ctx, args);
	}

	public void genPart(Part part, Context ctx, TabbedWriter out, Object... args) {
		genPackageStatement(part, ctx, out, args);
		ctx.gen(genClassHeader, part, ctx, out, args);
		ctx.gen(genClassBody, part, ctx, out, args);
		out.println("}");
	}

	@SuppressWarnings("unchecked")
	public void genPackageStatement(Part part, Context ctx, TabbedWriter out, Object... args) {
		String packageName = CommonUtilities.packageName(part);
		if (packageName != null && packageName.length() > 0) {
			out.print("package ");
			out.print(packageName);
			out.println(";");
		}
		out.println("import org.eclipse.edt.javart.resources.*;");
		out.println("import org.eclipse.edt.javart.*;");
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partTypesImported);
		for (String imported : typesImported) {
			// we don't want to use ctx.gen here, because we want the type template logic to handle this to avoid any array <
			// ... > being added
			out.println("import " + imported + ";");
		}
	}

	@SuppressWarnings("unchecked")
	public void genPartName(Part part, Context ctx, TabbedWriter out, Object... args) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else {
			String value = part.getClassifier().getTypeSignature();
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partTypesImported);
			for (String imported : typesImported) {
				if (value.equalsIgnoreCase(imported)) {
					// it was is the table, so use the short name
					if (value.indexOf('.') >= 0)
						value = value.substring(value.lastIndexOf('.') + 1);
					break;
				}
			}
			out.print(value);
		}
	}

	public void genClassName(Part part, Context ctx, TabbedWriter out, Object... args) {
		// Data tables might have an alias property.
		String nameOrAlias;
		Annotation annot = part.getAnnotation(IEGLConstants.PROPERTY_ALIAS);
		if (annot != null)
			nameOrAlias = (String) annot.getValue();
		else
			nameOrAlias = part.getId();
		out.print(Aliaser.getAlias(nameOrAlias));
	}

	public void genSuperClass(Part part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genSuperClass, part, ctx, out, args);
	}

	public void genSerialVersionUID(Part part, Context ctx, TabbedWriter out, Object... args) {
		out.println("private static final long serialVersionUID = " + Constants.SERIAL_VERSION_UID + "L;");
	}
}
