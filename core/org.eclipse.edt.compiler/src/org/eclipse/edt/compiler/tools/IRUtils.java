/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.tools;

public class IRUtils {

	public final static char[] SUFFIX_eglxml = ".eglxml".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLXML = ".EGLXML".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_eglbin = ".eglbin".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLBIN = ".EGLBIN".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_mofxml = ".mofxml".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_MOFXML = ".MOFXML".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_mofbin = ".mofbin".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_MOFBIN = ".MOFBIN".toCharArray(); //$NON-NLS-1$
	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".eglxml" or ".eglbin" or ".mofxml" or ".mofbin")
	 * implementation is not creating extra strings.
	 */	
	public static boolean isEGLIRFileName(String name) {
		if (name == null) {
			return false;
		}
		return IRUtils.matchesFileName(name, IRUtils.SUFFIX_eglxml, IRUtils.SUFFIX_EGLXML)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_eglbin, IRUtils.SUFFIX_EGLBIN)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_mofxml, IRUtils.SUFFIX_MOFXML)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_mofbin, IRUtils.SUFFIX_MOFBIN);
	}

	public final static boolean matchesFileName(String name, char[] lowercaseExtension, char[] uppercaseExtension) {
		int nameLength = name.length();
		int suffixLength = lowercaseExtension.length;
		if (nameLength < suffixLength)
			return false;
		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != lowercaseExtension[i] && c != uppercaseExtension[i])
				return false;
		}
		return true;
	}


}
