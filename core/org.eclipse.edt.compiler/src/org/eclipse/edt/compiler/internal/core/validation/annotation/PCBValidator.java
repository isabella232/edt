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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.PCBKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class PCBValidator implements IAnnotationValidationRule {

	private static final Set invalidFieldsForType = new HashSet();
	static{
		invalidFieldsForType.add(InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX));
		invalidFieldsForType.add(InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM));
		invalidFieldsForType.add(InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY));
	}
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		
//		validateType(expression, container, allAnnotations, problemRequestor);
	
	}

	private void validateType(final Node expression, Node container, final Map allAnnotations, final IProblemRequestor problemRequestor) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PCBTYPE));
		if(annotationBinding != null) {
			final IDataBinding pcbKind = (IDataBinding)annotationBinding.getValue();
			
			if(pcbKind.getName().equalsIgnoreCase(PCBKind.TP.getName()) || pcbKind.getName().equalsIgnoreCase(PCBKind.GSAM.getName())){
				for (Iterator iter = invalidFieldsForType.iterator(); iter.hasNext();) {
					String fieldName = (String ) iter.next();
					
					if(allAnnotations.containsKey(fieldName)){
						problemRequestor.acceptProblem(expression, IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE, new String[]{fieldName, pcbKind.getName()});
					}
				}
			}else if(pcbKind.getName().equalsIgnoreCase(PCBKind.DB.getName())){
				
				container.accept(new DefaultASTVisitor(){
					public boolean visit(StructureItem structureItem) {
						String itemName = structureItem.getName().getIdentifier();
						if(itemName == InternUtil.intern("elawork") || itemName == InternUtil.intern("elamsg")){
							for (Iterator iter = invalidFieldsForType.iterator(); iter.hasNext();) {
								String fieldName = (String ) iter.next();
								
								if(allAnnotations.containsKey(fieldName)){
									problemRequestor.acceptProblem(expression, IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE_WHEN_ITEM_IS_ELAWORK_OR_ELAMSG, new String[]{fieldName, pcbKind.getCaseSensitiveName()});
								}
							}				
						}
						return false;
					}
				});
				
			}
		}
	}

}
