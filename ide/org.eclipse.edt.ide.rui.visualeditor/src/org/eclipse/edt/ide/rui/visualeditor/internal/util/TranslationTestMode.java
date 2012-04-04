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

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class TranslationTestMode {

	/**
	 * 
	 */
	protected void displayColorSelectionDialog( Shell shell ){
		ColorSelectionDialog colorDialog = new ColorSelectionDialog( shell, "" );
		colorDialog.open();
	}
	
	/**
	 * 
	 */
	protected void displayFunctionNameDialog( Shell shell ){
		EvFunctionNameDialog dialog = new EvFunctionNameDialog( shell, null, null );
		dialog.setTranslationTestMode( true );
		dialog.open();
	}
	
	/**
	 * 
	 */
	protected void displayStringArrayDialog( Shell shell ){
		ArrayList list = new ArrayList();
		list.add( "111" );
		list.add( "222" );
		list.add( "333" );
		list.add( "444" );
		
		StringArrayDialog dialog = new StringArrayDialog( shell, list, "" );
		dialog.open();
	}
	
	/**
	 * 
	 */
	protected void displayWidgetNameDialog( Shell shell ){
		EvWidgetNameDialog dialog = new EvWidgetNameDialog( shell, "", null );
		dialog.setTranslationTestMode( true );
		dialog.open();
	}
	
	/**
	 * 
	 */
	public void execute( EvEditor editor ){
		Shell shell = Display.getCurrent().getActiveShell();
		
		displayWidgetNameDialog( shell );
		displayFunctionNameDialog( shell );
		displayColorSelectionDialog( shell );
		displayStringArrayDialog( shell );
	}	
}
