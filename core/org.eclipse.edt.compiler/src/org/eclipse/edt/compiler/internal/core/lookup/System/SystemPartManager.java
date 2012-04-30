/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup.System;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.IrFactory;


/**
 * @author Harmon
 */
public class SystemPartManager {
    
        
    private static Map<String, IRPartBinding> systemParts = null;
    
    public static Map<String, IRPartBinding> getSystemParts() {
        if (systemParts == null) {
            initializeSystemParts();
        }
        return systemParts;
    }
    
    public static final IRPartBinding INTERNALREF_BINDING = createEGLCoreReflectBinding("InternalRef");    
    public static final IRPartBinding FIELDREF_BINDING = createEGLCoreReflectBinding("FieldRef");
    public static final IRPartBinding FIELDINTARGETREF_BINDING = createEGLCoreReflectBinding("FieldInTargetRef");
    public static final IRPartBinding FUNCTIONREF_BINDING = createEGLCoreReflectBinding("FunctionRef");
    public static final IRPartBinding FUNCTIONMEMBERREF_BINDING = createEGLCoreReflectBinding("FunctionMemberRef");
    public static final IRPartBinding RECORDREF_BINDING = createEGLCoreReflectBinding("RecordRef");
    public static final IRPartBinding SERVICEREF_BINDING = createEGLCoreReflectBinding("ServiceRef");
    public static final IRPartBinding SQLSTRING_BINDING = createEGLCoreReflectBinding("SQLString");
    public static final IRPartBinding TYPEREF_BINDING = createEGLCoreReflectBinding("TypeRef");    
    
    private static void initializeSystemParts() {    
    	systemParts = new HashMap<String, IRPartBinding>();
    	systemParts.put(INTERNALREF_BINDING.getName(), INTERNALREF_BINDING);    	
    	systemParts.put(FIELDREF_BINDING.getName(), FIELDREF_BINDING);
    	systemParts.put(FIELDINTARGETREF_BINDING.getName(), FIELDINTARGETREF_BINDING);
    	systemParts.put(FUNCTIONREF_BINDING.getName(), FUNCTIONREF_BINDING);
    	systemParts.put(FUNCTIONMEMBERREF_BINDING.getName(), FUNCTIONMEMBERREF_BINDING);
    	systemParts.put(RECORDREF_BINDING.getName(), RECORDREF_BINDING);
    	systemParts.put(SERVICEREF_BINDING.getName(), SERVICEREF_BINDING);
    	systemParts.put(SQLSTRING_BINDING.getName(), SQLSTRING_BINDING);
    	systemParts.put(TYPEREF_BINDING.getName(), TYPEREF_BINDING);
    }
    
    
    
    private static IRPartBinding createEGLCoreReflectBinding(String name) {
    	
    	ExternalType et = IrFactory.INSTANCE.createExternalType();
    	et.setName(name);
    	et.setPackageName(SystemEnvironmentPackageNames.EGL_CORE_REFLECT_STRING);
    	IRPartBinding binding = new IRPartBinding(et);
    	return binding;
    }
    
}
