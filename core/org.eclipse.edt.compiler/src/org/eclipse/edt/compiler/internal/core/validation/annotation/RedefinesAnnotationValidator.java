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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author
 */
public class RedefinesAnnotationValidator implements IAnnotationValidationRule {

	protected IAnnotationTypeBinding annotationType;

	protected String canonicalAnnotationName;

	private IDataBinding getDataBinding(Node target) {
		IDataBinding result = getNonEmbeddedStructureItemBinding(target);
		if(result == null) {
			if (target instanceof ClassDataDeclaration) {
				result = ((Name) ((ClassDataDeclaration) target).getNames().get(0)).resolveDataBinding();
			}
			if (target instanceof FunctionDataDeclaration) {
				result = ((Name) ((FunctionDataDeclaration) target).getNames().get(0)).resolveDataBinding();
			}
		}
		return result;
	}
	
	private StructureItemBinding getNonEmbeddedStructureItemBinding(Node target) {

		if (target instanceof StructureItem) {
			StructureItem si = (StructureItem) target;
			if (si.isEmbedded()) {
				return null;
			}
			IBinding binding = si.resolveBinding();
			if (Binding.isValidBinding(binding) && binding.isDataBinding() && ((IDataBinding) binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
				return (StructureItemBinding) binding;
			}
			return null;
		}
		
		if (target instanceof Name) {
			IBinding binding = ((Name) target).resolveBinding();
			if (Binding.isValidBinding(binding) && binding.isDataBinding() && ((IDataBinding) binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
				return (StructureItemBinding) binding;
			}
			return null;
		}
		
		
		return null;
	}
	
	private boolean isNonEmbeddedFixedRecordField(Node target) {
		return getNonEmbeddedStructureItemBinding(target) != null;
	}

	private boolean isBasicFixedRecord(ITypeBinding targetTypeBinding) {
		if (!StatementValidator.isValidBinding(targetTypeBinding)) {
			return true; // prevent extra errors when the resolution failed
		}

		return targetTypeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING
				&& targetTypeBinding.getAnnotation(new String[] { "egl", "core" }, "BasicRecord") != null;
	}

	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations,
			final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		if (allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PCB)) != null) {
			return;
		}

		if (!isBasicFixedRecord(targetTypeBinding) && !isNonEmbeddedFixedRecordField(target)) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINING_MUST_BE_FIXED_RECORD);
			return;
		}

		final IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil
				.intern(IEGLConstants.PROPERTY_REDEFINES));

		Object value = annotationBinding.getValue();
		if (value instanceof IBinding && StatementValidator.isValidBinding((IBinding) value) && ((IBinding) value).isDataBinding()) {
			IDataBinding redefined = (IDataBinding) value;

			if (redefined.getAnnotation(new String[] { "egl", "core" }, "Redefines") != null) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_CANNOT_BE_REDEFINES,
						new String[] { IEGLConstants.PROPERTY_REDEFINES });
			}

			// Only allowed to redefine a structureItemBinding, if you are in a
			// fixed record
			if (redefined.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
				if (!isNonEmbeddedFixedRecordField(target)) {
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION,
							new String[] { IEGLConstants.PROPERTY_REDEFINES });
				}
				else {
					StructureItemBinding redefinedStructureBinding = (StructureItemBinding) redefined;
					StructureItemBinding redefining = getNonEmbeddedStructureItemBinding(target);
					if (!immediatelyFollows(redefinedStructureBinding, redefining)) {
						problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_MUST_FOLLOW,
								new String[] {redefining.getCaseSensitiveName(), redefined.getCaseSensitiveName()});
					}
					
					if(redefinedStructureBinding.hasOccurs()) {
						problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_TARGET_IS_ARRAY,
								new String[] {redefined.getCaseSensitiveName()});
					}
				}
			}

			ITypeBinding typeBinding = redefined.getType();

			if (StatementValidator.isValidBinding(typeBinding)) {
				if (typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING
						&& redefined.getKind() != IDataBinding.STRUCTURE_ITEM_BINDING) {
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION,
							new String[] { IEGLConstants.PROPERTY_REDEFINES });
				}

				else {
					IDataBinding redefiner = getDataBinding(target);
					if(Binding.isValidBinding(redefiner) && redefiner.getDeclaringPart() != redefined.getDeclaringPart()) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.REDEFINER_AND_REDEFINED_MUST_BE_DECLARED_IN_SAME_PART,
							new String[] {
								redefiner.getCaseSensitiveName(),
								redefined.getCaseSensitiveName()
							});
					}
					else {
						int recordBeingRedefinedSize = getSizeInBytes(redefined);
						int recordDoingTheRedefiningSize = getSizeInBytes(targetTypeBinding, target);
	
						if (recordBeingRedefinedSize < recordDoingTheRedefiningSize) {
							problemRequestor.acceptProblem(errorNode, IProblemRequestor.REDEFINES_SIZE_MISMATCH, new String[] {
									IEGLConstants.PROPERTY_REDEFINES, redefined.getCaseSensitiveName(),
									Integer.toString(recordBeingRedefinedSize), getTargetName(targetTypeBinding, target),
									Integer.toString(recordDoingTheRedefiningSize) });
						}
					}
				}

			}

		}

	}

	private String getTargetName(ITypeBinding targetTypeBinding, Node target) {
		if (isNonEmbeddedFixedRecordField(target)) {
			StructureItemBinding siBinding = getNonEmbeddedStructureItemBinding(target);
			return siBinding.getCaseSensitiveName();
		}

		return targetTypeBinding.getCaseSensitiveName();
	}

	private int getSizeInBytes(ITypeBinding targetTypeBinding, Node target) {
		/*
		 * When comparing redefined records, SQL indicators need to be added to
		 * a SQL record's length. Each field has a 4 character SQL indicator.
		 * The size of the record needs to be adjusted to allow redefining to
		 * work.
		 */
		if (isNonEmbeddedFixedRecordField(target)) {
			StructureItemBinding siBinding = getNonEmbeddedStructureItemBinding(target);
			int len = siBinding.getLengthInBytes();
			if (Binding.isValidBinding(siBinding.getEnclosingStructureBinding())) {
				if (siBinding.getEnclosingStructureBinding().getAnnotation(new String[] { "egl", "io", "sql" }, "SQLRecord") != null) {
					len = len + 4;
				}
				return len;
			}
		}

		if (Binding.isValidBinding(targetTypeBinding) && targetTypeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
			return ((FixedRecordBinding) targetTypeBinding).getSizeInBytes();
		}

		return -1;
	}

	private int getSizeInBytes(IDataBinding dBinding) {
		/*
		 * When comparing redefined records, SQL indicators need to be added to
		 * a SQL record's length. Each field has a 4 character SQL indicator.
		 * The size of the record needs to be adjusted to allow redefining to
		 * work.
		 */
		if (Binding.isValidBinding(dBinding)) {
			if (dBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
				StructureItemBinding siBinding = (StructureItemBinding) dBinding;
				int len = siBinding.getLengthInBytes();
				if (siBinding.getDeclaringPart().getAnnotation(new String[] { "egl", "io", "sql" }, "SQLRecord") != null) {
					len = len + 4;
				}
				return len;
			}

			ITypeBinding tBinding = dBinding.getType();
			if (Binding.isValidBinding(tBinding)) {
				if (tBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
					FixedRecordBinding rec = (FixedRecordBinding) tBinding;
					int len = rec.getSizeInBytes();
					if (rec.getAnnotation(new String[] { "egl", "io", "sql" }, "SQLRecord") != null) {
						len = len + 4 * rec.getStructureItems().size();
					}
					return len;
				}
			}
		}
		return -1;
	}

	private boolean immediatelyFollows(StructureItemBinding first, StructureItemBinding second) {
		if (first.getParentItem() != second.getParentItem()) {
			return false;
		}

		List items;
		if (first.getParentItem() == null) {
			items = first.getEnclosingStructureBinding().getStructureItems();
		} else {
			items = first.getParentItem().getChildren();
		}
		Iterator i = items.iterator();
		boolean foundFirst = false;
		while (i.hasNext()) {
			StructureItemBinding current = (StructureItemBinding) i.next();
			if (current == first) {
				foundFirst = true;
				continue;
			}
			if (current == second) {
				return foundFirst;
			}
			// ignore the item if it has a redefines
			if (foundFirst && current.getAnnotation(new String[] { "egl", "core" }, "Redefines") == null) {
				return false;
			}
		}

		return false;
	}

}
