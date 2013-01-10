/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;

public class CommandFile {
	private static final String EGLCOMMANDS_ELEMENT = "EGLCOMMANDS"; //$NON-NLS-1$
	private static final String DOCTYPE_PUBLID_ID = "-//IBM//DTD EGLCOMMANDS 5.1//EN"; //$NON-NLS-1$
	private static final String DOCTYPE_SYSTEM_ID = "\"\""; //$NON-NLS-1$
	private static final String XML_VERSION = "version=\"1.0\""; //$NON-NLS-1$
	private static final String XML_ROOT_TAG = "<" + EGLCOMMANDS_ELEMENT + ">\n</" + EGLCOMMANDS_ELEMENT + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String XML_DOCTYPE = "<!DOCTYPE " + EGLCOMMANDS_ELEMENT + " PUBLIC \"" + DOCTYPE_PUBLID_ID + "\" "
			+ DOCTYPE_SYSTEM_ID + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	public static String createEmptyCommandFile() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createXMLProlog());
		buffer.append("\n\n"); //$NON-NLS-1$
		buffer.append(XML_DOCTYPE);
		buffer.append("\n\n"); //$NON-NLS-1$
		buffer.append(XML_ROOT_TAG);
		return buffer.toString();
	}
	private static String getXMLEncoding() {
		String enc = ContentTypeEncodingPreferences.getUserSpecifiedDefaultEncodingPreference();
		if (enc != null && enc.trim().length() > 0) {
			return enc.trim();
		} else {
			if (enc == null || enc.trim().length() == 0) {
				enc = getWorkbenchSpecifiedDefaultEncoding();
				if (enc != null && enc.trim().length() > 0) {
					return enc.trim();
				}
			}
		}
		return "UTF-8";
	}
	private static final String getWorkbenchSpecifiedDefaultEncoding() {
		ResourcesPlugin resourcePlugin = ResourcesPlugin.getPlugin();
		String enc = resourcePlugin.getPluginPreferences().getString(ResourcesPlugin.PREF_ENCODING);
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}
	private static String createXMLProlog() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml "); //$NON-NLS-1$
		buffer.append(XML_VERSION);
		buffer.append(" "); //$NON-NLS-1$
		buffer.append("encoding=\"" + getXMLEncoding() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append("?>"); //$NON-NLS-1$
		return buffer.toString();
	}
}
