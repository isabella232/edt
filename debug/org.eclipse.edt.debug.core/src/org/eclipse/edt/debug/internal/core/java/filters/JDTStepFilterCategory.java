package org.eclipse.edt.debug.internal.core.java.filters;

import org.eclipse.edt.debug.core.java.filters.FilterStepType;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;

/**
 * For the JDT step filter support, we want to honor the "step thru" setting.
 */
@SuppressWarnings("restriction")
public class JDTStepFilterCategory extends DefaultTypeFilterCategory
{
	@Override
	public FilterStepType getStepType()
	{
		if ( JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean( IJDIPreferencesConstants.PREF_STEP_THRU_FILTERS ) )
		{
			return FilterStepType.STEP_INTO;
		}
		return FilterStepType.STEP_RETURN;
	}
}
