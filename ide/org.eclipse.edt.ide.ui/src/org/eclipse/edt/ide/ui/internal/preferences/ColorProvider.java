/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Manager for colors used in the EGL editor
 * 
 * This provider should be hooked up to the preferences page.
 */
public class ColorProvider {

	public static final String DEFAULT = "DEFAULT";//$NON-NLS-1$
	public static final String KEYWORD = "KEYWORD";//$NON-NLS-1$
	public static final String SINGLE_LINE_COMMENT = "SINGLE_LINE_COMMENT";//$NON-NLS-1$
	public static final String MULTI_LINE_COMMENT = "MULTI_LINE_COMMENT";//$NON-NLS-1$
	public static final String LITERAL = "LITERAL";//$NON-NLS-1$
	
	protected Map fColorTable = new HashMap(10);
	protected IPreferenceStore preferenceStore = null;
	
	public ColorProvider(IPreferenceStore prefs) {
		preferenceStore = prefs;
		
		// All Color Preferences should be set in EGLUIPlugin
			// If not...set them
		if (!preferenceStore.contains(EDTUIPreferenceConstants.EDITOR_DEFAULT_COLOR))
			EDTUIPreferenceConstants.initializeDefaultEGLColorPreferences(preferenceStore);
	}

	public Color getColorForRGB(RGB rgb) {
		Color color= (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(null, rgb);
			fColorTable.put(rgb, color);
		}
		
		return color;
	}

	
	/**
	 * Release all of the color resources held onto by the receiver.
	 */
	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}

	public TextAttribute getTextAttribute(String namedStyle){
		String key = null;
	
		if(namedStyle == KEYWORD)
			key = EDTUIPreferenceConstants.EDITOR_KEYWORD_COLOR;

		if(namedStyle == DEFAULT)
			key = EDTUIPreferenceConstants.EDITOR_DEFAULT_COLOR;

		if(namedStyle == LITERAL)
			key = EDTUIPreferenceConstants.EDITOR_STRING_COLOR;

		if(namedStyle == MULTI_LINE_COMMENT)
			key = EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR;

		if(namedStyle == SINGLE_LINE_COMMENT)
			key = EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR;

		return new TextAttribute(getColorForRGB(PreferenceConverter.getColor(preferenceStore, key)),
									null,	// No background color for Text Attributes
									(preferenceStore.getBoolean(key + EDTUIPreferenceConstants.EDITOR_BOLD_SUFFIX)) ? SWT.BOLD : SWT.NORMAL);
	}
	

}
