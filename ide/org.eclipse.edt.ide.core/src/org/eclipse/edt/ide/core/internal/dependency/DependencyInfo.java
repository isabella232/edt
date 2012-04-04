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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class DependencyInfo extends AbstractDependencyInfo implements IDependencyInfo, IDependencyRequestor {

	private Set simpleNames = new HashSet();
	private Set qualifiedNames = new HashSet();
	
	public Set getQualifiedNames() {
		return Collections.unmodifiableSet(qualifiedNames);
	}
	public Set getSimpleNames() {
		return Collections.unmodifiableSet(simpleNames);
	}
	
	public void recordName(Name name){
		if(name.isSimpleName()){
			recordSimpleName(name.getIdentifier());
		}else if(name.isQualifiedName()){
			recordQualifiedName(InternUtil.intern(((QualifiedName)name).getNameComponents()));
		}
	}
	
	public void recordSimpleName(String simpleName) {
		if(!containsSimpleName(simpleName)){
			addSimpleName(simpleName);
		}
	}
	
	public void recordBinding(IBinding binding) {
		if(binding.isDataBinding()){
			// Never record this
		}else if(binding.isTypeBinding()){
			recordTypeBinding((ITypeBinding)binding);
		}else if(binding.isPackageBinding()){
			recordPackageBinding((IPackageBinding)binding);
		}
	}	
	
	public void recordPackageBinding(IPackageBinding packageBinding){
		recordQualifiedName(InternUtil.intern(packageBinding.getPackageName()));
	}
	
	public void recordTypeBinding(ITypeBinding typeBinding) {
		
		if (!Binding.isValidBinding(typeBinding)) {
			return;
		}
		
		if(typeBinding.getKind() == ITypeBinding.ANNOTATION_BINDING || typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			return;
		}
		if(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
			recordTypeBinding(typeBinding.getBaseType());
			return;
		}
		if(typeBinding.getKind() == ITypeBinding.FUNCTION_BINDING && !typeBinding.isPartBinding()) {
			return;
		}
		if(typeBinding.getKind() == ITypeBinding.EXTERNALTYPE_BINDING){
			List extendedTypes = ((ExternalTypeBinding)typeBinding).getExtendedTypes();
			for (Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
				doRecordTypeBinding((ITypeBinding) iter.next());			
			}
		}
		doRecordTypeBinding(typeBinding);
	}
	
	private void doRecordTypeBinding(ITypeBinding typeBinding){
		String[] qualifiedName = typeBinding.getPackageName();
		int length = qualifiedName.length;
		System.arraycopy(qualifiedName, 0, qualifiedName = new String[length + 1], 0, length);
		qualifiedName[length] = typeBinding.getName();
		
		recordQualifiedName(InternUtil.intern(qualifiedName));
	}
	
	protected void recordQualifiedName(String[] qualifiedName){
		int length = qualifiedName.length;
		if (length > 1) {
			while (!containsQualifiedName(qualifiedName)) {
				addQualifiedName(qualifiedName);
				if (length == 2) {
					recordSimpleName(qualifiedName[0]);
					recordSimpleName(qualifiedName[1]);
					return;
				}
				length--;
				recordSimpleName(qualifiedName[length]);
				System.arraycopy(qualifiedName, 0, qualifiedName = new String[length], 0, length);
				qualifiedName = InternUtil.intern(qualifiedName);
			}
		} else if (length == 1) {
			recordSimpleName(qualifiedName[0]);
		}
	}
	
	private void addSimpleName(String simpleName){
		simpleNames.add(simpleName);
	}
	
	private void addQualifiedName(String[] qualifiedName){
		qualifiedNames.add(qualifiedName);
	}
	
	private boolean containsSimpleName(String simpleName){
		return simpleNames.contains(simpleName);
	}
	
	private boolean containsQualifiedName(String[] qualifiedName){
		return qualifiedNames.contains(qualifiedName);
	}
}
