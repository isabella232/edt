/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;


/**
 * @author Dave Murray
 */
public abstract class FixedStructureValidator extends AbstractASTVisitor {
	
	protected static final class StructureItemInfo {
		Node nodeForErrors;
		String canonicalName;
		StructureItem structureItemNode;
		StructureItemBinding binding;
	}
	
	protected abstract class StructureItemValidator {
		public StructureItemValidator() {
		}
		
		public abstract void validate(StructureItemInfo sItemInfo);
		public abstract void validateEmbeddedItem(StructureItem embeddedItem);
		public abstract void validatePart(FixedStructureBinding fixedStructureBinding);
	}
	
	protected class DefaultStructureItemValidator extends StructureItemValidator {		
		public DefaultStructureItemValidator() {
		}
		
		public void validate(StructureItemInfo sItemInfo) {
			
			if(!sItemInfo.structureItemNode.isEmbedded() && !sItemInfo.structureItemNode.isFiller()) {
				EGLNameValidator.validate(sItemInfo.structureItemNode.getName(), EGLNameValidator.INTERNALFIELD, problemRequestor, compilerOptions);
			}
			
			if(sItemInfo.binding.numOccursDimensions() > 7) {
				problemRequestor.acceptProblem(
					sItemInfo.nodeForErrors,
					IProblemRequestor.TOO_MANY_DIMENSIONS_FOR_ARRAY,
					new String[] {sItemInfo.canonicalName});
			}
			
			boolean itemIsLeaf = sItemInfo.binding.getChildren().isEmpty();
			
			if(itemIsLeaf) {
				if(!sItemInfo.structureItemNode.hasType()) {
					problemRequestor.acceptProblem(
						sItemInfo.nodeForErrors,
						IProblemRequestor.STRUCTURE_LEAF_ITEM_WITH_NO_PRIM_CHAR,
						new String[] {sItemInfo.canonicalName, canonicalStructureName});
				}
			}
			else {
				boolean checkLength = true;
				Type type = sItemInfo.structureItemNode.getType();
				if (type != null){
					if (type.isArrayType()){
						type = ((ArrayType)type).getElementType();
					}
					
					if (type.isPrimitiveType() && !((PrimitiveType) type).getPrimitive().hasDefaultLength()){						
						checkLength = ((PrimitiveType)type).hasPrimLength();
					}
				}
				
				if (checkLength){
					int itemLengthWithoutOccurs = getLengthWithoutOccurs(sItemInfo.binding);
					int totalChildLength = getTotalLengthOfChildren(sItemInfo.binding);
					if(itemLengthWithoutOccurs != totalChildLength) {
						problemRequestor.acceptProblem(
							sItemInfo.nodeForErrors,
							IProblemRequestor.INVALID_SUMMED_RECORD_LENGTH,
							new String[] {
								sItemInfo.canonicalName,
								Integer.toString(itemLengthWithoutOccurs),
								Integer.toString(totalChildLength)
							});
					}	
				}
			}
			
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sItemInfo.binding.getType().getKind()) {
				PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) sItemInfo.binding.getType();
				Primitive prim = primTypeBinding.getPrimitive();
				
				switch(prim.getType()) {
					case Primitive.BOOLEAN_PRIMITIVE:
						break;
					case Primitive.STRING_PRIMITIVE:
					case Primitive.BLOB_PRIMITIVE:
					case Primitive.CLOB_PRIMITIVE:
					case Primitive.ANY_PRIMITIVE:
						problemRequestor.acceptProblem(
							sItemInfo.structureItemNode.getType(),
							IProblemRequestor.INVALID_TYPE_IN_FIXED_RECORD,
							new String[] {prim.getName()});
						break;						
				}
			}
		}
		
		public void validateEmbeddedItem(StructureItem embeddedItem) {
		}
		
		public void validatePart(FixedStructureBinding fixedStructureBinding) {
		}
		
		private int getLengthWithoutOccurs(StructureItemBinding sItemBinding) {
			int result = sItemBinding.getLengthInBytes();
			if(sItemBinding.hasOccurs()) {
				result /= sItemBinding.getOccurs();
			}
			return result;
		}
		
		private int getTotalLengthOfChildren(StructureItemBinding sItemBinding) {
			int sum = 0;
			for(Iterator iter = sItemBinding.getChildren().iterator(); iter.hasNext();) {
				
        		StructureItemBinding siBinding = (StructureItemBinding) iter.next();
        		
        		//Do not count redefined storage in the size
        		if (siBinding.getAnnotation(new String[] { "egl", "core" }, "Redefines") == null) {
    				sum += siBinding.getLengthInBytes();
        		}                    	
			}
			return sum;
		}
	}
	
	protected abstract class StructureItemValidatorFactory {
		abstract StructureItemValidator createStructureItemValidator();
	}
	
	protected class DefaultStructureItemValidatorFactory extends StructureItemValidatorFactory {
		StructureItemValidator createStructureItemValidator() {
			return new DefaultStructureItemValidator();
		}
	}
	
	protected IProblemRequestor problemRequestor;
	protected ICompilerOptions compilerOptions;
	protected String canonicalStructureName;
	protected FixedStructureBinding structureBinding;
	protected StructureItemValidatorFactory sItemValidatorFactory;
	
	public FixedStructureValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(StructureItem structureItem) {
		if(structureItem.isEmbedded()) {
			sItemValidatorFactory.createStructureItemValidator().validateEmbeddedItem(structureItem);
		}
		else {
			StructureItemInfo sItemInfo = createStructureItemInfo(structureItem);
			if(sItemInfo != null) {
				sItemValidatorFactory.createStructureItemValidator().validate(sItemInfo);
			}
		}
		return false;
	}
	
	private StructureItemInfo createStructureItemInfo(StructureItem sItem) {
		StructureItemInfo result = new StructureItemInfo();;
		IBinding binding = sItem.resolveBinding();
		if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		StructureItemBinding itemBinding = (StructureItemBinding) binding;
		
		if(sItem.isFiller()) {
			result.nodeForErrors = sItem;
			result.canonicalName = "*";
		}
		else {
			result.nodeForErrors = sItem.getName();
			result.canonicalName = sItem.getName().getCanonicalName();
		}
		result.binding = itemBinding;
		result.structureItemNode = sItem;
		
		return result;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
