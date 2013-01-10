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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;


public class BidiFormat {
	private String widgetOrientation;
	private String textLayout;
	private String reverseTextDirection;
	private String symmetricSwapping;
	private String numericSwapping;

	public BidiFormat (String widgetOrientation, String textLayout,
			String reverseTextDirection, String symmetricSwapping, String numericSwapping){
		this.widgetOrientation = widgetOrientation;
		this.textLayout = textLayout;
		this.reverseTextDirection = reverseTextDirection;
		this.symmetricSwapping = symmetricSwapping;
		this.numericSwapping = numericSwapping;
	}

	public String getWidgetOrientation() {
		return widgetOrientation;
	}

	public String getTextLayout() {
		return textLayout;
	}

	public String getReverseTextDirection() {
		return reverseTextDirection;
	}

	public String getSymmetricSwapping() {
		return symmetricSwapping;
	}
	
	public String getNumericSwapping() {
		return numericSwapping;
	}

	public boolean isDefaultBidiFormat() {
		if (isDefaultWidgetOrientation() &&
			isDefaultTextLayout() &&
			isDefaultReverseTextDirection() &&
			isDefaultSymmetricSwapping() &&
			isDefaultNumericSwapping())
			return true;
		return false;
	}

	public boolean isDefaultNumericSwapping() {
		return (EvConstants.PREFERENCE_DEFAULT_BIDI_NUM_SWAPPING.equals(numericSwapping));
	}

	public boolean isDefaultSymmetricSwapping() {
		return (EvConstants.PREFERENCE_DEFAULT_BIDI_SYM_SWAPPING.equals(symmetricSwapping));
	}

	public boolean isDefaultReverseTextDirection() {
		return (EvConstants.PREFERENCE_DEFAULT_BIDI_REVERSE_TEXT_DIRECTION.equals(reverseTextDirection));
	}

	public boolean isDefaultTextLayout() {
		return (EvConstants.PREFERENCE_DEFAULT_BIDI_TEXT_LAYOUT.equals(textLayout));
	}

	public boolean isDefaultWidgetOrientation() {
		return (EvConstants.PREFERENCE_DEFAULT_BIDI_WIDGET_ORIENTATION.equals(widgetOrientation));
	}


	
}
