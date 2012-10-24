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
package org.eclipse.edt.mof.egl.lookup;

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EVisitor;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.DataType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeKind;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.NameUtile;

public class ProxyPart extends ProxyEObject implements Part, DataType, ParameterizableType {
	private static String KeyScheme = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;
	
	private String typeSignature;
	private String name;
	private String packageName;
	
	public ProxyPart(String typeSignature) {
		int i = typeSignature.indexOf(KeyScheme);
		if (i == -1)
			this.typeSignature = typeSignature;
		else 
			this.typeSignature = typeSignature.substring(i + KeyScheme.length());
	}	
	

	@Override
	public String getMofSerializationKey() {
		if (typeSignature.startsWith(KeyScheme)) {
			return typeSignature.toUpperCase().toLowerCase();
		}
		return (KeyScheme + typeSignature).toUpperCase().toLowerCase();
	}
	
	@Override
	public AccessKind getAccessKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return typeSignature;
	}

	@Override
	public String getTypeSignature() {
		return typeSignature;
	}

	@Override
	public Stereotype getStereotype() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Stereotype> getStereotypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stereotype getSubType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean hasCompileErrors() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAccessKind(AccessKind value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFileName(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHasCompileErrors(Boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<TypeParameter> getTypeParameters() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(EVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(EField field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSet(EField field, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSet(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNull(EField field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEClass(EClass type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		
		int index = getFullyQualifiedName().lastIndexOf(".");
		if (index < 0) {
			return getFullyQualifiedName();
		}
		return getFullyQualifiedName().substring(index + 1);
	}

	@Override
	public void addAnnotation(Annotation ann) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Annotation getAnnotation(String typeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Annotation> getAnnotations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean equals(Type eglType) {
		return eglType == this;
	}

	@Override
	public Classifier getClassifier() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getPackageName() {
		if (packageName == null) {
			packageName = NameUtile.getAsName(getCaseSensitivePackageName());
		}
		return packageName;
	}
	
	@Override
	public String getCaseSensitivePackageName() {
		int index = getFullyQualifiedName().lastIndexOf(".");
		if (index < 0) {
			return "";
		}
		return getFullyQualifiedName().substring(0, index);
	}

	@Override
	public String getName() {
		if (name == null) {
			name = NameUtile.getAsName(getCaseSensitiveName());
		}
		return name;
	}
	
	@Override
	public String getCaseSensitiveName() {
		int index = getFullyQualifiedName().lastIndexOf(".");
		if (index < 0) {
			return getFullyQualifiedName();
		}
		return getFullyQualifiedName().substring(index + 1);
	}

	@Override
	public void setPackageName(String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Function> getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Operation> getOperations() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TypeKind getTypeKind() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setTypeKind(TypeKind value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addMember(Member mbr) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Member> getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EClass getParameterizedType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setParameterizedType(EClass value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Boolean isAbstract() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setIsAbstract(Boolean value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addInitializerStatements(StatementBlock block) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Field getField(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Field> getFields() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Function getFunction(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Function> getFunctions(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Part> getUsedParts() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Constructor> getConstructors() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Interface> getInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<StructuredField> getStructuredFields() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<StructuredField> getStructuredFields(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<StructPart> getSuperTypes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Annotation getAnnotation(AnnotationType type) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeAnnotation(Annotation ann) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isSubtypeOf(StructPart part) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isNativeType() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<Member> getAllMembers() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void collectMembers(List<Member> members, List<StructPart> alreadySeen) {
		// TODO Auto-generated method stub
		
	}

  
	@Override
	public boolean isInstantiable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<Operation> getOperations(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public StatementBlock getInitializerStatements() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setInitializerStatements(StatementBlock value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Element resolveElement() {
		return this;
	}


	@Override
	public Part resolvePart() {
		return this;
	}

}
