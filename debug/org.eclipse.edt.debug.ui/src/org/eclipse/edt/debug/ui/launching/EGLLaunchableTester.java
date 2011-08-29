/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;

public class EGLLaunchableTester extends PropertyTester
{
	private static final String PROPERTY_HAS_PROJECT_NATURES = "hasProjectNatures"; //$NON-NLS-1$
	private static final String HAS_JAVA_MAIN_PROGRAM = "hasJavaMainProgram"; //$NON-NLS-1$
	
	protected Object resolveElement( Object receiver )
	{
		Object element = null;
		if ( receiver instanceof IAdaptable )
		{
			IEGLElement eglElement = (IEGLElement)((IAdaptable)receiver).getAdapter( IEGLElement.class );
			if ( eglElement != null && eglElement.exists() )
			{
				element = eglElement;
			}
			else
			{
				IFile ifile = (IFile)((IAdaptable)receiver).getAdapter( IFile.class );
				if ( ifile != null && ifile.exists() )
				{
					element = ifile;
				}
			}
		}
		return element;
	}
	
	@Override
	public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
	{
		Object element = resolveElement( receiver );
		if ( element == null )
		{
			return false;
		}
		
		if ( HAS_JAVA_MAIN_PROGRAM.equals( property ) )
		{
			return hasJavaMainProgram( element );
		}
		if ( PROPERTY_HAS_PROJECT_NATURES.equals( property ) )
		{
			return args.length > 0 && hasProjectNatures( element, args );
		}
		
		return false;
	}
	
	private boolean hasJavaMainProgram( Object element )
	{
		IFile eglFile = null;
		if ( element instanceof IFile )
		{
			eglFile = (IFile)element;
		}
		else if ( element instanceof IEGLElement )
		{
			IResource resource = ((IEGLElement)element).getResource();
			if ( resource.getType() == IResource.FILE )
			{
				eglFile = (IFile)resource;
			}
		}
		
		if ( eglFile != null )
		{
			List<IFile> files = EGLJavaLaunchShortcut.getMainJavaOutputFiles( eglFile );
			return files != null && files.size() > 0;
		}
		
		return false;
	}
	
	private boolean hasProjectNatures( Object element, Object[] natures )
	{
		IProject project = null;
		if ( element instanceof IEGLElement )
		{
			IEGLProject eproj = ((IEGLElement)element).getEGLProject();
			if ( eproj != null )
			{
				project = eproj.getProject();
			}
		}
		else if ( element instanceof IFile )
		{
			project = ((IFile)element).getProject();
		}
		
		if ( project != null )
		{
			return hasProjectNatures( project, natures );
		}
		return false;
	}
	
	private boolean hasProjectNatures( IProject project, Object[] natures )
	{
		try
		{
			if ( project.isAccessible() )
			{
				for ( int i = 0; i < natures.length; i++ )
				{
					if ( !project.hasNature( (String)natures[ i ] ) )
					{
						return false;
					}
				}
				return true;
			}
		}
		catch ( CoreException ce )
		{
		}
		return false;
	}
}
