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
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;

public abstract class StructTemplate extends PartTemplate {

	public void genDefaultConstructor(StructPart part, TemplateContext ctx, TabbedWriter out, Object... args) {

	}

	public void genRuntimeTypeName(StructPart part, Context ctx, TabbedWriter out, Object... args) {
		genPartName(part, ctx, out, args);
	}

	/**
	 * Return whether the DataStructure needs an initialize method. An initialize method is needed when there are
	 * initializers, set value blocks, or fields that are redefined records.
	 * @return boolean
	 */
	protected boolean needToInitialize(StructPart part) {
		// Look for fields that are redefined records.
		List<StructuredField> fields = part.getStructuredFields();
		for (StructuredField f : fields) {
			if (f.getAnnotation(IEGLConstants.PROPERTY_REDEFINES) != null) {
				return true;
			}
		}

		return false;
	}

}
