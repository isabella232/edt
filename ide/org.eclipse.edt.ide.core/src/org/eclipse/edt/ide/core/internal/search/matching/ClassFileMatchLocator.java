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
package org.eclipse.edt.ide.core.internal.search.matching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;

public class ClassFileMatchLocator {
	private MatchLocator2 locator;
	private int matchContainer;
	
	private boolean locateDeclaration;
	private boolean locateReference;

	public ClassFileMatchLocator(MatchLocator2 locator2) {
		this.locator = locator2;
		this.matchContainer = locator.matchContainer();
		this.locateDeclaration = this.locator.getPattern().getPatternType() == SearchPattern.DECLARATION
			||this.locator.getPattern().getPatternType() == SearchPattern.ALLOCCUR;
		this.locateReference = this.locator.getPattern().getPatternType() == SearchPattern.REFERENCE
			|| this.locator.getPattern().getPatternType() == SearchPattern.ALLOCCUR;
	}

	public void locateMatches(ClassFile classFile) {
		// matchLocator2.
		matchBinary(classFile);
	}

	boolean matchBinary(ClassFile classFile) {
		
		IPart part = classFile.getPart();
		char[] fullyQualifiedTypeName = convertClassFileFormat(classFile.getPart().getFullyQualifiedName().toCharArray());
		try {
			IPackageDeclaration[] declarations = classFile.getPackageDeclarations();
			//TODO Rocky report package
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return matchPartDeclaration(part);
	}
	
	boolean matchFunctionDeclaration(IFunction function){
		if(locateReference){
			try {
				IProject project = function.getEGLProject().getProject();
				//look for function parameters	
				String[] parameterTypes = function.getParameterTypes();
				String[] parameterPackages = function.getParameterPackages();
				for(int i=0; i<function.getNumberOfParameters(); i++){
					//TODO if(parameter is not primitive) and (parameter is not system type)
					if(parameterPackages[i] != null){
						String type = Signature.toString(parameterTypes[i]);
						type = getElementType(type);
						PartInfo fieldPartInfo = Util.getDataDeclarationPart(EGLCore.create(project), parameterPackages[i], type);
						if(fieldPartInfo != null){
							IPart fieldPart = fieldPartInfo.resolvePart(Util.createSearchScope(project));
							matchFieldReference(fieldPart, function.getDeclaringPart());
						}
					}
				}
				
				//look for function return type
				//TODO decide if function.getReturnTypeName() is a Signature or the string value
				String returnType = function.getReturnTypeName();
				returnType = getElementType(returnType);
				String returnPackage = function.getReturnTypePackage();
				if(returnPackage != null){
					PartInfo fieldPartInfo = Util.getDataDeclarationPart(EGLCore.create(project), returnPackage, returnType);
					if(fieldPartInfo != null){
						IPart fieldPart = fieldPartInfo.resolvePart(Util.createSearchScope(project));
						matchFieldReference(fieldPart, function.getDeclaringPart());
					}
				}
			} catch (EGLModelException e) {
				e.printStackTrace();
			}
		}
		
		//for Delegate and TLF, the corresponding functions are resolved twice, therefore eliminate one.
		try {
			if(function.getParent() instanceof IPart){
				if(((SourcePartElementInfo)((EGLElement)function.getParent()).getElementInfo()).isDelegate()){
					return true;
				}
				if(((SourcePartElementInfo)((EGLElement)function.getParent()).getElementInfo()).isFunction()){
					return true;
				}
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		
		if(locateDeclaration){
			if((this.matchContainer & SearchPattern.ALL_FUNCTIONS) != 0 ||
					(this.matchContainer & SearchPattern.PART) != 0) {
				int level = getMatchingLevel(function);
				if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
					level = locator.getPattern().matchesFunctionPartType(function);				
				}
				if (level == SearchPattern.ACCURATE_MATCH ||  level == SearchPattern.INACCURATE_MATCH){
					try {
						this.locator.reportFunctionDeclaration(function, function.getDeclaringPart(), level == SearchPattern.ACCURATE_MATCH ? IEGLSearchResultCollector.EXACT_MATCH
								: IEGLSearchResultCollector.POTENTIAL_MATCH);	
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
		
	boolean matchPartDeclaration(IPart part){
		if(locateDeclaration){
			if((this.matchContainer & SearchPattern.EGL_FILE) != 0){
				int level = getMatchingLevel(part);
				if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
					level = locator.matchesPart(part);
				}
				if (level == SearchPattern.ACCURATE_MATCH || level == SearchPattern.INACCURATE_MATCH){
					try {
						this.locator.reportPartDeclaration(part, null, level == SearchPattern.ACCURATE_MATCH ? IEGLSearchResultCollector.EXACT_MATCH
								: IEGLSearchResultCollector.POTENTIAL_MATCH);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			SourcePartElementInfo partInfo = (SourcePartElementInfo)((EGLElement)part).getElementInfo();
			IProject project = part.getEGLProject().getProject();
			//use statement should also be counted as reference
			if(locateReference){
				String[] usagePartTypes = part.getUsePartTypes();
				String[] usagePartPackages = part.getUsePartPackages();
				if(usagePartTypes != null){
					for(int i=0; i<usagePartTypes.length; i++){
						//TODO if(type is not primitive) and (type is not system type)
						if(usagePartPackages[i] != null){
							String type = usagePartTypes[i];
							type = getElementType(type);
							PartInfo fieldPartInfo = Util.getUsePart(EGLCore.create(project), usagePartPackages[i], type);
							if(fieldPartInfo != null){
								IPart fieldPart = fieldPartInfo.resolvePart(Util.createSearchScope(project));
								matchFieldReference(fieldPart, part);
							}
						}
					}
				}
			}
			if(locateReference){
				//implemented interfaces should be counted as reference (for Service)
				if(partInfo.getInterfaceNames() != null){
					for(char[] interfaceName: partInfo.getInterfaceNames()){
						String fullqualifiedName = String.valueOf(interfaceName);
						String pkg = "";
						String typeName = fullqualifiedName;
						int index = fullqualifiedName.lastIndexOf(".");
						if(index != -1){
							pkg = fullqualifiedName.substring(0, index);
							typeName = fullqualifiedName.substring(index + 1);
						}
						typeName = getElementType(typeName);
						PartInfo interfacePartInfo = Util.getDataDeclarationPart(EGLCore.create(project), pkg, typeName);
						if(interfacePartInfo != null){
							IPart interfacePart = interfacePartInfo.resolvePart(Util.createSearchScope(project));
							matchInterfaceReference(interfacePart, part);
						}
					}
				}
			}
			//children
			for(IEGLElement child: partInfo.getChildren()){
				if(child instanceof IFunction){
					matchFunctionDeclaration((IFunction)child);
				}
				else if(child instanceof IField){
					if(locateReference){
						String pkg = ((IField) child).getTypeDeclaredPackage();					
						if(pkg != null){
							String type = ((IField) child).getTypeName();
							type = getElementType(type);
							PartInfo fieldPartInfo = Util.getDataDeclarationPart(EGLCore.create(project), pkg, type);
							if(fieldPartInfo != null){
								IPart fieldPart = fieldPartInfo.resolvePart(Util.createSearchScope(project));
								matchFieldReference(fieldPart, ((IField) child).getDeclaringPart());
							}		
						}
					}
				}
			}					
		} catch (EGLModelException e1) {
			e1.printStackTrace();
		}
		return true;
	}
	
	//for interface reference
	boolean matchInterfaceReference(IPart part, IPart declaredPart){
		if(this.locator.getPattern().getPatternType() == SearchPattern.REFERENCE ||
				this.locator.getPattern().getPatternType() == SearchPattern.ALLOCCUR){
			if((this.matchContainer & SearchPattern.PART) != 0){
				int level = getMatchingLevel(part);
				if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
					level = locator.matchesPart(part);
				}
				if (level == SearchPattern.ACCURATE_MATCH ||  level == SearchPattern.INACCURATE_MATCH){
					locator.reportReference(part, declaredPart, level == SearchPattern.ACCURATE_MATCH ? IEGLSearchResultCollector.EXACT_MATCH
							: IEGLSearchResultCollector.POTENTIAL_MATCH);
				}
			}
		}
		return true;
	}
	
	//for field reference
	boolean matchFieldReference(IPart part, IPart declaredPart){
		if(locateReference){
			if((this.matchContainer & SearchPattern.FIELD) != 0){
				
				int level = getMatchingLevel(part);
				if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
					level = locator.matchesPart(part);
				}
				if (level == SearchPattern.ACCURATE_MATCH ||  level == SearchPattern.INACCURATE_MATCH){
					locator.reportReference(part, declaredPart, level == SearchPattern.ACCURATE_MATCH ? IEGLSearchResultCollector.EXACT_MATCH
							: IEGLSearchResultCollector.POTENTIAL_MATCH);
				}

			}		
		}
		return true;
	}

	public static char[] convertClassFileFormat(char[] name) {
		return CharOperation.replaceOnCopy(name, '/', '.');
	}

	public int getMatchingLevel(IPart part) {
		return this.locator.matchCheck(part);
	}
	
	//if the type is array, return its element type; otherwise, return the type itself
	private String getElementType(String type){
		while(true){
			int lp = type.lastIndexOf("[");
			int rp = type.lastIndexOf("]");
			if(lp == -1 || rp == -1 || lp >= rp){
				return type;
			}
			type = type.substring(0, lp);
		}
	}
}
