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
package org.eclipse.edt.javart;

import org.eclipse.edt.javart.resources.RunUnitBase;
import org.eclipse.edt.javart.resources.StartupInfo;

import egl.lang.AnyException;

public class JSERunUnit extends RunUnitBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public JSERunUnit(StartupInfo startInfo) throws AnyException {
		super(startInfo);
	}

}
