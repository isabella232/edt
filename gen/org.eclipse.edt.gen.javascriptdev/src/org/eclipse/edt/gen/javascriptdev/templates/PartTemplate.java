/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascriptdev.templates;

import java.util.ArrayList;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.egl.LabelStatement;
import org.eclipse.edt.mof.egl.Part;

public class PartTemplate extends org.eclipse.edt.gen.javascript.templates.PartTemplate {
	
	@Override
	public void preGenPart(Part part, Context ctx) {
		super.preGenPart( part, ctx );
		ctx.putAttribute(ctx.getClass(), Constants.SubKey_labelsForNextStatement, new ArrayList<LabelStatement>());
	}
}
