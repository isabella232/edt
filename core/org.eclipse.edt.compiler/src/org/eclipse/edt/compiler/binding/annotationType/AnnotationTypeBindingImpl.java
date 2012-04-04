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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.internal.core.lookup.SpecificTypedLiteral;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;

 
 
public class AnnotationTypeBindingImpl extends AnnotationTypeBinding implements IPartSubTypeAnnotationTypeBinding {

	private transient FlexibleRecordBinding annotationRecord;
	private transient IPartBinding declaringPart;
	private transient IAnnotationTypeBinding validationProxy;

	public AnnotationTypeBindingImpl(FlexibleRecordBinding annotationRecord, IPartBinding declaringPart) {
		super(annotationRecord.getCaseSensitiveName(), new Object[0]);
		this.annotationRecord = annotationRecord;
		this.declaringPart = declaringPart;
	}

	public boolean isApplicableFor(IBinding binding) {
		if(binding.isAnnotationBinding()) {
			return true;
		}
		
		IAnnotationBinding targets = (IAnnotationBinding) annotationRecord.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()).findData(InternUtil.intern("targets"));
		return isApplicableFor(binding, targets == IBinding.NOT_FOUND_BINDING ? new Object[0] : (Object[]) targets.getValue());
	}
	
	private boolean isApplicableFor(IBinding targetBinding, Object[] allowedTargets) {
		for(int i = 0; i < allowedTargets.length; i++) {
			EnumerationDataBinding nextTarget = (EnumerationDataBinding) allowedTargets[i];
			
			switch(nextTarget.geConstantValue()) {
				case ElementKind.DATATABLEPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.DATATABLE_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.DATATABLEUSE_CONSTANT:
					if(targetBinding.isUsedTypeBinding() && ITypeBinding.DATATABLE_BINDING == ((UsedTypeBinding) targetBinding).getType().getKind()) {
						return true;
					}
					break;
				case ElementKind.DATAITEMPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.DATAITEM_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.DELEGATEPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.DELEGATE_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.EXTERNALTYPEPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.EXTERNALTYPE_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.FORMPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.FORM_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.FORMUSE_CONSTANT:
					if(targetBinding.isUsedTypeBinding() && ITypeBinding.FORM_BINDING == ((UsedTypeBinding) targetBinding).getType().getKind()) {
						return true;
					}
					break;
				case ElementKind.FORMGROUPPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.FORMGROUP_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.FORMGROUPUSE_CONSTANT:
					if(targetBinding.isUsedTypeBinding() && ITypeBinding.FORMGROUP_BINDING == ((UsedTypeBinding) targetBinding).getType().getKind()) {
						return true;
					}
					break;
				case ElementKind.FUNCTIONPART_CONSTANT:
					if(targetBinding.isFunctionBinding() && ((IFunctionBinding) targetBinding).isPartBinding()) {
						return true;
					}
					break;
				case ElementKind.HANDLERPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.HANDLER_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.INTERFACEPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.INTERFACE_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.PART_CONSTANT:
					if(targetBinding.isTypeBinding() && ((ITypeBinding) targetBinding).isPartBinding()) {
						return true;
					}
					break;
				case ElementKind.PROGRAMPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.PROGRAM_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.RECORDPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.FLEXIBLE_RECORD_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.VGUIRECORDPART_CONSTANT:
				case ElementKind.STRUCTUREDRECORDPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.FIXED_RECORD_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.LIBRARYPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.LIBRARY_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.LIBRARYUSE_CONSTANT:
					if(targetBinding.isUsedTypeBinding() && ITypeBinding.LIBRARY_BINDING == ((UsedTypeBinding) targetBinding).getType().getKind()) {
						return true;
					}
					break;					
				case ElementKind.SERVICEPART_CONSTANT:
					if(targetBinding.isTypeBinding() && ITypeBinding.SERVICE_BINDING == ((ITypeBinding) targetBinding).getKind()) {
						return true;
					}
					break;
				case ElementKind.FIELDMBR_CONSTANT:
					if(targetBinding.isDataBinding()) {
						int kind = ((IDataBinding) targetBinding).getKind();
						if(kind == IDataBinding.FLEXIBLE_RECORD_FIELD ||
						   kind == IDataBinding.CLASS_FIELD_BINDING ||
						   kind == IDataBinding.LOCAL_VARIABLE_BINDING ||
						   kind == IDataBinding.FUNCTION_PARAMETER_BINDING ||
						   kind == IDataBinding.PROGRAM_PARAMETER_BINDING ||
						   kind == IDataBinding.FORM_FIELD) {
							return true;
						}
					}
					else if(targetBinding.isTypeBinding()) {
						if(ITypeBinding.DATAITEM_BINDING == ((ITypeBinding) targetBinding).getKind()) {
							return true;
						}
					}
					break;
				case ElementKind.STRUCTUREDFIELDMBR_CONSTANT:
					if(targetBinding.isDataBinding()) {
						int kind = ((IDataBinding) targetBinding).getKind();
						if(kind == IDataBinding.STRUCTURE_ITEM_BINDING) {
							return true;
						}
					}
					else if(targetBinding.isTypeBinding()) {
						if(ITypeBinding.DATAITEM_BINDING == ((ITypeBinding) targetBinding).getKind()) {
							return true;
						}
					}
					break;
				case ElementKind.FUNCTIONMBR_CONSTANT:
					if(targetBinding.isFunctionBinding() && !((FunctionBinding) targetBinding).isPartBinding()) {
						return true;
					}
					break;
				case ElementKind.CONSTRUCTORMBR_CONSTANT:
					if(targetBinding instanceof ConstructorBinding) {
						return true;
					}
					break;
				case ElementKind.ANNOTATIONTYPE_CONSTANT:
					break;
				case ElementKind.ANNOTATIONVALUE_CONSTANT:
					break;
				case ElementKind.OPENUISTATEMENT_CONSTANT:
					if(targetBinding.isOpenUIStatementBinding()) {
						return true;
					}
					break;
				case ElementKind.CALLSTATEMENT_CONSTANT:
					if(targetBinding.isCallStatementBinding()) {
						return true;
					}
					break;
				case ElementKind.TRANSFERSTATEMENT_CONSTANT:
					if(targetBinding.isTransferStatementBinding()) {
						return true;
					}
					break;
				case ElementKind.SHOWSTATEMENT_CONSTANT:
					if(targetBinding.isShowStatementBinding()) {
						return true;
					}
					break;
				case ElementKind.EXITSTATEMENT_CONSTANT:
					if(targetBinding.isExitStatementBinding()) {
						return true;
					}
					break;
			}
		}
		return false;
	}

	public ITypeBinding getSingleValueType() {
		IDataBinding[] fields = annotationRecord.getFields();
		return fields.length == 0 ? null : fields[0].getType();
	}
	
	public IDataBinding findData(String simpleName) {
		IDataBinding fieldBinding = annotationRecord.findData(InternUtil.intern(simpleName));

		// Check the declarer, because defaultSuperType fields may be found
		if(IBinding.NOT_FOUND_BINDING != fieldBinding && fieldBinding.getDeclaringPart() == annotationRecord) {
			final AnnotationTypeBinding aTypeBinding = this;
			final ITypeBinding fieldBindingType = fieldBinding.getType();
			return new AnnotationFieldBinding(fieldBinding.getCaseSensitiveName(), declaringPart, new AnnotationTypeBinding(fieldBinding.getCaseSensitiveName(), new Object[0]) {
				public boolean isApplicableFor(IBinding binding) {
					return binding == aTypeBinding;
				}
				
				public boolean hasSingleValue() {
					return true;
				}
				
				public ITypeBinding getSingleValueType() {
					return fieldBindingType; 
				}
				
				public EnumerationTypeBinding getEnumerationType() {
					ITypeBinding baseType = fieldBindingType.getBaseType();
					return ITypeBinding.ENUMERATION_BINDING == baseType.getKind() ?
						(EnumerationTypeBinding) baseType : null;
				}

				public boolean isValueless() {
					return false;
				}

			}, this);
		}
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public boolean isPartSubType() {
		return AnnotationAnnotationTypeBinding.getInstance() == annotationRecord.getSubType() &&
		       annotationRecord.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()).getAnnotation(StereotypeAnnotationTypeBinding.getInstance()) != null;
	}

	public List getPartSubTypeAnnotations() {
		return Collections.EMPTY_LIST;
	}

	public List getPartTypeAnnotations() {
		return Collections.EMPTY_LIST;
	}
	
	public List getFieldAccessAnnotations() {
		return Collections.EMPTY_LIST;
	}
	
	public Object getDefaultValueForField(String fieldName) {
		IDataBinding field = annotationRecord.findData(fieldName);
		
		if (IBinding.NOT_FOUND_BINDING == field) {
			return null;
		}
		else {
			return SpecificTypedLiteral.getNonSpecificValue(((FlexibleRecordFieldBinding) field).getInitialValue());
		}
	}
	
	public List getFieldNames() {
		IDataBinding[] fields = annotationRecord.getFields();
		List result = new ArrayList();
		for(int i = 0; i < fields.length; i++) {
			result.add(fields[i].getName());
		}
		return result;
	}
	
	public List getCaseSensitiveFieldNames() {
		IDataBinding[] fields = annotationRecord.getFields();
		List result = new ArrayList();
		for(int i = 0; i < fields.length; i++) {
			result.add(fields[i].getCaseSensitiveName());
		}
		return result;
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
		return annotationRecord.getAnnotation(annotationType);
	}

	public FlexibleRecordBinding getAnnotationRecord() {
		return annotationRecord;
	}
	
	public boolean hasSingleValue() {
		IDataBinding[] fields = annotationRecord.getFields();
		return (fields.length == 1) && Binding.isValidBinding(fields[0].getType());
	}
	
	public boolean isValueless() {
		IDataBinding[] fields = annotationRecord.getFields();
		return (fields.length == 0);
	}
	
	public IAnnotationTypeBinding getValidationProxy() {
		if(validationProxy == null) {
			IAnnotationBinding validationProxyABinding = (IAnnotationBinding) annotationRecord.getAnnotation(annotationRecord.getSubType()).findData("validationProxy");
			if(IBinding.NOT_FOUND_BINDING != validationProxyABinding) {
				String validationProxyClassName = (String) validationProxyABinding.getValue();
				try {
					Class validationProxyClass = getClass().getClassLoader().loadClass(validationProxyClassName);
					validationProxy = (IAnnotationTypeBinding) validationProxyClass.getMethod("getInstance", new Class[0]).invoke(null, null);
				}
				catch(ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				catch(IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				}
				catch (SecurityException e) {
					throw new RuntimeException(e);
				}
				catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			}
			
			if(validationProxy == null) {
				validationProxy = this;
			}
		}
		
		return validationProxy;
	}

	public void setAnnotationRecord(FlexibleRecordBinding annotationRecord) {
		this.annotationRecord = annotationRecord;
	}
	
	public String[] getPackageName() {
		return annotationRecord.getPackageName();
	}
	
	public EnumerationTypeBinding getEnumerationType() {
		ITypeBinding singleValueType = getSingleValueType();
		if(singleValueType != null) {
			ITypeBinding baseType = singleValueType.getBaseType();
			if(ITypeBinding.ENUMERATION_BINDING == baseType.getKind()) {
				return (EnumerationTypeBinding) baseType;
			}
		}
		return null;
	}
	
	public boolean isComplex() {
		return annotationRecord.getFields().length > 1;
	}
	
	public boolean supportsElementOverride() {
		IAnnotationBinding binding = (IAnnotationBinding) annotationRecord.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()).findData(InternUtil.intern("IsFormFieldArrayProperty"));
		return binding != IBinding.NOT_FOUND_BINDING && Boolean.YES == binding.getValue();
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(annotationRecord.getKind());
        out.writeUnshared(annotationRecord.getPackageName());
        out.writeUTF(annotationRecord.getCaseSensitiveName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();       
        int partKind = in.readInt();
        String[] packageName = (String[]) in.readUnshared();
        if(packageName.length != 0) {packageName = InternUtil.intern(packageName);}
        String partName = InternUtil.internCaseSensitive(in.readUTF());
        annotationRecord =  (FlexibleRecordBinding)PartBinding.newPartBinding(partKind, packageName, partName);
    }
    
    public boolean takesExpressionInOpenUIStatement() {
    	IAnnotationBinding aBinding = annotationRecord.getAnnotation(EGLValueCanBeExpressionForOpenUIAnnotationTypeBinding.getInstance());
    	return aBinding != null && Boolean.YES == aBinding.getValue();
    }
    
    public boolean isSystemAnnotation() {
		IAnnotationBinding aBinding = annotationRecord.getAnnotation(EGLIsSystemAnnotationAnnotationTypeBinding.getInstance());
		return aBinding != null && Boolean.YES == aBinding.getValue();
	}
    
    public boolean isBIDIEnabled() {
		IAnnotationBinding aBinding = annotationRecord.getAnnotation(EGLIsBIDIEnabledAnnotationTypeBinding.getInstance());
		return aBinding != null && Boolean.YES == aBinding.getValue();
	}
}
