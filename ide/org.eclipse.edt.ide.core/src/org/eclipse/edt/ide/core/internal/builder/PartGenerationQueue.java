/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.generation.GeneratePartsOperation;
import org.eclipse.edt.ide.core.internal.generation.IEGLPartWrapper;
import org.eclipse.edt.ide.core.internal.generation.PartWrapper;
import org.eclipse.swt.widgets.Display;

public class PartGenerationQueue {

	private Set<PartWrapper> partsToGenerate = new LinkedHashSet<PartWrapper>();

	public void generate() {
		if (partsToGenerate.size() > 0) {
			Display display = Display.getDefault();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						GeneratePartsOperation genOp = new GeneratePartsOperation(true);
						genOp.generate((IEGLPartWrapper[]) partsToGenerate.toArray(new IEGLPartWrapper[partsToGenerate.size()]), false, true);
					}
				});
			}
		}
	}

	public void add(Part partAST, IFile eglFile) {
		partsToGenerate.add(new PartWrapper(partAST.getIdentifier(), eglFile));
	}
}
