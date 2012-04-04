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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class BindingUtilities {
	
	/**
	 * Adds an entry for newBinding.getName() to map, if map does not already
	 * contain an entry for that String. If it does, the string is mapped to
	 * AmbiguousDataBinding.getInstance() in the map.
	 * 
	 * @param map			A map of unqualified names to IDataBinding objects
	 * @param newBinding
	 */
	public static void addToUnqualifiedBindingNameMap(Map map, IDataBinding dataBindingWhichContainsNewBinding, IDataBinding newBinding) {
		String newBindingName = newBinding.getName();
		map.put(
			newBindingName,
			map.containsKey(newBindingName) ?
				(IDataBinding) AmbiguousDataBinding.getInstance() :
				dataBindingWhichContainsNewBinding == null ?
					newBinding :
					new DataBindingWithImplicitQualifier(newBinding, dataBindingWhichContainsNewBinding));
	}
	
	/**
	 * For each key in newBindings, adds an entry for the key to map, if map
	 * does not already contain an entry for that key. If it does, the string
	 * is mapped to AmbiguousDataBinding.getInstance() in map.
	 * 
	 * @param map			A map of unqualified names to IDataBinding objects
	 * @param newBindings   A map of unqualified names to IDataBinding objects
	 */
	public static void addAllToUnqualifiedBindingNameMap(Map map, IDataBinding dataBindingWhichContainsNewBindings, Map newBindings) {
		for(Iterator iter = newBindings.keySet().iterator(); iter.hasNext();) {
			String nextKey = (String) iter.next();
			map.put(
				nextKey,
				map.containsKey(nextKey) ?
					(IDataBinding) AmbiguousDataBinding.getInstance() :
					dataBindingWhichContainsNewBindings == null ?
						newBindings.get(nextKey) :
						new DataBindingWithImplicitQualifier((IDataBinding) newBindings.get(nextKey), dataBindingWhichContainsNewBindings));
		}
	}
	
	public static void addToUnqualifiedFunctionNameMap(Map map, IFunctionBinding functionBinding, IDataBinding dataBindingWhichContainsFunctionBinding) {
		String newBindingName = functionBinding.getName();
		map.put(
			newBindingName,
			map.containsKey(newBindingName) ?
				AmbiguousFunctionBinding.getInstance() :
					dataBindingWhichContainsFunctionBinding == null ?
						functionBinding :
						new FunctionBindingWithImplicitQualifier(functionBinding, dataBindingWhichContainsFunctionBinding));				
	}
	
	public static ITypeBinding getBaseType(IDataBinding dataBinding) {
		return dataBinding.getType().getBaseType();
	}
	
	public static void addResourceAssociationBindingToMap(Map namesToBindingsMap, IDataBinding ioObject) {
		ITypeBinding typeFromIOObject = BindingUtilities.getBaseType(ioObject);
		
		if(hasResourceAssociation(typeFromIOObject)) {	        				
			if(namesToBindingsMap.containsKey(FixedRecordBinding.RESOURCEASSOCIATION.getName())) {
				IDataBinding dBinding = (IDataBinding) namesToBindingsMap.get(FixedRecordBinding.RESOURCEASSOCIATION.getName());
				if(dBinding == AmbiguousDataBinding.getInstance()) {
					return;
				}

				DataBindingWithImplicitQualifier dbFromMap = (DataBindingWithImplicitQualifier) dBinding; 
				if(dbFromMap.getWrappedDataBinding() != AmbiguousDataBinding.getInstance()) {
					
					ITypeBinding typeFromMap = BindingUtilities.getBaseType(dbFromMap.getImplicitQualifier());					 
					
					String fileOrQueueNameFromMap = null;
					String fileOrQueueNameFromIOObject = null;
					
					if(typeFromIOObject.getAnnotation(new String[] {"egl", "io", "mq"}, "MQRecord") != null) {
						fileOrQueueNameFromMap = getQueueNameValue(typeFromMap);
						fileOrQueueNameFromIOObject = getQueueNameValue(typeFromIOObject);
					}
					else {
						fileOrQueueNameFromMap = getFileNameValue(typeFromMap);
						fileOrQueueNameFromIOObject = getFileNameValue(typeFromIOObject);
					}
    				
					if((fileOrQueueNameFromMap == null && fileOrQueueNameFromIOObject == null) ||
					   fileOrQueueNameFromMap != fileOrQueueNameFromIOObject) {
						namesToBindingsMap.put(FixedRecordBinding.RESOURCEASSOCIATION.getName(), AmbiguousDataBinding.getInstance());
					}
				}
			}
			else {
				BindingUtilities.addToUnqualifiedBindingNameMap(namesToBindingsMap, ioObject, FixedRecordBinding.RESOURCEASSOCIATION);
			}
		}
	}
	
    private static String getFileNameValue(ITypeBinding type) {
    	IAnnotationBinding subTypeAnnotation = type.getAnnotation(new String[] {"egl", "io", "file"}, "IndexedRecord");
    	if(subTypeAnnotation == null) subTypeAnnotation = type.getAnnotation(new String[] {"egl", "io", "file"}, "SerialRecord");
    	if(subTypeAnnotation == null) subTypeAnnotation = type.getAnnotation(new String[] {"egl", "io", "file"}, "RelativeRecord");
    	if(subTypeAnnotation == null) subTypeAnnotation = type.getAnnotation(new String[] {"egl", "io", "file"}, "CSVRecord");
    	
    	if(subTypeAnnotation != null) {
    		IAnnotationBinding fileNameAnnotation = (IAnnotationBinding) subTypeAnnotation.findData(InternUtil.intern(IEGLConstants.PROPERTY_FILENAME));
			if(IBinding.NOT_FOUND_BINDING != fileNameAnnotation) {
				return InternUtil.intern(fileNameAnnotation.getValue().toString());
			}
    	}
    	return null;
    }
    
    private static String getQueueNameValue(ITypeBinding type) {
    	IAnnotationBinding subTypeAnnotation = type.getAnnotation(new String[] {"egl", "io", "mq"}, "MQRecord");
    	if(subTypeAnnotation != null) {
    		IAnnotationBinding queueNameAnnotation = (IAnnotationBinding) subTypeAnnotation.findData(InternUtil.intern(IEGLConstants.PROPERTY_QUEUENAME));
			if(IBinding.NOT_FOUND_BINDING != queueNameAnnotation) {
				return InternUtil.intern(queueNameAnnotation.getValue().toString());
			}
    	}
    	return null;
    }
    
    private static class PackageAndName {
    	String[] pkg;
    	String name;
    	
    	public PackageAndName(String[] pkg, String name) {
			this.pkg = pkg;
			this.name = name;
		}
    }
    
    private static List annotationsForRecordsWithResourceAssocation = Arrays.asList(new PackageAndName[] {
    	new PackageAndName(new String[] {"egl", "io", "file"}, "IndexedRecord"),
    	new PackageAndName(new String[] {"egl", "io", "mq"}, "MQRecord"),
    	new PackageAndName(new String[] {"egl", "io", "file"}, "RelativeRecord"),
    	new PackageAndName(new String[] {"egl", "io", "file"}, "CSVRecord"),
    	new PackageAndName(new String[] {"egl", "io", "file"}, "SerialRecord"),		
    });
    
    public static boolean hasResourceAssociation(ITypeBinding type) {
    	for(Iterator iter = annotationsForRecordsWithResourceAssocation.iterator(); iter.hasNext();) {
    		PackageAndName next = (PackageAndName) iter.next();
    		if(type.getAnnotation(next.pkg, next.name) != null) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean isLooseType(ITypeBinding typeBinding) {
    	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind()) {
    		PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) typeBinding;
    		Primitive prim = primTypeBinding.getPrimitive();
    		if(Primitive.CHAR == prim   || Primitive.MBCHAR == prim ||
    		   Primitive.DBCHAR == prim ||	Primitive.HEX == prim ||
    		   Primitive.UNICODE == prim) {
    			return primTypeBinding.getLength() == 0;
    		}
    		if(Primitive.INTERVAL == prim) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean isValidDeclarationType(ITypeBinding typeBinding) {
    	if(ITypeBinding.ARRAY_TYPE_BINDING == typeBinding.getKind()) {
    		return isValidDeclarationType(((ArrayTypeBinding) typeBinding).getElementType());
    	}
    	if (StatementValidator.isAnnotationRecord(typeBinding)) {
    		return false;
    	}
    	
    	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind() ||
    	   ITypeBinding.FIXED_RECORD_BINDING == typeBinding.getKind() ||
		   ITypeBinding.FLEXIBLE_RECORD_BINDING == typeBinding.getKind() ||
		   ITypeBinding.DATAITEM_BINDING == typeBinding.getKind() ||
		   ITypeBinding.ENUMERATION_BINDING == typeBinding.getKind() ||
		   SystemPartManager.findType(typeBinding.getName()) == typeBinding ||
		   typeBinding.isReference()) {
    		return true;
    	}
    	return false;
    }
}
