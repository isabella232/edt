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
public class SegmentSearchArgument extends Node implements ISegmentSearchArgument {

    private IName segmentName;
    private ICommandCodes commandCodes;
    private ISSAConditions ssaConditions;
    
    /**
     * @param name
     * @param commandCodes
     * @param conditions
     * @param startOffset
     * @param endOffset
     */
    public SegmentSearchArgument(IName segmentName, ICommandCodes commandCodes, ISSAConditions ssaConditions, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.segmentName = segmentName;
        this.commandCodes = commandCodes;
        this.ssaConditions = ssaConditions;
        
        segmentName.setParent(this);
        if(commandCodes != null) commandCodes.setParent(this);
        if(ssaConditions != null) ssaConditions.setParent(this);
    }
    
    /**
     * @return Returns the commandCodes.
     */
    public ICommandCodes getCommandCodes() {
        return commandCodes;
    }
    
    /**
     * @return Returns the ssaConditions.
     */
    public ISSAConditions getSSAConditions() {
        return ssaConditions;
    }

    /**
     * @return Returns the name.
     */
    public IName getSegmentName() {
        return segmentName;
    }

}
