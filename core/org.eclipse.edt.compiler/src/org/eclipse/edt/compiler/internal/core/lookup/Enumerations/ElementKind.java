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
package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class ElementKind extends Enumeration{
	public final static ElementKind INSTANCE = new ElementKind();
	public final static int TYPE_CONSTANT = ELEMENTKIND;

	public final static int DATATABLEPART_CONSTANT = 1;
	public final static int DATAITEMPART_CONSTANT = 2;
	public final static int DELEGATEPART_CONSTANT = 3;
	public final static int EXTERNALTYPEPART_CONSTANT = 4;
	public final static int FORMPART_CONSTANT = 5;
	public final static int FORMGROUPPART_CONSTANT = 6;
	public final static int FUNCTIONPART_CONSTANT = 7;
	public final static int HANDLERPART_CONSTANT = 8;
	public final static int INTERFACEPART_CONSTANT = 9;
	public final static int PART_CONSTANT = 10;
	public final static int PROGRAMPART_CONSTANT = 11;
	public final static int RECORDPART_CONSTANT = 12;
	public final static int LIBRARYPART_CONSTANT = 13;
	public final static int SERVICEPART_CONSTANT = 14;
	public final static int FIELDMBR_CONSTANT = 15;
	public final static int STRUCTUREDFIELDMBR_CONSTANT = 16;
	public final static int FUNCTIONMBR_CONSTANT = 17;
	public final static int CONSTRUCTORMBR_CONSTANT = 18;
	public final static int ANNOTATIONTYPE_CONSTANT = 19;
	public final static int ANNOTATIONVALUE_CONSTANT = 20;
	public final static int OPENUISTATEMENT_CONSTANT = 21;
	public final static int DATATABLEUSE_CONSTANT = 22;
	public final static int FORMGROUPUSE_CONSTANT = 23;
	public final static int FORMUSE_CONSTANT = 24;
	public final static int LIBRARYUSE_CONSTANT = 25;
	public final static int CALLSTATEMENT_CONSTANT = 26;
	public final static int TRANSFERSTATEMENT_CONSTANT = 27;
	public final static int SHOWSTATEMENT_CONSTANT = 28;
	public final static int STRUCTUREDRECORDPART_CONSTANT = 29;
	public final static int VGUIRECORDPART_CONSTANT = 30;
	public final static int EXITSTATEMENT_CONSTANT = 31;
	public final static int ENUMERATIONPART_CONSTANT = 32;
	public final static int ENUMERATIONENTRY_CONSTANT = 33;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("ElementKind"), ELEMENTKIND);
	public final static SystemEnumerationDataBinding DATATABLEPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DataTablePart"), null, TYPE, DATATABLEPART_CONSTANT);
	public final static SystemEnumerationDataBinding DATAITEMPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DataItemPart"), null, TYPE, DATAITEMPART_CONSTANT);
	public final static SystemEnumerationDataBinding DELEGATEPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DelegatePart"), null, TYPE, DELEGATEPART_CONSTANT);
	public final static SystemEnumerationDataBinding EXTERNALTYPEPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ExternalTypePart"), null, TYPE, EXTERNALTYPEPART_CONSTANT);
	public final static SystemEnumerationDataBinding FORMPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FormPart"), null, TYPE, FORMPART_CONSTANT);
	public final static SystemEnumerationDataBinding FORMGROUPPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FormGroupPart"), null, TYPE, FORMGROUPPART_CONSTANT);
	public final static SystemEnumerationDataBinding FUNCTIONPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FunctionPart"), null, TYPE, FUNCTIONPART_CONSTANT);
	public final static SystemEnumerationDataBinding HANDLERPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("HandlerPart"), null, TYPE, HANDLERPART_CONSTANT);
	public final static SystemEnumerationDataBinding INTERFACEPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("InterfacePart"), null, TYPE, INTERFACEPART_CONSTANT);
	public final static SystemEnumerationDataBinding PART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("Part"), null, TYPE, PART_CONSTANT);
	public final static SystemEnumerationDataBinding PROGRAMPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ProgramPart"), null, TYPE, PROGRAMPART_CONSTANT);
	public final static SystemEnumerationDataBinding RECORDPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("RecordPart"), null, TYPE, RECORDPART_CONSTANT);
	public final static SystemEnumerationDataBinding LIBRARYPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("LibraryPart"), null, TYPE, LIBRARYPART_CONSTANT);
	public final static SystemEnumerationDataBinding SERVICEPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ServicePart"), null, TYPE, SERVICEPART_CONSTANT);
	public final static SystemEnumerationDataBinding FIELDMBR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FieldMbr"), null, TYPE, FIELDMBR_CONSTANT);
	public final static SystemEnumerationDataBinding STRUCTUREDFIELDMBR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("StructuredFieldMbr"), null, TYPE, STRUCTUREDFIELDMBR_CONSTANT);
	public final static SystemEnumerationDataBinding FUNCTIONMBR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FunctionMbr"), null, TYPE, FUNCTIONMBR_CONSTANT);
	public final static SystemEnumerationDataBinding CONSTRUCTORMBR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ConstructorMbr"), null, TYPE, CONSTRUCTORMBR_CONSTANT);
	public final static SystemEnumerationDataBinding ANNOTATIONTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("AnnotationType"), null, TYPE, ANNOTATIONTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding ANNOTATIONVALUE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("AnnotationValue"), null, TYPE, ANNOTATIONVALUE_CONSTANT);
	public final static SystemEnumerationDataBinding OPENUISTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("OpenUIStatement"), null, TYPE, OPENUISTATEMENT_CONSTANT);
	public final static SystemEnumerationDataBinding DATATABLEUSE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DataTableUse"), null, TYPE, DATATABLEUSE_CONSTANT);
	public final static SystemEnumerationDataBinding FORMGROUPUSE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FormgroupUse"), null, TYPE, FORMGROUPUSE_CONSTANT);
	public final static SystemEnumerationDataBinding FORMUSE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("FormUse"), null, TYPE, FORMUSE_CONSTANT);
	public final static SystemEnumerationDataBinding LIBRARYUSE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("LibraryUse"), null, TYPE, LIBRARYUSE_CONSTANT);
	public final static SystemEnumerationDataBinding CALLSTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("CallStatement"), null, TYPE, CALLSTATEMENT_CONSTANT);
	public final static SystemEnumerationDataBinding TRANSFERSTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("TransferStatement"), null, TYPE, TRANSFERSTATEMENT_CONSTANT);
	public final static SystemEnumerationDataBinding SHOWSTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ShowStatement"), null, TYPE, SHOWSTATEMENT_CONSTANT);
	public final static SystemEnumerationDataBinding STRUCTUREDRECORDPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("StructuredRecordPart"), null, TYPE, STRUCTUREDRECORDPART_CONSTANT);
	public final static SystemEnumerationDataBinding VGUIRECORDPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("VGUIRecordPart"), null, TYPE, VGUIRECORDPART_CONSTANT);
	public final static SystemEnumerationDataBinding EXITSTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ExitStatement"), null, TYPE, EXITSTATEMENT_CONSTANT);
	public final static SystemEnumerationDataBinding ENUMERATIONPART = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("EnumerationPart"), null, TYPE, ENUMERATIONPART_CONSTANT);
	public final static SystemEnumerationDataBinding ENUMERATIONENTRY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("EnumerationEntry"), null, TYPE, ENUMERATIONENTRY_CONSTANT);
	
	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(DATATABLEPART);
		TYPE.addEnumeration(DATAITEMPART);
		TYPE.addEnumeration(DELEGATEPART);
		TYPE.addEnumeration(EXTERNALTYPEPART);
		TYPE.addEnumeration(FORMPART);
		TYPE.addEnumeration(FORMGROUPPART);
		TYPE.addEnumeration(FUNCTIONPART);
		TYPE.addEnumeration(HANDLERPART);
		TYPE.addEnumeration(INTERFACEPART);
		TYPE.addEnumeration(PART);
		TYPE.addEnumeration(PROGRAMPART);
		TYPE.addEnumeration(RECORDPART);
		TYPE.addEnumeration(LIBRARYPART);
		TYPE.addEnumeration(SERVICEPART);
		TYPE.addEnumeration(FIELDMBR);
		TYPE.addEnumeration(STRUCTUREDFIELDMBR);
		TYPE.addEnumeration(FUNCTIONMBR);
		TYPE.addEnumeration(CONSTRUCTORMBR);
		TYPE.addEnumeration(ANNOTATIONTYPE);
		TYPE.addEnumeration(ANNOTATIONVALUE);
		TYPE.addEnumeration(OPENUISTATEMENT);
		TYPE.addEnumeration(DATATABLEUSE);
		TYPE.addEnumeration(FORMGROUPUSE);
		TYPE.addEnumeration(FORMUSE);
		TYPE.addEnumeration(LIBRARYUSE);
		TYPE.addEnumeration(CALLSTATEMENT);
		TYPE.addEnumeration(TRANSFERSTATEMENT);
		TYPE.addEnumeration(SHOWSTATEMENT);
		TYPE.addEnumeration(STRUCTUREDRECORDPART);
		TYPE.addEnumeration(VGUIRECORDPART);
		TYPE.addEnumeration(EXITSTATEMENT);
		TYPE.addEnumeration(ENUMERATIONPART);
		TYPE.addEnumeration(ENUMERATIONENTRY);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
