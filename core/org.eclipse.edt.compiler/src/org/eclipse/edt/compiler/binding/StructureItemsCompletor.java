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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullableType;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DataBindingScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FixedStructureScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class StructureItemsCompletor extends AbstractBinder {
	
	private static final int MAX_STRUCTURE_DEPTH = 45;

    public class ItemCompletor extends DefaultBinder {

        private StructureItemBinding itemBinding;

        public ItemCompletor(Scope currentScope, StructureItemBinding itemBinding, IDependencyRequestor dependencyRequestor,
                IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, itemBinding.getDeclaringPart(), dependencyRequestor, problemRequestor, compilerOptions);
            this.itemBinding = itemBinding;
        }

        public boolean visit(StructureItem structureItem) {
            if (structureItem.hasSettingsBlock()) {
                structureItem.getSettingsBlock().accept(this);
            }
            return false;
        }

        public boolean visit(SettingsBlock settingsBlock) {

            Scope itemScope = new DataBindingScope(currentScope, itemBinding);
            AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(itemScope, itemBinding, itemBinding.getType(),
                    itemBinding, -1, itemBinding.getDeclaringPart());
            settingsBlock.accept(new SettingsBlockAnnotationBindingsCompletor(new FixedStructureScope(currentScope, fixedStructureBinding), itemBinding.getDeclaringPart(), annotationScope, dependencyRequestor,
                    problemRequestor, compilerOptions));
            return false;
        }
    }

    private IProblemRequestor problemRequestor;

    private Stack structureItemsStack = new Stack();

    private StructureItemBindingAndLevel topBindingAndLevel;

    private FixedStructureBinding fixedStructureBinding;
    private String canonicalFixedStructureName;
    
    private boolean visitingFirstItem = true;
    private boolean firstItemHasLevel;

    private static class StructureItemBindingAndLevel {
        StructureItem structureItem;
    	StructureItemBinding structureItemBinding;
        int level;
        SettingsBlock settingsBlock;
        Name nameNode;
        boolean isFromEmbeddedRecord;
        String embeddedRecordName;

        StructureItemBindingAndLevel(StructureItem structureItem, StructureItemBinding structureItemBinding, int level, SettingsBlock settingsBlock, Name nameNode, boolean isFromEmbeddedRecord) {
        	this.structureItem = structureItem;
            this.structureItemBinding = structureItemBinding;
            this.level = level;
            this.settingsBlock = settingsBlock;
            this.nameNode = nameNode;
            this.isFromEmbeddedRecord = isFromEmbeddedRecord;
        }
    }

    public StructureItemsCompletor(Scope currentScope, FixedStructureBinding fixedStructureBinding,
    		String canonicalFixedStructureName, IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, fixedStructureBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.fixedStructureBinding = fixedStructureBinding;
        this.canonicalFixedStructureName = canonicalFixedStructureName;
    }

    public boolean visit(StructureItem structureItem) {

        String caseSensitiveFieldName = getCaseSensitiveFieldName(structureItem);

        ITypeBinding typeBinding = null;
        if (structureItem.hasType()) {
            try {
                typeBinding = bindType(structureItem.getType());
                if (typeBinding.getBaseType().getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
                    FixedRecordBinding fixedBinding = (FixedRecordBinding) typeBinding.getBaseType();

                    fixedStructureBinding.addReferencedStructure(fixedBinding);
                    if (loopExists(fixedBinding)) {
                        problemRequestor.acceptProblem(structureItem.getType(), IProblemRequestor.RECURSIVE_LOOP_STARTED_WITHIN_RECORD,
                                new String[] { fixedBinding.getCaseSensitiveName() });
                        if (structureItem.getName() != null) {
                            structureItem.getName().setBinding(IBinding.NOT_FOUND_BINDING);
                        }
                        structureItem.getType().accept(new DefaultASTVisitor() {
                            public boolean visit(NameType nameType) {
                                nameType.getName().setBinding(IBinding.NOT_FOUND_BINDING);
                                return false;
                            }

                            public boolean visit(ArrayType arrayType) {
                                arrayType.setTypeBinding(null);
                                return false;
                            }
                        });
                        return false;
                    }
                }
                
                if(isNullable(typeBinding)) {
                	problemRequestor.acceptProblem(structureItem.getType(), IProblemRequestor.NULLABLE_TYPE_NOT_ALLOWED_IN_PART);
                	typeBinding = typeBinding.getBaseType();
                }
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
                        .getInserts());
                if(structureItem.hasSettingsBlock()) {
                	bindNamesToNotFound(structureItem.getSettingsBlock());
                }
                return false; // Do not create the field binding if its type
                // cannot be resolved
            }
        } else {
            typeBinding = PrimitiveTypeBinding.getInstance(Primitive.CHAR, -1);

            if (structureItem.hasOccurs()) {
                typeBinding = ArrayTypeBinding.getInstance(typeBinding);
            }
        }

        if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
            FixedRecordBinding otherFixedRecord = (FixedRecordBinding) typeBinding;
            typeBinding = getFixedRecordTypeBinding(structureItem, otherFixedRecord);
            if (typeBinding == null) {
                return false;
            }
        } else if (typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
            // nothing to do for dataItems
        } else if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            ArrayTypeBinding arrayType = (ArrayTypeBinding) typeBinding;
            ITypeBinding elementType = arrayType.getElementType();

            if (elementType.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
                typeBinding = getFixedRecordTypeBinding(structureItem, (FixedRecordBinding) elementType);
                if (typeBinding == null) {
                    return false;
                }
                typeBinding = ArrayTypeBinding.getInstance(typeBinding.copyTypeBinding());
            } else if (elementType.getKind() == ITypeBinding.DATAITEM_BINDING) {
                // nothing to do for dataItems
            } else if (elementType.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
                problemRequestor.acceptProblem(structureItem.getType().isArrayType() ? ((ArrayType) structureItem.getType())
                        .getElementType() : structureItem.getType(), IProblemRequestor.PART_DOES_NOT_RESOLVE_TO_DATAITEM_OR_RECORD,
                        new String[] { elementType.getCaseSensitiveName() });
                return false;
            }
        } else if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
        	//Will issue a better error for embedded records below
        	if(!structureItem.isEmbedded()) {
	            problemRequestor.acceptProblem(structureItem.getType(), IProblemRequestor.PART_DOES_NOT_RESOLVE_TO_DATAITEM_OR_RECORD,
	                    new String[] { typeBinding.getCaseSensitiveName() });
	            return false;
        	}
        }
        
        if(visitingFirstItem) {
        	firstItemHasLevel = structureItem.hasLevel();
        	visitingFirstItem = false;
        }

        StructureItemBinding newItemBinding = createStructureItemBinding(caseSensitiveFieldName, structureItem, typeBinding);
        if (newItemBinding == null) {
            return false;
        }
        if (structureItem.hasInitializer()) {
         	newItemBinding.setInitialValue(getConstantValue(structureItem.getInitializer(), NullProblemRequestor.getInstance(), true));
        }
 
        StructureItemBindingAndLevel newItemAndLevel = null;
        if (structureItem.hasLevel()) {        	
            newItemAndLevel = new StructureItemBindingAndLevel(structureItem, newItemBinding, Integer.parseInt(structureItem.getLevel()), structureItem.getSettingsBlock(), getName(structureItem), false);
            
            if(!firstItemHasLevel) {
            	problemRequestor.acceptProblem(
            		getNodeForErrors(structureItem),
					IProblemRequestor.INCONSISTENT_LEVEL_NUMBERING,
					new String[] {getCanonicalName(structureItem)});
            }
        } else {
            newItemAndLevel = new StructureItemBindingAndLevel(structureItem, newItemBinding, 10, structureItem.getSettingsBlock(), getName(structureItem), false);
            
            if(firstItemHasLevel) {
            	problemRequestor.acceptProblem(
            		getNodeForErrors(structureItem),
					IProblemRequestor.INCONSISTENT_LEVEL_NUMBERING,
					new String[] {getCanonicalName(structureItem)});
            }
        }

        if (structureItem.isEmbedded()) {
            
            if (newItemBinding.getType().getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
                structureItem.setBinding(newItemBinding);
            	List structureItems = ((FixedRecordBinding) newItemBinding.getType()).getStructureItems();
            	if(structureItems.isEmpty()) {
            		problemRequestor.acceptProblem(
        				structureItem.getType(),
						IProblemRequestor.EMBED_RERERS_TO_EMPTY_RECORD,
						new String[] {structureItem.getType().getCanonicalName()});
            	}            	
                for (Iterator iter = structureItems.iterator(); iter.hasNext();) {
                    handleItem(new StructureItemBindingAndLevel(structureItem, (StructureItemBinding) iter.next(), newItemAndLevel.level, null, getName(structureItem), true));
                }
                ItemCompletor itemCompletor = new ItemCompletor(currentScope, newItemBinding, dependencyRequestor, problemRequestor, compilerOptions);
                structureItem.accept(itemCompletor);
            } else {
            	if(newItemBinding.getType().getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
            		problemRequestor.acceptProblem(
            			structureItem.getType(),
						IProblemRequestor.FLEXIBLE_RECORD_EMBEDDED_IN_FIXED);            		
            	}
            	else {
	                problemRequestor.acceptProblem(
	                	structureItem.getType(),
						IProblemRequestor.EMBEDED_ITEM_DOES_NOT_RESOLVE,
						new String[] {structureItem.getType().getCanonicalName()});
            	}
                return false;
            }
        } else {
            handleItem(newItemAndLevel);
            if(structureItem.isFiller()) {
            	structureItem.setBinding(newItemBinding);
            }
            else {
                structureItem.getName().setBinding(newItemBinding);
            }
        }

        return false;
    }
    
    private Name getName(StructureItem sItem) {
    	if(sItem.isEmbedded()) {
    		return getName(sItem.getType());
    	}
    	if(!sItem.isFiller()) {
    		return sItem.getName();
    	}
    	return null;
    }
    
    private Name getName(Type type) {
    	final Name[] result = new Name[] {null};
    	type.accept(new DefaultASTVisitor() {
    		public boolean visit(NameType nameType) {
    			result[0] = nameType.getName();
				return false;
			}
    	});
    	return result[0];
    }
    
    private String getCanonicalName(StructureItem sItem) {
    	if(sItem.isEmbedded()) {
    		Name name = getName(sItem.getType());
    		return name == null ? sItem.getType().getCanonicalName() : name.getCanonicalName();
    	}
    	else if(sItem.isFiller()) {
    		return "*";
    	}
    	else {
    		return sItem.getName().getCanonicalName();
    	}
    }
    
    private Node getNodeForErrors(StructureItem sItem) {
    	if(sItem.isEmbedded()) {
    		return sItem.getType();
    	}
    	else if(sItem.isFiller()) {
    		return sItem;
    	}
    	else {
    		return sItem.getName();
    	}
    }

    private String getCaseSensitiveFieldName(StructureItem item) {
        if (item.isEmbedded()) {
            return null;
        }
        if (item.isFiller()) {
            return InternUtil.internCaseSensitive("*");
        }
        return item.getName().getCaseSensitiveIdentifier();
    }

    private StructureItemBinding createStructureItemBinding(String caseSensitiveFieldName, StructureItem structureItem, ITypeBinding typeBinding) {
        StructureItemBinding result;
        int occurs = -1;
        if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            ArrayTypeBinding arrayTypeBinding = (ArrayTypeBinding) typeBinding;
            if (structureItem.hasType()) {
                ArrayType arrayTypeAST = (ArrayType) structureItem.getType();
                if (arrayTypeAST.hasInitialSize()) {
                    Expression initialSizeExpr = arrayTypeAST.getInitialSize();
                    OccursValueFinder occursFinder = new OccursValueFinder(problemRequestor, InternUtil.intern(caseSensitiveFieldName));
                    initialSizeExpr.accept(occursFinder);
                    occurs = occursFinder.getOccursValue();
                    if (occurs == -1) {
                        return null;
                    }

                    typeBinding = arrayTypeBinding.getElementType();
                    if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
                        //TODO: need real validation error
                        problemRequestor.acceptProblem(structureItem, 0);
                        return null;
                    }
                } else {
                    problemRequestor.acceptProblem(structureItem, IProblemRequestor.DYNAMIC_ARRAY_USED_IN_FIXED_RECORD);
                    return null;
                }
            } else {
                if (structureItem.hasOccurs()) {
                    occurs = Integer.parseInt(structureItem.getOccurs());
                    typeBinding = arrayTypeBinding.getElementType();
                }
            }
        }

        switch (typeBinding.getKind()) {
        case ITypeBinding.FIXED_RECORD_BINDING:
            if (structureItem.isEmbedded()) {
                // For embedded records, leave the type as fixed record
                result = new StructureItemBinding(caseSensitiveFieldName, fixedStructureBinding, typeBinding);
            } else {
                // For typedef'd records, create a character primitive for the
                // type
                ITypeBinding charType = PrimitiveTypeBinding.getInstance(Primitive.CHAR, -1);
                result = new StructureItemBinding(caseSensitiveFieldName, fixedStructureBinding, charType);
            }
            for (Iterator iter = ((FixedRecordBinding) typeBinding).getStructureItems().iterator(); iter.hasNext();) {
                result.addChild((StructureItemBinding) iter.next());
            }
            break;

        default:
            result = new StructureItemBinding(caseSensitiveFieldName, fixedStructureBinding, typeBinding);
        }

        if (occurs >=
        	1) {
            result.setOccurs(occurs);
        }

        return result;
    }

    private void handleItem(StructureItemBindingAndLevel newItemAndLevel) {
        if (structureItemsStack.isEmpty()) {
            addToRecord(newItemAndLevel);
        } else {
            // newItemAndLevel.level == topBindingAndLevel.level
            if (newItemAndLevel.level == topBindingAndLevel.level) {
                popTopItemAndAdd(newItemAndLevel);
            }
            // newItemAndLevel.level > topBindingAndLevel.level
            else if (newItemAndLevel.level > topBindingAndLevel.level) {
                addToTopItem(newItemAndLevel);
            }
            // newItemAndLevel.level < topBindingAndLevel.level
            else {
                while (newItemAndLevel.level < topBindingAndLevel.level) {
                    popFromItemStack();
                    if (structureItemsStack.isEmpty()) {
                        break;
                    }
                    topBindingAndLevel = (StructureItemBindingAndLevel) structureItemsStack.peek();
                }

                if (structureItemsStack.isEmpty()) {
                	problemRequestor.acceptProblem(
                		getNodeForErrors(newItemAndLevel.structureItem),
						IProblemRequestor.INVALID_STRUCTURE_LEVEL_NUMBERS,
						new String[] {Integer.toString(newItemAndLevel.level)});
                } else {
                    if (topBindingAndLevel.level == newItemAndLevel.level) {
                        popTopItemAndAdd(newItemAndLevel);
                    } else {
                        // topBindingAndLevel.level < newItemAndLevel.level
                    	problemRequestor.acceptProblem(
                    		getNodeForErrors(newItemAndLevel.structureItem),
    						IProblemRequestor.INVALID_STRUCTURE_LEVEL_NUMBERS,
    						new String[] {Integer.toString(newItemAndLevel.level)});
                    }
                }
            }
        }
    }

    private void popAllFromItemsStack() {
        while (!structureItemsStack.isEmpty()) {
            popFromItemStack();
        }
    }

    private void popFromItemStack() {
        StructureItemBindingAndLevel completed = (StructureItemBindingAndLevel) structureItemsStack.pop();
        if (completed.settingsBlock != null) {
            ItemCompletor itemCompletor = new ItemCompletor(currentScope, completed.structureItemBinding, dependencyRequestor,
                    problemRequestor, compilerOptions);
            completed.settingsBlock.accept(itemCompletor);
        }
    }

    private void popTopItemAndAdd(StructureItemBindingAndLevel newItemAndLevel) {
        popFromItemStack();

        if (structureItemsStack.isEmpty()) {
            addItem(fixedStructureBinding, newItemAndLevel);
        } else {
            addItem(
            	((StructureItemBindingAndLevel) structureItemsStack.peek()).structureItemBinding,
				newItemAndLevel);
        }
        structureItemsStack.push(newItemAndLevel);
        topBindingAndLevel = newItemAndLevel;
    }

    private void addToRecord(StructureItemBindingAndLevel newItemAndLevel) {
        addItem(fixedStructureBinding, newItemAndLevel);        
        topBindingAndLevel = newItemAndLevel;
        structureItemsStack.push(newItemAndLevel);
    }

    private void addToTopItem(StructureItemBindingAndLevel newItemAndLevel) {
    	if(!topBindingAndLevel.structureItemBinding.getChildren().isEmpty()) {
    		Type type = topBindingAndLevel.structureItem.getType();
    		if(type != null) {
    			ITypeBinding tBinding = type.resolveTypeBinding();
    			if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding &&
    			   ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind()) {
		    		//This should only happen when the top item on the stack is a typedef'd
		    		//fixed record. Therefore, will assume that topBinidngAndLevel.nameNode != null
		    		problemRequestor.acceptProblem(    			
		    			topBindingAndLevel.nameNode,
						IProblemRequestor.INVALID_RECORD_REFERENCED_WITHIN_ANOTHER_RECORD,
						new String[] {
		    				topBindingAndLevel.structureItem.getType().getCanonicalName(),
							topBindingAndLevel.nameNode.getCanonicalName()
		    			});
    			}
    		}
    	}
    	
        addItem(topBindingAndLevel.structureItemBinding, newItemAndLevel);
        
        structureItemsStack.push(newItemAndLevel);
        topBindingAndLevel = newItemAndLevel;
    }
    
    private Map namesFor = new HashMap();
    
    private boolean nameIsDefined(Object parent, String name) {
    	if("*".equals(name)) {
    		return false;
    	}
    	Set definedNames = (Set) namesFor.get(parent);
    	if(definedNames == null) {
    		definedNames = new HashSet();
    		definedNames.add(name);    		
    		namesFor.put(parent, definedNames);
    		return false;
    	}
    	else {
    		if(definedNames.contains(name)) {
    			return true;
    		}
    		definedNames.add(name);
    		return false;
    	}
    }
    
    private void requestDuplicateNameProblem(StructureItemBindingAndLevel itemAndLevel) {
    	if(itemAndLevel.isFromEmbeddedRecord) {
    		problemRequestor.acceptProblem(
    			itemAndLevel.nameNode,
				IProblemRequestor.DUPLICATE_ITEM_NAME_DUE_TO_EMBED,
				new String[] {
    				canonicalFixedStructureName,
					itemAndLevel.structureItemBinding.getName(),
					itemAndLevel.nameNode.getCanonicalName()});
    	}
    	else {
    		problemRequestor.acceptProblem(
    			itemAndLevel.nameNode,
				IProblemRequestor.DUPLICATE_ITEM_NAME,
				new String[] {
    				canonicalFixedStructureName,
					itemAndLevel.nameNode.getCanonicalName()
    			});
    	}
    }
    
    private void addItem(FixedStructureBinding parent, StructureItemBindingAndLevel child) {
    	if(nameIsDefined(parent, child.structureItemBinding.getName())) {
    		requestDuplicateNameProblem(child);
    	}
    	else {
	    	fixedStructureBinding.addStructureItem(child.structureItemBinding);
	        child.structureItemBinding.setParentItem(null);
    	}
    }
    
    private void addItem(StructureItemBinding parent, StructureItemBindingAndLevel child) {
    	if(structureItemsStack.size() > MAX_STRUCTURE_DEPTH-1) {
        	problemRequestor.acceptProblem(
        		getNodeForErrors(child.structureItem),
        		IProblemRequestor.INVALID_AMOUNT_OF_NESTING,
				new String[] {Integer.toString(MAX_STRUCTURE_DEPTH)});
        }
    	
    	if(nameIsDefined(parent, child.structureItemBinding.getName())) {
    		requestDuplicateNameProblem(child);
    	}
    	else {    		
    		parent.addChild(child.structureItemBinding);
    	}
    }

    private ITypeBinding getFixedRecordTypeBinding(StructureItem structureItem, FixedStructureBinding otherFixedRecord) {

        if (!otherFixedRecord.isValid()) {
            otherFixedRecord = (FixedStructureBinding) otherFixedRecord.realize();
        }
        otherFixedRecord = (FixedStructureBinding) otherFixedRecord.copyTypeBinding();
        return otherFixedRecord;
    }

    public boolean visit(Record record) {
        return true;
    }

    public boolean visit(DataTable dataTable) {
        return true;
    }

    public void endVisit(Record record) {
        popAllFromItemsStack();
    }

    public void endVisit(DataTable dataTable) {
        popAllFromItemsStack();
    }

    public boolean loopExists(FixedRecordBinding recType) {

        if (recType.containsReferenceTo(fixedStructureBinding)) {
            return true;
        }
        if (!recType.isValid) {
            recType = (FixedRecordBinding)recType.realize();
            if (recType.containsReferenceTo(fixedStructureBinding)) {
                return true;
            }
        }
        return false;
    }
    
    public ITypeBinding bindType(Type type) throws ResolutionException {
    	
    	if (type.getKind() == Type.PRIMITIVETYPE) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            if (!primitiveType.hasPrimLength() && !primitiveType.hasPrimPattern()) {
            	PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding)primitiveType.resolveTypeBinding();
            	if (primTypeBinding.getLength() == 0) {
            		return PrimitiveTypeBinding.getInstance(primTypeBinding.getPrimitive(), -1);
            	}
            }
       	}
    	
    	return super.bindType(type);
    	
     }


}
