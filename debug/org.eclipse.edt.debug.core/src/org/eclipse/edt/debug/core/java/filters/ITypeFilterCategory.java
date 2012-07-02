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

import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * A type filter category represents one or more type filters. When the category is enabled, all its contained filters are checked when suspending to
 * see if we should continue execution. They are contributed via the "org.eclipse.edt.debug.core.typeFilters" extension point.
 */
public interface ITypeFilterCategory
{
	/**
	 * @return this category's ID.
	 */
	public String getId();
	
	/**
	 * Sets this category's ID.
	 * 
	 * @param id The ID.
	 */
	public void setId( String id );
	
	/**
	 * @return this category's name.
	 */
	public String getName();
	
	/**
	 * Sets this category's name.
	 * 
	 * @param name The name.
	 */
	public void setName( String name );
	
	/**
	 * @return this category's description.
	 */
	public String getDescription();
	
	/**
	 * Sets this category's description.
	 * 
	 * @param description The description.
	 */
	public void setDescription( String description );
	
	/**
	 * @return true if this category is enabled.
	 */
	public boolean isEnabled();
	
	/**
	 * Sets the enablement of this category.
	 * 
	 * @param enabled The enablement.
	 */
	public void setEnabled( boolean enabled );
	
	/**
	 * @return true if this category is visible in the UI for configuration.
	 */
	public boolean isVisible();
	
	/**
	 * Sets the visibility of this category.
	 * 
	 * @param visible The visibility.
	 */
	public void setVisible( boolean visible );
	
	/**
	 * @return the default enablement of this category.
	 */
	public boolean getDefaultEnablement();
	
	/**
	 * Sets the default enablement of this category.
	 * 
	 * @param defaultEnablement The default enablement.
	 */
	public void setDefaultEnablement( boolean defaultEnablement );
	
	/**
	 * Returns this category's step type. When a frame is being filtered, that frame is passed in for optional step type criteria.
	 * Null may be passed in when a client is looking for the user-chosen step type and there is no Java frame to be consulted for
	 * additional criteria.
	 * 
	 * @param frame The Java frame that's being filtered, possible null.
	 * @return this category's step type.
	 */
	public FilterStepType getStepType( IJavaStackFrame frame );
	
	/**
	 * Sets this category's step type.
	 * 
	 * @param stepType The step type.
	 */
	public void setStepType( FilterStepType stepType );
	
	/**
	 * @return this category's default step type.
	 */
	public FilterStepType getDefaultStepType();
	
	/**
	 * Sets this category's default step type.
	 * 
	 * @param defaultStepType The default step type.
	 */
	public void setDefaultStepType( FilterStepType defaultStepType );
	
	/**
	 * @return this category's filters.
	 */
	public ITypeFilter[] getFilters();
	
	/**
	 * Adds a filter to this category.
	 * 
	 * @param filter The filter to be added.
	 */
	public void addFilter( ITypeFilter filter );
	
	/**
	 * Sets the filters for this category.
	 * 
	 * @param filters The new list of filters for this category.
	 */
	public void setFilters( ITypeFilter[] filters );
	
	/**
	 * Called when this category should be disposed.
	 */
	public void dispose();
}
