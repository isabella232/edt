/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.sql.impl;

import java.util.List;

import org.eclipse.edt.mof.egl.impl.NamedElementImpl;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlToken;


public class SqlClauseImpl extends NamedElementImpl implements SqlClause {
	private static int Slot_tokens=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + NamedElementImpl.totalSlots();
	}
	
	static {
		int offset = NamedElementImpl.totalSlots();
		Slot_tokens += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<SqlToken> getTokens() {
		return (List<SqlToken>)slotGet(Slot_tokens);
	}
	
}
