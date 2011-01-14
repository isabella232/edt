/*******************************************************************************
 * Copyright © 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.Enumeration;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ParameterModifierKind;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.TypeKind;


/**
 * @author Harmon
 */
public class EnumerationManager {
    private static EnumerationManager INSTANCE = new EnumerationManager();

    private Map resolvableEnumDataBindings = Collections.EMPTY_MAP;
    private Map enumTypes =  Collections.EMPTY_MAP;
    private List resolvableEnumTypes =  Collections.EMPTY_LIST;
    
    public IDataBinding findData(String simpleName) {
        EnumerationDataBinding result = (EnumerationDataBinding) getResolvableEnumDataBindings().get(simpleName);
        if (result != null) {
            EnumerationTypeBinding type = (EnumerationTypeBinding)result.getType();
            return new DataBindingWithImplicitQualifier(result, type.getStaticEnumerationTypeDataBinding());
        }
        return result;
    }
     
    public static EnumerationManager getInstance() {
        return INSTANCE;
    }

    public List getResolvableEnumTypes() {
        if (resolvableEnumTypes == Collections.EMPTY_LIST) {
            initializeEnumTypes();
        }
        return resolvableEnumTypes;
    }

    public Map getEnumTypes() {
        if (enumTypes == Collections.EMPTY_MAP) {
            initializeEnumTypes();
        }
        return enumTypes;
    }

    private Map getResolvableEnumDataBindings() {
        if (resolvableEnumDataBindings == Collections.EMPTY_MAP) {
            initializeResolvableEnumDataBindings();
        }
        return resolvableEnumDataBindings;
    }


    private void initializeEnumTypes() {
        enumTypes = new HashMap();
        resolvableEnumTypes =  new ArrayList();
//        addEnumType(AlignKind.INSTANCE);
//        addEnumType(CallingConventionKind.INSTANCE);
//        addEnumType(CaseFormatKind.INSTANCE);
//        addEnumType(ColorKind.INSTANCE);
//        addEnumType(DataSource.INSTANCE);
//        addEnumType(DeviceTypeKind.INSTANCE);
//        addEnumType(DisplayUseKind.INSTANCE);
//        addEnumType(DLICallInterfaceKind.INSTANCE);
        addEnumType(ElementKind.INSTANCE);
//        addEnumType(EventKind.INSTANCE);
//        addEnumType(ExportFormat.INSTANCE);
//        addEnumType(HighlightKind.INSTANCE);
//        addEnumType(IndexOrientationKind.INSTANCE);
//        addEnumType(IntensityKind.INSTANCE);
//        addEnumType(LineWrapKind.INSTANCE);
//        addEnumType(OrderingKind.INSTANCE);
//        addEnumType(OutlineKind.INSTANCE);
//        addEnumType(PCBKind.INSTANCE);
//        addEnumType(PFKeyKind.INSTANCE);
//        addEnumType(ProtectKind.INSTANCE);
//        addEnumType(ScopeKind.INSTANCE);
//        addEnumType(SelectTypeKind.INSTANCE);
//        addEnumType(SignKind.INSTANCE);
        addEnumType(TypeKind.INSTANCE);
//        addEnumType(ParameterModifierKind.INSTANCE);
//        addEnumType(UITypeKind.INSTANCE);
//        addEnumType(WhitespaceKind.INSTANCE);
//        addEnumType(WindowAttributeKind.INSTANCE);        
    }
    
    private void addEnumType(Enumeration enumeration) {
        EnumerationTypeBinding enumType =  enumeration.getType();
        enumTypes.put(enumType.getName(), enumType);
        if (enumeration.isResolvable()) {
            resolvableEnumTypes.add(enumType);
        }
    }
    
    private void initializeResolvableEnumDataBindings() {
        resolvableEnumDataBindings = new HashMap();
        Iterator i = getResolvableEnumTypes().iterator();
        while (i.hasNext()) {
            EnumerationTypeBinding type = (EnumerationTypeBinding)i.next();
            addResolvableDataBindings(type);
        }
    }
    
    public void addResolvableDataBindings(EnumerationTypeBinding enumTypeBinding) {
    	if(resolvableEnumDataBindings == Collections.EMPTY_MAP) {
    		resolvableEnumDataBindings = new HashMap();
    	}
        Iterator i = enumTypeBinding.getEnumerations().iterator();
        while (i.hasNext()) {
            EnumerationDataBinding enumDataBinding = (EnumerationDataBinding) i.next();
            resolvableEnumDataBindings.put(enumDataBinding.getName(), enumDataBinding);
        }
    }

}
