/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public class RUILoadTabGroup extends AbstractLaunchConfigurationTabGroup
{
	
	/**
	 * @see ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog, String)
	 */
	@Override
	public void createTabs( ILaunchConfigurationDialog dialog, String mode )
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[ 4 ];
		tabs[ 0 ] = new RUILoadMainTab();
		tabs[ 1 ] = new SourceLookupTab();
		tabs[ 2 ] = new EnvironmentTab();
		tabs[ 3 ] = new CommonTab();
		setTabs( tabs );
	}
}
