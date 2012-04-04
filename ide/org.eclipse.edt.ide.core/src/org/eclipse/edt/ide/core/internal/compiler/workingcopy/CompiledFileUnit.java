/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfoManager;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.search.ICompiledFileUnit;

public class CompiledFileUnit implements ICompiledFileUnit,IPartASTRequestor {
	File fileAST = null;
	FileBinding fileBinding = null;
	ArrayList boundPartList = new ArrayList();
	HashMap fileASTs = new HashMap();
	ArrayList asts = new ArrayList();
	HashMap referenceFiles = new HashMap();
	HashMap referencedParts = new HashMap();
	private List<IBinding> bindings = new ArrayList<IBinding>();
	
	public CompiledFileUnit(){
		
	}
	
	public File getFile() {
		return fileAST;

	}

	public Node[] getAllParts(){
		return (Node[])referencedParts.keySet().toArray(new Node[referencedParts.size()]);
	}
	
	public IFile getContainingFile(Node part){
		IFile retVal = null;
		if (referencedParts.containsKey(part)){
			retVal = (IFile)referencedParts.get(part);
		}
		
		if (retVal == null) {
			Part myPart = getPart(part);
			if (myPart != null) {
				IPartBinding binding = (IPartBinding)myPart.getName().resolveBinding();
				if (binding != null && binding.getEnvironment() instanceof WorkingCopyProjectEnvironment) {
					WorkingCopyProjectEnvironment env = (WorkingCopyProjectEnvironment) binding.getEnvironment();
					IProject project = env.getProject();
					IPartOrigin origin = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project).getPartOrigin(binding.getPackageName(), binding.getName());
					if (origin != null ) {
						retVal = origin.getEGLFile();
					}				
				}
			}
		}
		return retVal;
	}
	
	private Part getPart(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof Part) {
			return (Part) node;
		}
		
		return getPart(node.getParent());
	}
	
	public HashMap getReferencedFiles(){
		return referenceFiles;
	}
	
	public FileBinding getFileBinding() {
		return fileBinding;
	}

	public List getFileParts() {
		return boundPartList;

	}

	public void setFileAST(File fileAST) {
		this.fileAST = fileAST;
	}

	public void setFileBinding(FileBinding fileBinding) {
		this.fileBinding = fileBinding;
	}

	public void addBoundPart(IFile file,Part part){
		boundPartList.add(part);
	}
	
	public Node getPartAST(IBinding partBinding){
		if (partBinding != null && partBinding != IBinding.NOT_FOUND_BINDING){
			Node part = (Node)fileASTs.get(partBinding);
			return part;
		}
		return null;
	}
	
	public Node getPartInContextAST(FunctionContainerBinding functionContainerBinding, TopLevelFunctionBinding functionBinding){
		for(Iterator iter = fileASTs.keySet().iterator(); iter.hasNext();) {
			Object nextKey = iter.next();
			if(nextKey instanceof TopLevelFunctionBinding) {
				String nextKeyName = ((IBinding) nextKey).getName().toLowerCase();
				if(nextKeyName.startsWith(functionContainerBinding.getName().toLowerCase() + "$") &&
				   nextKeyName.endsWith(functionBinding.getPackageQualifiedName().toLowerCase())) {
					return (Node) fileASTs.get(nextKey);
				}
			}
		}		
		return null;
	}
	
	public void addPartAST(IFile file,Node part){
		asts.add(part);
		referenceFiles.put(file, file);
		referencedParts.put(part, file);
	}
	
	public void indexASTs(){
		for(int i = 0; i < asts.size(); i++){
			Node node = (Node)asts.get(i);
			if (node instanceof Part){
				Part part = (Part)node;
				IBinding binding = part.getName().resolveBinding();
				if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
					if (binding.isTypeBinding() && ((ITypeBinding)binding).isPartBinding()){
						fileASTs.put(binding, part);
					}else if (binding.isFunctionBinding()){
						fileASTs.put(binding, part);
					}
				}
			}else if (node instanceof NestedFunction){
				NestedFunction function = (NestedFunction)node;
				IBinding binding = function.getName().resolveBinding();
				if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
					fileASTs.put(binding,function);
				}
			}
						
		}
	}
	
	public void addIBinding(IBinding binding) {
		if(!bindings.contains(binding))
			bindings.add(binding);
	}
	
	public List<IBinding> getIBindingsFromIR() {
		List<IBinding> temp = new ArrayList<IBinding>(this.bindings);
		return temp;
	}
}
