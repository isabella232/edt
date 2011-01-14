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
package org.eclipse.edt.compiler.binding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


public class NullableFlexibleRecordBinding extends FlexibleRecordBinding implements INullableTypeBinding {

	private FlexibleRecordBinding recordBinding;

	public NullableFlexibleRecordBinding(FlexibleRecordBinding recordBinding) {
		super(recordBinding.getPackageName(), recordBinding.getCaseSensitiveName());
		this.recordBinding = recordBinding;
	}
	
	public ITypeBinding getValueType() {
		return recordBinding;
	}
	
	public List getDeclaredFields() {
		return recordBinding.getDeclaredFields();
	}
	
	public boolean containsReferenceTo(FlexibleRecordBinding record) {
		return recordBinding.containsReferenceTo(record);
	}

	public IPartBinding realize() {
		return (IPartBinding) recordBinding.realize().getNullableInstance();
	}

	public void clear() {
		recordBinding.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		return recordBinding.isStructurallyEqual(anotherPartBinding);
	}

	public void setValid(boolean isValid) {
		recordBinding.setValid(isValid);
	}

	public IPartSubTypeAnnotationTypeBinding getSubType() {
		return recordBinding.getSubType();
	}

	public IAnnotationBinding getSubTypeAnnotationBinding() {
		return recordBinding.getSubTypeAnnotationBinding();
	}


	public boolean isDeclarablePart() {
		return recordBinding.isDeclarablePart();
	}

	public boolean isPrivate() {
		return recordBinding.isPrivate();
	}

	public int getKind() {
		return recordBinding.getKind();
	}

	public boolean isValid() {
		return recordBinding.isValid();
	}

	public String[] getPackageName() {
		return recordBinding.getPackageName();
	}

	public IDataBinding findData(String simpleName) {
		return recordBinding.findData(simpleName);
	}

	public IDataBinding findPublicData(String simpleName) {
		return recordBinding.findPublicData(simpleName);
	}

	public Map getSimpleNamesToDataBindingsMap() {
		return recordBinding.getSimpleNamesToDataBindingsMap();
	}

	public IFunctionBinding findFunction(String simpleName) {
		return recordBinding.findFunction(simpleName);
	}

	public IFunctionBinding findPublicFunction(String simpleName) {
		return recordBinding.findPublicFunction(simpleName);
	}

	public boolean isReference() {
		return recordBinding.isReference();
	}

	public boolean isDynamic() {
		return recordBinding.isDynamic();
	}

	public boolean isDynamicallyAccessible() {
		return recordBinding.isDynamicallyAccessible();
	}

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		return recordBinding.isReferentiallyEqual(anotherTypeBinding);
	}

	public boolean isPartBinding() {
		return recordBinding.isPartBinding();
	}

	public ITypeBinding copyTypeBinding() {
		return recordBinding.copyTypeBinding().getNullableInstance();
	}

	public ITypeBinding getBaseType() {
		return recordBinding.getBaseType();
	}

	public boolean isNullable() {
		return true;
	}

	public ITypeBinding getNullableInstance() {
		return this;
	}

	public List getAnnotations() {
		return recordBinding.getAnnotations();
	}

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
		return recordBinding.getAnnotation(annotationType, index);
	}

	public void addAnnotation(IAnnotationBinding annotation) {
		recordBinding.addAnnotation(annotation);
	}

	public void addAnnotations(Collection annotations) {
		recordBinding.addAnnotations(annotations);
	}
	
	public void setEnvironment(IEnvironment environment) {
		recordBinding.setEnvironment(environment);
	}
	
	public IEnvironment getEnvironment() {
		return recordBinding.getEnvironment();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeTypeBindingReference(out, recordBinding);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        recordBinding = (FlexibleRecordBinding) readTypeBindingReference(in);
    }

	public IDataBinding[] getFields() {
		return recordBinding.getFields();
	}

	public void addField(FlexibleRecordFieldBinding fieldBinding) {
		recordBinding.addField(fieldBinding);
	}

	public void addField(FlexibleRecordFieldBinding fieldBinding, int index) {
		recordBinding.addField(fieldBinding, index);
	}

	public void addReference(FlexibleRecordFieldBinding fieldBinding) {
		recordBinding.addReference(fieldBinding);		
	}

	void addReferencedRecord(FlexibleRecordBinding record) {
		recordBinding.addReferencedRecord(record);		
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
		return recordBinding.getAnnotation(annotationType);
	}
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		return recordBinding.getAnnotation(packageName, annotationName);
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, List list) {
		return recordBinding.getAnnotation(annotationType, list);
	}
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, List list) {
		return recordBinding.getAnnotation(packageName, annotationName, list);
	}
	
	protected List getReferencedRecords() {
		return recordBinding.getReferencedRecords();
	}
}
