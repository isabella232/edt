/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.dependency;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class DependencyInfo extends AbstractDependencyInfo implements IDependencyInfo, IDependencyRequestor {

	private Set<String> simpleNames = new HashSet();
	private Set<String> qualifiedNames = new HashSet();
	
	@Override
	public Set getQualifiedNames() {
		return Collections.unmodifiableSet(qualifiedNames);
	}
	
	@Override
	public Set getSimpleNames() {
		return Collections.unmodifiableSet(simpleNames);
	}
	
	@Override
	public void recordName(Name name){
		if(name.isSimpleName()){
			recordSimpleName(name.getIdentifier());
		}else if(name.isQualifiedName()){
			recordQualifiedName(NameUtile.getAsName(((QualifiedName)name).getNameComponents()));
		}
	}
	
	@Override
	public void recordSimpleName(String simpleName) {
		if(!containsSimpleName(simpleName)){
			addSimpleName(simpleName);
		}
	}
	
	@Override
	public void recordPackageBinding(IPackageBinding packageBinding){
		recordQualifiedName(NameUtile.getAsName(packageBinding.getPackageName()));
	}
	
	@Override
	public void recordType(Type type) {
		
		if (type == null) {
			return;
		}
		
		if(type instanceof AnnotationType){
			return;
		}
		if(type instanceof ArrayType){
			recordType(((ArrayType)type).getElementType());
			return;
		}
		if(type instanceof Function || type instanceof Constructor) {
			return;
		}
		if(type instanceof StructPart){
			List<StructPart> extendedTypes = ((StructPart)type).getSuperTypes();
			for (StructPart struct : extendedTypes) {
				doRecordTypeBinding(struct);			
			}
		}
		doRecordTypeBinding(type);
	}
	
	private void doRecordTypeBinding(Type type){
		recordQualifiedName(NameUtile.getAsName(type.getTypeSignature()));
	}
	
	@Override
	protected void recordQualifiedName(String qualifiedName){
		if (qualifiedName.length() == 0) {
			return;
		}
		
		int lastDot = qualifiedName.lastIndexOf('.');
		while (lastDot != -1) {
			if (containsQualifiedName(qualifiedName)) {
				qualifiedName = null;
				lastDot = -1;
			}
			else {
				addQualifiedName(qualifiedName);
				recordSimpleName(NameUtile.getAsName(qualifiedName.substring(lastDot + 1)));
				
				qualifiedName = qualifiedName.substring(0, lastDot);
				lastDot = qualifiedName.lastIndexOf('.');
			}
		}
		
		if (qualifiedName != null) {
			recordSimpleName(qualifiedName);
		}
	}
	
	private void addSimpleName(String simpleName){
		simpleNames.add(simpleName);
	}
	
	private void addQualifiedName(String qualifiedName){
		qualifiedNames.add(qualifiedName);
	}
	
	private boolean containsSimpleName(String simpleName){
		return simpleNames.contains(simpleName);
	}
	
	private boolean containsQualifiedName(String qualifiedName){
		return qualifiedNames.contains(qualifiedName);
	}
}
