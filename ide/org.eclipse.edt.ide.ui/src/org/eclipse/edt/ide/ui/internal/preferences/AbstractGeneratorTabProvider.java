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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Base implementation of IGeneratorTabProvider intended to be 
 * subclassed by clients.
 */
public class AbstractGeneratorTabProvider implements IGeneratorTabProvider
{
	private String title;
	private String helpId = "";
	private Image image;
	private IResource resource;
	private IStatusChangeListener statusChangeListener;

	/**
	 * Define the tab contents within the parent composite.
	 * 
	 * @param parent
	 * @return
	 */
	public Control getTabContent(Composite parent) {	
		ScrolledComposite sComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
  		Composite composite = new Composite(sComposite, SWT.NONE);
  		sComposite.setContent( composite );

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
		
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return sComposite;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle( String title ) {
		this.title = title;
	}
	
	public String getHelpId() {
		return this.helpId;
	}
	
	public void setHelpId( String helpId ) {
		this.helpId = helpId;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public void setImage( Image image ) {
		this.image = image;
	}
	
	/**
	 * Returns the resource, which should have been set if this tab is
	 * displayed in a resource's (project, package, or file) properties.  
	 * Resource should be null when this tab is displayed in workspace 
	 * preferences.
	 *
	 * @return
	 */
	public IResource getResource() {
		return this.resource;
	}
	
	/**
	 * Sets the resource.  This method should be called when this 
	 * tab is displayed in a resource's properties.  Resource should
	 * be null when this tab is displayed in workspace preferences.
	 * 
	 * @param resource
	 */
	public void setResource( IResource resource ) {
		this.resource = resource;
	}
	
	public IStatusChangeListener getStatusChangeListener() {
		return this.statusChangeListener;
	}
	
	public void setStatusChangeListener( IStatusChangeListener listener ) {
		this.statusChangeListener = listener;
	}
	
	public void performApply() {
		performOk();
	}
	
	public void performDefaults() {
	}
	
	public boolean performCancel() {
		return true;
	}
	
	public boolean performOk() {
		return true;
	}	
	
	public void dispose() {
	}
	
}
