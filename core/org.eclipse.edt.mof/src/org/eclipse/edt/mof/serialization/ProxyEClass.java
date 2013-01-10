/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.serialization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.ETypeParameter;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.impl.EObjectImpl;


public class ProxyEClass extends ProxyEObject implements EClass {


	public String typeSignature;
	public List<ProxyEObject> proxyObjects = new ArrayList<ProxyEObject>();
	
	public List<ProxyEObject> getProxyObjects() {
		return proxyObjects;
	}
	
	public void updateReferences(EClass type) {
		updateReferences((Object)type);
		for (ProxyEObject proxy : proxyObjects) {
			EObjectImpl obj = (EObjectImpl)type.newInstance(false);
			obj.setSlots(proxy.slots);
			proxy.updateReferences(obj);
		}
		
	}
	
	public ProxyEClass(String typeSignature) {
		this.typeSignature = typeSignature;
	}
	@Override
	public String getETypeSignature() {
		return typeSignature;
	}

	@Override
	public EClass getEClass() {
		return MofFactory.INSTANCE.getEClassifierClass();
	}
	@Override
	public List<EClass> getSuperTypes() {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isAbstract() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void setIsAbstract(boolean value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public EField getEField(String name) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<EField> getEFields() {
		throw new UnsupportedOperationException();
	}
	
	public EFunction getFunction(String name) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<EFunction> getEFunctions() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getPackageName() {
		return getCaseSensitivePackageName();
	}
	@Override
	public String getCaseSensitivePackageName() {
		int i = getETypeSignature().lastIndexOf('.');
		if (i != -1) {
			return getETypeSignature().substring(0,i);
		}
		else {
			return null;
		}
	}
	@Override
	public void setPackageName(String name) {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getName() {
		int i = getETypeSignature().lastIndexOf('.');
		if (i != -1) {
			return getETypeSignature().substring(i+1);
		}
		else {
			return getETypeSignature();
		}
	}
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}
	@Override
	public EMetadataObject getMetadata(String typeName) {
		return null;
	}
	@Override
	public List<EMetadataObject> getMetadataList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<EField> getAllEFields() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSubClassOf(EClass class1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize(EObject object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize(EObject object, boolean useInitialValues) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EMetadataObject getMetadata(EClass annType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ETypeParameter> getETypeParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EField getEField(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject newInstance(boolean init) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public EObject newInstance(boolean init, boolean useInitialValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EClassifier getEClassifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInstance(EObject obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInterface() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsInterface(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMember(EMember mbr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMofSerializationKey() {
		return getETypeSignature().toLowerCase();
	}


	@Override
	public void addSuperTypes(List<EClass> superTypes) {
		// TODO Auto-generated method stub
		
	}

}
