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
package org.eclipse.edt.compiler.internal.dli;

/**
 * @author winghong
 */
public class CommandCodes extends Node implements ICommandCodes {
    
    private String commandCodes;
    
    /**
     * @param commandCodes
     * @param startOffset
     * @param endOffset
     */
    public CommandCodes(String commandCodes, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.commandCodes = commandCodes;
    }
    
    /**
     * @return Returns the commandCodes.
     */
    public String getCommandCodes() {
        return commandCodes;
    }

}
