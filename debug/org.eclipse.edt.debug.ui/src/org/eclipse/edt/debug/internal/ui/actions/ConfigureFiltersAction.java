/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ConfigureFiltersAction extends AbstractHandler
{
	@Override
	public Object execute( ExecutionEvent event ) throws ExecutionException
	{
		PreferencesUtil.createPreferenceDialogOn( EDTDebugUIPlugin.getShell(), "org.eclipse.edt.debug.ui.eglJavaPreferencePage", null, null ).open(); //$NON-NLS-1$
		return null;
	}
}
