/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.search.ICompiledFileUnit;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

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
		if (partBinding != null){
			Node part = (Node)fileASTs.get(partBinding);
			return part;
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
				Type binding = part.getName().resolveType();
				if (binding != null){
					if (binding instanceof org.eclipse.edt.mof.egl.Part){
						fileASTs.put(binding, part);
					}
				}
				else {
					Member m = part.getName().resolveMember();
					if (m instanceof FunctionMember) {
						fileASTs.put(m, part);
					}
				}
			}else if (node instanceof NestedFunction){
				NestedFunction function = (NestedFunction)node;
				Member binding = function.getName().resolveMember();
				if (binding instanceof FunctionMember){
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
