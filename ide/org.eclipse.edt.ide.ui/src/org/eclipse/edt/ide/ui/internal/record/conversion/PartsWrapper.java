/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.record.conversion;

import org.eclipse.edt.ide.ui.templates.parts.Part;

public class PartsWrapper {
	Part[] parts;
	private static String eol = System.getProperty("line.separator", "\n");//$NON-NLS-1$

	public PartsWrapper(Part[] parts) {
		super();
		this.parts = parts;
	}
	public String toString() {
		if (parts == null) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				buff.append(eol+eol);
			}
			buff.append(parts[i].toString());
		}
		buff.append(eol);
		return buff.toString();
	}
}
