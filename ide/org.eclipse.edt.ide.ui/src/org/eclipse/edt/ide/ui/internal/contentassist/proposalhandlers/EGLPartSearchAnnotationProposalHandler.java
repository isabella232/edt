/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class EGLPartSearchAnnotationProposalHandler extends EGLPartSearchProposalHandler {

	private boolean addPrefix;
	private boolean addSuffix;
	private List<AnnotationType> exclude;
	private Node boundNode;
	private ProjectEnvironment env;
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 */
	public EGLPartSearchAnnotationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		boolean addPrefix,
		boolean addSuffix,
		List<AnnotationType> exclude,
		Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		this.addPrefix = addPrefix;
		this.addSuffix = addSuffix;
		this.exclude = exclude;
		this.boundNode = boundNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo part, boolean includePackageName) {
		String partName = part.getPartName();
		if (addSuffix) {
			partName = partName + "{}";
		}
		if (addPrefix) {
			return "@" + partName;
		}
		else {
			return partName;
		}
			
	}
	
	protected String getPartTypeImgKeyStr(String partType){
		if (IEGLConstants.KEYWORD_RECORD == partType) {
			return PluginImages.IMG_OBJS_ANNOTATION;
		}else  {
			return super.getPartTypeImgKeyStr(partType);
		}
	}
	
	protected boolean shouldInclude(PartDeclarationInfo partDeclarationInfo) {
		String qualifiedName;
		if (partDeclarationInfo.getPackageName().isEmpty()) {
			qualifiedName = partDeclarationInfo.getPartName();
		}
		else {
			qualifiedName = partDeclarationInfo.getPackageName() + "." + partDeclarationInfo.getPartName();
		}
		
		// exclude all the annotations that have already been specified
		for (AnnotationType annType : exclude) {
			if(qualifiedName.equalsIgnoreCase(annType.getETypeSignature())) {
				return false;
			}
		}
		
		//exclude the annotations whose targets do not include the current element
		Element elem = getElementForBoundNode(boundNode);
		if (elem != null) {
			ProjectEnvironment env = getEnv();
			if (env != null) {
				IPartBinding part = env.getPartBinding(NameUtile.getAsName(partDeclarationInfo.getPackageName()), NameUtile.getAsName(partDeclarationInfo.getPartName()));
				if (part instanceof IRPartBinding && ((IRPartBinding)part).getIrPart() instanceof AnnotationType) {
					AnnotationType annType = (AnnotationType) ((IRPartBinding)part).getIrPart();
					return BindingUtil.isApplicableFor(annType, elem);
				}
			}			
		}
		
		return true;
	}
	
	protected int getCursorPosition(String proposalString) {
		if (addPrefix) {
			return proposalString.length() - 1;
		}
		else {
			return proposalString.length();
		}
	
	}
	
	private Element getElementForBoundNode(Node node) {
		if (node == null) {
			return null;
		}
		
		if (node instanceof Part) {
			return ((Part)node).getName().resolveType();
		}
		
		if (node instanceof NestedFunction) {
			return ((NestedFunction)node).getName().resolveMember();
		}

		if (node instanceof Constructor) {
			return ((Constructor)node).getBinding();
		}

		if (node instanceof StructureItem) {
			return ((StructureItem)node).getName().resolveMember();
		}
		
		if (node instanceof ClassDataDeclaration) {
			return ((ClassDataDeclaration)node).getNames().get(0).resolveMember();
		}

		if (node instanceof FunctionDataDeclaration) {
			return ((FunctionDataDeclaration)node).getNames().get(0).resolveMember();
		}

		if (node instanceof EnumerationField) {
			return ((EnumerationField) node).getName().resolveMember();
		}
		
		return getElementForBoundNode(node.getParent());
	}

	private ProjectEnvironment getEnv() {
		if (env == null) {
			
			IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
			IProject project = editorInput.getFile().getProject();
			if (project != null) {
				env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
				Environment.pushEnv( env.getIREnvironment() );
				env.initIREnvironments();
			}			
		}
		return env;
	}
	
	protected List createProposals(List parts, boolean quoted) {
		try {
			return super.createProposals(parts, quoted);
		}
		finally{
			if (env != null) {
				Environment.popEnv();
			}
		}
	}


}
