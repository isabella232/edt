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

import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Member;

public interface SqlHostVariableToken extends SqlToken {
	LHSExpr getHostVarExpression();
	
	void setHostVarExpression(LHSExpr value);
	
	Member getMember();

}
