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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.edt.debug.core.EDTDebugCoreMessages;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.PreferenceUtil;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.internal.core.java.filters.DefaultTypeFilterCategory;
import org.eclipse.osgi.util.NLS;

/**
 * Various type filter utility methods.
 */
public class TypeFilterUtil implements IPreferenceChangeListener
{
	/**
	 * The single instance.
	 */
	public static final TypeFilterUtil INSTANCE = new TypeFilterUtil();
	
	/**
	 * The ID for the type filters extension point.
	 */
	public static final String EXTENSION_POINT_TYPE_FILTERS = "javaTypeFilters"; //$NON-NLS-1$
	
	/**
	 * The type filter categories.
	 */
	private ITypeFilterCategory[] filterCategories;
	
	/**
	 * The active type filters.
	 */
	private ITypeFilter[] activeFilters;
	
	/**
	 * All the type filters.
	 */
	private ITypeFilter[] allFilters;
	
	private TypeFilterUtil()
	{
		// Only allow the singleton.
	}
	
	/**
	 * @return all the installed filters.
	 */
	public synchronized ITypeFilter[] getAllFilters()
	{
		if ( allFilters == null )
		{
			loadFilters();
		}
		return allFilters;
	}
	
	/**
	 * @return the currently enabled filters.
	 */
	public synchronized ITypeFilter[] getActiveFilters()
	{
		if ( activeFilters == null )
		{
			loadFilters();
		}
		return activeFilters;
	}
	
	private void calculateActiveFilters()
	{
		List<ITypeFilter> filterList = new ArrayList<ITypeFilter>( 10 );
		for ( ITypeFilterCategory category : getTypeFilterCategories() )
		{
			if ( category.isEnabled() )
			{
				for ( ITypeFilter filter : category.getFilters() )
				{
					filterList.add( filter );
				}
			}
		}
		activeFilters = filterList.toArray( new ITypeFilter[ filterList.size() ] );
	}
	
	/**
	 * @return all the installed filter categories.
	 */
	public synchronized ITypeFilterCategory[] getTypeFilterCategories()
	{
		if ( filterCategories == null )
		{
			loadFilters();
		}
		return filterCategories;
	}
	
	private void loadFilters()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor( EDTDebugCorePlugin.PLUGIN_ID,
				EXTENSION_POINT_TYPE_FILTERS );
		List<ITypeFilterCategory> categories = new ArrayList<ITypeFilterCategory>( elements.length );
		List<ITypeFilter> filters = new ArrayList<ITypeFilter>( elements.length );
		for ( IConfigurationElement element : elements )
		{
			String name = element.getName();
			if ( "category".equals( name ) ) //$NON-NLS-1$
			{
				ITypeFilterCategory category = createCategory( element );
				if ( category != null )
				{
					categories.add( category );
				}
			}
			else if ( "filter".equals( name ) ) //$NON-NLS-1$
			{
				ITypeFilter filter = createFilter( element );
				if ( filter != null )
				{
					filters.add( filter );
				}
			}
		}
		
		// Before we associate the filters with categories, save a copy so that we have a list of all the filters.
		allFilters = filters.toArray( new ITypeFilter[ filters.size() ] );
		
		// Associate providers with the categories.
		if ( filters.size() > 0 )
		{
			// For each provider, find its category.
			for ( Iterator<ITypeFilter> it = filters.iterator(); it.hasNext(); )
			{
				ITypeFilter filter = it.next();
				String targetId = filter.getCategoryId();
				for ( ITypeFilterCategory category : categories )
				{
					if ( targetId.equals( category.getId() ) )
					{
						filter.setCategory( category );
						category.addFilter( filter );
						it.remove();
						break;
					}
				}
			}
			
			if ( filters.size() > 0 )
			{
				// For each provider still in the list, its target category wasn't found.
				StringBuilder buf = new StringBuilder( 100 );
				for ( ITypeFilter filter : filters )
				{
					buf.append( "id=" ); //$NON-NLS-1$
					buf.append( filter.getId() );
					buf.append( ", categoryId=" ); //$NON-NLS-1$
					buf.append( filter.getCategoryId() );
					buf.append( '\n' );
				}
				EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
						EDTDebugCoreMessages.TypeFilterProviderMissingCategory, buf.toString() ) ) );
			}
		}
		
		filterCategories = categories.toArray( new ITypeFilterCategory[ categories.size() ] );
		applyFilterEnablementPreferences();
		applyStepTypePreferences();
		calculateActiveFilters();
		PreferenceUtil.addPreferenceChangeListener( this );
	}
	
	private void applyFilterEnablementPreferences()
	{
		String enablement = PreferenceUtil.getString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_ENABLEMENT, null );
		if ( enablement != null && enablement.length() > 0 )
		{
			// For any categories not in the preference, set it to use its defaults.
			List<ITypeFilterCategory> categoriesNotListed = new ArrayList<ITypeFilterCategory>( Arrays.asList( filterCategories ) );
			
			// Comma-separated list of "categoryId=enablement"
			StringTokenizer tok = new StringTokenizer( enablement, ",", false ); //$NON-NLS-1$
			while ( tok.hasMoreTokens() )
			{
				String next = tok.nextToken();
				int equals = next.indexOf( '=' );
				if ( equals != -1 && equals < next.length() - 1 )
				{
					String id = next.substring( 0, equals );
					for ( ITypeFilterCategory category : filterCategories )
					{
						if ( id.equals( category.getId() ) )
						{
							category.setEnabled( Boolean.parseBoolean( next.substring( equals + 1 ) ) );
							categoriesNotListed.remove( category );
							break;
						}
					}
				}
			}
			
			for ( ITypeFilterCategory category : categoriesNotListed )
			{
				category.setEnabled( category.getDefaultEnablement() );
			}
		}
		else
		{
			// Use defaults.
			for ( ITypeFilterCategory category : filterCategories )
			{
				category.setEnabled( category.getDefaultEnablement() );
			}
		}
	}
	
	private void applyStepTypePreferences()
	{
		String stepTypes = PreferenceUtil.getString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_STEP_TYPES, null );
		if ( stepTypes != null && stepTypes.length() > 0 )
		{
			// For any categories not in the preference, set it to use its defaults.
			List<ITypeFilterCategory> categoriesNotListed = new ArrayList<ITypeFilterCategory>( Arrays.asList( filterCategories ) );
			
			// Comma-separated list of "categoryId=stepType"
			StringTokenizer tok = new StringTokenizer( stepTypes, ",", false ); //$NON-NLS-1$
			while ( tok.hasMoreTokens() )
			{
				String next = tok.nextToken();
				int equals = next.indexOf( '=' );
				if ( equals != -1 && equals < next.length() - 1 )
				{
					String id = next.substring( 0, equals );
					for ( ITypeFilterCategory category : filterCategories )
					{
						if ( id.equals( category.getId() ) )
						{
							category.setStepType( FilterStepType.parse( next.substring( equals + 1 ) ) );
							categoriesNotListed.remove( category );
							break;
						}
					}
				}
			}
			
			for ( ITypeFilterCategory category : categoriesNotListed )
			{
				category.setStepType( category.getDefaultStepType() );
			}
		}
		else
		{
			// Use defaults.
			for ( ITypeFilterCategory category : filterCategories )
			{
				category.setStepType( category.getDefaultStepType() );
			}
		}
	}
	
	private ITypeFilterCategory createCategory( IConfigurationElement element )
	{
		ITypeFilterCategory category = null;
		
		// Required attributes.
		
		String id = element.getAttribute( "id" ); //$NON-NLS-1$
		if ( id == null || (id = id.trim()).length() == 0 )
		{
			EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
					EDTDebugCoreMessages.TypeFilterExtensionRequiredAttributeMissing, "id" ) ) ); //$NON-NLS-1$
			return null;
		}
		
		String name = element.getAttribute( "name" ); //$NON-NLS-1$
		if ( name == null || (name = name.trim()).length() == 0 )
		{
			EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
					EDTDebugCoreMessages.TypeFilterExtensionRequiredAttributeMissing, "name" ) ) ); //$NON-NLS-1$
			return null;
		}
		
		// Optional attributes.
		
		String clazz = element.getAttribute( "class" ); //$NON-NLS-1$
		if ( clazz != null && (clazz = clazz.trim()).length() > 0 )
		{
			try
			{
				Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$
				if ( o instanceof ITypeFilterCategory )
				{
					category = (ITypeFilterCategory)o;
				}
			}
			catch ( CoreException ce )
			{
				EDTDebugCorePlugin.log( ce );
			}
		}
		
		if ( category == null )
		{
			category = new DefaultTypeFilterCategory();
		}
		
		category.setId( id );
		category.setName( name );
		
		String desc = element.getAttribute( "description" ); //$NON-NLS-1$
		if ( desc == null || (desc = desc.trim()).length() == 0 )
		{
			category.setDescription( EDTDebugCoreMessages.NoDescription );
		}
		else
		{
			category.setDescription( desc );
		}
		
		String visible = element.getAttribute( "visible" ); //$NON-NLS-1$
		if ( visible == null || (visible = visible.trim()).length() == 0 )
		{
			category.setVisible( true );
		}
		else
		{
			category.setVisible( Boolean.parseBoolean( visible ) );
		}
		
		String defStepType = element.getAttribute( "defaultStepType" ); //$NON-NLS-1$
		if ( defStepType == null || (defStepType = defStepType.trim()).length() == 0 )
		{
			category.setDefaultStepType( FilterStepType.STEP_INTO );
		}
		else
		{
			category.setDefaultStepType( FilterStepType.parse( defStepType ) );
		}
		
		String defEnablement = element.getAttribute( "defaultEnablement" ); //$NON-NLS-1$
		if ( defEnablement == null || (defEnablement = defEnablement.trim()).length() == 0 )
		{
			category.setDefaultEnablement( true );
		}
		else
		{
			category.setDefaultEnablement( Boolean.parseBoolean( defEnablement ) );
		}
		
		// Initialize these to their default values, and then when processing the preferences these values might change if not using the default.
		category.setStepType( category.getDefaultStepType() );
		category.setEnabled( category.getDefaultEnablement() );
		
		return category;
	}
	
	private ITypeFilter createFilter( IConfigurationElement element )
	{
		// All attributes are required.
		String id = element.getAttribute( "id" ); //$NON-NLS-1$
		if ( id == null || (id = id.trim()).length() == 0 )
		{
			EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
					EDTDebugCoreMessages.TypeFilterExtensionRequiredAttributeMissing, "id" ) ) ); //$NON-NLS-1$
			return null;
		}
		
		String categoryId = element.getAttribute( "categoryId" ); //$NON-NLS-1$
		if ( categoryId == null || (categoryId = categoryId.trim()).length() == 0 )
		{
			EDTDebugCorePlugin.log( new Status( IStatus.WARNING, EDTDebugCorePlugin.PLUGIN_ID, NLS.bind(
					EDTDebugCoreMessages.TypeFilterExtensionRequiredAttributeMissing, "categoryId" ) ) ); //$NON-NLS-1$
			return null;
		}
		
		try
		{
			Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$
			if ( o instanceof ITypeFilter )
			{
				ITypeFilter filter = (ITypeFilter)o;
				filter.setId( id );
				filter.setCategoryId( categoryId );
				return filter;
			}
		}
		catch ( CoreException ce )
		{
			EDTDebugCorePlugin.log( ce );
		}
		return null;
	}
	
	@Override
	public void preferenceChange( PreferenceChangeEvent event )
	{
		String key = event.getKey();
		if ( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_STEP_TYPES.equals( key ) )
		{
			applyStepTypePreferences();
		}
		else if ( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_ENABLEMENT.equals( key ) )
		{
			applyFilterEnablementPreferences();
			calculateActiveFilters();
			
			// Have each EGL Java target initialize the active filters, in case any are newly enabled.
			ITypeFilter[] filters = getActiveFilters();
			for ( IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets() )
			{
				IEGLJavaDebugTarget eglTarget = (IEGLJavaDebugTarget)target.getAdapter( IEGLJavaDebugTarget.class );
				if ( eglTarget != null )
				{
					for ( ITypeFilter filter : filters )
					{
						filter.initialize( eglTarget );
					}
				}
			}
		}
	}
	
	public void dispose()
	{
		if ( filterCategories != null )
		{
			for ( ITypeFilterCategory category : filterCategories )
			{
				category.dispose();
			}
			filterCategories = null;
		}
		PreferenceUtil.removePreferenceChangeListener( this );
	}
}
