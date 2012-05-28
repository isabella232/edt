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
package org.eclipse.edt.ide.core.internal.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormDataBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.TypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

public class BoundNodeLocationUtility {
	
	private static class BoundPartAddress implements IBoundNodeAddress{
		
		private IFile declaringFile;
		private String partName;
		
		public BoundPartAddress(IFile declaringFile, String partName) {
			this.declaringFile = declaringFile;
			this.partName = partName;
		}

		public IFile getDeclaringFile() {
			return declaringFile;
		}

		public String getPartName() {
			return partName;
		}
		
	}
	
	private static class BoundDataBindingAddress extends BoundPartAddress {
		
		private int bindingKind;
		private String address;
		
		public BoundDataBindingAddress(IFile declaringFile, String partName, int bindingKind, String address) {
			super(declaringFile, partName);

			this.bindingKind = bindingKind;
			this.address = address;
		}		
	}	
	
	private static class BoundFunctionParameterBindingAddress extends BoundPartAddress {
		
		private String address;
		private String functionSignature;
		
		public BoundFunctionParameterBindingAddress(IFile declaringFile, String partName, FunctionParameterBinding binding) {
			super(declaringFile, partName);

			this.address = binding.getName();
			this.functionSignature = getSignature(binding.getFunctionBinding());
		}
	}
	
	private class BoundStructureItemBindingAddress extends BoundPartAddress {
		
		private int bindingKind;
		private StructureItemAddressInfo[] address;
		
		public BoundStructureItemBindingAddress(IFile declaringFile, String partName, int bindingKind, StructureItemAddressInfo[] address) {
			super(declaringFile, partName);

			this.bindingKind = bindingKind;
			this.address = address;
		}		
	}

	private class BoundFormFieldBindingAddress extends BoundPartAddress {
		
		private String formName;
		private String fieldName;
		
		public BoundFormFieldBindingAddress(IFile declaringFile, String partName, String formName, String fieldName) {
			super(declaringFile, partName);

			this.formName = formName;
			this.fieldName = fieldName;
		}		
	}
	
	
	private class BoundFormBindingAddress extends BoundPartAddress {
		
		private String formName;
		
		public BoundFormBindingAddress(IFile declaringFile, String partName, String formName) {
			super(declaringFile, partName);

			this.formName = formName;
		}		
	}
	
	private class BoundDataBindingNodeLocator extends AbstractASTVisitor {

		private BoundDataBindingAddress address;
		private Node result;
		
		public BoundDataBindingNodeLocator(BoundDataBindingAddress address) {
			this.address = address;
		}

		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			if(address.bindingKind == IDataBinding.CLASS_FIELD_BINDING){
				for (Iterator iter = classDataDeclaration.getNames().iterator(); iter.hasNext();) {
					Name name = (Name) iter.next();
					
					if(name.getIdentifier() == address.address){
						result = classDataDeclaration;
						return false;
					}
				}
			}
			return true;
		}

		public boolean visit(NestedFunction nestedFunction) {
			if(address.bindingKind == IDataBinding.NESTED_FUNCTION_BINDING){
				if(address.address == nestedFunction.getName().getIdentifier()){
					result = nestedFunction;
					return false;
				}
			}
			
			return true;
		}

		public boolean visit(ProgramParameter programParameter) {
			if(address.bindingKind == IDataBinding.PROGRAM_PARAMETER_BINDING){
				if(address.address == programParameter.getName().getIdentifier()){
					result = programParameter;
					return false;
				}
			}
			return false;
		}

		public boolean visit(StructureItem structureItem){			
			IDataBinding binding = (IDataBinding)structureItem.resolveBinding();
			if(binding != null && address.bindingKind == IDataBinding.FLEXIBLE_RECORD_FIELD && binding.getKind() == IDataBinding.FLEXIBLE_RECORD_FIELD){
				if(structureItem.getName() != null && address.address == structureItem.getName().getIdentifier()){
					result = structureItem;
					return false;
				}
			}	
			return true;
		}
	}
	
	private class BoundFunctionParameterBindingNodeLocator extends AbstractASTVisitor {
		
		private BoundFunctionParameterBindingAddress address;
		private Node result;
		
		public BoundFunctionParameterBindingNodeLocator(BoundFunctionParameterBindingAddress address) {
			this.address = address;
		}
		
		public boolean visit(FunctionParameter functionParameter) {
			IDataBinding binding = (IDataBinding)functionParameter.getName().resolveDataBinding();
			if(Binding.isValidBinding(binding)) {
				if(address.address == binding.getName() && address.functionSignature.equals(getSignature(((FunctionParameterBinding) binding).getFunctionBinding()))) {
					result = functionParameter;
					return false;
				}
			}
			return true;
		}
	}

	private class BoundFormBindingNodeLocator extends AbstractASTVisitor {
		
		private BoundFormBindingAddress address;
		private Node result;
		
		public BoundFormBindingNodeLocator(BoundFormBindingAddress address) {
			this.address = address;
		}
		
		public boolean visit(NestedForm nestedForm) {
				if(address.formName == nestedForm.getName().getIdentifier()) {
					result = nestedForm;
					return false;
				}
			return false;
		}
	}

	private class BoundFormFieldBindingNodeLocator extends DefaultASTVisitor {
		
		private BoundFormFieldBindingAddress address;
		private Node result;
		
		public BoundFormFieldBindingNodeLocator(BoundFormFieldBindingAddress address) {
			this.address = address;
		}
		
		public boolean visit(FormGroup fg) {
			return true;
		}
		
		public boolean visit(NestedForm nestedForm) {
				if(address.formName == nestedForm.getName().getIdentifier()) {
					return true;  //found the right form! Now look inside for the right field
				}
			return false;
		}

		public boolean visit(TopLevelForm tlForm) {
			return true;
		}
		
		public boolean visit(VariableFormField field){
			if(result != null) {return false;};
			
			IDataBinding binding = field.getName().resolveDataBinding();
			if (Binding.isValidBinding(binding) && binding.getName() == address.fieldName) {
				result = field;
			}
			
			return false;
		}
		
		
	}

	private class BoundStructureItemBindingNodeLocator extends AbstractASTVisitor {

		protected BoundStructureItemBindingAddress address;
		protected Node result;
		
		public BoundStructureItemBindingNodeLocator(BoundStructureItemBindingAddress address) {
			this.address = address;
		}
		
		public boolean visit(StructureItem structureItem){
			if(result != null) {return false;};
			
			IDataBinding binding = (IDataBinding)structureItem.resolveBinding();
			if(address.bindingKind == IDataBinding.STRUCTURE_ITEM_BINDING && binding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
				StructureItemBinding sItemBinding = (StructureItemBinding) binding;
				StructureItemAddressInfo[] structureItemNameSegments = getStructureItemAddressInfos(sItemBinding);
				
				if(structureItemNameSegments.length == address.address.length){
					boolean equalNames = true;
					for (int i = 0; i < structureItemNameSegments.length; i++) {
						if(structureItemNameSegments[i].name != address.address[i].name ||
						   structureItemNameSegments[i].indexInContainer != address.address[i].indexInContainer){
							equalNames = false;
							break;
						}						
					}
					
					if(equalNames){
						result = structureItem; 
					}
				}
			}				
			return true;
		}
	}
	
	private static final BoundNodeLocationUtility INSTANCE = new BoundNodeLocationUtility();
	
	private BoundNodeLocationUtility(){}
	
	public static BoundNodeLocationUtility getInstance(){
		return INSTANCE;
	}
	
	private IBoundNodeAddress getAddressOfForm(FormDataBinding form) {
		if (form == null) {
			return null;
		}
		return getAddressOfForm((FormBinding) form.getType());
	}
	
	private IBoundNodeAddress getAddressOfForm(FormBinding form) {
		if (form == null) {
			return null;
		}
		
		if (form.getEnclosingFormGroup() == null) {
			return new BoundPartAddress(getFileForNode(form), form.getName());
		}
		else {
			//embedded form
			return new BoundFormBindingAddress(getFileForNode(form.getEnclosingFormGroup()), form.getEnclosingFormGroup().getName(), form.getName());		}
	}
	
	/**
	 * Create an address for a binding that can later be used to locate the AST node for the binding within a bound AST tree produced by the 
	 * Working Copy Compiler.
	 * 
	 * It is ok to cache the address.
	 * 
	 * --NOTE--
	 * If selectedNodeBinding is a data binding representing a local function variable,
	 * the result of a later call to getASTNodeForAddress() will not always be correct.
	 * Local variables are unique, in that within a given part or even function there
	 * can be more than one variable with the same name, and there is no way to
	 * differentiate between them from a binding alone. Clients who need locations for
	 * local variables are encouraged to handle local variables in a special way. For
	 * an example, see class EGLOpenOnSelectionAction in plugin com.ibm.etools.egl.ui.
	 */
	public IBoundNodeAddress createBoundNodeAddress(IBinding selectedNodeBinding){
		
		IBoundNodeAddress address = null;
		
		if(selectedNodeBinding.isTypeBinding()){
			if(((ITypeBinding)selectedNodeBinding).isPartBinding()){
				
				//TODO must add code to handle nested forms for EGLARs
				if (((ITypeBinding)selectedNodeBinding).getKind() == TypeBinding.FORM_BINDING) {
					return getAddressOfForm((FormBinding)selectedNodeBinding);
				}
				
				// This address involves only a file and a part name
				IFile file = getFileForNode((IPartBinding)selectedNodeBinding);
				address = new BoundPartAddress(file, ((IPartBinding)selectedNodeBinding).getName());
			}
		}else if(selectedNodeBinding.isDataBinding()){
			// This address is a node within a part
			IDataBinding dataBinding = (IDataBinding)selectedNodeBinding;
			
			if (dataBinding.getKind() == IDataBinding.FORM_BINDING) {
				//form bindings are tricky, because they may be top level or embedded
				address = getAddressOfForm((FormDataBinding)dataBinding);				
			}
			else {
				IPartBinding declaringPart = dataBinding.getDeclaringPart();
				
				switch(dataBinding.getKind()){
					case IDataBinding.ANNOTATION_BINDING:
						if (dataBinding.getType() instanceof AnnotationTypeBindingImpl){
							address = new BoundPartAddress(getFileForNode(((AnnotationTypeBindingImpl)dataBinding.getType()).getAnnotationRecord()), dataBinding.getName());
						}
						else {
							if (dataBinding instanceof AnnotationFieldBinding) {
								IAnnotationTypeBinding encl = ((AnnotationFieldBinding)dataBinding).getEnclosingAnnotationType();
								if (encl instanceof AnnotationTypeBindingImpl) {
									address = new BoundDataBindingAddress(getFileForNode(((AnnotationTypeBindingImpl)encl).getAnnotationRecord()), encl.getName(), IDataBinding.FLEXIBLE_RECORD_FIELD, dataBinding.getName());
								}
							}
						}
						break;
					case IDataBinding.LIBRARY_BINDING:
					case IDataBinding.EXTERNALTYPE_BINDING:
					case IDataBinding.DATATABLE_BINDING:
					case IDataBinding.PROGRAM_BINDING:
					case IDataBinding.TOP_LEVEL_FUNCTION_BINDING:
						if(Binding.isValidBinding(declaringPart)) {
							address = new BoundPartAddress(getFileForNode(declaringPart), declaringPart.getName());
						}
						break;
					case IDataBinding.STRUCTURE_ITEM_BINDING:
						if(Binding.isValidBinding(declaringPart)) {
							address = new BoundStructureItemBindingAddress(getFileForNode(declaringPart), declaringPart.getName(), dataBinding.getKind(), getStructureItemAddressInfos((StructureItemBinding)dataBinding));
						}
						break;
					case IDataBinding.FORM_FIELD:
						if(Binding.isValidBinding(declaringPart)) {
							FormBinding form = (FormBinding) declaringPart;
							if (form.getEnclosingFormGroup() == null) {
								address = new BoundFormFieldBindingAddress(getFileForNode(declaringPart), declaringPart.getName(), null, dataBinding.getName());
							}
							else {
								address = new BoundFormFieldBindingAddress(getFileForNode(form.getEnclosingFormGroup()), form.getEnclosingFormGroup().getName(), form.getName(), dataBinding.getName());
							}
						}
						break;
					case IDataBinding.FUNCTION_PARAMETER_BINDING:
						if(Binding.isValidBinding(declaringPart)) {
							address = new BoundFunctionParameterBindingAddress(getFileForNode(declaringPart), declaringPart.getName(), (FunctionParameterBinding) dataBinding);
						}
						break;
					default:
						if(Binding.isValidBinding(declaringPart)) {
							address = new BoundDataBindingAddress(getFileForNode(declaringPart), declaringPart.getName(), dataBinding.getKind(), dataBinding.getName());
						}
						break;
				}
			}
		}		
		
		return address;
	}	
	
	private IFile getFileForNode(IPartBinding partBinding){
		IFile result = null;
		String[] packageName = partBinding.getPackageName();
		String partName = partBinding.getName();
//roll back f3 for eglar
//		if(packageName != null)
//			packageName = InternUtil.intern(packageName);
//		if(partName != null)
//			partName = InternUtil.intern(partName);
	
		IEnvironment ienv = partBinding.getEnvironment();
		if (ienv instanceof WorkingCopyProjectEnvironment) {
			WorkingCopyProjectEnvironment environment = (WorkingCopyProjectEnvironment) ienv;
			IPartOrigin origin = environment.getPartOrigin(packageName, partName);
			if(origin == null){	
				return null;
			} else{
				result = origin.getEGLFile();
			}
		} 
//roll back f3 for eglar
//		else if ( ienv instanceof SystemEnvironment ) {
//			List<ISystemPackageBuildPathEntry> list = ((SystemEnvironment)ienv).getSysPackages();
//			for ( ISystemPackageBuildPathEntry entry : list ) {
//				if (!(entry instanceof SystemPackageMOFPathEntry) && entry.getPartBinding( packageName, partName ) != null ) {
//					String mofSignature = IRUtils.concatWithSeparator(packageName, ".") + "." + partName;
//					String eglSignature = org.eclipse.edt.mof.egl.Type.EGL_KeyScheme + ":" + mofSignature;
//					EObject irPart = null;
//					
//					String sourceName = null;
//					try {
//						irPart = ((SystemEnvironment)ienv).getIREnvironment().find(eglSignature);
//						sourceName = irPart.eGet("filename").toString();
//					} catch (MofObjectNotFoundException e1) {
//						e1.printStackTrace();
//					} catch (DeserializationException e1) {
//						e1.printStackTrace();
//					}
//
//					result = new BinaryReadOnlyFile( entry.getID(), sourceName);
//					break;
//				}
//			}
//		}  if ( ienv instanceof ProjectEnvironment ) {
//			IBuildPathEntry[] list = ((ProjectEnvironment)ienv).getBuildPathEntries();
//			for ( IBuildPathEntry entry : list ) {
//				if (entry instanceof EglarBuildPathEntry && entry.getPartBinding( packageName, partName ) != null ) {
//					EObject irPart = null;
//					
//					String sourceName = null;
//					try {
//						irPart = entry.findPart( packageName, partName );
//						if(irPart != null) {
//							sourceName = irPart.eGet("filename").toString();
//							result = new BinaryReadOnlyFile( entry.getID(), sourceName);
//							break;
//						}
//						   
//					} catch (PartNotFoundException e) {
//						e.printStackTrace();
//					}
//				}
//			} //for
//		}
		
		return result;
	}		
	
	/**
	 * This method should NOT be invoked from within a Working Copy Compile.
	 */
	public Node getASTNodeForAddress(final IBoundNodeAddress address, IWorkingCopy[] workingCopies){
		
		if(address.getDeclaringFile() == null){
			// TODO: When we create an "IR" Editor, we need a different type of bound node address so that we can open this editor
			return null;
		}
		
		final Node[] astNode = new Node[]{null};
		
		// Invoke the WCC on the file and part in the address
		WorkingCopyCompiler compiler = WorkingCopyCompiler.getInstance();
		compiler.compilePart(address.getDeclaringFile().getProject(), ((EGLFile)EGLCore.create(address.getDeclaringFile())).getPackageName(), address.getDeclaringFile(), workingCopies, address.getPartName(), new IWorkingCopyCompileRequestor(){

			public void acceptResult(WorkingCopyCompilationResult result) {
				
				Node boundPart = result.getBoundPart();
//roll back f3 for eglar
//				Node partAST = WorkingCopyASTManager.getInstance().getAST(address.getDeclaringFile(), address.getPartName());
//				Node boundPart = partAST;	
				if(boundPart instanceof Part){
					Part part = (Part)boundPart;
					// we found the part we are looking for
					if(address instanceof BoundDataBindingAddress){
						// We need to find the binding within the bound part
						BoundDataBindingNodeLocator locator = new BoundDataBindingNodeLocator((BoundDataBindingAddress)address);
						part.accept(locator);							
						astNode[0] = locator.result;
					}
					else if(address instanceof BoundFunctionParameterBindingAddress) {
						BoundFunctionParameterBindingNodeLocator locator = new BoundFunctionParameterBindingNodeLocator((BoundFunctionParameterBindingAddress)address);
						part.accept(locator);							
						astNode[0] = locator.result;
					}
					else if(address instanceof BoundStructureItemBindingAddress) {
						BoundStructureItemBindingNodeLocator locator = new BoundStructureItemBindingNodeLocator((BoundStructureItemBindingAddress)address);
						part.accept(locator);							
						astNode[0] = locator.result;
					}
					else if(address instanceof BoundFormBindingAddress) {
						BoundFormBindingNodeLocator locator = new BoundFormBindingNodeLocator((BoundFormBindingAddress)address);
						part.accept(locator);							
						astNode[0] = locator.result;
					}
					else if(address instanceof BoundFormFieldBindingAddress) {
						BoundFormFieldBindingNodeLocator locator = new BoundFormFieldBindingNodeLocator((BoundFormFieldBindingAddress)address);
						part.accept(locator);							
						astNode[0] = locator.result;
					}
					else{
						// return this node
						astNode[0] = part;
					}
				}else{
					// it's a file part
					if(result.getDeclaringFile().equals(address.getDeclaringFile())){
						// TODO locate a node within the file part 
					}
				}						
			}
		});
		
		return astNode[0];
	}
	
	private static class StructureItemAddressInfo {
		String name;
		int indexInContainer;
		
		public StructureItemAddressInfo(String name, int indexInContainer) {
			this.name = name;
			this.indexInContainer = indexInContainer;
		}
	}
	
	/**
	 * The name segments for a strutcure item include the name of the current item, and the name of all parent structure items
	 * that share the same declaring part.  This covers the following cases:
	 * 
	 *  RecordA
	 *  	10 item1 int;
	 *  		20 item2 RecordB;
	 *  end
	 *  
	 *  RecordB
	 *  	10 item3 int;
	 *  end
	 *  
	 *  Given the bound structure items for the following statements:
	 *  myVar RecordA;
	 *  myVar.item1 = 1;
	 *  myVar.item1.item2.item3 = 2;
	 *
	 *  The Segments for item1 are ["item1"]
	 *  The Segments for item2 are ["item1", "item2]
	 *  The Segments for item3 are ["item3"]
	 */	
	private StructureItemAddressInfo[] getStructureItemAddressInfos(StructureItemBinding binding) {
		List segments = new ArrayList();

		segments.add(new StructureItemAddressInfo(binding.getName(), getIndexInContainer(binding)));
		
		IPartBinding declaringPart = binding.getDeclaringPart();
		StructureItemBinding parentItem = binding.getParentItem();
		
		while(parentItem != null && parentItem.getDeclaringPart() == declaringPart){
			segments.add(0, new StructureItemAddressInfo(parentItem.getName(), getIndexInContainer(parentItem)));
			
			parentItem = parentItem.getParentItem();
		}
		return (StructureItemAddressInfo[])segments.toArray(new StructureItemAddressInfo[segments.size()]);
	}
	
	private int getIndexInContainer(StructureItemBinding binding) {
		List structureItems = Collections.EMPTY_LIST;
		StructureItemBinding parentItem = binding.getParentItem();
		if(parentItem == null) {
			FixedStructureBinding enclosingStructureBinding = binding.getEnclosingStructureBinding();
			structureItems = enclosingStructureBinding.getStructureItems();			
		}
		else {
			structureItems = parentItem.getChildren();
		}
		
		for(int i = 0; i < structureItems.size(); i++) {
			if(binding == structureItems.get(i)) {
				return i;
			}
		}
		return -1;
	}
	
	private static String getSignature(IFunctionBinding functionBinding) {
		StringBuffer sig = new StringBuffer();
		if(functionBinding == null) {
			sig.append("null");
		}
		else {
			sig.append(functionBinding.getName());		
			for(Iterator iter = functionBinding.getParameters().iterator(); iter.hasNext();) {
				sig.append("|");
				ITypeBinding tBinding = ((IDataBinding) iter.next()).getType();
				sig.append(tBinding == null ? "null" : tBinding.getPackageQualifiedName());
			}
			ITypeBinding tBinding = functionBinding.getReturnType();
			if(tBinding != null) {
				sig.append("|");
				sig.append(tBinding.getPackageQualifiedName());
			}
		}
		return sig.toString();
	}
}
