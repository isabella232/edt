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
package org.eclipse.edt.debug.core.java.filters;

import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;

/**
 * Base class that handles the common properties for type filters.
 */
public abstract class AbstractTypeFilter implements ITypeFilter
{
	private String id;
	private String categoryId;
	private ITypeFilterCategory category;
	
	@Override
	public String getId()
	{
		return id;
	}
	
	@Override
	public void setId( String id )
	{
		this.id = id;
	}
	
	@Override
	public String getCategoryId()
	{
		return categoryId;
	}
	
	@Override
	public void setCategoryId( String id )
	{
		this.categoryId = id;
	}
	
	@Override
	public ITypeFilterCategory getCategory()
	{
		return category;
	}
	
	@Override
	public void setCategory( ITypeFilterCategory category )
	{
		this.category = category;
	}
	
	@Override
	public void initialize( IEGLJavaDebugTarget target )
	{
		// Subclasses should override to do any required setup.
	}
	
	@Override
	public void dispose( IEGLJavaDebugTarget target )
	{
		// Subclasses should override to do any required cleanup.
	}
	
	@Override
	public void dispose()
	{
		// Subclasses should override to do any required cleanup.
		id = null;
		categoryId = null;
		category = null;
	}
	
	@Override
	public String toString()
	{
		return id;
	}
}
