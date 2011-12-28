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
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.StructPart;

public class StructPartTemplate extends org.eclipse.edt.gen.java.templates.StructPartTemplate implements Constants{

	public void genImports(StructPart part, Context ctx, TabbedWriter out) {
		if(ctx.containsKey(PART_HAS_IBMI_FUNCTION)){			
			out.println("import org.eclipse.edt.javart.services.*;");
			out.println("import org.eclipse.edt.javart.json.Json;");
		}
		genImports(ctx, out);
		ctx.invokeSuper(this, genImports, part, ctx, out);
	}

	static void genImports(Context ctx, TabbedWriter out) {
		if(ctx.containsKey(PART_HAS_IBMI_FUNCTION)){			
			out.println("import eglx.lang.SysLib;");
			out.println("import com.ibm.as400.access.*;");
			out.println("import java.beans.PropertyVetoException;");
			out.println("import java.io.IOException;");
			out.println("import org.eclipse.edt.java.jtopen.*;");
			out.println("import org.eclipse.edt.javart.json.Json;");
		}
	}

}
