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
package org.eclipse.edt.debug.internal.ui.actions;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandlerWithState;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.PreferenceUtil;
import org.eclipse.jface.commands.ToggleState;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * Toggles the type filter enablement preference.
 */
public class EnableFiltersAction extends AbstractHandlerWithState implements IElementUpdater, IPreferenceChangeListener
{
	private State state;
	private UIElement element;
	
	public EnableFiltersAction()
	{
		state = new ToggleState();
		state.setValue( PreferenceUtil.getBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true ) );
		addState( "STYLE", state ); //$NON-NLS-1$
		PreferenceUtil.addPreferenceChangeListener( this );
	}
	
	public void dispose()
	{
		super.dispose();
		PreferenceUtil.removePreferenceChangeListener( this );
	}
	
	@Override
	public void handleStateChange( State state, Object oldValue )
	{
	}
	
	@Override
	public Object execute( ExecutionEvent event ) throws ExecutionException
	{
		boolean newValue = !PreferenceUtil.getBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true );
		PreferenceUtil.setBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, newValue );
		state.setValue( Boolean.valueOf( newValue ) );
		
		return null;
	}
	
	@Override
	public void updateElement( UIElement element, Map parameters )
	{
		this.element = element;
		element.setChecked( ((Boolean)state.getValue()).booleanValue() );
	}
	
	@Override
	public void preferenceChange( PreferenceChangeEvent event )
	{
		if ( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED.equals( event.getKey() ) )
		{
			// If we don't already have the new value (someone else triggered the preference change) then
			// update the state, the menu item checked state, and refresh the viewer.
			if ( !state.getValue().equals( event.getNewValue() ) )
			{
				boolean checked = PreferenceUtil.getBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true );
				state.setValue( Boolean.valueOf( checked ) );
				if ( element != null )
				{
					element.setChecked( checked );
				}
			}
		}
	}
}
