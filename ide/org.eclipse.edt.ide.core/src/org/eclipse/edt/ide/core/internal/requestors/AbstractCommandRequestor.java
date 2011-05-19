/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.requestors;


import java.io.InputStream;
import java.io.Reader;

/**
 * @author harmon
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class AbstractCommandRequestor {
	
	private boolean isXMLFile(String fileName) {
		if (fileName == null) {
			return false;
		}
		return "xml".equalsIgnoreCase(getExtension(fileName)); //$NON-NLS-1$
	}
	
	private String getExtension(String fileName) {

		if (fileName != null) {
			int index = fileName.lastIndexOf("."); //$NON-NLS-1$
			if (index >= 0) {
				return fileName
					.substring(index + 1, fileName.length())
					.toUpperCase().toLowerCase();
			}
		}
		return null;
	}
	
	private char[] getContents(InputStream inputStream) {
		return null;
	}
	private StringBuffer buildStringBuffer(Reader reader) {
		return null;
	}

}
