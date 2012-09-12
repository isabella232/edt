/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;
import org.eclipse.edt.ide.core.model.IUseDeclaration;



/**
 * Handle for a source type. Info object is a SourcePartElementInfo.
 *
 * Note: Parent is either an IClassFile, an IEGLFile or an IPart.
 *
 * @see IPart
 */

public class SourcePart extends Member implements IPart {
	/**
	 * An empty list of Strings
	 */
	protected static final String[] fgEmptyList= new String[] {};
	protected static final IProperty[] fgEmptyProperties = new IProperty[0];
	
protected SourcePart(int type, IEGLElement parent, String name) {
	super(type, parent, name);
}
protected SourcePart(IEGLElement parent, String name) {
	super(PART, parent, name);
	Assert.isTrue(name.indexOf('.') == -1);
}
/**
 * @see IPart
 */
/* TODO codeassist
public void codeComplete(char[] snippet,int insertion,int position,char[][] localVariableTypeNames,char[][] localVariableNames,int[] localVariableModifiers,boolean isStatic,ICompletionRequestor requestor) throws EGLModelException {
	if (requestor == null) {
		throw new IllegalArgumentException(Util.bind("codeAssist.nullRequestor")); //$NON-NLS-1$
	}
	
	EGLProject project = (EGLProject) getEGLProject();
	SearchableEnvironment environment = (SearchableEnvironment) project.getSearchableNameEnvironment();
	NameLookup nameLookup = project.getNameLookup();
	CompletionEngine engine = new CompletionEngine(environment, new CompletionRequestorWrapper(requestor,nameLookup), project.getOptions(true), project);
	
	String source = getEGLFile().getSource();
	if (source != null && insertion > -1 && insertion < source.length()) {
		String encoding = project.getOption(EGLCore.CORE_ENCODING, true);
		
		char[] prefix = CharOperation.concat(source.substring(0, insertion).toCharArray(), new char[]{'{'});
		char[] suffix = CharOperation.concat(new char[]{'}'}, source.substring(insertion).toCharArray());
		char[] fakeSource = CharOperation.concat(prefix, snippet, suffix);
		
		BasicEGLFile cu = 
			new BasicEGLFile(
				fakeSource, 
				null,
				getElementName(),
				encoding); 

		engine.complete(cu, prefix.length + position, prefix.length);
	} else {
		engine.complete(this, snippet, position, localVariableTypeNames, localVariableNames, localVariableModifiers, isStatic);
	}
}
*/
/*
 * @see IPart
 */
public IFunction[] findFunctions(IFunction method) {
	try {
		return this.findFunctions(method, this.getFunctions());
	} catch (EGLModelException e) {
		// if type doesn't exist, no matching method can exist
		return null;
	}
}
/**
 * @see IMember
 */
public IPart getDeclaringPart() {
	IEGLElement parent = getParent();
	while (parent != null) {
		if (parent.getElementType() == IEGLElement.PART) {
			return (IPart) parent;
		} else
			if (parent instanceof IMember) {
				parent = parent.getParent();
			} else {
				return null;
			}
	}
	return null;
}
/**
 * @see IPart#getField
 */
public IField getField(String name) {
	return new SourceField(this, name);
}
/**
 * @see IPart
 */
public IField[] getFields() throws EGLModelException {
	ArrayList list = getChildrenOfType(FIELD);
	IField[] array= new IField[list.size()];
	list.toArray(array);
	return array;
}

public IUseDeclaration getUseDeclaration(String name) {
	return new SourceUseDeclaration(this, name);
}

public IUseDeclaration[] getUseDeclarations() throws EGLModelException {
	ArrayList list = getChildrenOfType(USE_DECLARATION);
	IUseDeclaration[] array= new IUseDeclaration[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see IPart#getFullyQualifiedName(char)
 */
public String getFullyQualifiedName(char enclosingTypeSeparator) {
	String packageName = getPackageFragment().getElementName();
	if (packageName.equals(IPackageFragment.DEFAULT_PACKAGE_NAME)) {
		return getTypeQualifiedName(enclosingTypeSeparator);
	}
	return packageName + '.' + getTypeQualifiedName(enclosingTypeSeparator);
}

/**
 * @see IPart#getFunction
 */
public IFunction getFunction(String name, String[] parameterTypeSignatures) {
	return new SourceFunction(this, name, parameterTypeSignatures);
}
/**
 * @see IPart
 */
public IFunction[] getFunctions() throws EGLModelException {
	ArrayList list = getChildrenOfType(IEGLElement.FUNCTION);
	IFunction[] array= new IFunction[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see IPart
 */
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
/**
 * @see IPart
 */
public IPart getPart(String name) {
	return new SourcePart(this, name);
}

/* (non-Javadoc)
 * @see com.ibm.etools.egl.internal.model.core.IMember#getProperties(java.lang.String)
 */
public IProperty[] getProperties(String key) throws EGLModelException {
	ArrayList list = getChildrenOfType(PROPERTY_BLOCK);
	if (list.isEmpty())
		return fgEmptyProperties;
	else
		return ((IPropertyContainer)list.get(0)).getProperties();
}

/**
 * @see IPart#getTypeQualifiedName
 */
public String getFullyQualifiedName() {
	return this.getFullyQualifiedName(':');
}
/**
 * @see IPart#getFullyQualifiedName(char)
 */
public String getTypeQualifiedName(char partSeparator) {
	if (fParent.getElementType() == IEGLElement.EGL_FILE) {
		return fName;
//		String name = fParent.getElementName();
//		int index = name.lastIndexOf('.');
//		name = name.substring(0, index);
//		return name + partSeparator + fName;
	} else {
		return ((IPart) fParent).getFullyQualifiedName(partSeparator) + partSeparator + fName;
	}
}

/**
 * @see IPart
 */
public IPart[] getParts() throws EGLModelException {
	ArrayList list= getChildrenOfType(PART);
	IPart[] array= new IPart[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see IParent 
 */
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
/*
 public String[][] resolveType(String typeName) throws EGLModelException {
	IEGLPart info = (IEGLPart) this.getElementInfo();
	ISearchableNameEnvironment environment = ((EGLProject)getEGLProject()).getSearchableNameEnvironment();

	class TypeResolveRequestor implements ISelectionRequestor {
		String[][] answers = null;
		void acceptType(String[] answer){
			if (answers == null) {
				answers = new String[][]{ answer };
			} else {
				// grow
				int length = answers.length;
				System.arraycopy(answers, 0, answers = new String[length+1][], 0, length);
				answers[length] = answer;
			}
		}
		public void acceptClass(char[] packageName, char[] className, boolean needQualification) {
			acceptType(new String[]  { new String(packageName), new String(className) });
		}
		
		public void acceptInterface(char[] packageName, char[] interfaceName, boolean needQualification) {
			acceptType(new String[]  { new String(packageName), new String(interfaceName) });
		}

		public void acceptError(IProblem error) {}
		public void acceptField(char[] declaringTypePackageName, char[] declaringTypeName, char[] name) {}
		public void acceptFunction(char[] declaringTypePackageName, char[] declaringTypeName, char[] selector, char[][] parameterPackageNames, char[][] parameterTypeNames, boolean isConstructor) {}
		public void acceptPackage(char[] packageName){}

	}
	TypeResolveRequestor requestor = new TypeResolveRequestor();
	SelectionEngine engine = 
		new SelectionEngine(environment, requestor, this.getEGLProject().getOptions(true));
		
 	IPart[] topLevelTypes = this.getEGLFile().getParts();
 	int length = topLevelTypes.length;
 	IEGLPart[] topLevelInfos = new IEGLPart[length];
 	for (int i = 0; i < length; i++) {
		topLevelInfos[i] = (IEGLPart)((SourcePart)topLevelTypes[i]).getElementInfo();
	}
		
	engine.selectType(info, typeName.toCharArray(), topLevelInfos, false);
	return requestor.answers;
}
*/
/**
 * @private Debugging purposes
 */
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
	
	public boolean isFunction() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isFunction();
		}
		catch (EGLModelException e) {
			return false;
		}
	}

	public boolean isGeneratable() {
		return isProgram() || isLibrary() || isHandler() || isService();
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
	
	public boolean isEnumeration(){
		try {
			return ((SourcePartElementInfo)getElementInfo()).isEnumeration();
		}
		catch (EGLModelException e) {
			return false;
		}
	}

	public boolean isHandler() {
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

	public boolean isProgram() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isProgram();
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

	public boolean isPublic() {
		try {
			return ((SourcePartElementInfo)getElementInfo()).isPublic();
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

	public String getSubTypeSignature() {
		try {
			SourcePartElementInfo info = (SourcePartElementInfo) getElementInfo();
			return info.getSubTypeSignature();
		}
		catch (EGLModelException e) {
			return null;
		}
	}
    
	/* (non-Javadoc)
     * @see com.ibm.etools.egl.model.core.IPart#getImplementedInterfaceNames()
     */
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
