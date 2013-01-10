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
package org.eclipse.edt.debug.core.java.filters;

import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Type filters let the user prevent suspending inside a particular Java type. They are contributed via the "org.eclipse.edt.debug.core.typeFilters"
 * extension point.
 */
public interface ITypeFilter
{
	/**
	 * @return this filter's ID.
	 */
	public String getId();
	
	/**
	 * Sets this filter's ID.
	 * 
	 * @param id The ID.
	 */
	public void setId( String id );
	
	/**
	 * @return the ID of this filter's type filter category.
	 */
	public String getCategoryId();
	
	/**
	 * Sets the ID of this filter's type filter category.
	 * 
	 * @param id The category ID.
	 */
	public void setCategoryId( String id );
	
	/**
	 * @return this filter's type filter category.
	 */
	public ITypeFilterCategory getCategory();
	
	/**
	 * Sets the type filter category to which this filter belongs (i.e. its parent).
	 * 
	 * @param category The type filter category.
	 */
	public void setCategory( ITypeFilterCategory category );
	
	/**
	 * Given a frame this filter returns true if the frame should be filtered, or false if it should not be filtered.
	 * 
	 * @param frame The Java stack frame.
	 * @param target The EGL debug target.
	 * @return true if the frame should be filtered, otherwise false.
	 */
	public boolean filter( IJavaStackFrame frame, IEGLJavaDebugTarget target );
	
	/**
	 * Called when installing this filter to a target. Note that this can be called multiple times for the same target, for example if a filter was
	 * disabled during the running of an application, and then re-enabled.
	 * 
	 * @param target The EGL debug target.
	 */
	public void initialize( IEGLJavaDebugTarget target );
	
	/**
	 * Called when the given debug target is terminating.
	 * 
	 * @param target The EGL debug target.
	 */
	public void dispose( IEGLJavaDebugTarget target );
	
	/**
	 * Called when this filter as a whole should be disposed.
	 */
	public void dispose();
}
