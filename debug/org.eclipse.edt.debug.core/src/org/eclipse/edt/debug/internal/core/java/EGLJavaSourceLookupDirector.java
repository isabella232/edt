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
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.edt.debug.core.EGLPackageFragmentRootSourceContainer;

public class EGLJavaSourceLookupDirector extends AbstractSourceLookupDirector
{
	private final ISourceLocator originalLocator;
	private final ISourceLookupDirector originalDirector;
	
	public EGLJavaSourceLookupDirector( ISourceLocator locator )
	{
		originalLocator = locator;
		originalDirector = locator instanceof ISourceLookupDirector
				? (ISourceLookupDirector)locator
				: null;
	}
	
	@Override
	public void initializeParticipants()
	{
		addParticipants( new ISourceLookupParticipant[] { new EGLJavaSourceLookupParticipant() } );
		if ( originalDirector != null )
		{
			addParticipants( ((ISourceLookupDirector)originalLocator).getParticipants() );
		}
	}
	
	@Override
	public Object getSourceElement( IStackFrame stackFrame )
	{
		Object element = super.getSourceElement( stackFrame );
		if ( element != null )
		{
			return element;
		}
		
		if ( originalLocator != null )
		{
			return originalLocator.getSourceElement( stackFrame );
		}
		return null;
	}
	
	@Override
	public Object getSourceElement( Object element )
	{
		Object result = super.getSourceElement( element );
		if ( result != null )
		{
			return result;
		}
		
		if ( originalDirector != null )
		{
			return originalDirector.getSourceElement( element );
		}
		return null;
	}
	
	@Override
	public Object[] findSourceElements( Object object ) throws CoreException
	{
		Object[] elements = super.findSourceElements( object );
		if ( elements.length > 0 )
		{
			return elements;
		}
		
		if ( originalDirector != null )
		{
			return originalDirector.findSourceElements( object );
		}
		return new Object[ 0 ];
	}
	
	@Override
	public boolean isFindDuplicates()
	{
		return super.isFindDuplicates() || (originalDirector != null && originalDirector.isFindDuplicates());
	}
	
	@Override
	public boolean supportsSourceContainerType( ISourceContainerType type )
	{
		return type.getId().equals( EGLPackageFragmentRootSourceContainer.TYPE_ID ) || (originalDirector == null
				? super.supportsSourceContainerType( type )
				: originalDirector.supportsSourceContainerType( type ));
	}
	
	@Override
	public synchronized void dispose()
	{
		super.dispose();
		
		if ( originalDirector != null )
		{
			originalDirector.dispose();
		}
	}
}
