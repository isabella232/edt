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
package org.eclipse.edt.compiler.binding;

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public abstract class FixedStructureBindingCompletor extends DefaultBinder {
	
	protected PartSubTypeAndAnnotationCollector partSubTypeAndAnnotationCollector;
    private FixedStructureBinding fixedStructureBinding;
    private IAnnotationBinding partSubTypeAnnotationBinding;


    public FixedStructureBindingCompletor(FixedStructureBinding fixedStructureBinding, Scope currentScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, fixedStructureBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.fixedStructureBinding = fixedStructureBinding;
    }
    
    public PartSubTypeAndAnnotationCollector getPartSubTypeAndAnnotationCollector() {
        if (partSubTypeAndAnnotationCollector == null) {
            partSubTypeAndAnnotationCollector = new PartSubTypeAndAnnotationCollector(fixedStructureBinding, this, currentScope, problemRequestor);
        }
        return partSubTypeAndAnnotationCollector;
    }
    
    protected void processSubType() {
        if (!getPartSubTypeAndAnnotationCollector().isFoundSubTypeInSettingsBlock()) {
            if (getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding() == null) {
                if (getDefaultSubType() != null) {
                    AnnotationBinding annotation =  new AnnotationBinding(getDefaultSubType().getCaseSensitiveName(), fixedStructureBinding, getDefaultSubType());
                    fixedStructureBinding.addAnnotation(annotation);
                    partSubTypeAnnotationBinding = annotation;
                }
            }
            else {
                partSubTypeAnnotationBinding = getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding();
            }
        }
        else {
            partSubTypeAnnotationBinding = getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding();
        }
       
    }
    
    protected void processSettingsBlocks() {

        if (getPartSubTypeAndAnnotationCollector().getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(getFixedStructureScope(), fixedStructureBinding, fixedStructureBinding, fixedStructureBinding, -1, fixedStructureBinding);
            if (!getPartSubTypeAndAnnotationCollector().isFoundSubTypeInSettingsBlock() && partSubTypeAnnotationBinding != null) {
                scope = new AnnotationLeftHandScope(scope, partSubTypeAnnotationBinding, partSubTypeAnnotationBinding.getType(), partSubTypeAnnotationBinding, -1, fixedStructureBinding);
            }
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, fixedStructureBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
            Iterator i = getPartSubTypeAndAnnotationCollector().getSettingsBlocks().iterator();
            while (i.hasNext()) {
                SettingsBlock block = (SettingsBlock)i.next();
                block.accept(blockCompletor);
            }
        }
    }
    
    protected abstract IPartSubTypeAnnotationTypeBinding getDefaultSubType();
    protected abstract Scope getFixedStructureScope();
    
    public boolean visit(SettingsBlock settingsBlock) {
        return false;
    }
}
