/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.codegen.api.TemplateFactory;
import org.eclipse.edt.mof.codegen.api.TemplateException;

public class MofImplTemplateFactory extends TemplateFactory {
	private final static String propertiesName = "org.eclipse.edt.mof.codegen.java.template";

	public MofImplTemplateFactory() throws TemplateException {
		super();
		load(propertiesName, getClass().getClassLoader());
	}
	
	public String getPropertyFileName() {
		return propertiesName;
	}
}
