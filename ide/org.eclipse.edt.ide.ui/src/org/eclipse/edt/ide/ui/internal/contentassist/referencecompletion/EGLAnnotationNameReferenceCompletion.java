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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPropertyNameProposalHandler;
import org.eclipse.edt.ide.ui.internal.util.CapabilityFilterUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLAnnotationNameReferenceCompletion extends EGLAbstractPropertyReferenceCompletion {
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a {@"); //$NON-NLS-1$
		addContext("package a; program a function a(){"); //$NON-NLS-1$
	}
	
	protected List returnCompletionProposals(
			final ParseStack parseStack,
			final String prefix,
			final ITextViewer viewer,
			final int documentOffset) {
		this.viewer = viewer;
		this.documentOffset = documentOffset;
		final List proposals = new ArrayList();

		final boolean invokeAfterAnnoIcon = isState(parseStack, ((Integer) validStates.get(0)).intValue());
		
		getBoundASTNode(viewer, documentOffset, new String[] {"", "a{}", "a", "}};", "a};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public boolean nodeIsValid(Node astNode) {
				return getSettingsBlock(astNode) != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				if (isState(parseStack, ((Integer) validStates.get(0)).intValue()) ||
						isState(parseStack, ((Integer) validStates.get(1)).intValue())
						) {
					Node settingsBlock = getSettingsBlock(boundNode);
					Node container = settingsBlock.getParent();
					AnnotationGatherer aGatherer = getAnnotationGatherer(container, settingsBlock);
					if(invokeAfterAnnoIcon){
						proposals.addAll(aGatherer.getProposals(viewer, documentOffset, prefix, true));
					}else if(container instanceof Constructor || container instanceof NestedFunction){
						proposals.addAll(aGatherer.getProposals(viewer, documentOffset, prefix));
					}
				}				
			}
		});
		
		return proposals;
	}
	
	private AnnotationGatherer getAnnotationGatherer(Node container, Node settingsBlock) {
		if(container instanceof Part) {
			switch(((Part) container).getPartType()) {
			case Part.PROGRAM:
				return new ForElementKindAnnotationGatherer(ElementKind.PROGRAMPART, settingsBlock);
			case Part.RECORD:
				return new ForElementKindAnnotationGatherer(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART, ElementKind.VGUIRECORDPART}, settingsBlock);
			case Part.FUNCTION:
				return new ForElementKindAnnotationGatherer(ElementKind.FUNCTIONPART, settingsBlock);
			case Part.DATAITEM:
				return new ForElementKindAnnotationGatherer(ElementKind.DATAITEMPART, settingsBlock);
			case Part.FORM:
				return new ForElementKindAnnotationGatherer(ElementKind.FORMPART, settingsBlock);
			case Part.FORMGROUP:
				return new ForElementKindAnnotationGatherer(ElementKind.FORMGROUPPART, settingsBlock);
			case Part.DATATABLE:
				return new ForElementKindAnnotationGatherer(ElementKind.DATATABLEPART, settingsBlock);
			case Part.LIBRARY:
				return new ForElementKindAnnotationGatherer(ElementKind.LIBRARYPART, settingsBlock);
			case Part.HANDLER:
				return new ForElementKindAnnotationGatherer(ElementKind.HANDLERPART, settingsBlock);
			case Part.SERVICE:
				return new ForElementKindAnnotationGatherer(ElementKind.SERVICEPART, settingsBlock);
			case Part.INTERFACE:
				return new ForElementKindAnnotationGatherer(ElementKind.INTERFACEPART, settingsBlock);
			case Part.DELEGATE:
				return new ForElementKindAnnotationGatherer(ElementKind.DELEGATEPART, settingsBlock);
			case Part.EXTERNALTYPE:
				return new ForElementKindAnnotationGatherer(ElementKind.EXTERNALTYPEPART, settingsBlock);
			case Part.CLASS:
				return new ForElementKindAnnotationGatherer(ElementKind.CLASSPART, settingsBlock);
			case Part.ENUMERATION:
				return new ForElementKindAnnotationGatherer(ElementKind.ENUMERATIONPART, settingsBlock);
			}
		}
		else if(container instanceof UseStatement) {
			Name firstName = (Name) ((UseStatement) container).getNames().get(0);
			IBinding binding = firstName.resolveBinding();
			if(Binding.isValidBinding(binding) && binding.isTypeBinding()) {
				switch(((ITypeBinding) binding).getKind()) {
				case ITypeBinding.DATATABLE_BINDING:
					return new ForElementKindAnnotationGatherer(ElementKind.DATATABLEUSE, settingsBlock);
				case ITypeBinding.FORMGROUP_BINDING:
					return new ForElementKindAnnotationGatherer(ElementKind.FORMGROUPUSE, settingsBlock);
				}
			}
		}		
		else if(container instanceof NestedFunction) {
			return new ForElementKindAnnotationGatherer(ElementKind.FUNCTIONMBR, settingsBlock);
		}
		else {
			IDataBinding nameBinding = null;
			if(container instanceof ClassDataDeclaration) {
				nameBinding = ((Name) ((ClassDataDeclaration) container).getNames().get(0)).resolveDataBinding();
			}
			if(container instanceof FunctionDataDeclaration) {
				nameBinding = ((Name) ((FunctionDataDeclaration) container).getNames().get(0)).resolveDataBinding();
			}
			if(container instanceof VariableFormField) {
				nameBinding = ((Name) ((VariableFormField) container).getName()).resolveDataBinding();
			}
			if(container instanceof ConstantFormField) {
				nameBinding = ((ConstantFormField) container).resolveBinding();
			}
			if(container instanceof StructureItem) {
				nameBinding = (IDataBinding) ((StructureItem) container).resolveBinding();
			}
			
			if(Binding.isValidBinding(nameBinding)) {
				CompoundAnnotationGatherer result = new CompoundAnnotationGatherer(settingsBlock);
				IPartBinding declaringPart = nameBinding.getDeclaringPart();
				if(Binding.isValidBinding(declaringPart)) {
					IPartSubTypeAnnotationTypeBinding subType = declaringPart.getSubType();
					if(Binding.isValidBinding(subType)) {
						result.addGatherer(new ForMemberAnnotationsAnnotationGatherer(subType.getName(), settingsBlock));
					}
				}
				ITypeBinding nameType = nameBinding.getType();
				if(Binding.isValidBinding(nameType)) {
					switch(nameType.getKind()) {
					case ITypeBinding.ARRAY_TYPE_BINDING:
						result.addGatherer(new ForNameAnnotationGatherer(IEGLConstants.PROPERTY_MAXSIZE, settingsBlock));
						break;
					case ITypeBinding.DICTIONARY_BINDING:
						result.addGatherer(new ForNameAnnotationGatherer(IEGLConstants.PROPERTY_CASESENSITIVE, settingsBlock));
						result.addGatherer(new ForNameAnnotationGatherer(IEGLConstants.PROPERTY_ORDERING, settingsBlock));
						break;
					case ITypeBinding.INTERFACE_BINDING:
					case ITypeBinding.SERVICE_BINDING:
						result.addGatherer(new ForNameAnnotationGatherer(IEGLConstants.PROPERTY_BINDSERVICE, settingsBlock));
						break;
					case ITypeBinding.EXTERNALTYPE_BINDING:
						result.addGatherer(new ForNameAnnotationGatherer(IEGLConstants.PROPERTY_RESOURCE, settingsBlock));
						break;
					}
				}
				return result;
			}
		}
		return new AllNonSubtypeAnnotationGatherer(settingsBlock);
	}

	private Node getSettingsBlock(Node astNode) {
		while(astNode != null && !(astNode instanceof SettingsBlock)) {
			astNode = astNode.getParent();
		}
		return astNode;
	}
	
	private static abstract class AnnotationGatherer {
		private Node settingsBlock;

		protected AnnotationGatherer(Node settingsBlock) {
			this.settingsBlock = settingsBlock;
		}

		public Collection getProposals(ITextViewer viewer, int documentOffset, String prefix) {
			return new EGLPropertyNameProposalHandler(
				viewer,
				documentOffset,
				prefix,
				true, false).getProposals(CapabilityFilterUtility.filterPropertyRules(getPropertyRules()), Arrays.asList(new Node[] {settingsBlock}));
		}
		
		public Collection getProposals(ITextViewer viewer, int documentOffset, String prefix, boolean invokeFromAtIcon) {
			return new EGLPropertyNameProposalHandler(
				viewer,
				documentOffset,
				prefix,
				true, invokeFromAtIcon).getProposals(CapabilityFilterUtility.filterPropertyRules(getPropertyRules()), Arrays.asList(new Node[] {settingsBlock}));
		}

		protected abstract Collection getPropertyRules();
	}
	
	private static class AllNonSubtypeAnnotationGatherer extends AnnotationGatherer {
		public AllNonSubtypeAnnotationGatherer(Node settingsBlock) {
			super(settingsBlock);
		}
		
		protected Collection getPropertyRules() {
			return EGLNewPropertiesHandler.getPropertyRules(EGLNewPropertiesHandler.allNonSubtype);
		}
	}
	
	private static class CompoundAnnotationGatherer extends AnnotationGatherer {
		private List aGatherers = new ArrayList();
		
		public CompoundAnnotationGatherer(Node settingsBlock) {
			super(settingsBlock);
		}
		
		public void addGatherer(AnnotationGatherer gatherer) {
			aGatherers.add(gatherer);
		}

		protected Collection getPropertyRules() {
			Collection result = new ArrayList();
			for(Iterator iter = aGatherers.iterator(); iter.hasNext();) {
				result.addAll(((AnnotationGatherer) iter.next()).getPropertyRules());
			}
			return result;
		}
	}
	
	private static class ForElementKindAnnotationGatherer extends AnnotationGatherer {
		private EnumerationDataBinding[] elementKinds;

		public ForElementKindAnnotationGatherer(EnumerationDataBinding elementKind, Node settingsBlock) {
			this(new EnumerationDataBinding[] {elementKind}, settingsBlock);
		}
		
		public ForElementKindAnnotationGatherer(EnumerationDataBinding[] elementKinds, Node settingsBlock) {
			super(settingsBlock);
			this.elementKinds = elementKinds;
		}
		
		protected Collection getPropertyRules() {
			return EGLNewPropertiesHandler.createRulesForElementKinds(elementKinds);
		}
	}
	
	private static class ForNameAnnotationGatherer extends AnnotationGatherer {
		private String annotationName;

		public ForNameAnnotationGatherer(String annotationName, Node settingsBlock) {
			super(settingsBlock);
			this.annotationName = annotationName;
		}
		
		protected Collection getPropertyRules() {
			return EGLNewPropertiesHandler.createRulesFor(annotationName);
		}
	}
	
	private static class ForMemberAnnotationsAnnotationGatherer extends AnnotationGatherer {
		private String subtypeName;

		public ForMemberAnnotationsAnnotationGatherer(String subtypeName, Node settingsBlock) {
			super(settingsBlock);
			this.subtypeName = subtypeName;
		}
		
		protected Collection getPropertyRules() {
			Collection memberRulers = EGLNewPropertiesHandler.createRulesForMemberAnnotations(subtypeName);
			Collection filedmbrAnnotation = EGLNewPropertiesHandler.createRulesForElementKinds(new EnumerationDataBinding[]{ElementKind.FIELDMBR});
			memberRulers.addAll(filedmbrAnnotation);
			return memberRulers;
		}
	}
}
