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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.AbstractTypeFilter;
import org.eclipse.edt.debug.internal.core.java.EGLJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * JDT does not apply its step filters to non-Java stratums, so this filter allows the user to enable them for EGL stratums.
 */
@SuppressWarnings("restriction")
public class JDTStepFilter extends AbstractTypeFilter implements IPropertyChangeListener
{
	private String[] packageFilters;
	private Map<String, Object> classFilters;
	
	private static final Method isFilterGetters;
	private static final Method isFilterSetters;
	private static final Method isGetterMethod;
	private static final Method isSetterMethod;
	private static final Method setFilterGetters;
	private static final Method setFilterSetters;
	
	/**
	 * This key is new to 3.7 so we cannot reference it from JDT or we won't compile with 3.6.
	 */
	private static final String PREF_FILTER_GETTERS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_get"; //$NON-NLS-1$
	
	/**
	 * This key is new to 3.7 so we cannot reference it from JDT or we won't compile with 3.6.
	 */
	private static final String PREF_FILTER_SETTERS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_setters"; //$NON-NLS-1$
	
	static
	{
		// Filter getters and setters are new to 3.7. Need to use reflection so that this compiles in 3.6.
		Method filterGetters = null;
		Method filterSetters = null;
		Method getterMethod = null;
		Method setterMethod = null;
		Method setGetters = null;
		Method setSetters = null;
		
		try
		{
			// No need to wrap each in their own try/catch - they'll either all exist, or none.
			filterGetters = IJavaDebugTarget.class.getMethod( "isFilterGetters", (Class[])null ); //$NON-NLS-1$
			filterSetters = IJavaDebugTarget.class.getMethod( "isFilterSetters", (Class[])null ); //$NON-NLS-1$
			setGetters = IJavaDebugTarget.class.getMethod( "setFilterGetters", Boolean.TYPE ); //$NON-NLS-1$
			setSetters = IJavaDebugTarget.class.getMethod( "setFilterSetters", Boolean.TYPE ); //$NON-NLS-1$
			
			Class<?> JDIMethodClass = Class.forName( "org.eclipse.jdt.internal.debug.core.model.JDIMethod" ); //$NON-NLS-1$
			getterMethod = JDIMethodClass.getMethod( "isGetterMethod", com.sun.jdi.Method.class ); //$NON-NLS-1$
			setterMethod = JDIMethodClass.getMethod( "isSetterMethod", com.sun.jdi.Method.class ); //$NON-NLS-1$
		}
		catch ( Throwable t )
		{
		}
		
		isFilterGetters = filterGetters;
		isFilterSetters = filterSetters;
		isGetterMethod = getterMethod;
		isSetterMethod = setterMethod;
		setFilterGetters = setGetters;
		setFilterSetters = setSetters;
	}
	
	@Override
	public boolean filter( IJavaStackFrame frame, IEGLJavaDebugTarget target )
	{
		if ( !target.isStepFiltersEnabled() )
		{
			return false;
		}
		
		// Check certain frame types that are to be filtered.
		IJavaDebugTarget javaTarget = target.getJavaDebugTarget();
		if ( filterConstructor( frame, javaTarget ) || filterStaticInitializer( frame, javaTarget ) || filterSynthetic( frame, javaTarget )
				|| filterGetter( frame, javaTarget ) || filterSetter( frame, javaTarget ) )
		{
			return true;
		}
		
		// Finally, check the type and package filters.
		try
		{
			String typeName = frame.getReferenceType().getName(); // This way
																	// avoids
																	// '<>' from
																	// generics.
			
			if ( classFilters.containsKey( typeName ) )
			{
				return true;
			}
			
			for ( String filter : packageFilters )
			{
				if ( typeName.startsWith( filter ) )
				{
					return true;
				}
			}
			return false;
		}
		catch ( DebugException de )
		{
			return false;
		}
	}
	
	@Override
	public void initialize( IEGLJavaDebugTarget target )
	{
		initActiveFilters();
	}
	
	/**
	 * Initializes the package and class filters.
	 * 
	 * @return an array of all the active filters.
	 */
	private String[] initActiveFilters()
	{
		IPreferenceStore store = JDIDebugUIPlugin.getDefault().getPreferenceStore();
		if ( classFilters == null )
		{
			classFilters = new HashMap<String, Object>( 50 );
			
			// First time through set up a listener.
			store.addPropertyChangeListener( this );
		}
		else
		{
			classFilters.clear();
		}
		
		// JDT is forcing us to keep track of this ourselves... If the debug
		// target isn't exactly IJavaDebugTarget it isn't told about filter
		// updates
		// (IAdaptable would solve this).
		// Also their code that parses the filters from the preference isn't
		// visible so we have to duplicate that too.
		// Since we're going through all this work, might as well split it into
		// package-based and class-based for faster processing in filter().
		String[] allFilters = JavaDebugOptionsManager.parseList( store.getString( IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST ) );
		
		List<String> pkgFilters = new ArrayList<String>();
		for ( String filter : allFilters )
		{
			int len = filter.length();
			if ( len > 0 && filter.charAt( len - 1 ) == '*' )
			{
				pkgFilters.add( filter.substring( 0, len - 1 ) );
			}
			else
			{
				classFilters.put( filter, null );
			}
		}
		
		packageFilters = pkgFilters.toArray( new String[ pkgFilters.size() ] );
		return allFilters;
	}
	
	private boolean filterConstructor( IJavaStackFrame frame, IJavaDebugTarget target )
	{
		if ( target.isFilterConstructors() )
		{
			try
			{
				return frame.isConstructor();
			}
			catch ( DebugException de )
			{
			}
		}
		return false;
	}
	
	private boolean filterStaticInitializer( IJavaStackFrame frame, IJavaDebugTarget target )
	{
		if ( target.isFilterStaticInitializers() )
		{
			try
			{
				return frame.isStaticInitializer();
			}
			catch ( DebugException de )
			{
			}
		}
		return false;
	}
	
	private boolean filterSynthetic( IJavaStackFrame frame, IJavaDebugTarget target )
	{
		if ( target.isFilterSynthetics() )
		{
			try
			{
				return frame.isSynthetic();
			}
			catch ( DebugException de )
			{
			}
		}
		return false;
	}
	
	private boolean filterGetter( IJavaStackFrame frame, IJavaDebugTarget target )
	{
		if ( isFilterGetters != null )
		{
			try
			{
				if ( (Boolean)isFilterGetters.invoke( target, (Object[])null ) )
				{
					if ( isGetterMethod != null && frame instanceof JDIStackFrame )
					{
						return (Boolean)isGetterMethod.invoke( null, ((JDIStackFrame)frame).getUnderlyingMethod() );
					}
					
					// No API in the interface to get the bytes to determine if this
					// is a simple getter...
					String method = frame.getMethodName();
					return method.startsWith( "get" ) || method.startsWith( "is" ); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			catch ( Exception e )
			{
			}
		}
		return false;
	}
	
	private boolean filterSetter( IJavaStackFrame frame, IJavaDebugTarget target )
	{
		if ( isFilterSetters != null )
		{
			try
			{
				if ( (Boolean)isFilterSetters.invoke( target, (Object[])null ) )
				{
					if ( isSetterMethod != null && frame instanceof JDIStackFrame )
					{
						return (Boolean)isSetterMethod.invoke( null, ((JDIStackFrame)frame).getUnderlyingMethod() );
					}
					
					// No API in the interface to get the bytes to determine if this
					// is a simple setter...
					String method = frame.getMethodName();
					return method.startsWith( "set" ); //$NON-NLS-1$
				}
			}
			catch ( Exception e )
			{
			}
		}
		return false;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		JDIDebugUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
	}
	
	@Override
	public void propertyChange( PropertyChangeEvent event )
	{
		boolean refreshFrames = false;
		String property = event.getProperty();
		if ( IJDIPreferencesConstants.PREF_FILTER_CONSTRUCTORS.equals( property ) )
		{
			boolean filterConstructors = JDIDebugUIPlugin.getDefault().getPreferenceStore()
					.getBoolean( IJDIPreferencesConstants.PREF_FILTER_CONSTRUCTORS );
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.getJavaDebugTarget().setFilterConstructors( filterConstructors );
				}
			}
		}
		else if ( IJDIPreferencesConstants.PREF_FILTER_STATIC_INITIALIZERS.equals( property ) )
		{
			boolean filterStaticInitializers = JDIDebugUIPlugin.getDefault().getPreferenceStore()
					.getBoolean( IJDIPreferencesConstants.PREF_FILTER_STATIC_INITIALIZERS );
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.getJavaDebugTarget().setFilterStaticInitializers( filterStaticInitializers );
				}
			}
		}
		else if ( PREF_FILTER_GETTERS.equals( property ) )
		{
			boolean filterGetters = JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean( PREF_FILTER_GETTERS );
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null && setFilterGetters != null )
				{
					try
					{
						setFilterGetters.invoke( eglTarget.getJavaDebugTarget(), filterGetters );
					}
					catch ( Exception e )
					{
						EDTDebugCorePlugin.log( e );
					}
				}
			}
		}
		else if ( PREF_FILTER_SETTERS.equals( property ) )
		{
			boolean filterSetters = JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean( PREF_FILTER_SETTERS );
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null && setFilterSetters != null )
				{
					try
					{
						setFilterSetters.invoke( eglTarget.getJavaDebugTarget(), filterSetters );
					}
					catch ( Exception e )
					{
						EDTDebugCorePlugin.log( e );
					}
				}
			}
		}
		else if ( IJDIPreferencesConstants.PREF_FILTER_SYNTHETICS.equals( property ) )
		{
			boolean filterSynthetics = JDIDebugUIPlugin.getDefault().getPreferenceStore()
					.getBoolean( IJDIPreferencesConstants.PREF_FILTER_SYNTHETICS );
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.getJavaDebugTarget().setFilterSynthetics( filterSynthetics );
				}
			}
		}
		else if ( IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST.equals( property ) )
		{
			String[] allFilters = initActiveFilters();
			refreshFrames = true;
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.getJavaDebugTarget().setStepFilters( allFilters );
				}
			}
		}
		else if ( IJDIPreferencesConstants.PREF_STEP_THRU_FILTERS.equals( property ) )
		{
			boolean stepThru = JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean( IJDIPreferencesConstants.PREF_STEP_THRU_FILTERS );
			
			// EGL-wrapped Java targets don't get notified by JDT of filter
			// preference changes.
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.getJavaDebugTarget().setStepThruFilters( stepThru );
				}
			}
		}
		
		if ( refreshFrames )
		{
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				EGLJavaDebugTarget eglTarget = (EGLJavaDebugTarget)target.getAdapter( EGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					eglTarget.refreshAllFrames();
				}
			}
		}
	}
}
