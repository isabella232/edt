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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;


public class AnnotationTypeManager {
	
    private static Map annotationTypeMap = Collections.EMPTY_MAP;
    
    private Map systemPackageAnnotations = new HashMap();

    
    public AnnotationTypeManager(AnnotationTypeManager parent) {
		super();
		
		if (parent != null) {
			systemPackageAnnotations.putAll(parent.systemPackageAnnotations);
		}
	}

	public static IAnnotationTypeBinding getAnnotationType(String name) {
        return (IAnnotationTypeBinding) getAnnotationTypeMap().get(name);
    }
    
    public static IAnnotationTypeBinding getAnnotationType(Name name) {
    	Map annotationTypeMap = getAnnotationTypeMap();
        return (IAnnotationTypeBinding) annotationTypeMap.get(name.getIdentifier());
    }

    /**
     * @return Returns the annotationTypeMap.
     */
    public static Map getAnnotationTypeMap() {
        if (annotationTypeMap == Collections.EMPTY_MAP) {
            intializeAnnotationTypeMap();
        }
        return annotationTypeMap;
    }
    
    private static void intializeAnnotationTypeMap() {
        annotationTypeMap = new HashMap();
        
        annotationTypeMap.put(AnnotationAnnotationTypeBinding.name, AnnotationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(StereotypeAnnotationTypeBinding.name, StereotypeAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(FinalAnnotationTypeBinding.name, FinalAnnotationTypeBinding.getInstance());       
        annotationTypeMap.put(EGLAnnotationGroupAnnotationTypeBinding.name, EGLAnnotationGroupAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLImplicitExtendedTypeAnnotationTypeBinding.name, EGLImplicitExtendedTypeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLDataItemPropertyProblemsAnnotationTypeBinding.name, EGLDataItemPropertyProblemsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLDataItemPropertyProblemAnnotationTypeBinding.name, EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(EGLIsReadOnlyAnnotationTypeBinding.name, EGLIsReadOnlyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsSystemAnnotationAnnotationTypeBinding.name, EGLIsSystemAnnotationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsBIDIEnabledAnnotationTypeBinding.name, EGLIsBIDIEnabledAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsSystemPartAnnotationTypeBinding.name, EGLIsSystemPartAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(EGLNotInCurrentReleaseAnnotationTypeBinding.name, EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSpecificValuesAnnotationTypeBinding.name, EGLSpecificValuesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSystemConstantAnnotationTypeBinding.name, EGLSystemConstantAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSystemParameterTypesAnnotationTypeBinding.name, EGLSystemParameterTypesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLLineNumberAnnotationTypeBinding.name, EGLLineNumberAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLLocationAnnotationTypeBinding.name, EGLLocationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLValidNumberOfArgumentsAnnotationTypeBinding.name, EGLValidNumberOfArgumentsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLValueCanBeExpressionForOpenUIAnnotationTypeBinding.name, EGLValueCanBeExpressionForOpenUIAnnotationTypeBinding.getInstance());
    }
    
    public void addSystemPackageRecord(FlexibleRecordBinding binding) {
    	systemPackageAnnotations.put(binding.getName(), binding);
    }
    
    /**
     * @deprecated This is part of a temporary solution to bypass the fact that the
     *             search mechanism does not return system parts. It should only be
     *             used by the class EGLPropertiesHandler or EGLNewPropertiesHandler. 
     */
    public Map getSystemPackageAnnotations() {
    	return systemPackageAnnotations;
    }

}
