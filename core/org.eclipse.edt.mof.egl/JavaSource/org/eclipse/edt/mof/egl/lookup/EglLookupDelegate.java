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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.IEnvironment.LookupDelegate;

/**
 * Implementation of the <code>LookupDelegate</code> interface specific for finding
 * EGL model instances.  LookupDelegates are registered to a given IEnvironment by
 * a scheme.  The scheme for looking up EGL models is <code>Type.EGLKeyScheme</code>
 * 
 * @see LookupDelegate
 *
 */
public class EglLookupDelegate implements LookupDelegate {
	
	private IrFactory ir = IrFactory.INSTANCE;
	private String typeArgsSplitter = "[" + Type.TypeArgDelimiter + "]";
	private String primArgsSplitter = "[(" + Type.PrimArgDelimiter + " )]";
	
	@Override
	public EObject find(String key, IEnvironment env)
			throws MofObjectNotFoundException, DeserializationException {
		return find(key, false, env);
	}

	@Override
	public EObject find(String key, boolean useProxies, IEnvironment env)
			throws MofObjectNotFoundException, DeserializationException {
		int i, j, k = 0;

		String baseType = null;
		String nestedType = null;
		String[] typeArgs = null;
		String[] primArgs = null;
		i = key.indexOf('<');	
		j = key.lastIndexOf('(');
		k = key.lastIndexOf(Type.NestedPartDelimiter);
		
		if (i == -1 && j == -1 && k == -1) { 
			return env.lookup(key); 
		}
		if (i != -1) { // We have a generic type signature
			baseType = key.substring(0, i);
			typeArgs = key.substring(i+1, key.length()-1).split(typeArgsSplitter);
		}
		if (i == -1 && j != -1) { // We have a primitive type signature
			baseType = key.substring(0, j);
			primArgs = key.substring(j+1, key.length()-1).split(primArgsSplitter);
		}
		
		if (k != -1) { // We have a nested type reference , i.e. a Form within a FormGroup
			baseType = key.substring(0, k);
			nestedType = key.substring(k+1);
			// Assume FormGroup is only type that does this nesting of types
			FormGroup fg = (FormGroup)env.lookup(baseType);
			return fg.getForm(nestedType);
		}
		
		// If we get here we know we have a generic or primitive type signature
		Type result = null;
		try {
			// Since an instance of PrimitiveType is meant to be created here do not do
			// lookup of the type allowing ProxyEClasses to be returned
			Classifier eBaseType = baseType == null || baseType.equals(Type.EGL_KeyScheme + Type.KeySchemeDelimiter) 
				? null 
				: (Classifier)env.find(baseType, false);
			if (typeArgs == null) {
				if (eBaseType instanceof ParameterizableType) {
					EClass parameterizedTypeClass = ((ParameterizableType)eBaseType).getParameterizedType();
					if (parameterizedTypeClass != null) {
						ParameterizedType type = (ParameterizedType)parameterizedTypeClass.newInstance();
						type.setParameterizableType((ParameterizableType)eBaseType);
						int totalSlots = parameterizedTypeClass.getAllEFields().size();
						for (int l=totalSlots-primArgs.length; l<totalSlots; l++) {
							String value = primArgs[l - (totalSlots-primArgs.length)];
							if (!"".equals(value)) {
								EField field = parameterizedTypeClass.getAllEFields().get(l);
								Object converted = MofFactory.INSTANCE.createFromString((EDataType)field.getEType(), value);
								type.eSet(field, converted);
							}
						}
						result = type;
					}
				}
				else {
					result = eBaseType;
				}
			}
			else {
				GenericType generic;
				boolean isArrayType = false;
				if (eBaseType != null && eBaseType.getTypeSignature().equalsIgnoreCase(TypeUtils.Type_List)) {
					generic = ir.createArrayType();
					isArrayType = true;
				}
				else {
					generic = ir.createGenericType();
				}
				generic.setClassifier(eBaseType);
				if (typeArgs != null) {
					for (String arg : typeArgs) {
						if (eBaseType == null) {
							// We are dealing with a reference to a TypeParameter
							TypeParameter parm = ir.createTypeParameter();
							parm.setName(arg);
							generic.setTypeParameter(parm);
							return generic;
						}
						else
						// Tack on EGL key scheme to guide further lookup
						// of TypeArguments within the same EGL delegate
						// that started the lookup for the generic type
						// in the first place.  This is done because the
						// nested type references embedded in the key do not
						// have the scheme as part of the signature
						if (isArrayType && arg.charAt(arg.length()-1) == Type.NullableIndicator) {
							arg = arg.substring(0, arg.length()-1);
							((ArrayType)generic).setElementsNullable(true);
						}
						Type eType = (Type)env.find(Type.EGL_KeyScheme + Type.KeySchemeDelimiter + arg, useProxies);
						generic.addTypeArgument(eType);
					}
				}
				result = generic;
			}
			env.save(key, result, false);
		} catch (SerializationException e) {
			// Cannot happen as the save does not do a serialization
		} 
		return result;
	}

	public Class<? extends ProxyEObject> getProxyClass() {
		return ProxyPart.class;
	}

	@Override
	public boolean supportsScheme(String scheme) {
		return Type.EGL_KeyScheme.equals(scheme); 
	}

	@Override
	public String normalizeKey(String key) {
		// EGL is case insensitive lookup so force key to be lower case
		// TODO: May should use InternUtil here
		return key.toUpperCase().toLowerCase();
	}

}
