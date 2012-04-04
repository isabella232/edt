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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.dialogs.PartSelectionDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public abstract class AbstractEGLApplicationTab extends AbstractLaunchConfigurationTab
{
	protected final ProjectLabelProvider projectLabelProvider = new ProjectLabelProvider();
	
	protected IEGLElement browseForPart( int partTypes, final String[] subTypes, String title, String message )
	{
		IEGLProject project = getEGLProject();
		IEGLElement[] elements = null;
		if ( project == null || !project.exists() )
		{
			IEGLModel model = EGLCore.create( ResourcesPlugin.getWorkspace().getRoot() );
			if ( model != null )
			{
				try
				{
					elements = model.getEGLProjects();
				}
				catch ( EGLModelException e )
				{
					EDTDebugUIPlugin.log( e );
				}
			}
		}
		else
		{
			elements = new IEGLElement[] { project };
		}
		
		if ( elements == null )
		{
			elements = new IEGLElement[ 0 ];
		}
		
		PartSelectionDialog dialog = new PartSelectionDialog( getShell(), getLaunchConfigurationDialog(), partTypes, null,
				SearchEngine.createEGLSearchScope( elements, false ) ) {
			@Override
			protected int addParts( ArrayList partsList, IEGLSearchScope scope, int elementKinds, String subType )
			{
				int length = subTypes == null
						? 0
						: subTypes.length;
				if ( length < 2 )
				{
					return super.addParts( partsList, scope, elementKinds, length == 0
							? null
							: subTypes[ 0 ] );
				}
				
				// For multiple subtypes, add parts in multiple passes. Also make sure not to add duplicates.
				Set allParts = new HashSet();
				for ( String nextSubType : subTypes )
				{
					ArrayList nextParts = new ArrayList();
					if ( super.addParts( nextParts, scope, elementKinds, nextSubType ) == CANCEL )
					{
						return CANCEL;
					}
					
					allParts.addAll( nextParts );
				}
				partsList.addAll( allParts );
				
				return OK;
			}
		};
		dialog.setTitle( title );
		dialog.setMessage( message );
		dialog.setMultipleSelection( false );
		dialog.setFilter( "*" ); //$NON-NLS-1$
		if ( dialog.open() == Window.OK )
		{
			Object result = dialog.getFirstResult();
			if ( result instanceof IEGLElement )
			{
				return (IEGLElement)result;
			}
		}
		return null;
	}
	
	protected String browseForProject()
	{
		ElementListSelectionDialog dialog = new ElementListSelectionDialog( getShell(), projectLabelProvider );
		dialog.setTitle( EGLLaunchingMessages.egl_java_main_tab_project_browse_title );
		dialog.setMessage( EGLLaunchingMessages.egl_java_main_tab_project_browse_message );
		
		try
		{
			dialog.setElements( EGLCore.create( ResourcesPlugin.getWorkspace().getRoot() ).getEGLProjects() );
		}
		catch ( EGLModelException e )
		{
			EDTDebugUIPlugin.log( e );
		}
		
		IEGLProject eglProject = getEGLProject();
		if ( eglProject != null )
		{
			dialog.setInitialSelections( new Object[] { eglProject } );
		}
		
		if ( dialog.open() == Window.OK )
		{
			IEGLProject project = (IEGLProject)dialog.getFirstResult();
			if ( project != null )
			{
				return project.getElementName();
			}
			return ""; //$NON-NLS-1$
		}
		return null;
	}
	
	protected IEGLProject getEGLProject()
	{
		String projectName = getProjectName();
		if ( projectName == null )
		{
			return null;
		}
		
		projectName = projectName.trim();
		if ( projectName.length() < 1 )
		{
			return null;
		}
		return EGLCore.create( ResourcesPlugin.getWorkspace().getRoot() ).getEGLProject( projectName );
	}
	
	protected abstract String getProjectName();
	
	/**
	 * Initialize the configuration name based on the current selection in the workspace.
	 * 
	 * @param resource The currently selected resource.
	 * @param configuration The launch configuration.
	 */
	protected void initializeConfigName( IResource resource, ILaunchConfigurationWorkingCopy configuration )
	{
		String configName = null;
		String name = resource.getName();
		int index = name.lastIndexOf( '.' );
		if ( index > 0 )
		{
			name = name.substring( 0, index );
		}
		ILaunchConfigurationDialog dialog = getLaunchConfigurationDialog();
		if ( dialog != null )
		{
			configName = getLaunchConfigurationDialog().generateName( name );
		}
		if ( configName != null )
		{
			configuration.rename( configName );
		}
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage()
	{
		return EGLJavaLaunchUtils.getImage( IEGLJavaLaunchConstants.IMG_LAUNCH_MAIN_TAB );
	}
	
	protected class ProjectLabelProvider extends LabelProvider
	{
		public String getText( Object element )
		{
			if ( element instanceof IEGLProject )
			{
				return ((IEGLProject)(element)).getElementName();
			}
			return super.getText( element );
		}
	}
}
