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
package org.eclipse.edt.mof.egl.api.gen;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.MofFactory;

public class TestToString {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EClass obj = (EClass)MofFactory.INSTANCE.getEClassClass();
		obj.toString();
	}

}
