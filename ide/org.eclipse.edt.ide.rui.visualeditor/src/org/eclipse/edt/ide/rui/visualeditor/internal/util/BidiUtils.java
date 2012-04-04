/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;


public class BidiUtils {
	private static Combo					_comboWidgetOrientation				= null;
	private static Combo					_comboTextLayout					= null;
	private static Combo					_comboTextDirection					= null;
	private static Combo					_comboSymSwapping					= null;
	private static Combo					_comboNumSwapping					= null;
	
	public static BidiFormat createBidiControls(Composite parentComposite, BidiFormat bidiFormat){
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL );
		parentComposite.setLayoutData(gridData);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parentComposite.setLayout( gridLayout );

		if (parentComposite instanceof Group)
			((Group) parentComposite).setText( Messages.NL_Bidirectional_options );
		
		parentComposite.setFocus();

		// Widget Orientation
		//-----------------
		Label label = new Label( parentComposite, SWT.NULL );
		label.setText( Messages.NL_BIDI_Widget_OrientationXcolonX );
		label.setLayoutData( new GridData() );

		_comboWidgetOrientation = new Combo(parentComposite, SWT.READ_ONLY);
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_comboWidgetOrientation.setLayoutData( gridData );
		_comboWidgetOrientation.add(Messages.NL_BIDI_LTR);
		_comboWidgetOrientation.add(Messages.NL_BIDI_RTL);
		_comboWidgetOrientation.setText(bidiFormat.getWidgetOrientation());

		// Text Layout
		//-----------------
		label = new Label( parentComposite, SWT.NULL );
		label.setText( Messages.NL_BIDI_Text_LayoutXcolonX );
		label.setLayoutData( new GridData() );

		_comboTextLayout = new Combo(parentComposite, SWT.READ_ONLY);  
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_comboTextLayout.setLayoutData( gridData );
		_comboTextLayout.add(Messages.NL_BIDI_Logical);
		_comboTextLayout.add(Messages.NL_BIDI_Visual);
		_comboTextLayout.setText(bidiFormat.getTextLayout());
		
		// Text Direction
		//-----------------
		label = new Label( parentComposite, SWT.NULL );
		label.setText( Messages.NL_BIDI_Reverse_Text_DirectionXcolonX );
		label.setLayoutData( new GridData() );

		_comboTextDirection = new Combo(parentComposite, SWT.READ_ONLY);  
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_comboTextDirection.setLayoutData( gridData );
		_comboTextDirection.add(Messages.NL_BIDI_Yes);
		_comboTextDirection.add(Messages.NL_BIDI_No);
		_comboTextDirection.setText(bidiFormat.getReverseTextDirection());
		
		return new BidiFormat (_comboWidgetOrientation.getText(), _comboTextLayout.getText(),
				_comboTextDirection.getText(), "Yes", "Yes");

	}
	
	public static void updateBidiPropertiesEnablement(){
		boolean isBidi = isBidi();
		_comboWidgetOrientation.setEnabled(isBidi);
		_comboTextLayout.setEnabled(isBidi);
		_comboTextDirection.setEnabled(isBidi);
		/*_comboSymSwapping.setEnabled(isBidi);
		_comboNumSwapping.setEnabled(isBidi);*/
		if (!isBidi){
			performDefaults();
		}
	}
	public static BidiFormat getDefaultBidiFormat (){
		return new BidiFormat(
				EvConstants.PREFERENCE_DEFAULT_BIDI_WIDGET_ORIENTATION,
				EvConstants.PREFERENCE_DEFAULT_BIDI_TEXT_LAYOUT,
				EvConstants.PREFERENCE_DEFAULT_BIDI_REVERSE_TEXT_DIRECTION,
				EvConstants.PREFERENCE_DEFAULT_BIDI_SYM_SWAPPING,
				EvConstants.PREFERENCE_DEFAULT_BIDI_NUM_SWAPPING);
	}
	public static void performDefaults(){
		BidiFormat bidiFormat = getDefaultBidiFormat();
		_comboWidgetOrientation.setText(bidiFormat.getWidgetOrientation());
		_comboTextLayout.setText(bidiFormat.getTextLayout());
		_comboTextDirection.setText(bidiFormat.getReverseTextDirection());
/*		_comboSymSwapping.setText(bidiFormat.getSymmetricSwapping());
		_comboNumSwapping.setText(bidiFormat.getNumericSwapping());*/
	}
	
	public static void performOk() {
		
		BidiFormat bidiFormat = getBidiFormat();
		
		// Widget Orientation
		//-----------------
		String currentProp = EvPreferences.getString(EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION);
		
		if (!bidiFormat.getWidgetOrientation().equals(currentProp))
			EvPreferences.setString(EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION, bidiFormat.getWidgetOrientation());
		
		// Text Layout
		//-----------------
		currentProp = EvPreferences.getString(EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT);
		
		if (!bidiFormat.getTextLayout().equals(currentProp))
			EvPreferences.setString(EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT, bidiFormat.getTextLayout());
		
		// Text Direction
		//-----------------
		currentProp = EvPreferences.getString(EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION);
		
		if (!bidiFormat.getReverseTextDirection().equals(currentProp))
			EvPreferences.setString(EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION, bidiFormat.getReverseTextDirection());
		
		// Symmetric Swapping
		//-----------------
		currentProp = EvPreferences.getString(EvConstants.PREFERENCE_BIDI_SYM_SWAPPING);
		
		if(!bidiFormat.getSymmetricSwapping().equals(currentProp))
			EvPreferences.setString(EvConstants.PREFERENCE_BIDI_SYM_SWAPPING, bidiFormat.getSymmetricSwapping());
		
		
		// Numeric Swapping
		//-----------------
		currentProp = EvPreferences.getString(EvConstants.PREFERENCE_BIDI_NUM_SWAPPING);
		
		if(!bidiFormat.getNumericSwapping().equals(currentProp))
			EvPreferences.setString(EvConstants.PREFERENCE_BIDI_NUM_SWAPPING, bidiFormat.getNumericSwapping());
	}
	
	public static BidiFormat getBidiFormatFromPreferences(){
		return new BidiFormat(
				EvPreferences.getString(EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION),
				EvPreferences.getString(EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT),
				EvPreferences.getString(EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION),
				EvPreferences.getString(EvConstants.PREFERENCE_BIDI_SYM_SWAPPING),
				EvPreferences.getString(EvConstants.PREFERENCE_BIDI_NUM_SWAPPING)
				);
	}
	public static BidiFormat getBidiFormat(){
		return new BidiFormat(
				_comboWidgetOrientation.getText(),
				_comboTextLayout.getText(),
				_comboTextDirection.getText(),
/*				_comboSymSwapping.getText(),
				_comboNumSwapping.getText(),*/"Yes","Yes");
	}

	public static String updateTemplateWithBidi(String strWidgetCreationTemplate, BidiFormat bidiFormat) {
		String resultTemplate = strWidgetCreationTemplate.substring(0, strWidgetCreationTemplate.lastIndexOf('}'));
		if (!bidiFormat.isDefaultBidiFormat()){
			if (!resultTemplate.trim().endsWith("{"))
				resultTemplate += ", ";
			if (!bidiFormat.isDefaultWidgetOrientation())
				resultTemplate += addWidgetOrientation(bidiFormat.getWidgetOrientation());
			if (!bidiFormat.isDefaultTextLayout())
				resultTemplate += addTextLayout(bidiFormat.getTextLayout());
			if(!bidiFormat.isDefaultReverseTextDirection())
				resultTemplate += addReverseTextDirection(bidiFormat.getReverseTextDirection());
/*			if(!bidiFormat.isDefaultSymmetricSwapping())
				resultTemplate += addSymmetricSwapping(bidiFormat.getSymmetricSwapping());
			if(!bidiFormat.isDefaultNumericSwapping())
				resultTemplate += addNumericSwapping(bidiFormat.getNumericSwapping());*/
			if (resultTemplate.trim().endsWith(","))
				resultTemplate = resultTemplate.trim().substring(0, resultTemplate.lastIndexOf(","));
		}
		resultTemplate += strWidgetCreationTemplate.substring(strWidgetCreationTemplate.lastIndexOf('}'));
		return resultTemplate;
	}

	private static String addNumericSwapping(String numericSwapping) {
		return EvConstants.FIELD_NAME_BIDI_NUM_SWAPPING + " = \"" + numericSwapping + "\"" + ", ";
	}

	private static String addSymmetricSwapping(String symmetricSwapping) {
		return EvConstants.FIELD_NAME_BIDI_SYM_SWAPPING + " = \"" + symmetricSwapping + "\"" + ", ";

	}

	private static String addReverseTextDirection(String reverseTextDirection) {
		return EvConstants.FIELD_NAME_BIDI_REVERSE_TEXT_DIRECTION + " = \"" + reverseTextDirection + "\"" + ", ";
	}

	private static String addTextLayout(String textLayout) {
		return EvConstants.FIELD_NAME_BIDI_TEXT_LAYOUT + " = \"" + textLayout + "\"" + ", ";
	}

	private static String addWidgetOrientation(String widgetOrientation) {
		return EvConstants.FIELD_NAME_BIDI_WIDGET_ORIENTATION + " = \"" + widgetOrientation + "\"" + ", ";
	}
	
	public static boolean isBidi(){
		return false;
//TODO EDT BIDI
//		return EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getBoolean(EDTCoreIDEPlugin.BIDI_ENABLED_OPTION);
	}
	
	//@bd1a Start
	public static boolean isRtlOrientation() {
		 int orientation = org.eclipse.jface.window.Window.getDefaultOrientation();
		 if (orientation == SWT.RIGHT_TO_LEFT)
			 return true;
		 else 
			 return false;
	 }	
	//@bd1a End
}
