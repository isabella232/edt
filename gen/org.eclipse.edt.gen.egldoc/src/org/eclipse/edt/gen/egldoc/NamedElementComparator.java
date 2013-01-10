/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

import java.util.Comparator;

import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementComparator implements Comparator<NamedElement> {

	@Override
	public int compare(NamedElement arg0, NamedElement arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}

	
	
}
