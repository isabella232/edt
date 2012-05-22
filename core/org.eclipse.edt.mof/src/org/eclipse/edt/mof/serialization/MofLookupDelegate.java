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
package org.eclipse.edt.mof.serialization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.impl.DynamicEClass;
import org.eclipse.edt.mof.serialization.IEnvironment.LookupDelegate;


public class MofLookupDelegate implements LookupDelegate {

	@Override
	public EObject find(String key, IEnvironment env) throws MofObjectNotFoundException, DeserializationException {
		return find(key, false, env);
	}
	@Override
	public EObject find(String key, boolean useProxies, IEnvironment env) throws MofObjectNotFoundException, DeserializationException {
		int i = 0;
		
		if (isDynamicReference(key)) {
			return createDynamicEClass(key, env);
		}
		String baseType;
		String[] typeArgs = null;
		i = key.indexOf('<');	
		int typeEnd = -1;
		
		if (i == -1) { 
			return env.lookup(key); 
		}
		typeEnd = (i == -1) ? key.length()-1 : i;
		baseType = key.substring(0, typeEnd);
		if (i != -1) {
			String types = key.substring(i+1);
			int j = types.lastIndexOf('>');
			typeArgs = types.substring(0,j).split("[,]");
		}
		
		// If we get here we know we have a generic type signature
		EType result;
		result = null;
		EClassifier eBaseType = (EClassifier)env.find(baseType, useProxies);
		
		// Resolve type args first to make sure they are all there
		List<EType> types = new ArrayList<EType>();
		if (typeArgs != null) {
			for (String arg : typeArgs) {
				EType eType = (EType)env.find(arg, useProxies);
				types.add(eType);
			}
		}
		EGenericType generic;
		generic = (EGenericType)MofFactory.INSTANCE.getEGenericTypeClass().newInstance();
		generic.setEClassifier(eBaseType);
		if (typeArgs != null) {
			for (EType arg : types) {
				generic.addETypeArgument(arg);
			}
		}
		result = generic;
		env.save(key, result, false);
		return result;

	}

	@Override
	public boolean supportsScheme(String scheme) {
		return IEnvironment.DefaultScheme.equals(scheme);
	}

	public Class<? extends ProxyEObject> getProxyClass() {
		return ProxyEClass.class;
	}

	@Override
	public String normalizeKey(String key) {
		// Default normalization is to do nothing
		return key;
	}

	private boolean isDynamicReference(String key) {
		return key.startsWith(IEnvironment.DynamicScheme);
	}
	
	private DynamicEClass createDynamicEClass(String key, IEnvironment env) throws MofObjectNotFoundException, DeserializationException {
		DynamicEClass eClass = new DynamicEClass();
		String[] names = key.split("[:]");
		String typeSignature = names[2];
		int i = typeSignature.lastIndexOf('.');
		if (i != -1) {
			eClass.setPackageName(typeSignature.substring(0,i));
			eClass.setName(typeSignature.substring(i+1));
		}
		else {
			eClass.setName(typeSignature);
		}
		EClass superType = (EClass)env.find(names[1]);
		((EClass)eClass).getSuperTypes().add(superType);
		env.save(eClass, false);
		return eClass;
	}
}
