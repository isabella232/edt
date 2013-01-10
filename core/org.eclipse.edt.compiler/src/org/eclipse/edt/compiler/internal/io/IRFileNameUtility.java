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
package org.eclipse.edt.compiler.internal.io;

public class IRFileNameUtility {
	
	public static String toIRFileName(String name) {
		return name.toUpperCase().toLowerCase();
	}
	
	public static String[] toIRFileName(String[] name) {
		String[] result = new String[name.length];
		for(int i = 0; i < name.length; i++) {
			result[i] = name[i].toUpperCase().toLowerCase();
		}
		return result;
	}
}
