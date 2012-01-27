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

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.egl.StructPart;

public class StructPartTemplate extends org.eclipse.edt.gen.java.templates.StructPartTemplate implements Constants{

	public void preGenPart(StructPart part, Context ctx) {
		ctx.invokeSuper(this, preGenPart, part, ctx);
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), org.eclipse.edt.gen.java.Constants.SubKey_partTypesImported);
		typesImported.add("org.eclipse.edt.javart.services.*");
		typesImported.add("org.eclipse.edt.javart.json.Json");
		if(ctx.containsKey(PART_HAS_IBMI_FUNCTION)){			
			typesImported.add("eglx.lang.SysLib");
			typesImported.add("com.ibm.as400.access.*");
			typesImported.add("java.beans.PropertyVetoException");
			typesImported.add("java.io.IOException");
			typesImported.add("org.eclipse.edt.java.jtopen.*");
		}
	}
}
