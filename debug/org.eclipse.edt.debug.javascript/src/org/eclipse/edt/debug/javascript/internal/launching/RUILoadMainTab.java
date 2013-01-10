/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.javascript.internal.model.IRUILaunchConfigurationConstants;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugMessages;
import org.eclipse.edt.debug.ui.launching.AbstractEGLApplicationTab;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class RUILoadMainTab extends AbstractEGLApplicationTab implements ModifyListener, SelectionListener
{
	
	protected Label fProjectLabel;
	protected Text fProjectText;
	protected Button fProjectBrowseButton;
	protected Label fHandlerFileLabel;
	protected Text fHandlerFileText;
	protected Button fHandlerFileSearchButton;
	
	/**
	 * @see ILaunchConfigurationTab#createControl
	 */
	@Override
	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NONE );
		
		GridLayout layout = new GridLayout();
		composite.setLayout( layout );
		GridData gd;
		
		createVerticalSpacer( composite, 1 );
		
		Composite projectComposite = new Composite( composite, SWT.NONE );
		GridLayout projectLayout = new GridLayout();
		projectLayout.numColumns = 2;
		projectLayout.marginHeight = 0;
		projectLayout.marginWidth = 0;
		projectComposite.setLayout( projectLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		projectComposite.setLayoutData( gd );
		
		fProjectLabel = createLabel( projectComposite, RUIDebugMessages.rui_load_main_tab_project_label );
		gd = new GridData();
		gd.horizontalSpan = 2;
		fProjectLabel.setLayoutData( gd );
		
		fProjectText = createText( projectComposite );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fProjectText.setLayoutData( gd );
		fProjectText.addModifyListener( this );
		
		fProjectBrowseButton = createPushButton( projectComposite, RUIDebugMessages.rui_load_main_tab_browse_button, null );
		fProjectBrowseButton.addSelectionListener( this );
		
		createVerticalSpacer( composite, 1 );
		
		Composite handlerFileComposite = new Composite( composite, SWT.NONE );
		GridLayout handlerFileLayout = new GridLayout();
		handlerFileLayout.numColumns = 2;
		handlerFileLayout.marginHeight = 0;
		handlerFileLayout.marginWidth = 0;
		handlerFileComposite.setLayout( handlerFileLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		handlerFileComposite.setLayoutData( gd );
		
		fHandlerFileLabel = createLabel( handlerFileComposite, RUIDebugMessages.rui_load_main_tab_handler_file_label );
		gd = new GridData();
		gd.horizontalSpan = 2;
		fHandlerFileLabel.setLayoutData( gd );
		
		fHandlerFileText = createText( handlerFileComposite );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fHandlerFileText.setLayoutData( gd );
		fHandlerFileText.addModifyListener( this );
		
		fHandlerFileSearchButton = createPushButton( handlerFileComposite, RUIDebugMessages.rui_load_main_tab_search_button, null );
		fHandlerFileSearchButton.addSelectionListener( this );
		fHandlerFileSearchButton.setEnabled( false );
		
		createVerticalSpacer( composite, 1 );
		
		Composite handlerComposite = new Composite( composite, SWT.NONE );
		GridLayout handlerLayout = new GridLayout();
		handlerLayout.numColumns = 2;
		handlerLayout.marginHeight = 0;
		handlerLayout.marginWidth = 0;
		handlerComposite.setLayout( handlerLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		handlerComposite.setLayoutData( gd );
		
		Dialog.applyDialogFont( composite );
		setControl( composite );
		PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), IRUIHelpIDConstants.RUIHandlerLaunch );
	}
	
	/**
	 * Create a label widget.
	 * 
	 * @param parent The parent composite.
	 * @param labelText The label text.
	 */
	protected Label createLabel( Composite parent, String labelText )
	{
		Label label = new Label( parent, SWT.LEFT );
		label.setText( labelText );
		return label;
	}
	
	/**
	 * Create a text widget.
	 * 
	 * @param parent The parent composite.
	 */
	protected Text createText( Composite parent )
	{
		Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );
		text.setText( "" ); //$NON-NLS-1$
		text.addModifyListener( this );
		text.setEnabled( true );
		return text;
	}
	
	/**
	 * Create a button widget.
	 * 
	 * @param parent The parent composite.
	 * @param style The button style.
	 * @param label The button label.
	 */
	protected Button createButton( Composite parent, int style, String label )
	{
		Button button = new Button( parent, style );
		button.setText( label );
		return button;
	}
	
	/**
	 * Create a vertical spacer.
	 * 
	 * @param parent The parent composite.
	 * @param numColumns The number of columns to cover.
	 */
	@Override
	protected void createVerticalSpacer( Composite parent, int numColumns )
	{
		Label label = new Label( parent, SWT.NONE );
		GridData gridData = new GridData();
		gridData.horizontalSpan = numColumns;
		label.setLayoutData( gridData );
	}
	
	/**
	 * @see ModifyListener#modifyText(ModifyEvent)
	 */
	@Override
	public void modifyText( ModifyEvent e )
	{
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * @see SelectionListener#widgetSelected(SelectionEvent)
	 */
	@Override
	public void widgetSelected( SelectionEvent e )
	{
		if ( e.getSource() == fProjectBrowseButton )
		{
			handleBrowseButtonPushed();
		}
		else if ( e.getSource() == fHandlerFileSearchButton )
		{
			handleHandlerFileSearchButtonPushed();
		}
		
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * @see SelectionListener#widgetDefaultSelected(SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected( SelectionEvent e )
	{
		if ( e.getSource() == fProjectBrowseButton )
		{
			handleBrowseButtonPushed();
		}
		else if ( e.getSource() == fHandlerFileSearchButton )
		{
			handleHandlerFileSearchButtonPushed();
		}
		
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * Open the project browse dialog.
	 */
	protected void handleBrowseButtonPushed()
	{
		String project = browseForProject();
		if ( project != null )
		{
			fProjectText.setText( project );
		}
	}
	
	@Override
	protected String getProjectName()
	{
		return fProjectText.getText();
	}
	
	/**
	 * Open the handler file search dialog.
	 */
	protected void handleHandlerFileSearchButtonPushed()
	{
		IEGLElement part = browseForPart( IEGLSearchConstants.HANDLER, new String[] { "RUIHandler", "RUIWidget" }, //$NON-NLS-1$ //$NON-NLS-2$
				RUIDebugMessages.rui_load_main_tab_handler_file_search_title, RUIDebugMessages.rui_load_main_tab_handler_file_search_message );
		
		if ( part != null )
		{
			fHandlerFileText.setText( ((IEGLElement)part).getResource().getProjectRelativePath().toString() );
		}
	}
	
	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom( ILaunchConfiguration configuration )
	{
		updateProjectNameFromConfig( configuration );
		updateHandlerFileFromConfig( configuration );
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	@Override
	public boolean isValid( ILaunchConfiguration launchConfig )
	{
		
		setErrorMessage( null );
		setMessage( null );
		
		String projectName = fProjectText.getText().trim();
		if ( projectName.length() < 1 )
		{
			setErrorMessage( RUIDebugMessages.rui_load_launch_configuration_no_project_specified );
			fHandlerFileSearchButton.setEnabled( false );
			return false;
		}
		IProject project = getWorkspaceRoot().getProject( projectName );
		if ( project == null || !project.exists() )
		{
			setErrorMessage( RUIDebugMessages.rui_load_launch_configuration_invalid_project );
			fHandlerFileSearchButton.setEnabled( false );
			return false;
		}
		
		fHandlerFileSearchButton.setEnabled( true );
		
		String handlerFileName = fHandlerFileText.getText().trim();
		if ( handlerFileName.length() < 1 )
		{
			setErrorMessage( RUIDebugMessages.rui_load_launch_configuration_no_handler_file_specified );
			return false;
		}
		if ( !DebugUtil.isEGLFileName( handlerFileName ) )
		{
			setErrorMessage( RUIDebugMessages.rui_load_launch_configuration_invalid_handler_file );
			return false;
		}
		
		IFile file = project.getFile( handlerFileName );
		if ( !file.exists() )
		{
			setErrorMessage( RUIDebugMessages.rui_load_main_tab_handler_file_not_in_project );
			return false;
		}
		
		if ( !Util.isVESupportType( file ) )
		{
			setErrorMessage( RUIDebugMessages.rui_load_launch_configuration_file_not_handler );
			return false;
		}
		
		return true;
	}
	
	/**
	 * @see ILaunchConfigurationTab#dispose()
	 */
	@Override
	public void dispose()
	{
		fProjectLabel = null;
		fProjectText = null;
		fProjectBrowseButton = null;
		fHandlerFileSearchButton = null;
		fHandlerFileLabel = null;
		fHandlerFileText = null;
	}
	
	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply( ILaunchConfigurationWorkingCopy configuration )
	{
		String projectName = fProjectText.getText().trim();
		configuration.setAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName );
		configuration.setAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, fHandlerFileText.getText().trim() );
	}
	
	/**
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults( ILaunchConfigurationWorkingCopy configuration )
	{
		IResource resource = DebugUtil.getContext();
		if ( resource != null )
		{
			if ( DebugUtil.isEGLFileName( resource.getName() ) )
			{
				initializeProject( resource, configuration );
				initializeConfigName( resource, configuration );
				
				if ( resource.getType() == IResource.FILE && Util.isVESupportType( (IFile)resource ) )
				{
					initializeHandlerFile( resource, configuration );
				}
			}
			else
			{
				// Try to at least set the project correctly
				initializeProject( resource, configuration );
			}
			
		}
		else
		{
			configuration.setAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null );
		}
	}
	
	/**
	 * Initialize the handler file text field based on the current selection in the workspace.
	 * 
	 * @param resource The currently selected resource.
	 * @param configuration The launch configuration.
	 */
	protected void initializeHandlerFile( IResource resource, ILaunchConfigurationWorkingCopy configuration )
	{
		String pathStr = resource.getFullPath().toString();
		int index = pathStr.indexOf( '/', 1 );
		configuration.setAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, pathStr.substring( index + 1 ) );
	}
	
	/**
	 * Initialize the project text field based on the current selection in the workspace.
	 * 
	 * @param resource The currently selected resource.
	 * @param configuration The launch configuration.
	 */
	protected void initializeProject( IResource resource, ILaunchConfigurationWorkingCopy configuration )
	{
		IProject project = resource.getProject();
		String name = null;
		if ( project != null && project.exists() )
		{
			name = project.getName();
		}
		configuration.setAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, name );
	}
	
	/**
	 * Initialize the project text field from the configuration.
	 * 
	 * @param configuration The launch configuration.
	 */
	protected void updateProjectNameFromConfig( ILaunchConfiguration configuration )
	{
		try
		{
			fProjectText.setText( configuration.getAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ) ); //$NON-NLS-1$
		}
		catch ( CoreException e )
		{
			fProjectText.setText( "" ); //$NON-NLS-1$
		}
	}
	
	/**
	 * Initialize the handler file text field from the configuration.
	 * 
	 * @param configuration The launch configuration.
	 */
	protected void updateHandlerFileFromConfig( ILaunchConfiguration configuration )
	{
		try
		{
			fHandlerFileText.setText( configuration.getAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, "" ) ); //$NON-NLS-1$
		}
		catch ( CoreException e )
		{
			fHandlerFileText.setText( "" ); //$NON-NLS-1$
		}
	}
	
	/**
	 * Convenience method for getting the workspace root.
	 */
	protected IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName()
	{
		return RUIDebugMessages.rui_load_main_tab_name;
	}
}
