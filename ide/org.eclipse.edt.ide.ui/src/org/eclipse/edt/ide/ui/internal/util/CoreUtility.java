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
package org.eclipse.edt.ide.ui.internal.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class CoreUtility {
	
	/**
	 * Creates a folder and all parent folders if not existing.
	 * Project must exist.
	 * <code> org.eclipse.ui.dialogs.ContainerGenerator</code> is too heavy
	 * (creates a runnable)
	 */
	public static void createFolder(IFolder folder, boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent= folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder)parent, force, local, null);
			}
			folder.create(force, local, monitor);
		}
	}
		
	public static String getValidProjectName(String toValidate) {
		String validatedString = toValidate;
		char replacementChar = '_';
		char invalidCharacters[] = { '.', ' ', ',', '\'', ';', '!', '@', '#',
				'%', '^', '&', '(', ')', '+', '=', '[', ']', '{', '}' };

		for (int i = 0; i < invalidCharacters.length; i++) {
			if (validatedString.indexOf(invalidCharacters[i]) != -1) {
				validatedString = validatedString.replace(invalidCharacters[i],replacementChar);
			}
		}

		// can't start with number either, will prepend letter 'a'
		if (validatedString.charAt(0) >= '0' && validatedString.charAt(0) <= '9') {
			validatedString = 'a' + validatedString;
		}
		return validatedString;
	}
}

