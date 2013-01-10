/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit.templates;

import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.Constants;
import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.gen.eunit.TestCounter;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Part;

public class PartTemplate extends EUnitTemplate {

	public void preGenPart(Part part, Context ctx, TestCounter counter) {
		ctx.invoke(preGenClassBody, part, ctx, counter);
	}

	public void genPart(Part part, Context ctx, TabbedWriter out, TestCounter counter) {
		genPackageStatement(part, ctx, out);
		ctx.invoke(genImports, part, ctx, out);
		ctx.invoke(genClassHeader, part, ctx, out);
		ctx.invoke(genClassBody, part, ctx, out, counter);
	}
	
	public void genLibDriver(Part part, Context ctx, TabbedWriter out, String driverPartNameAppend, TestCounter counter) {
		genPackageStatement(part, ctx, out);		
		ctx.invoke(genLibDriverImports, part, ctx, out);
		ctx.invoke(genLibDriverClassBody, part, ctx, out, driverPartNameAppend, counter);
	}

	public void genPackageStatement(Part part, Context ctx, TabbedWriter out) {
		out.print("package ");
		out.print(CommonUtilities.getECKGenPackageName(part));
		out.println(';');
		out.println();
	}

	public void genPartName(Part part, Context ctx, TabbedWriter out) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else
			out.print(part.getTypeSignature());
	}

	public void genSerialVersionUID(Part part, Context ctx, TabbedWriter out) {
		out.println("private static final long serialVersionUID = " + Constants.SERIAL_VERSION_UID + "L;");
	}
}
