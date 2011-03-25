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
package org.eclipse.edt.gen.javascript;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

/**
 * A Context stores information about the Part being generated. It is usually shared by a set of analyzers and generators.
 */
public class Context extends EglContext {

	private static final long serialVersionUID = 6429116299734843162L;

	public Context(AbstractGeneratorCommand processor) {
		super(processor);
	}

	@Override
	public void handleValidationError(Element ex) {
	// TODO Auto-generated method stub
	}

	@Override
	public void handleValidationError(Annotation ex) {
	// TODO Auto-generated method stub
	}

	@Override
	public void handleValidationError(Type ex) {
	// TODO Auto-generated method stub
	}
}
