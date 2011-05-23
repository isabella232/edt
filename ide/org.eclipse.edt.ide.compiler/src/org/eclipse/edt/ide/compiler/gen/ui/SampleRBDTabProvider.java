/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.compiler.gen.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractGeneratorTabProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * This is a sample compiler tab provider class.
 */
public class SampleRBDTabProvider extends AbstractGeneratorTabProvider 
{
	/**
	 * Define the tab contents within the parent composite.
	 * 
	 * @param parent
	 * @return
	 */
	@Override
	public Control getTabContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		
		Label label = new Label(composite, SWT.LEFT);
		label.setText( "This is a sample RBD tab." );
		label.setLayoutData(data);
		
		return composite;
	}
	
	@Override
	public IEclipsePreferences getProjectPreferenceStore() {
		return null;
	}
	
	@Override
	public Image getImage() {
		//TODO images should be located in this plug-in
		return null;
//		ImageRegistry imageRegistry= Activator.getDefault().getImageRegistry();
//		return imageRegistry.get(PluginImages.IMG_OBJS_EGL_GENERATION);
	}
	
}
