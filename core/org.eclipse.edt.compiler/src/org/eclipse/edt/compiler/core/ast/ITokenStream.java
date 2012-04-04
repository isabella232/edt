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
package org.eclipse.edt.compiler.core.ast;

import java_cup.runtime.Symbol;

/**
 * @author winghong
 */
public interface ITokenStream {

    Terminal getLookAhead();

    void advanceLookAhead();

    void rollBack(Terminal terminal, Symbol symbol);
    
    int getCacheCapcity();

    Terminal peekLookAhead(int pos);

    ITokenStream createTokenStreamAtOffset(int offset);

}
