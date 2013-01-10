/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui;

import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class BaseDebugPreferencePage extends AbstractPreferencePage
{
	@Override
	protected Control createContents( Composite parent )
	{
		Composite composite = createComposite( parent, 1 );
		
		createLabel( composite, EDTDebugUIMessages.BasePreferencePageMessage );
		
		// spacer
		createLabel( composite, "" ); //$NON-NLS-1$
		
		PreferenceLinkArea link = new PreferenceLinkArea( composite, SWT.NONE, "org.eclipse.debug.ui.DebugPreferencePage", //$NON-NLS-1$
				EDTDebugUIMessages.BasePreferencePageLink, (IWorkbenchPreferenceContainer)getContainer(), null );
		link.getControl().setFont( composite.getFont() );
		
		return composite;
	}
}
