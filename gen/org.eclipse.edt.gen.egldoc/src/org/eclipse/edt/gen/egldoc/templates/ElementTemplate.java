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
package org.eclipse.edt.gen.egldoc.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.gen.egldoc.Constants;
import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.jface.text.DefaultLineTracker;

public class ElementTemplate extends EGLDocTemplate {

	public void preGenContent(Element part, Context ctx) {

		String docType = (String) ctx.get(Constants.DOCTYPE);

		if (docType == null) {
			docType = part.getEClass().getName();
			ctx.put(Constants.DOCTYPE, docType);
			ctx.put(Constants.FIELDCONTAINERTYPE, docType);
		}

		ctx.invokeSuper(this, preGenContent, part, ctx);
	}
}
