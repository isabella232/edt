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

import java.util.Map;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author demurray
 */
public class SelectedRowItemAnnotationValidator extends AbstractSelectedItemAnnotationValidator {
	
	public SelectedRowItemAnnotationValidator() {
		super(IEGLConstants.PROPERTY_SELECTEDROWITEM);
	}
	
	protected void targetIsDataTableOrRecordArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		boolean typeValid = false;
		if(IDataBinding.STRUCTURE_ITEM_BINDING == value.getKind()) {
			if(((StructureItemBinding) value).isMultiplyOccuring() || ITypeBinding.DATATABLE_BINDING == value.getDeclaringPart().getKind()) {
				if(PrimitiveTypeBinding.getInstance(Primitive.INT) == selectedItemType ||
				   PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == selectedItemType) {
					typeValid = true;
				}
			}
			else {
				IAnnotationBinding aBinding = value.getAnnotation(new String[] {"egl", "ui"}, IEGLConstants.PROPERTY_ISBOOLEAN);
				if(aBinding != null && Boolean.YES == aBinding.getValue()) { 
					typeValid = true;
				}
			}
		}
		else {
			if(ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT)) == selectedItemType ||
			   ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN)) == selectedItemType) {
				typeValid = true;
			}
			else {
				IAnnotationBinding aBinding = value.getAnnotation(new String[] {"egl", "ui"}, IEGLConstants.PROPERTY_ISBOOLEAN);
				if(aBinding != null && Boolean.YES == aBinding.getValue()) { 
					typeValid = true;
				}
			}
		}
		
		if(!typeValid) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.SELECTEDROWITEM_TYPE_INVALID_FOR_RECORD_ARRAY_OR_TABLE,
				new String[] {
					value.getCaseSensitiveName()	
				});
		}
		
	}

	protected void targetIsPrimitiveArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		PrimitiveTypeBinding intPrim = PrimitiveTypeBinding.getInstance(Primitive.INT);		
		if(intPrim != selectedItemType &&
		   ArrayTypeBinding.getInstance(intPrim) != selectedItemType) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.SELECTEDROWITEM_TYPE_INVALID_FOR_ITEM_ARRAY,
				new String[] {
					value.getCaseSensitiveName()	
				});
		}
	}
}
