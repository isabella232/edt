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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.SystemPackageMOFPathEntry;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.utils.NameUtile;

public class BoundNodeLocationUtility {
	
	private static class BoundPartAddress implements IBoundNodeAddress{
		
		private IFile declaringFile;
		private String partName;
		
		public BoundPartAddress(IFile declaringFile, String partName) {
			this.declaringFile = declaringFile;
			this.partName = partName;
		}

		@Override
		public IFile getDeclaringFile() {
			return declaringFile;
		}

		@Override
		public String getPartName() {
			return partName;
		}
		
	}
	
	private static class BoundDataBindingAddress extends BoundPartAddress {
		
		private String address;
		
		public BoundDataBindingAddress(IFile declaringFile, String partName, String address) {
			super(declaringFile, partName);

			this.address = NameUtile.getAsName(address);
		}		
	}	
	
	private static class BoundFunctionParameterBindingAddress extends BoundPartAddress {
		
		private String address;
		private String functionSignature;
		
		public BoundFunctionParameterBindingAddress(IFile declaringFile, String partName, org.eclipse.edt.mof.egl.FunctionParameter binding) {
			super(declaringFile, partName);

			this.address = binding.getName();
			this.functionSignature = getSignature((FunctionMember)binding.getContainer());
		}
	}
	
	private static class BoundFunctionBindingAddress extends BoundPartAddress {
		
		private FunctionMember functionBinding;
		public BoundFunctionBindingAddress(IFile declaringFile, String partName, FunctionMember binding) {
			super(declaringFile, partName);
			this.functionBinding = binding;
		}
		
		public FunctionMember getFunctionBinding(){
			return this.functionBinding;
		}
	}
	
	private class BoundDataBindingNodeLocator extends AbstractASTVisitor {

		private BoundDataBindingAddress address;
		private Node result;
		
		public BoundDataBindingNodeLocator(BoundDataBindingAddress address) {
			this.address = address;
		}

		@Override
		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			for (Iterator iter = classDataDeclaration.getNames().iterator(); iter.hasNext();) {
				Name name = (Name) iter.next();
				
				if(NameUtile.equals(name.getIdentifier(), address.address)){
					result = classDataDeclaration;
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean visit(NestedFunction nestedFunction) {
			if(NameUtile.equals(address.address, nestedFunction.getName().getIdentifier())){
				result = nestedFunction;
				return false;
			}
			
			return true;
		}

		@Override
		public boolean visit(StructureItem structureItem){			
			if(structureItem.getName() != null && NameUtile.equals(address.address, structureItem.getName().getIdentifier())){
				result = structureItem;
				return false;
			}
			return true;
		}
		
		@Override
		public boolean visit(EnumerationField enumerationField) {
			if (enumerationField.getName() != null && NameUtile.equals(address.address, enumerationField.getName().getIdentifier())){
				result = enumerationField;
				return false;
			}
			return true;
		};
	}
	
	private class BoundFunctionParameterBindingNodeLocator extends AbstractASTVisitor {
		
		private BoundFunctionParameterBindingAddress address;
		private Node result;
		
		public BoundFunctionParameterBindingNodeLocator(BoundFunctionParameterBindingAddress address) {
			this.address = address;
		}
		
		@Override
		public boolean visit(FunctionParameter functionParameter) {
			Member binding = functionParameter.getName().resolveMember();
			if(binding instanceof org.eclipse.edt.mof.egl.FunctionParameter) {
				if(NameUtile.equals(address.address, binding.getName()) && address.functionSignature.equals(getSignature((FunctionMember)((org.eclipse.edt.mof.egl.FunctionParameter)binding).getContainer()))) {
					result = functionParameter;
					return false;
				}
			}
			return true;
		}
	}
	
	private class BoundFunctionNodeLocator extends AbstractASTVisitor {
		
		private BoundFunctionBindingAddress address;
		private Node result;
		private FunctionMember functionBinding;
		private List<org.eclipse.edt.mof.egl.FunctionParameter> fSearchFuncParaList;
		
		public BoundFunctionNodeLocator(BoundFunctionBindingAddress address) {
			this.address = address;
			this.functionBinding = address.getFunctionBinding();
			this.fSearchFuncParaList = this.functionBinding.getParameters();
		}
		
		@Override
		public boolean visit(NestedFunction nestedFunction) {
			return visitFunctionMember(nestedFunction, nestedFunction.getName().getIdentifier(), nestedFunction.getFunctionParameters());
		}
		
		@Override
		public boolean visit(Constructor constructor) {
			return visitFunctionMember(constructor, NameUtile.getAsName(IEGLConstants.KEYWORD_CONSTRUCTOR), constructor.getParameters());
		};
		
		private boolean visitFunctionMember(Node node, String funcName, List<FunctionParameter> parms) {
			if(NameUtile.equals(address.getFunctionBinding().getName(), funcName)){
				if(parms.size() == fSearchFuncParaList.size()){
					Iterator<FunctionParameter> aParaItr = parms.iterator();
					Iterator<org.eclipse.edt.mof.egl.FunctionParameter> fSearchParaItr = fSearchFuncParaList.iterator();
					FunctionParameter aParameter;
					org.eclipse.edt.mof.egl.FunctionParameter aSearchParameter;
					
					while(aParaItr.hasNext()){
						aParameter = aParaItr.next();
						aSearchParameter = fSearchParaItr.next();
						
						if(!aSearchParameter.getType().equals(aParameter.getType().resolveType())){
							return(true);
						}
					}
					
					result = node;
					return false;
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
	public IBoundNodeAddress createBoundNodeAddress(Element selectedNodeBinding, Expression expr, IProject  project){
		
		IBoundNodeAddress address = null;
		
		if(selectedNodeBinding instanceof Type){
			// Annotation fields will come in as an AnnotationType. Check if the parent is an Assignment.
			if (selectedNodeBinding instanceof AnnotationType) {
				Node parent = expr.getParent();
				if (parent instanceof Assignment) {
					Assignment assignment = (Assignment)parent;
					String name = assignment.getLeftHandSide().getCanonicalString();
					if (((AnnotationType)selectedNodeBinding).getEField(NameUtile.getAsName(name)) != null) {
						IFile file = getFileForNode((org.eclipse.edt.mof.egl.Part)selectedNodeBinding, project);
						address = new BoundDataBindingAddress(file, ((org.eclipse.edt.mof.egl.Part)selectedNodeBinding).getName(), name);
					}
				}
			}
			
			if(address == null && selectedNodeBinding instanceof org.eclipse.edt.mof.egl.Part){
				
				// This address involves only a file and a part name
				IFile file = getFileForNode((org.eclipse.edt.mof.egl.Part)selectedNodeBinding, project);
				address = new BoundPartAddress(file, ((org.eclipse.edt.mof.egl.Part)selectedNodeBinding).getName());
			}
		}else if(selectedNodeBinding instanceof Member){
			// This address is a node within a part
			Member dataBinding = (Member)selectedNodeBinding;
			
			org.eclipse.edt.mof.egl.Part declaringPart = getContainingBinding(dataBinding, expr);
			if (dataBinding instanceof org.eclipse.edt.mof.egl.FunctionParameter) {
				if (declaringPart != null) {
					address = new BoundFunctionParameterBindingAddress(getFileForNode(declaringPart, project), declaringPart.getName(), (org.eclipse.edt.mof.egl.FunctionParameter) dataBinding);
				}
			}
			else if (dataBinding instanceof FunctionMember) {
				if (declaringPart != null) {
					address = new BoundFunctionBindingAddress(getFileForNode(declaringPart, project), declaringPart.getName(), (FunctionMember)dataBinding);
				}
			}
			else {
				if (declaringPart != null) {
					address = new BoundDataBindingAddress(getFileForNode(declaringPart, project), declaringPart.getName(), /*dataBinding.getKind(),*/ dataBinding.getName());
				}
			}
		}		
		
		return address;
	}	
	
	private org.eclipse.edt.mof.egl.Part getContainingBinding(Member binding, Expression expr) {
		org.eclipse.edt.mof.egl.Part decl = BindingUtil.getDeclaringPart(binding);
		if (decl != null) {
			return decl;
		}
		
		final Type[] result = new Type[1];
		expr.accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName) {
				result[0] = qualifiedName.getQualifier().resolveType();
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.FieldAccess fieldAccess) {
				result[0] = fieldAccess.getPrimary().resolveType();
				return false;
			}
		});
		return result[0] instanceof org.eclipse.edt.mof.egl.Part ? (org.eclipse.edt.mof.egl.Part)result[0] : null;
	}

	
	private IFile getFileForNode(org.eclipse.edt.mof.egl.Part partBinding, IProject project){
		IFile result = null;
		String packageName = partBinding.getPackageName();
		String partName = partBinding.getName();
	
		IEnvironment ienv = BindingUtil.getEnvironment(partBinding);
		
		if (ienv instanceof WorkingCopyProjectEnvironment) {
			WorkingCopyProjectEnvironment environment = (WorkingCopyProjectEnvironment) ienv;
			IPartOrigin origin = environment.getPartOrigin(packageName, partName);
			if(origin == null){	
				return null;
			} else{
				result = origin.getEGLFile();
			}
		} else if(ienv instanceof SystemEnvironment){
			List<ISystemPackageBuildPathEntry> list = ((SystemEnvironment)ienv).getSysPackages();
			for ( ISystemPackageBuildPathEntry entry : list ) {
				if (!(entry instanceof SystemPackageMOFPathEntry) && entry.getPartBinding( packageName, partName ) != null ) {
					String mofSignature = packageName + "." + partName;
					String eglSignature = org.eclipse.edt.mof.egl.Type.EGL_KeyScheme + ":" + mofSignature;
					EObject irPart = null;
					
					String sourceName = null;
					String irName = null;
					try {
						irPart = ((SystemEnvironment)ienv).getIREnvironment().find(eglSignature);
						sourceName = irPart.eGet("filename").toString();
						irName = IRFileNameUtility.toIRFileName(irPart.eGet("name").toString());
					} catch (MofObjectNotFoundException e1) {
						e1.printStackTrace();
					} catch (DeserializationException e1) {
						e1.printStackTrace();
					}

					result = new BinaryReadOnlyFile( entry.getID(), sourceName, irName);
					break;
				}
			}//for loop
		}
		
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
		String[] packageName;
		if(address.getDeclaringFile().isReadOnly()) {
			packageName = ((BinaryReadOnlyFile)address.getDeclaringFile()).getPackageSegments();
		} else {
			packageName = ((EGLFile)EGLCore.create(address.getDeclaringFile())).getPackageName();
		}
		if(packageName == null) 
			return null;
		
		compiler.compilePart(address.getDeclaringFile().getProject(), Util.stringArrayToQualifiedName(packageName), address.getDeclaringFile(), workingCopies, address.getPartName(), new IWorkingCopyCompileRequestor(){

			public void acceptResult(WorkingCopyCompilationResult result) {
				
				Node boundPart = result.getBoundPart();
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
					else if(address instanceof BoundFunctionBindingAddress) {
						BoundFunctionNodeLocator locator = new BoundFunctionNodeLocator((BoundFunctionBindingAddress)address);
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
	
	private static String getSignature(FunctionMember functionBinding) {
		StringBuffer sig = new StringBuffer();
		if(functionBinding == null) {
			sig.append("null");
		}
		else {
			sig.append(functionBinding.getName());		
			for(Iterator<org.eclipse.edt.mof.egl.FunctionParameter> iter = functionBinding.getParameters().iterator(); iter.hasNext();) {
				sig.append("|");
				Type tBinding = iter.next().getType();
				sig.append(tBinding == null ? "null" : tBinding.getTypeSignature());
			}
			Type tBinding = functionBinding.getReturnType();
			if(tBinding != null) {
				sig.append("|");
				sig.append(tBinding.getTypeSignature());
			}
		}
		return sig.toString();
	}
}
