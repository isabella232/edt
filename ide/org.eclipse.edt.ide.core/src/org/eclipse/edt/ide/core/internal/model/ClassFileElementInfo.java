/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import java.util.HashMap;

import org.eclipse.edt.ide.core.internal.model.compiler.env.IBinaryType;
import org.eclipse.edt.ide.core.model.EGLModelException;




public class ClassFileElementInfo extends OpenableElementInfo {
//	protected EGLElement[] binaryChildren = null;
	
	private String eglFileName = null;
	private String[] caseSensitivePackageName = null;
	
	/**
	 * Returns true if the <code>readBinaryChildren</code> has already
	 * been called.
	 */
	boolean hasReadBinaryChildren() {
//		return this.binaryChildren != null;
		return false;
	}
	/**
	 * Creates the handles for <code>BinaryMember</code>s defined in this
	 * <code>ClassFile</code> and adds them to the
	 * <code>JavaModelManager</code>'s cache.
	 */
	protected void readBinaryChildren(ClassFile classFile, HashMap newElements, IBinaryType typeInfo) {
//		ArrayList<EGLElement> childrenHandles = new ArrayList<EGLElement>();
		//TODO BinaryType type = (BinaryType) classFile.getType();
//		ArrayList typeParameterHandles = new ArrayList();
//		if (typeInfo != null) { //may not be a valid class file
//			generateAnnotationsInfos(type, typeInfo.getAnnotations(), typeInfo.getTagBits(), newElements);
//			generateTypeParameterInfos(type, typeInfo.getGenericSignature(), newElements, typeParameterHandles);
//			generateFieldInfos(type, typeInfo, newElements, childrenHandles);
//			generateMethodInfos(type, typeInfo, newElements, childrenHandles, typeParameterHandles);
//			generateInnerClassHandles(type, typeInfo, childrenHandles); // Note inner class are separate openables that are not opened here: no need to pass in newElements
//		}
//
//		this.binaryChildren = new EGLElement[childrenHandles.size()];
//		childrenHandles.toArray(this.binaryChildren);
//		int typeParameterHandleSize = typeParameterHandles.size();
//		if (typeParameterHandleSize == 0) {
//			this.typeParameters = TypeParameter.NO_TYPE_PARAMETERS;
//		} else {
//			this.typeParameters = new ITypeParameter[typeParameterHandleSize];
//			typeParameterHandles.toArray(this.typeParameters);
//		}
	}
	/**
	 * Removes the binary children handles and remove their infos from
	 * the <code>JavaModelManager</code>'s cache.
	 */
	void removeBinaryChildren() throws EGLModelException {
//		if (this.binaryChildren != null) {
//			EGLModelManager manager = EGLModelManager.getEGLModelManager();
//			for (int i = 0; i <this.binaryChildren.length; i++) {
//				EGLElement child = this.binaryChildren[i];
//				if (child instanceof BinaryPart) {
//TODO					manager.removeInfoAndChildren((EGLElement)child.getParent());
//				} else {
//TODO					manager.removeInfoAndChildren(child);
//				}
//			}
//			this.binaryChildren = EGLElement.NO_ELEMENTS;
//		}
//		if (this.typeParameters != null) {
//			EGLModelManager manager = EGLModelManager.getJavaModelManager();
//			for (int i = 0; i <this.typeParameters.length; i++) {
//				TypeParameter typeParameter = (TypeParameter) this.typeParameters[i];
//				manager.removeInfoAndChildren(typeParameter);
//			}
//			this.typeParameters = TypeParameter.NO_TYPE_PARAMETERS;
//		}
	}
	
	public String getEglFileName() {
		return eglFileName;
	}
	public void setEglFileName(String eglFileName) {
		this.eglFileName = eglFileName;
	}

	public String[] getCaseSensitivePackageName() {
		return caseSensitivePackageName;
	}
	public void setCaseSensitivePackageName(String[] caseSensitivePackageName) {
		this.caseSensitivePackageName = caseSensitivePackageName;
	}

}
