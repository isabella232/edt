/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java.filters;

import org.eclipse.edt.debug.core.java.filters.FilterStepType;
import org.eclipse.edt.debug.core.java.filters.ITypeFilter;
import org.eclipse.edt.debug.core.java.filters.ITypeFilterCategory;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Default implementation of a type filter category.
 */
public class DefaultTypeFilterCategory implements ITypeFilterCategory
{
	private static final ITypeFilter[] EMPTY_FILTERS = {};
	
	private String id;
	private String name;
	private String description;
	private boolean enabled;
	private boolean defaultEnablement;
	private boolean visible;
	private FilterStepType stepType;
	private FilterStepType defaultStepType;
	
	private ITypeFilter[] filters;
	
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
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName( String name )
	{
		this.name = name;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public void setDescription( String description )
	{
		this.description = description;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible( boolean visible )
	{
		this.visible = visible;
	}
	
	@Override
	public boolean getDefaultEnablement()
	{
		return defaultEnablement;
	}
	
	@Override
	public void setDefaultEnablement( boolean defaultEnablement )
	{
		this.defaultEnablement = defaultEnablement;
	}
	
	@Override
	public ITypeFilter[] getFilters()
	{
		if ( filters == null )
		{
			return EMPTY_FILTERS;
		}
		return filters;
	}
	
	@Override
	public void addFilter( ITypeFilter filter )
	{
		if ( filters == null )
		{
			filters = new ITypeFilter[] { filter };
		}
		else
		{
			ITypeFilter[] temp = new ITypeFilter[ filters.length + 1 ];
			System.arraycopy( filters, 0, temp, 0, filters.length );
			temp[ filters.length ] = filter;
			filters = temp;
		}
	}
	
	@Override
	public void setFilters( ITypeFilter[] filters )
	{
		this.filters = filters;
	}
	
	@Override
	public FilterStepType getStepType( IJavaStackFrame frame )
	{
		return stepType;
	}
	
	@Override
	public void setStepType( FilterStepType stepType )
	{
		this.stepType = stepType;
	}
	
	@Override
	public FilterStepType getDefaultStepType()
	{
		return defaultStepType;
	}
	
	@Override
	public void setDefaultStepType( FilterStepType defaultStepType )
	{
		this.defaultStepType = defaultStepType;
	}
	
	@Override
	public void dispose()
	{
		id = null;
		name = null;
		description = null;
		
		if ( filters != null )
		{
			for ( ITypeFilter filter : filters )
			{
				filter.dispose();
			}
		}
	}
	
	@Override
	public String toString()
	{
		return name + " [id=" + id + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
