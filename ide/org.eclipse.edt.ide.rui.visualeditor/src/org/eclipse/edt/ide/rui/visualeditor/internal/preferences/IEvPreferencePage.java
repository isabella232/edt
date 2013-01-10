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
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;


/**
 * This interface describes the generalized behavior of all Ev Preference
 * page tabs. All tabs must implement this interface and it allows for the 
 * removal of some nasty conditional logic from the <code>EvPreferencePage</code>
 * class.
 */
public interface IEvPreferencePage {
	
	/**
	 * Returns the help ID for the preference page.
	 */
	public String getHelpID();
	
	/**
	 * Sets the default settings for the preference tab
	 */
	public void performDefaults();

}
