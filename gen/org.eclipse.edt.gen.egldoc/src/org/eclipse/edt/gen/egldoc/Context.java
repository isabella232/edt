/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egldoc;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {
	
	private static final long serialVersionUID = 336178899362850942L;

	public Context(AbstractGeneratorCommand processor) {
		super(processor);
	}

	@Override
	public void handleValidationError(Element ex) {
	}

	@Override
	public void handleValidationError(Annotation ex) {
	}

	@Override
	public void handleValidationError(Type ex) {
	}
}
