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

/**
 * How a filter should be treated - perform a "step into", a "step return", or "do nothing".
 */
public enum FilterStepType
{
	NO_STEP, STEP_INTO, STEP_RETURN;
	
	public static FilterStepType parse( String value )
	{
		return "return".equals( value ) //$NON-NLS-1$
				? STEP_RETURN
				: "none".equals( value ) //$NON-NLS-1$
						? NO_STEP
						: STEP_INTO;
	}
	
	public String toString()
	{
		switch ( this )
		{
			case NO_STEP:
				return "none"; //$NON-NLS-1$
				
			case STEP_RETURN:
				return "return"; //$NON-NLS-1$
				
			case STEP_INTO:
			default:
				return "into"; //$NON-NLS-1$
		}
	}
}
