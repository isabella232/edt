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
package org.eclipse.edt.compiler.binding;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class ConstructorBinding extends DataBinding {
	
	protected boolean isPrivate;

	
	public ConstructorBinding(IPartBinding declarer) {
		super(InternUtil.internCaseSensitive(IEGLConstants.KEYWORD_CONSTRUCTOR), declarer, new FunctionBinding(InternUtil.internCaseSensitive(IEGLConstants.KEYWORD_CONSTRUCTOR), declarer));
	}

	public int getKind() {
		return CONSTRUCTOR_BINDING;
	}
		
	public List getParameters() {
        return ((IFunctionBinding) typeBinding).getParameters();
    }
    
    public void addParameter(FunctionParameterBinding parameter) {
    	((FunctionBinding) typeBinding).addParameter(parameter);
    }
    
	public boolean isPrivate() {
		return isPrivate;
	}
	
    public void setPrivate(boolean isPrivate) {
    	this.isPrivate = isPrivate;
    }

}
