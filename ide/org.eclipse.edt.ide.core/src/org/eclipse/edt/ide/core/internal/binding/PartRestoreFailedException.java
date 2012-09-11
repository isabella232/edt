/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.binding;

import org.eclipse.edt.compiler.internal.core.builder.BuildException;

public class PartRestoreFailedException extends BuildException {
	private static final long serialVersionUID = 1L;

	public PartRestoreFailedException(String packageName, String partName, Exception e){
		super("Error restoring part: " + getPartName(packageName,partName),e);
	}
}
