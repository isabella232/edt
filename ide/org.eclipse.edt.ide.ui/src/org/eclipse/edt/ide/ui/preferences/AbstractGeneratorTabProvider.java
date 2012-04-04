/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
public abstract class AbstractGeneratorTabProvider implements IGeneratorTabProvider
{
	/**
	 * The composite for the tab.  Once a composite is instantiated, it
	 * will be reused until the tab provider is disposed.  If data is entered on
	 * a TabItem for a generator and the TabItem is then disposed (i.e. generator is
	 * deselected), the same data will be displayed from the tab provider if a new 
	 * TabItem is instantiated (i.e. generator is reselected).
	 */
	private Composite composite;
	
	/**
	 * The id of the compiler associated with a general tab.  This tab will be
	 * displayed whenever the compiler is selected.  It is not associated with a
	 * specific generator.  Null if not specified in the extension (i.e. the
	 * tab is a generatorTab).
	 */
	private String compilerId;
	
	/**
	 * The id of the generator associated with this tab.  It will be displayed
	 * whenever the generator is selected.  Null if not specified in the extension
	 * (i.e. the tab is a generalTab).
	 */
	private String generatorId;
	
	/**
	 * The title that is displayed in the generator tab.  This title will be the
	 * generator name.
	 */
	private String title;
	
	/**
	 *  A context-sensitive help id for this generator tab.
	 */
	private String helpId = "";
	
	/**
	 *  An image for this generator tab.
	 */
	private Image image;
	
	/**
	 * The project, folder, or file corresponding to this generator tab.  Null
	 * if the tab is shown in the workspace preferences.
	 */
	private IResource resource;
	
	/**
	 *  A listener that allows messages to be displayed on the preference or property
	 *   page that contains this generator tab.
	 */
	private IStatusChangeListener statusChangeListener;
	
	/**
	 * Define the tab contents within the parent composite.
	 * 
	 * @param parent
	 * @return
	 */
	public Control getTabContent( Composite parent ) {	
		if( getComposite() == null ) {
			setComposite( new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER) );
			Composite nestedComposite = new Composite( getComposite(), SWT.NONE );
			((ScrolledComposite)getComposite()).setContent( nestedComposite );

			//GridLayout
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			nestedComposite.setLayout(layout);
			nestedComposite.setFont(parent.getFont());

			//GridData
			GridData data = new GridData(GridData.FILL);
			data.horizontalIndent = 0;
			data.verticalAlignment = GridData.FILL;
			data.horizontalAlignment = GridData.FILL;
			nestedComposite.setLayoutData(data);

			nestedComposite.setSize(nestedComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		return getComposite();
	}
	
	public Composite getComposite() {
		return this.composite;
	}
	
	public void setComposite( Composite composite ) {
		this.composite = composite;
	}
	
	public String getCompilerId() {
		return this.compilerId;
	}
	
	public void setCompilerId( String compilerId ) {
		this.compilerId = compilerId;
	}
	
	public String getGeneratorId() {
		return this.generatorId;
	}
	
	public void setGeneratorId( String generatorId ) {
		this.generatorId = generatorId;
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
	
	public void performRemoval() {
	}
	
	public void performAddition(){
	}
	
	public void dispose() {
	}
	
	/**
	 * Return the preference store for a project.  Set this to null 
	 * if the generator tab belongs to the workspace preferences or
	 * the tab is not writing to the project preference store.
	 */
	public abstract IEclipsePreferences getProjectPreferenceStore();
		
	/**
	 * Remove any preferences that this tab previously stored in a 
	 * resource's preference store.
	 */
	public void removePreferencesForAResource() {
	}
	
	/**
	 * Remove ALL preferences that this tab previously stored in a 
	 * resource's preference store.
	 */
	public void removePreferencesForAllResources() {
	}

}
