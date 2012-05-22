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
package org.eclipse.edt.mof.egl.sql;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.GetByPositionKind;
import org.eclipse.edt.mof.egl.GetByPositionStatement;

public interface SqlGetByPositionStatement extends SqlIOStatement, GetByPositionStatement {
	GetByPositionKind getDirective();
	
	void setDirective(GetByPositionKind value);
	
	Expression getPosition();
	
	void setPosition(Expression value);
	
}
