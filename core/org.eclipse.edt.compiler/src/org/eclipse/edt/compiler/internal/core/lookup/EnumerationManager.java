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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.utils.NameUtile;


public class EnumerationManager {
	public final static String DATATABLEPART = NameUtile.getAsName("DataTablePart");
	public final static String DATAITEMPART = NameUtile.getAsName("DataItemPart");
	public final static String DELEGATEPART = NameUtile.getAsName("DelegatePart");
	public final static String EXTERNALTYPEPART = NameUtile.getAsName("ExternalTypePart");
	public final static String FORMPART = NameUtile.getAsName("FormPart");
	public final static String FORMGROUPPART = NameUtile.getAsName("FormGroupPart");
	public final static String FUNCTIONPART = NameUtile.getAsName("FunctionPart");
	public final static String HANDLERPART = NameUtile.getAsName("HandlerPart");
	public final static String INTERFACEPART = NameUtile.getAsName("InterfacePart");
	public final static String PART = NameUtile.getAsName("Part");
	public final static String PROGRAMPART = NameUtile.getAsName("ProgramPart");
	public final static String RECORDPART = NameUtile.getAsName("RecordPart");
	public final static String LIBRARYPART = NameUtile.getAsName("LibraryPart");
	public final static String SERVICEPART = NameUtile.getAsName("ServicePart");
	public final static String FIELDMBR = NameUtile.getAsName("FieldMbr");
	public final static String STRUCTUREDFIELDMBR = NameUtile.getAsName("StructuredFieldMbr");
	public final static String FUNCTIONMBR = NameUtile.getAsName("FunctionMbr");
	public final static String CONSTRUCTORMBR = NameUtile.getAsName("ConstructorMbr");
	public final static String ANNOTATIONTYPE = NameUtile.getAsName("AnnotationType");
	public final static String ANNOTATIONVALUE = NameUtile.getAsName("AnnotationValue");
	public final static String OPENUISTATEMENT = NameUtile.getAsName("OpenUIStatement");
	public final static String DATATABLEUSE = NameUtile.getAsName("DataTableUse");
	public final static String FORMGROUPUSE = NameUtile.getAsName("FormgroupUse");
	public final static String FORMUSE = NameUtile.getAsName("FormUse");
	public final static String LIBRARYUSE = NameUtile.getAsName("LibraryUse");
	public final static String CALLSTATEMENT = NameUtile.getAsName("CallStatement");
	public final static String TRANSFERSTATEMENT = NameUtile.getAsName("TransferStatement");
	public final static String SHOWSTATEMENT = NameUtile.getAsName("ShowStatement");
	public final static String STRUCTUREDRECORDPART = NameUtile.getAsName("StructuredRecordPart");
	public final static String VGUIRECORDPART = NameUtile.getAsName("VGUIRecordPart");
	public final static String EXITSTATEMENT = NameUtile.getAsName("ExitStatement");	

	private Map<String, EnumerationEntry> resolvableEnumDataBindings = Collections.EMPTY_MAP;
    private static Enumeration ElementKind = null;
    private static IRPartBinding ElementKindBinding = null;
   
    
    private static IRPartBinding getElementKindBinding() {
    	if (ElementKindBinding == null) {
    		ElementKindBinding = new IRPartBinding(getElementKind());
    	}
    	return ElementKindBinding;
    }

    private static Enumeration getElementKind() {
    	if (ElementKind == null) {
    		ElementKind = IrFactory.INSTANCE.createEnumeration();
    		ElementKind.setName("ElementKind");
    		ElementKind.setPackageName(SystemEnvironmentPackageNames.EGL_CORE_STRING);
    		ElementKind.addMember((Member)createEntry(DATATABLEPART)); 
    		ElementKind.addMember((Member)createEntry(DATAITEMPART));
    		ElementKind.addMember((Member)createEntry(DELEGATEPART));
    		ElementKind.addMember((Member)createEntry(EXTERNALTYPEPART));
    		ElementKind.addMember((Member)createEntry(FORMPART));
    		ElementKind.addMember((Member)createEntry(FORMGROUPPART));
    		ElementKind.addMember((Member)createEntry(FUNCTIONPART));
    		ElementKind.addMember((Member)createEntry(HANDLERPART));
    		ElementKind.addMember((Member)createEntry(INTERFACEPART));
    		ElementKind.addMember((Member)createEntry(PART));
    		ElementKind.addMember((Member)createEntry(PROGRAMPART));
    		ElementKind.addMember((Member)createEntry(RECORDPART));
    		ElementKind.addMember((Member)createEntry(LIBRARYPART));
    		ElementKind.addMember((Member)createEntry(SERVICEPART));
    		ElementKind.addMember((Member)createEntry(FIELDMBR));
    		ElementKind.addMember((Member)createEntry(STRUCTUREDFIELDMBR));
    		ElementKind.addMember((Member)createEntry(FUNCTIONMBR));
    		ElementKind.addMember((Member)createEntry(CONSTRUCTORMBR));
    		ElementKind.addMember((Member)createEntry(ANNOTATIONTYPE));
    		ElementKind.addMember((Member)createEntry(ANNOTATIONVALUE));
    		ElementKind.addMember((Member)createEntry(OPENUISTATEMENT));
    		ElementKind.addMember((Member)createEntry(DATATABLEUSE));
    		ElementKind.addMember((Member)createEntry(FORMGROUPUSE));
    		ElementKind.addMember((Member)createEntry(FORMUSE));
    		ElementKind.addMember((Member)createEntry(LIBRARYUSE));
    		ElementKind.addMember((Member)createEntry(CALLSTATEMENT));
    		ElementKind.addMember((Member)createEntry(TRANSFERSTATEMENT));
    		ElementKind.addMember((Member)createEntry(SHOWSTATEMENT));
    		ElementKind.addMember((Member)createEntry(STRUCTUREDRECORDPART));
    		ElementKind.addMember((Member)createEntry(VGUIRECORDPART));
    		ElementKind.addMember((Member)createEntry(EXITSTATEMENT));
    	}
    	return ElementKind;
    }
    private static EnumerationEntry createEntry(String name) {
    	EnumerationEntry entry = IrFactory.INSTANCE.createEnumerationEntry();
    	entry.setName(name);
    	return entry;
    }
    
    public EnumerationManager(EnumerationManager parentManager) {
		super();
		if (parentManager != null) {
			resolvableEnumDataBindings = new HashMap<String, EnumerationEntry>();
			resolvableEnumDataBindings.putAll(parentManager.resolvableEnumDataBindings);
		}
	}

	public EnumerationEntry findData(String simpleName) {
		return getResolvableEnumDataBindings().get(simpleName);
    }
     
    public Map<String, EnumerationEntry> getResolvableEnumDataBindings() {
        if (resolvableEnumDataBindings == Collections.EMPTY_MAP) {
            initializeResolvableEnumDataBindings();
        }
        return resolvableEnumDataBindings;
    }

    
    private void initializeResolvableEnumDataBindings() {
        resolvableEnumDataBindings = new HashMap<String, EnumerationEntry>();
        
        for (Enumeration type : getEnumTypes()) {
            addResolvableDataBindings(type);
        }
    }
    
    public static List<Enumeration> getEnumTypes() {
    	List<Enumeration> list = new ArrayList<Enumeration>();
    	list.add(getElementKind());
    	return list;
    }

    public static List<IRPartBinding> getEnumTypeBindings() {
    	List<IRPartBinding> list = new ArrayList<IRPartBinding>();
    	list.add(getElementKindBinding());
    	return list;
    }

    public void addResolvableDataBindings(Enumeration enumTypeBinding) {
    	if(resolvableEnumDataBindings == Collections.EMPTY_MAP) {
    		resolvableEnumDataBindings = new HashMap<String, EnumerationEntry>();
    	}
    	    	
    	for (EEnumLiteral enumDataBinding : enumTypeBinding.getEntries()) {
            resolvableEnumDataBindings.put(enumDataBinding.getName(), (EnumerationEntry)enumDataBinding);
    	}
    }

}
