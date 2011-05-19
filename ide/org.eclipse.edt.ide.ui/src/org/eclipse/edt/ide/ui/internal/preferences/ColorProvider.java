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

package org.eclipse.edt.ide.ui.internal.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.ui.internal.EGLPreferenceConstants;
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
//	public static final String SYSTEM_WORD = "SYSTEM_WORD";//$NON-NLS-1$
	
	protected Map fColorTable = new HashMap(10);
	protected IPreferenceStore preferenceStore = null;
	
	public ColorProvider(IPreferenceStore prefs) {
		preferenceStore = prefs;
		
		// All Color Preferences should be set in EGLUIPlugin
			// If not...set them
		if (!preferenceStore.contains(EGLPreferenceConstants.EDITOR_DEFAULT_COLOR))
			EGLPreferenceConstants.initializeDefaultEGLColorPreferences(preferenceStore);
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

//	private static RGB toRGB(String anRGBString) {
//		RGB result = null;
//		if (anRGBString.length() > 6 && anRGBString.charAt(0) == '#') {
//			int r = 0;
//			int g = 0;
//			int b = 0;
//			try {
//				r = Integer.valueOf(anRGBString.substring(1, 3), 16).intValue();
//				g = Integer.valueOf(anRGBString.substring(3, 5), 16).intValue();
//				b = Integer.valueOf(anRGBString.substring(5, 7), 16).intValue();
//				result = new RGB(r, g, b);
//			} catch (NumberFormatException nfExc) {
//				EGLUIPlugin.log(nfExc);//$NON-NLS-1$
//			}
//		}
//		return result;
//	}
	
	public TextAttribute getTextAttribute(String namedStyle){
		String key = null;
	
		if(namedStyle == KEYWORD)
			key = EGLPreferenceConstants.EDITOR_KEYWORD_COLOR;

		if(namedStyle == DEFAULT)
			key = EGLPreferenceConstants.EDITOR_DEFAULT_COLOR;

		if(namedStyle == LITERAL)
			key = EGLPreferenceConstants.EDITOR_STRING_COLOR;

		if(namedStyle == MULTI_LINE_COMMENT)
			key = EGLPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR;

		if(namedStyle == SINGLE_LINE_COMMENT)
			key = EGLPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR;

//		if(namedStyle == SYSTEM_WORD)
//			key = EGLPreferenceConstants.EDITOR_SYSTEM_WORD_COLOR;

		return new TextAttribute(getColorForRGB(PreferenceConverter.getColor(preferenceStore, key)),
									null,	// No background color for Text Attributes
									(preferenceStore.getBoolean(key + EGLPreferenceConstants.EDITOR_BOLD_SUFFIX)) ? SWT.BOLD : SWT.NORMAL);
	}
	

}
