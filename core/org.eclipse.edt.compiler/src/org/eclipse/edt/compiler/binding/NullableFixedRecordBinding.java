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


class NullableFixedRecordBinding extends FixedRecordBinding implements INullableTypeBinding {
	
	private FixedRecordBinding fixedRecordBinding;

	public NullableFixedRecordBinding(FixedRecordBinding fixedRecordBinding) {
		super(fixedRecordBinding.getPackageName(), fixedRecordBinding.getCaseSensitiveName());
		this.fixedRecordBinding = fixedRecordBinding;
	}
	
	public ITypeBinding getValueType() {
		return fixedRecordBinding;
	}

	public List getStructureItems() {
		return fixedRecordBinding.getStructureItems();
	}

	public boolean containsReferenceTo(FixedStructureBinding structure) {
		return fixedRecordBinding.containsReferenceTo(structure);
	}

	public int getSizeInBytes() {
		return fixedRecordBinding.getSizeInBytes();
	}

	public IPartBinding realize() {
		return (IPartBinding) fixedRecordBinding.realize().getNullableInstance();
	}

	public void clear() {
		fixedRecordBinding.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		return fixedRecordBinding.isStructurallyEqual(anotherPartBinding);
	}

	public void setValid(boolean isValid) {
		fixedRecordBinding.setValid(isValid);
	}

	public IPartSubTypeAnnotationTypeBinding getSubType() {
		return fixedRecordBinding.getSubType();
	}

	public IAnnotationBinding getSubTypeAnnotationBinding() {
		return fixedRecordBinding.getSubTypeAnnotationBinding();
	}


	public boolean isDeclarablePart() {
		return fixedRecordBinding.isDeclarablePart();
	}

	public boolean isPrivate() {
		return fixedRecordBinding.isPrivate();
	}

	public int getKind() {
		return fixedRecordBinding.getKind();
	}

	public boolean isValid() {
		return fixedRecordBinding.isValid();
	}

	public String[] getPackageName() {
		return fixedRecordBinding.getPackageName();
	}

	public IDataBinding findData(String simpleName) {
		return fixedRecordBinding.findData(simpleName);
	}

	public IDataBinding findPublicData(String simpleName) {
		return fixedRecordBinding.findPublicData(simpleName);
	}

	public Map getSimpleNamesToDataBindingsMap() {
		return fixedRecordBinding.getSimpleNamesToDataBindingsMap();
	}

	public IFunctionBinding findFunction(String simpleName) {
		return fixedRecordBinding.findFunction(simpleName);
	}

	public IFunctionBinding findPublicFunction(String simpleName) {
		return fixedRecordBinding.findPublicFunction(simpleName);
	}

	public boolean isReference() {
		return fixedRecordBinding.isReference();
	}

	public boolean isDynamic() {
		return fixedRecordBinding.isDynamic();
	}

	public boolean isDynamicallyAccessible() {
		return fixedRecordBinding.isDynamicallyAccessible();
	}

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		return fixedRecordBinding.isReferentiallyEqual(anotherTypeBinding);
	}

	public boolean isPartBinding() {
		return fixedRecordBinding.isPartBinding();
	}

	public ITypeBinding copyTypeBinding() {
		return fixedRecordBinding.copyTypeBinding().getNullableInstance();
	}

	public ITypeBinding getBaseType() {
		return fixedRecordBinding.getBaseType();
	}

	public boolean isNullable() {
		return true;
	}

	public ITypeBinding getNullableInstance() {
		return this;
	}

	public List getAnnotations() {
		return fixedRecordBinding.getAnnotations();
	}

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
		return fixedRecordBinding.getAnnotation(annotationType, index);
	}

	public void addAnnotation(IAnnotationBinding annotation) {
		fixedRecordBinding.addAnnotation(annotation);
	}

	public void addAnnotations(Collection annotations) {
		fixedRecordBinding.addAnnotations(annotations);
	}
	
	public void setEnvironment(IEnvironment environment) {
		fixedRecordBinding.setEnvironment(environment);
	}
	
	public IEnvironment getEnvironment() {
		return fixedRecordBinding.getEnvironment();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeTypeBindingReference(out, fixedRecordBinding);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        fixedRecordBinding = (FixedRecordBinding) readTypeBindingReference(in);
    }

	public IDataBinding[] getFields() {
		return fixedRecordBinding.getFields();
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
		return fixedRecordBinding.getAnnotation(annotationType);
	}
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		return fixedRecordBinding.getAnnotation(packageName, annotationName);
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, List list) {
		return fixedRecordBinding.getAnnotation(annotationType, list);
	}
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, List list) {
		return fixedRecordBinding.getAnnotation(packageName, annotationName, list);
	}
}
