/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.codegen.api;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;

public class MissingTemplate extends AbstractTemplate {

	@Override
	public final void gen(String genMethod, EObject object, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		String text = object instanceof MofSerializable ? ((MofSerializable)object).getMofSerializationKey() : object.getEClass().getMofSerializationKey();
		out.print("<MISSING TEMPLATE FOR: " + text);
	}

}
