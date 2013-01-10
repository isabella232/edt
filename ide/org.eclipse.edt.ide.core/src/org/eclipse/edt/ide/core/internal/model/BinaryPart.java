/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IUseDeclaration;
import org.eclipse.edt.mof.utils.NameUtile;


public class BinaryPart extends BinaryMember implements IPart {
	private static final IField[] NO_FIELDS = new IField[0];
	private static final IFunction[] NO_FUNCTIONS = new IFunction[0];
	private static final IPart[] NO_PARTS = new IPart[0];
	/**
	 * An empty list of Strings
	 */
	protected static final String[] fgEmptyList= new String[] {};
	protected static final IProperty[] fgEmptyProperties = new IProperty[0];
	
//	private static final IInitializer[] NO_INITIALIZERS = new IInitializer[0];
	
	protected BinaryPart(int type, IEGLElement parent, String name) {
		super(type, parent, name);
		
	}
	
	protected BinaryPart(IEGLElement parent, String name) {
		super(PART, parent, name);
		Assert.isTrue(name.indexOf('.') == -1);
	}

	public IFunction[] findFunctions(IFunction function) {
		try {
			return this.findFunctions(function, this.getFunctions());
		} catch (EGLModelException e) {
			// if type doesn't exist, no matching method can exist
			return null;
		}
	}

	public IField getField(String name) {
		return new BinaryField(this, name);
	}

	public IField[] getFields() throws EGLModelException {
		ArrayList<IField> list = getChildrenOfType(FIELD);
		if ((list.size()) == 0) {
			return NO_FIELDS;
		} 
		IField[] array= new IField[list.size()];
		list.toArray(array);
		return array;
	}

	public String getFullyQualifiedName() {
		return this.getFullyQualifiedName(':');
	}
	/**
	 * @see IPart#getFullyQualifiedName(char)
	 */
	public String getTypeQualifiedName(char partSeparator) {
		if (fParent.getElementType() == IEGLElement.CLASS_FILE) {
			return fName;
//			String name = fParent.getElementName();
//			int index = name.lastIndexOf('.');
//			name = name.substring(0, index);
//			return name + partSeparator + fName;
		} else {
			return ((IPart) fParent).getFullyQualifiedName(partSeparator) + partSeparator + fName;
		}
	}
	public String getFullyQualifiedName(char enclosingTypeSeparator) {
		String packageName = getPackageFragment().getElementName();
		if (packageName.equals(IPackageFragment.DEFAULT_PACKAGE_NAME)) {
			return getTypeQualifiedName(enclosingTypeSeparator);
		}
		//for typeQualifiedName with package name, do not append package name again
		String typeQualifiedName = getTypeQualifiedName(enclosingTypeSeparator);
		if(typeQualifiedName.startsWith(packageName)){
			return typeQualifiedName;
		} else{
			return packageName + '.' + getTypeQualifiedName(enclosingTypeSeparator);
		}
	}

	public IFunction getFunction(String name, String[] parameterTypeSignatures) {
		return new BinaryFunction(this, name, parameterTypeSignatures);
	}

	public IFunction[] getFunctions() throws EGLModelException {
		ArrayList<IFunction> list = getChildrenOfType(IEGLElement.FUNCTION);
		if ((list.size()) == 0) {
			return NO_FUNCTIONS;
		} 
		IFunction[] array= new IFunction[list.size()];
		list.toArray(array);
		return array;
	}

	public String[] getImplementedInterfaceNames() throws EGLModelException {
		SourcePartElementInfo info = (SourcePartElementInfo) getElementInfo();
    	char[][] names= info.getInterfaceNames();
    	if (names == null) {
    		return fgEmptyList;
    	}
    	String[] strings= new String[names.length];
    	for (int i= 0; i < names.length; i++) {
    		strings[i]= new String(names[i]);
    	}
    	return strings;
	}

	public IPackageFragment getPackageFragment() {
		IEGLElement parent = fParent;
		while (parent != null) {
			if (parent.getElementType() == IEGLElement.PACKAGE_FRAGMENT) {
				return (IPackageFragment) parent;
			}
			else {
				parent = parent.getParent();
			}
		}
		Assert.isTrue(false);  // should not happen
		return null;
	}

	public IPart getPart(String name) {
		return new BinaryPart(this, name);
	}
	/**
	 * @see IPart
	 */
	public IPart[] getParts() throws EGLModelException {
		ArrayList<IPart> list= getChildrenOfType(PART);
		if ((list.size()) == 0) {
			return NO_PARTS;
		} 
		IPart[] array= new IPart[list.size()];
		list.toArray(array);
		return array;
	}
	public String getSubTypeSignature() {
		try {
			SourcePartElementInfo info = (SourcePartElementInfo) getElementInfo();
			return info.getSubTypeSignature();
		}
		catch (EGLModelException e) {
			return null;
		}
	}

	public IUseDeclaration getUseDeclaration(String name) {
		return null;
	}

	public IUseDeclaration[] getUseDeclarations() throws EGLModelException {
		return null;
	}

	public boolean isPublic() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isPublic();
		}
		catch (EGLModelException e) {
			return false;
		}
	}

	public boolean hasChildren() throws EGLModelException {
		return getChildren().length > 0;
	}
	/**
	 * @see IPart#isAnonymous()
	 */
	public boolean isAnonymous() throws EGLModelException {
		return false; // cannot create source handle onto anonymous types
	}
	/**
	 * @see IPart#isLocal()
	 */
	public boolean isLocal() throws EGLModelException {
		return false; // cannot create source handle onto local types
	}
	/**
	 * @see IPart#isMember()
	 */
	public boolean isMember() throws EGLModelException {
		return getDeclaringPart() != null;
	}
	protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
		buffer.append(this.tabString(tab));
		if (info == null) {
			buffer.append(this.getElementName());
			buffer.append(" (not open)"); //$NON-NLS-1$
		} else if (info == NO_INFO) {
			buffer.append(getElementName());
		} else {
			buffer.append("part "); //$NON-NLS-1$
			buffer.append(this.getElementName());
		}
	}

	public IProperty[] getProperties(String key) throws EGLModelException {
		IEGLElement[] children = getChildren();
		for(IEGLElement child: children){
			if(child instanceof BinaryPropertyBlock){
				if(((BinaryPropertyBlock)child).getElementName().equals(key))
					return ((BinaryPropertyBlock)child).getProperties();
			}
		}
		return null;
	}
	
	public boolean isProgram() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isProgram();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isSQLRecord() {
		if (!isRecord()) return false;
		try {
			SourcePartElementInfo part = (SourcePartElementInfo) getElementInfo();
			if (part.getSubTypeName() == null) {return false;}
			return NameUtile.equals(NameUtile.getAsName(new String(part.getSubTypeName())), NameUtile.getAsName("SQLRecord"));
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isRecord() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isRecord();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isHandler(){
		try {
			return ((SourcePartElementInfo)getElementInfo()).isHandler();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isLibrary() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isLibrary();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isFunction() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isFunction();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isService() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isService();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isInterface() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isInterface();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isDelegate() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isDelegate();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isExternalType() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isExternalType();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isEnumeration() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isEnumeration();
		}
		catch (EGLModelException e) {
			return false;
		}
	}
	
	public boolean isClass() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isClass();
		}
		catch (EGLModelException e) {
			return false;
		}
	}

	public String[] getUsePartPackages() throws EGLModelException {
		SourcePartElementInfo info = (SourcePartElementInfo) getElementInfo();
		char[][] usePartPkgs = info.getUsagePartPackages();
		if(usePartPkgs == null){
			return null;
		}
		if(usePartPkgs.length == 0){
			return fgEmptyList;
		}
		String[] strings = new String[usePartPkgs.length];
		for (int i= 0; i < usePartPkgs.length; i++) {
			if(usePartPkgs[i] != null)
				strings[i]= new String(usePartPkgs[i]);
		}
		return strings;
	}

	public String[] getUsePartTypes() throws EGLModelException {
		SourcePartElementInfo info = (SourcePartElementInfo) getElementInfo();
		char[][] usePartTypes = info.getUsagePartTypes();
		if(usePartTypes == null){
			return null;
		}
		if(usePartTypes.length == 0){
			return fgEmptyList;
		}
		String[] strings = new String[usePartTypes.length];
		for (int i= 0; i < usePartTypes.length; i++) {
			if(usePartTypes[i] != null)
				strings[i]= new String(usePartTypes[i]);
		}
		return strings;
	}
}
