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
package org.eclipse.edt.ide.ui.internal.property.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

public class DefaultDeploymentDescriptorPropertyPage extends PropertyPage {
	private DefaultDeploymentDescriptorComposite[] dbdCmposites;
	private IResource thisResource;
	
	@Override
	protected Control createContents( Composite parent ) {
		initialize();
		createDescriptionLabel( parent );
		createResourceLabel( parent );
		Composite theComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		theComposite.setLayout(layout);
		dbdCmposites = new DefaultDeploymentDescriptorComposite[]{
				new DefaultDeploymentDescriptorComposite( theComposite, SWT.NONE, thisResource )
		};
		
		initializeValues();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.DEFAULT_DEPLOYMENT_DESCRIPTOR_CONTEXT);
		Dialog.applyDialogFont(parent);
		
		return theComposite;
	}
	
	@Override
	public boolean performOk() {
		if ( thisResource != null ) {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run( IProgressMonitor monitor ) throws InvocationTargetException
				{
					performOkAsync();
				}
			};
			
			try {
				new ProgressMonitorDialog( getControl().getShell() ).run( true, true, runnable );
			} catch ( InterruptedException e ) {
			} catch ( InvocationTargetException e ) {
				handle( e );
			}
		}
		return true;
	}
	
	protected void handle( InvocationTargetException e ) {
		IStatus error;
		Throwable target = e.getTargetException();
		if ( target instanceof CoreException ) {
			error = ((CoreException)target).getStatus();
		} else {
			String msg = target.getMessage();
			if ( msg == null ) {
				msg = "Internal error"; //$NON-NLS-1$
			}
			error = new Status( IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, 1, msg, target );
		}
		ErrorDialog.openError( getControl().getShell(), "Problems Occurred", null, error ); //$NON-NLS-1$
	}
	
	private void performOkAsync() {
		Display display = Display.getDefault();
		if ( display != null )
		{
			display.asyncExec( new Runnable() {
				public void run() {
					for (int i = 0; i < dbdCmposites.length; i++) {
						DefaultDeploymentDescriptorComposite dbdComposite = (DefaultDeploymentDescriptorComposite)dbdCmposites[i];
						DefaultDeploymentDescriptorComposite.DDSettings defaultDD = dbdComposite.getDefaultDDSettings();
						
						PartWrapper currentDD = defaultDD.getCurrentDD();
						if ( currentDD == null ) {
							DefaultDeploymentDescriptorUtility.setDefaultDeploymentDescriptor(thisResource, null);
						} else {
							String name = currentDD.getPartName();
							String file = currentDD.getPartPath();
							
							PartWrapper wrapper = new PartWrapper();
							wrapper.setPartName( name );
							wrapper.setPartPath( file );
							DefaultDeploymentDescriptorUtility.setDefaultDeploymentDescriptor(thisResource, wrapper);
						}
					}
				}
			} );
		}
	}

	
	/**
	 * Initializes this page.
	 */
	private void initialize() {
		thisResource = getSelectedResource();
		noDefaultAndApplyButton();
		setDescription( UINlsStrings.DefaultDDPropertiesPageLabelText );
	}
	
	/**
	 * Get the resource that has been selected
	 */
	private IResource getSelectedResource() {
		IResource resource = null;
		IAdaptable adaptable = getElement();

		if ( adaptable != null ) {
			resource = (IResource)adaptable.getAdapter( IResource.class );
		}
		
		EGLProject eglProj = (EGLProject)EGLCore.create(resource.getProject());
		eglProj.isOnEGLPath(resource);
		return resource;
	}
	
	private void createResourceLabel( Composite parent ) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		Label resourceLabel = new Label(composite, SWT.WRAP);
		resourceLabel.setFont(parent.getFont());
		resourceLabel.setText(UINlsStrings.DefaultDDPropertiesPageResourceLabelText);
		
		Label resource = new Label(composite, SWT.WRAP);
		resource.setFont(parent.getFont());
		
		String resourceText = thisResource.getFullPath().toString();
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) {
			resourceText = TextProcessor.process(resourceText, "/");
		}
		
		resource.setText(resourceText);
	}
	
	private void initializeValues() {
		for (int i = 0; i < dbdCmposites.length; i++) {
			DefaultDeploymentDescriptorComposite dbdComposite = (DefaultDeploymentDescriptorComposite)dbdCmposites[i];
			
			if ( dbdComposite != null ) {
				PartWrapper wrapper = getDefaultBuildDescriptor();
				DefaultDeploymentDescriptorComposite.DDSettings defaultDD = dbdComposite.getDefaultDDSettings();
				defaultDD.setInitialDD(wrapper);
			}
		}		
	}
	
	/**
	 * Get the default deployment descriptor for this type
	 */
	private PartWrapper getDefaultBuildDescriptor() {
		if ( thisResource != null ) {
			// get the default build descriptor
			return DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(thisResource );
		}

		return null;
	}
}
