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
package org.eclipse.edt.ide.ui.internal.editor.folding;

import org.eclipse.osgi.util.NLS;

final class FoldingMessages extends NLS {

	private static final String BUNDLE_NAME= FoldingMessages.class.getName();

	private FoldingMessages() {
		// Do not instantiate
	}

	public static String EGLFoldingPreferenceBlock_title;
	public static String EGLFoldingPreferenceBlock_comments;
	public static String EGLFoldingPreferenceBlock_parts;
	public static String EGLFoldingPreferenceBlock_functions;
	public static String EGLFoldingPreferenceBlock_imports;
	public static String EGLFoldingPreferenceBlock_partitions;
	public static String EGLFoldingPreferenceBlock_statementBlock;
	public static String EGLFoldingPreferenceBlock_propertiesBlock;	
	public static String EGLFoldingPreferenceBlock_propertiesBlockThreshold;
	public static String EmptyEGLFoldingPreferenceBlock_emptyCaption;

	static {
		NLS.initializeMessages(BUNDLE_NAME, FoldingMessages.class);
	}
}
