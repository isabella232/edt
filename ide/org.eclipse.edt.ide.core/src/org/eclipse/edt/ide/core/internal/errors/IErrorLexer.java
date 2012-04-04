/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

import java.io.IOException;

/**
 * @author winghong
 */
public interface IErrorLexer {
	public TerminalNode next();
	public void yyreset(java.io.Reader reader) throws IOException;
	public int yylex() throws java.io.IOException;
}
