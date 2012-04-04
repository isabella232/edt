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
package org.eclipse.edt.debug.ui.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
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

public class EGLJavaMainApplicationTab extends AbstractEGLApplicationTab implements ModifyListener, SelectionListener
{
	protected Label fProjectLabel;
	protected Text fProjectText;
	protected Button fProjectBrowseButton;
	protected Label fProgramFileLabel;
	protected Text fProgramFileText;
	protected Button fProgramFileSearchButton;
	
	/**
	 * @see ILaunchConfigurationTab#createControl
	 */
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
		
		fProjectLabel = createLabel( projectComposite, EGLLaunchingMessages.egl_java_main_tab_project_label );
		gd = new GridData();
		gd.horizontalSpan = 2;
		fProjectLabel.setLayoutData( gd );
		
		fProjectText = createText( projectComposite );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fProjectText.setLayoutData( gd );
		fProjectText.addModifyListener( this );
		
		fProjectBrowseButton = createPushButton( projectComposite, EGLLaunchingMessages.egl_java_main_tab_browse_button, null );
		fProjectBrowseButton.addSelectionListener( this );
		
		createVerticalSpacer( composite, 1 );
		
		Composite programFileComposite = new Composite( composite, SWT.NONE );
		GridLayout programFileLayout = new GridLayout();
		programFileLayout.numColumns = 2;
		programFileLayout.marginHeight = 0;
		programFileLayout.marginWidth = 0;
		programFileComposite.setLayout( programFileLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		programFileComposite.setLayoutData( gd );
		
		fProgramFileLabel = createLabel( programFileComposite, EGLLaunchingMessages.egl_java_main_tab_program_file_label );
		gd = new GridData();
		gd.horizontalSpan = 2;
		fProgramFileLabel.setLayoutData( gd );
		
		fProgramFileText = createText( programFileComposite );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fProgramFileText.setLayoutData( gd );
		fProgramFileText.addModifyListener( this );
		
		fProgramFileSearchButton = createPushButton( programFileComposite, EGLLaunchingMessages.egl_java_main_tab_search_button, null );
		fProgramFileSearchButton.addSelectionListener( this );
		fProgramFileSearchButton.setEnabled( false );
		
		createVerticalSpacer( composite, 1 );
		
		Composite programComposite = new Composite( composite, SWT.NONE );
		GridLayout programLayout = new GridLayout();
		programLayout.numColumns = 2;
		programLayout.marginHeight = 0;
		programLayout.marginWidth = 0;
		programComposite.setLayout( programLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		programComposite.setLayoutData( gd );
		
		Dialog.applyDialogFont( composite );
		setControl( composite );
		PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), IEGLJavaLaunchConstants.HELP_ID_PROGRAM_LAUNCH );
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
	public void modifyText( ModifyEvent e )
	{
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * @see SelectionListener#widgetSelected(SelectionEvent)
	 */
	public void widgetSelected( SelectionEvent e )
	{
		if ( e.getSource() == fProjectBrowseButton )
		{
			handleBrowseButtonPushed();
		}
		else if ( e.getSource() == fProgramFileSearchButton )
		{
			handleProgramFileSearchButtonPushed();
		}
		
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * @see SelectionListener#widgetDefaultSelected(SelectionEvent)
	 */
	public void widgetDefaultSelected( SelectionEvent e )
	{
		if ( e.getSource() == fProjectBrowseButton )
		{
			handleBrowseButtonPushed();
		}
		else if ( e.getSource() == fProgramFileSearchButton )
		{
			handleProgramFileSearchButtonPushed();
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
	 * Open the program file search dialog.
	 */
	protected void handleProgramFileSearchButtonPushed()
	{
		IEGLElement part = browseForPart( IEGLSearchConstants.PROGRAM, null, EGLLaunchingMessages.egl_java_main_tab_program_file_search_title,
				EGLLaunchingMessages.egl_java_main_tab_program_file_search_message );
		
		if ( part != null )
		{
			fProgramFileText.setText( ((IEGLElement)part).getResource().getProjectRelativePath().toString() );
		}
	}
	
	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom( ILaunchConfiguration configuration )
	{
		updateProjectNameFromConfig( configuration );
		updateProgramFileFromConfig( configuration );
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid( ILaunchConfiguration launchConfig )
	{
		setErrorMessage( null );
		setMessage( null );
		
		String projectName = fProjectText.getText().trim();
		if ( projectName.length() < 1 )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_launch_configuration_no_project_specified );
			fProgramFileSearchButton.setEnabled( false );
			return false;
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
		if ( project == null || !project.exists() )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_launch_configuration_invalid_project );
			fProgramFileSearchButton.setEnabled( false );
			return false;
		}
		
		fProgramFileSearchButton.setEnabled( true );
		
		String programFileName = fProgramFileText.getText().trim();
		if ( programFileName.length() < 1 )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_launch_configuration_no_program_file_specified );
			return false;
		}
		if ( !DebugUtil.isEGLFileName( programFileName ) )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_launch_configuration_invalid_program_file );
			return false;
		}
		
		IFile file = project.getFile( programFileName );
		if ( !file.exists() )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_tab_program_file_not_in_project );
			return false;
		}
		
		if ( !isProgram( file ) )
		{
			setErrorMessage( EGLLaunchingMessages.egl_java_main_launch_configuration_file_not_program );
			return false;
		}
		
		return true;
	}
	
	/**
	 * @see ILaunchConfigurationTab#dispose()
	 */
	public void dispose()
	{
		fProjectLabel = null;
		fProjectText = null;
		fProjectBrowseButton = null;
		fProgramFileSearchButton = null;
		fProgramFileLabel = null;
		fProgramFileText = null;
	}
	
	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply( ILaunchConfigurationWorkingCopy configuration )
	{
		String projectName = fProjectText.getText().trim();
		configuration.setAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, projectName );
		configuration.setAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, fProgramFileText.getText().trim() );
		
		// Add Java launch attributes
		EGLJavaLaunchUtils.addJavaAttributes( projectName, configuration );
	}
	
	private boolean isProgram( IResource resource )
	{
		IEGLFile file = (IEGLFile)EGLCore.create( resource );
		IPart part = file.getPart( new Path( resource.getName() ).removeFileExtension().toString() );
		return part != null && part.exists() && ((SourcePart)part).isProgram();
	}
	
	/**
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults( ILaunchConfigurationWorkingCopy configuration )
	{
		IResource resource = DebugUtil.getContext();
		if ( resource != null )
		{
			if ( DebugUtil.isEGLFileName( resource.getName() ) )
			{
				initializeProject( resource, configuration );
				initializeConfigName( resource, configuration );
				
				if ( isProgram( resource ) )
				{
					initializeProgramFile( resource, configuration );
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
			configuration.setAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, (String)null );
		}
	}
	
	/**
	 * Initialize the program file text field based on the current selection in the workspace.
	 * 
	 * @param resource The currently selected resource.
	 * @param configuration The launch configuration.
	 */
	protected void initializeProgramFile( IResource resource, ILaunchConfigurationWorkingCopy configuration )
	{
		String pathStr = resource.getFullPath().toString();
		int index = pathStr.indexOf( '/', 1 );
		configuration.setAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, pathStr.substring( index + 1 ) );
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
		configuration.setAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, name );
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
			fProjectText.setText( configuration.getAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, "" ) ); //$NON-NLS-1$
		}
		catch ( CoreException e )
		{
			fProjectText.setText( "" ); //$NON-NLS-1$
		}
	}
	
	/**
	 * Initialize the program file text field from the configuration.
	 * 
	 * @param configuration The launch configuration.
	 */
	protected void updateProgramFileFromConfig( ILaunchConfiguration configuration )
	{
		try
		{
			fProgramFileText.setText( configuration.getAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, "" ) ); //$NON-NLS-1$
		}
		catch ( CoreException e )
		{
			fProgramFileText.setText( "" ); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName()
	{
		return EGLLaunchingMessages.egl_java_main_tab_name;
	}
}
