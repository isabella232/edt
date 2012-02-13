/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FlexibleRecordScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class FlexibleRecordBindingCompletor extends DefaultBinder {

    private FlexibleRecordBinding recordBinding;

    private IAnnotationBinding partSubTypeAnnotationBinding;

	private FlexibleRecordBindingFieldsCompletor flexibleRecordBindingFieldsCompletor;

    public FlexibleRecordBindingCompletor(Scope currentScope, FlexibleRecordBinding recordBinding,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, recordBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.recordBinding = recordBinding;
    }

    public boolean visit(Record record) {
        //First, we need to look for record subType on the AST or in the
        // settings block
        PartSubTypeAndAnnotationCollector collector = new PartSubTypeAndAnnotationCollector(recordBinding, this, currentScope, problemRequestor);
        record.accept(collector);
        processSubType(collector);
        flexibleRecordBindingFieldsCompletor = new FlexibleRecordBindingFieldsCompletor(currentScope, recordBinding, record.getName().getCanonicalName(), dependencyRequestor, problemRequestor, compilerOptions);
		//Next, we need to complete the fields in the record
        record.accept(flexibleRecordBindingFieldsCompletor);

        //now we will need to process the SettingsBlock for the
        // record...the collector has already gathered those for us.

        processSettingsBlocksFromCollector(collector);
        
        convertItemsToNullableIfNeccesary();
        
        //now we need to process the property overrides in the record and items
        
        recordBinding.setPrivate(record.isPrivate());

        record.getName().setBinding(recordBinding);
        
        return false;
    }

	public void endVisit(Record record) {
    	recordBinding.setValid(true);

        currentScope = new FlexibleRecordScope(currentScope, recordBinding);

        record.accept(new DefaultASTVisitor() {
            public boolean visit(Record record) {
                return true;
            }

            public boolean visit(SettingsBlock settingsBlock) {
                processResolvableProperties(settingsBlock, null, null);
                return false;
            }

            public boolean visit(StructureItem structureItem) {
                processResolvableProperties(structureItem);
                return false;
            }
        });
        currentScope = currentScope.getParentScope();
        super.endVisit(record);
                
        addSqlNullableAnnotations();        

    }
    
    private void addSqlNullableAnnotations() {
		IAnnotationBinding aBinding = recordBinding.getAnnotation(new String[] {"egl", "io", "sql"}, IEGLConstants.PROPERTY_ISSQLNULLABLE);
		if (aBinding == null) {
			return;
		}
		
		if(aBinding != null && Boolean.YES == aBinding.getValue()) {
			IDataBinding[] fields = recordBinding.getFields();
			for(int i = 0; i < fields.length; i++) {
				if (fields[i].getAnnotation(new String[] {"egl", "io", "sql"}, IEGLConstants.PROPERTY_ISSQLNULLABLE) == null) {
					fields[i].addAnnotation(aBinding);
				}
			}
		}
	}

    private void processResolvableProperties(StructureItem structureItem) {
        if (structureItem.hasSettingsBlock()) {
            IDataBinding dataBinding = null;
            if (!structureItem.isEmbedded()) {
                dataBinding = (IDataBinding) structureItem.resolveBinding();
            }
            processResolvableProperties(structureItem.getSettingsBlock(), dataBinding, null);
        }
    }

    protected void processSubType(PartSubTypeAndAnnotationCollector collector) {
        if (!collector.isFoundSubTypeInSettingsBlock()) {
            if (collector.getSubTypeAnnotationBinding() == null) {
            	partSubTypeAnnotationBinding = null;
             } else {
                partSubTypeAnnotationBinding = collector.getSubTypeAnnotationBinding();
            }
        } else {
            partSubTypeAnnotationBinding = collector.getSubTypeAnnotationBinding();
        }

    }

    private void processSettingsBlocksFromCollector(PartSubTypeAndAnnotationCollector collector) {

        if (collector.getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(new FlexibleRecordScope(currentScope, recordBinding),
                    recordBinding, recordBinding, recordBinding, -1, recordBinding);
            if (!collector.isFoundSubTypeInSettingsBlock() && partSubTypeAnnotationBinding != null) {
                scope = new AnnotationLeftHandScope(scope, partSubTypeAnnotationBinding, partSubTypeAnnotationBinding.getType(), partSubTypeAnnotationBinding, -1, recordBinding);
            }
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, recordBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            Iterator i = collector.getSettingsBlocks().iterator();
            while (i.hasNext()) {
                SettingsBlock block = (SettingsBlock) i.next();
                block.accept(blockCompletor);
            }
        }
    }

    public boolean visit(StructureItem structureItem) {
        return false;
    }

    public boolean visit(SettingsBlock settingsBlock) {
        return false;
    }

    protected void processResolvableProperty(IAnnotationTypeBinding annotationType, Object value, IDataBinding dataBinding,
            IDataBinding[] path, Expression expr) {
        if (value == null || !isResolvable(annotationType)) {
            return;
        }
        IAnnotationBinding annotation = dataBinding.getAnnotationFor(annotationType, path);
        if (annotation == null) {
            return;
        }

        if (annotationIs(annotationType, new String[] {"egl", "ui"}, "NumElementsItem")) {
            if (isUIRecordItem(dataBinding, path)) {
                annotation.setValue(resolveNumElementsItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
            }
            return;
        }

        if (annotationIs(annotationType, new String[] {"egl", "ui"}, "SelectedIndexItem")) {
            if (isUIRecordItem(dataBinding, path)) {
                annotation.setValue(resolveSelectedIndexItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
            }
            return;
        }

        if (annotationIs(annotationType, new String[] {"egl", "core"}, "Redefines")) {
            annotation.setValue(resolveRedefines(expr, value, dataBinding, path), null, null, compilerOptions, false);
            return;
        }
        
        if (annotationIs(annotationType, new String[] {"egl", "ui", "console"}, "Binding")) {
        	annotation.setValue(resolveBinding(expr, value, dataBinding, path), null, null, compilerOptions, false);
            return;
        }
    }

    private boolean isResolvable(IAnnotationTypeBinding annotationType) {
        return annotationIs(annotationType, new String[] {"egl", "ui"}, "NumElementsItem")
                || annotationIs(annotationType, new String[] {"egl", "ui"}, "SelectedIndexItem")
                || annotationIs(annotationType, new String[] {"egl", "core"}, "Redefines")
				|| annotationIs(annotationType, new String[] {"egl", "ui", "console"}, "Binding");
    }
    
    private void convertItemsToNullableIfNeccesary() {
		IAnnotationBinding aBinding = recordBinding.getAnnotation(new String[] {"egl", "core"}, "I4GLItemsNullable");
		if(aBinding != null && Boolean.YES == aBinding.getValue()) {
			IDataBinding[] fields = recordBinding.getFields();
			for(int i = 0; i < fields.length; i++) {
				ITypeBinding type = fields[i].getType();
				if(type != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getBaseType().getKind()) {
					((DataBinding) fields[i]).setType(type.getNullableInstance());
				}
			}
		}
	}
}
