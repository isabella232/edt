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
package org.eclipse.edt.compiler.internal.core.lookup.System;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingImpl;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class SystemPartManager {
    
        
    private static Map systemParts = Collections.EMPTY_MAP;
    
    public static ITypeBinding findType(String simpleName) {
        return (ITypeBinding) getSystemParts().get(simpleName);
    }
    
    public static Map getSystemParts() {
        if (systemParts == Collections.EMPTY_MAP) {
            initializeSystemParts();
        }
        return systemParts;
    }
    
    public static final IPartBinding ANYEXCEPTION_BINDING = createAnyExceptionBinding();
    
    public static final IPartBinding INTERNALREF_BINDING = createEGLCoreReflectBinding("InternalRef");    
    public static final IPartBinding FIELDREF_BINDING = createEGLCoreReflectBinding("FieldRef");
    public static final IPartBinding FIELDINTARGETREF_BINDING = createEGLCoreReflectBinding("FieldInTargetRef");
    public static final IPartBinding FUNCTIONREF_BINDING = createEGLCoreReflectBinding("FunctionRef");
    public static final IPartBinding FUNCTIONMEMBERREF_BINDING = createEGLCoreReflectBinding("FunctionMemberRef");
    public static final IPartBinding RECORDREF_BINDING = createEGLCoreReflectBinding("RecordRef");
    public static final IPartBinding SERVICEREF_BINDING = createEGLCoreReflectBinding("ServiceRef");
    public static final IPartBinding SQLSTRING_BINDING = createEGLCoreReflectBinding("SQLString");
    public static final IPartBinding TYPEREF_BINDING = createEGLCoreReflectBinding("TypeRef");    
    
    private static void initializeSystemParts() {    
    	systemParts = new HashMap();
    	systemParts.put(InternUtil.intern(IEGLConstants.MIXED_DICTIONARY_STRING), DictionaryBinding.INSTANCE);
//    	systemParts.put(InternUtil.intern(IEGLConstants.MIXED_ARRAYDICTIONARY_STRING), ArrayDictionaryBinding.INSTANCE);
    	systemParts.put(InternUtil.intern("InternalRef"), INTERNALREF_BINDING);    	
    	systemParts.put(InternUtil.intern("FieldRef"), FIELDREF_BINDING);
    	systemParts.put(InternUtil.intern("FieldInTargetRef"), FIELDINTARGETREF_BINDING);
    	systemParts.put(InternUtil.intern("FunctionRef"), FUNCTIONREF_BINDING);
    	systemParts.put(InternUtil.intern("FunctionMemberRef"), FUNCTIONMEMBERREF_BINDING);
    	systemParts.put(InternUtil.intern("RecordRef"), RECORDREF_BINDING);
    	systemParts.put(InternUtil.intern("ServiceRef"), SERVICEREF_BINDING);
    	systemParts.put(InternUtil.intern("SQLString"), SQLSTRING_BINDING);
    	systemParts.put(InternUtil.intern("TypeRef"), TYPEREF_BINDING);
    }
    
    private static void addFlexibleRecordField(FlexibleRecordBinding rBinding, String fieldName, ITypeBinding type) {
    	addFlexibleRecordField(rBinding, fieldName, type, false);
    }
    
    private static void addFlexibleRecordField(FlexibleRecordBinding rBinding, String fieldName, ITypeBinding type, boolean isReadOnly) {
    	FlexibleRecordFieldBinding rb = new FlexibleRecordFieldBinding(InternUtil.internCaseSensitive(fieldName), rBinding, type);
    	rb.setIsReadOnly(isReadOnly);
    	rBinding.addField(rb);
    }
    
    private static void addStructureItem(FixedRecordBinding rBinding, String fieldName, ITypeBinding type) {
    	addStructureItem(rBinding, fieldName, type, false);
    }
    
    private static void addStructureItem(FixedRecordBinding rBinding, String fieldName, ITypeBinding type, boolean isReadOnly) {
    	StructureItemBinding si = new StructureItemBinding(InternUtil.internCaseSensitive(fieldName), rBinding, type);
    	si.setIsReadOnly(isReadOnly);
    	rBinding.addStructureItem(si);
    }
    
    private static IPartBinding createEGLCoreReflectBinding(String name) {
    	ExternalTypeBinding result = new ExternalTypeBinding(SystemEnvironmentPackageNames.EGL_CORE_REFLECT, InternUtil.internCaseSensitive(name));
    	result.setValid(true);
    	return result;
    }
    
    private static IPartBinding createAnyExceptionBinding() {
    	
    	FlexibleRecordBinding result = new FlexibleRecordBindingImpl(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("AnyException"));
//    	result.addAnnotation(new AnnotationBinding(null, null, ExceptionAnnotationTypeBinding.getInstance()));
    	
    	addFlexibleRecordField(
			result,
			"message",
			PrimitiveTypeBinding.getInstance(Primitive.STRING));
    	
    	result.setValid(true);
    	return result;
    }
}
