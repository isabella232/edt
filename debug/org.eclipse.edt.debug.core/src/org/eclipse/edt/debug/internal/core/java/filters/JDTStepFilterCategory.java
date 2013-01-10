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
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;

/**
 * For the JDT step filter support, we want to honor the "step thru" setting.
 */
@SuppressWarnings("restriction")
public class JDTStepFilterCategory extends DefaultTypeFilterCategory
{
	@Override
	public FilterStepType getStepType( IJavaStackFrame frame )
	{
		if ( JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean( IJDIPreferencesConstants.PREF_STEP_THRU_FILTERS ) )
		{
			return FilterStepType.STEP_INTO;
		}
		return FilterStepType.STEP_RETURN;
	}
}
