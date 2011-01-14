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
package org.eclipse.edt.mof.codegen.java;

import org.eclipse.edt.mof.codegen.api.AbstractTemplateFactory;
import org.eclipse.edt.mof.codegen.api.TemplateException;

public class MofImplTemplateFactory extends AbstractTemplateFactory {
	private final static String propertiesName = "org.eclipse.edt.mof.codegen.java.template";

	public MofImplTemplateFactory() throws TemplateException {
		super();
		load(propertiesName);
	}
	
	public String getPropertyFileName() {
		return propertiesName;
	}
}
