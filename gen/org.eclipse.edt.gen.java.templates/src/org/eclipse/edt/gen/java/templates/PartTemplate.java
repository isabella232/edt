/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DataTable;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class PartTemplate extends JavaTemplate {

	IRUtils utils = new IRUtils();

	public void preGenPart(Part part, Context ctx) {
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partDataTablesUsed, new ArrayList<DataTable>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partFormsUsed, new ArrayList<Form>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed, new ArrayList<Library>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partRecordsUsed, new ArrayList<Record>());
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_partTypesImported, new ArrayList<String>());
		ctx.invoke(preGenPartImport, part, ctx);
		ctx.invoke(preGenClassBody, part, ctx);
	}

	@SuppressWarnings("unchecked")
	public void preGenPartImport(Part part, Context ctx) {
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), org.eclipse.edt.gen.java.Constants.SubKey_partTypesImported);
		if (!typesImported.contains("org.eclipse.edt.javart.resources.*"))
			typesImported.add("org.eclipse.edt.javart.resources.*");
		if (!typesImported.contains("org.eclipse.edt.javart.*"))
			typesImported.add("org.eclipse.edt.javart.*");
	}

	public void genPart(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genPackageStatement, part, ctx, out);
		ctx.invoke(genImports, part, ctx, out);
		ctx.invoke(genClassHeader, part, ctx, out);
		ctx.invoke(genClassBody, part, ctx, out);
		out.println("}");
	}

	public void genPackageStatement(Part part, Context ctx, TabbedWriter out) {
		String packageName = CommonUtilities.packageName(part);
		if (packageName != null && packageName.length() > 0) {
			out.print("package ");
			out.print(packageName);
			out.println(";");
		}
	}

	@SuppressWarnings("unchecked")
	public void genImports(Part part, Context ctx, TabbedWriter out) {
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partTypesImported);
		for (String imported : typesImported) {
			// we don't want to use ctx.gen here, because we want the type template logic to handle this to avoid any array <
			// ... > being added
			// strip off any [...] or <...> found
			if (imported.indexOf("[") >= 0)
				imported = imported.substring(0, imported.indexOf("["));
			if (imported.indexOf("<") >= 0)
				imported = imported.substring(0, imported.indexOf("<"));
			// if this name starts with a period, then ignore the entry.
			if (!imported.startsWith(".")) {
				String type;
				int lastDot = imported.lastIndexOf('.');
				if (lastDot == -1) {
					type = JavaAliaser.getAlias(imported);
				} else {
					if (imported.startsWith("java.lang.")) {
						// If we're intentionally importing a class from java.lang, don't
						// alias the name.
						type = imported;
					} else {
						type = imported.substring(0, lastDot) + '.' + JavaAliaser.getAlias(imported.substring(lastDot + 1));
					}
				}
				out.println("import " + type + ";");
			}
		}
		out.println("@SuppressWarnings(\"unused\")");
	}

	@SuppressWarnings("unchecked")
	public void genPartName(Part part, Context ctx, TabbedWriter out) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else {
			String value = part.getClassifier().getTypeSignature();
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partTypesImported);
			for (String imported : typesImported) {
				if (value.equalsIgnoreCase(imported)) {
					// it was is the table, so use the short name
					if (value.indexOf('.') >= 0) {
						out.print(CommonUtilities.classAlias(part));
						return;
					}
				}
			}
			out.print(CommonUtilities.fullClassAlias(part));
		}
	}

	public void genClassName(Part part, Context ctx, TabbedWriter out) {
		out.print(JavaAliaser.getAlias(part.getCaseSensitiveName()));
	}

	public void genSuperClass(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genSuperClass, part, ctx, out);
	}

	public void genSerialVersionUID(Part part, Context ctx, TabbedWriter out) {
		out.println("private static final long serialVersionUID = " + Constants.SERIAL_VERSION_UID + "L;");
	}
}
