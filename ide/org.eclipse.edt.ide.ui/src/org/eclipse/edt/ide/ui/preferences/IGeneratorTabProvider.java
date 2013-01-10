/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.preferences;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
	public String getCompilerId();	
	public void setCompilerId( String compilerId );
	public String getGeneratorId(); 	
	public void setGeneratorId( String generatorId );
	
	public String getTitle();
	public void setTitle( String title );
	public String getHelpId();
	public Image getImage();
	public Control getTabContent( Composite parent );
	public void setResource( IResource resource );
	public void setStatusChangeListener( IStatusChangeListener listener );
	
	public void performApply();
	public void performDefaults();
	public boolean performOk();
	public boolean performCancel();
	public void performRemoval();
	public void performAddition();
	
	public void dispose();
	public IEclipsePreferences getProjectPreferenceStore();
	public void removePreferencesForAResource();
	public void removePreferencesForAllResources();
	
	
}
