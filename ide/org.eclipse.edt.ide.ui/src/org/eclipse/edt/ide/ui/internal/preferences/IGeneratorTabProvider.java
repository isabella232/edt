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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Contributors to the <code>edtGeneratorTabs</code> extension
 * point must specify an implementation of this interface.
 * <p>Clients may implement this interface.</p> 
 */

public interface IGeneratorTabProvider {
	public String getHelpId();
	public Image getImage();
	public Control getTabContent( Composite parent );
	public void setResource( IResource resource );
	public void setStatusChangeListener( IStatusChangeListener listener );
	
	public void performApply();
	public void performDefaults();
	public boolean performOk();
	public boolean performCancel();
	public void dispose();
	
}
