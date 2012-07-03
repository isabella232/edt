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
package org.eclipse.edt.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.model.ClassFileElementInfo;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Provides source lookup for an EGL package fragment root.
 */
public class EGLPackageFragmentRootSourceContainer extends AbstractSourceContainer implements IWorkbenchAdapter
{
	public static final String TYPE_ID = EDTDebugCorePlugin.PLUGIN_ID + ".sourceContainer.packageFragmentRoot"; //$NON-NLS-1$
	
	private final IPackageFragmentRoot root;
	
	public EGLPackageFragmentRootSourceContainer( IPackageFragmentRoot root )
	{
		this.root = root;
	}
	
	public Object[] findSourceElements( String name ) throws CoreException
	{
		if ( !DebugUtil.isEGLFileName( name ) )
		{
			return EMPTY;
		}
		
		String type = name;
		
		// Remove a file extension if it exists.
		int lastDot = type.lastIndexOf( '.' );
		if ( lastDot != -1 )
		{
			type = type.substring( 0, lastDot );
		}
		
		// Now change all separators to a dot.
		type = type.replace( '/', '.' );
		
		// Calculate the package.
		String pkg = ""; //$NON-NLS-1$
		lastDot = type.lastIndexOf( '.' );
		if ( lastDot != -1 )
		{
			pkg = type.substring( 0, lastDot );
			type = type.substring( lastDot + 1 );
		}
		
		IPackageFragment fragment = root.getPackageFragment( pkg );
		if ( !fragment.exists() )
		{
			// Check for case-insensitive match.
			fragment = null;
			IEGLElement[] kids = root.getChildren();
			for ( int i = 0; i < kids.length; i++ )
			{
				if ( kids[ i ] instanceof IPackageFragment && kids[ i ].getElementName().equalsIgnoreCase( pkg ) )
				{
					fragment = (IPackageFragment)kids[ i ];
					break;
				}
			}
		}
		
		if ( fragment != null )
		{
			switch ( fragment.getKind() )
			{
				case IPackageFragmentRoot.K_BINARY:
				{
					IClassFile file = findClassFile( fragment, type );
					if ( file.exists() )
					{
						return new Object[] { EditorUtility.getBinaryReadonlyFile( file.getEGLProject().getProject(), file.getPath().toString(),
								name, file ) };
					}
					else
					{
						// Some source files will have no parts matching the file name (e.g. a file full of records). Check all the kids of the parent
						// to see if they have the right file name, and go with one of those IRs instead. Doesn't matter which.
						IClassFile[] classes = fragment.getClassFiles();
						for ( int i = 0; i < classes.length; i++ )
						{
							Object info = EGLModelManager.getEGLModelManager().getInfo( classes[ i ] );
							if ( info instanceof ClassFileElementInfo && name.equalsIgnoreCase( ((ClassFileElementInfo)info).getEglFileName() ) )
							{
								return new Object[] { EditorUtility.getBinaryReadonlyFile( classes[ i ].getEGLProject().getProject(), classes[ i ]
										.getPath().toString(), name, classes[ i ] ) };
							}
						}
					}
					break;
				}
				
				case IPackageFragmentRoot.K_SOURCE:
				{
					String eglName = type + ".egl"; //$NON-NLS-1$
					IEGLFile file = fragment.getEGLFile( eglName );
					if ( file.exists() )
					{
						return new Object[] { file.getResource() };
					}
					else
					{
						// Check for case-insensitive match.
						IEGLElement[] kids = fragment.getChildren();
						for ( int i = 0; i < kids.length; i++ )
						{
							if ( kids[ i ] instanceof IEGLFile && kids[ i ].getElementName().equalsIgnoreCase( eglName ) )
							{
								return new Object[] { kids[ i ].getResource() };
							}
						}
					}
					break;
				}
			}
		}
		
		return EMPTY;
	}
	
	private IClassFile findClassFile( IPackageFragment fragment, String name )
	{
		name = IRFileNameUtility.toIRFileName( name );
		IClassFile file = fragment.getClassFile( name + ".eglxml" ); //$NON-NLS-1$
		if ( !file.exists() )
		{
			file = fragment.getClassFile( name + ".eglbin" ); //$NON-NLS-1$
		}
		if ( !file.exists() )
		{
			file = fragment.getClassFile( name + ".mofxml" ); //$NON-NLS-1$
		}
		if ( !file.exists() )
		{
			file = fragment.getClassFile( name + ".mofbin" ); //$NON-NLS-1$
		}
		return file;
	}
	
	public String getName()
	{
		return root.getElementName();
	}
	
	public ISourceContainerType getType()
	{
		return getSourceContainerType( TYPE_ID );
	}
	
	public boolean equals( Object obj )
	{
		return obj instanceof EGLPackageFragmentRootSourceContainer && ((EGLPackageFragmentRootSourceContainer)obj).root.equals( root );
	}
	
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IWorkbenchAdapter.class )
		{
			return this;
		}
		return super.getAdapter( adapter );
	}
	
	public Object[] getChildren( Object o )
	{
		return EMPTY;
	}
	
	public ImageDescriptor getImageDescriptor( Object object )
	{
		return root.isArchive()
				? PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR
				: PluginImages.DESC_OBJS_PACKFRAG_ROOT;
	}
	
	public String getLabel( Object o )
	{
		return root.getElementName();
	}
	
	public Object getParent( Object o )
	{
		return null;
	}
}
