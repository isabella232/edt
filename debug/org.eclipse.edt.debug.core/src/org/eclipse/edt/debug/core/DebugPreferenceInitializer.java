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
package org.eclipse.edt.debug.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Initializes the core debug preferences.
 */
public class DebugPreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		PreferenceUtil.setDefaultBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true );
		PreferenceUtil.setDefaultString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_STEP_TYPES, null );
		PreferenceUtil.setDefaultString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_ENABLEMENT, null );
		PreferenceUtil.savePreferences();
	}
}
