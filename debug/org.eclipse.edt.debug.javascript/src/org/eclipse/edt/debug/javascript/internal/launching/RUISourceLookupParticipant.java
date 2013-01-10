/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.edt.debug.javascript.internal.model.RUIStackFrame;

/**
 * The RUI source lookup participant knows how to translate a RUI stack frame into a source file name
 */
public class RUISourceLookupParticipant extends AbstractSourceLookupParticipant
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	@Override
	public String getSourceName( Object object ) throws CoreException
	{
		if ( object instanceof RUIStackFrame )
		{
			return ((RUIStackFrame)object).getSourceName();
		}
		return null;
	}
}
