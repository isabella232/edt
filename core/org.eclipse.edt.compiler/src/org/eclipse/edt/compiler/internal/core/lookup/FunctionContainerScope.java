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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.BindingUtilities;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class FunctionContainerScope extends Scope {
	
	FunctionContainerBinding functionContainerBinding;
	private boolean unqualifiedItemReferencesAreAllowed;
	private boolean I4GLItemsNullableIsEnabled;
	private List usedLibraries = Collections.EMPTY_LIST;	
	private List usedDataTables = Collections.EMPTY_LIST;
	private List usedEnumerations = Collections.EMPTY_LIST;
//	private FormGroupBinding mainFormGroup;
	private Set usedForms = Collections.EMPTY_SET;
	private List recordClassFields;
	private List formClassFields;
	private boolean returnFunctionContainerFunctions = true;
	
	// For data resolution:
	private Map level4Items;
	private Map level5Items;
	private Map level6Items;
	private Map usedEnumerationItems;
	
	private Map itemsWhoseNamesCanBeUnqualified;
	private Map recordsFormsAndDataTables;
	
	// For function resolution:	
	private Map usedLibraryFunctionBindings;
	
	private boolean hasDLIAnnotation;
    
    public FunctionContainerScope(Scope parentScope, Part functionContainerBinding) {
        super(parentScope);
        this.functionContainerBinding = functionContainerBinding;
        
        if(functionContainerBinding != null) {
	        IAnnotationBinding annotation = functionContainerBinding.getAnnotation(InternUtil.intern(new String[] {"egl", "core"}), InternUtil.intern("AllowUnqualifiedItemReferences"));
	        if (annotation != null && Boolean.YES == annotation.getValue()) {
	            unqualifiedItemReferencesAreAllowed = true;
	        }
	        annotation = functionContainerBinding.getAnnotation(InternUtil.intern(new String[] {"egl", "core"}), InternUtil.intern("IncludeReferencedFunctions"));
	        if(annotation == null || annotation.getValue() == Boolean.NO) {
	            	stopReturningTopLevelFunctions();
	        }
	        
	        annotation = functionContainerBinding.getAnnotation(InternUtil.intern(new String[] {"egl", "io", "dli"}), InternUtil.intern("DLI"));
	        if(annotation != null) {
	            hasDLIAnnotation = true;
	        }
	        
	        annotation = functionContainerBinding.getAnnotation(InternUtil.intern(new String[] {"egl", "core"}), InternUtil.intern("I4GLItemsNullable"));
	        if (annotation != null && Boolean.YES == annotation.getValue()) {
	            I4GLItemsNullableIsEnabled = true;
	        }
        }
    }
    
    public FunctionContainerScope(Scope parentScope, FunctionContainerScope fContainerScope) {
    	super(parentScope);
    	
    	functionContainerBinding = fContainerScope.functionContainerBinding;
    	unqualifiedItemReferencesAreAllowed = fContainerScope.unqualifiedItemReferencesAreAllowed;
    	usedLibraries = fContainerScope.usedLibraries;	
    	usedDataTables = fContainerScope.usedDataTables;
//    	mainFormGroup = fContainerScope.mainFormGroup;
    	usedForms = fContainerScope.usedForms;
    	recordClassFields = fContainerScope.recordClassFields;
    	formClassFields = fContainerScope.formClassFields;
    	returnFunctionContainerFunctions = fContainerScope.returnFunctionContainerFunctions;
    	level4Items = fContainerScope.level4Items;
    	level5Items = fContainerScope.level5Items;
    	level6Items = fContainerScope.level6Items;
    	usedEnumerationItems = fContainerScope.usedEnumerationItems;
    	itemsWhoseNamesCanBeUnqualified = fContainerScope.itemsWhoseNamesCanBeUnqualified;
    	recordsFormsAndDataTables = fContainerScope.recordsFormsAndDataTables;
    	usedLibraryFunctionBindings = fContainerScope.usedLibraryFunctionBindings;
    	hasDLIAnnotation = fContainerScope.hasDLIAnnotation;
    	I4GLItemsNullableIsEnabled = fContainerScope.I4GLItemsNullableIsEnabled;
    }

    public IDataBinding findData(String simpleName) {
    	/*
    	 * 3) Search for constant, item or record in the global storage or
    	 *    parameter list of the program/pageHandler/library
    	 */
    	IDataBinding result = functionContainerBinding.findData(simpleName);
    	
    	if(result != IBinding.NOT_FOUND_BINDING &&
    	   (IDataBinding.NESTED_FUNCTION_BINDING == result.getKind() ||
    	    IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == result.getKind())) {
    		if(!returnFunctionContainerFunctions) {
    			result = IBinding.NOT_FOUND_BINDING;
    		}
    	}

    	if(result != IBinding.NOT_FOUND_BINDING) return result;
    	
    	/*
    	 * 3.5) Search for 'used' form or datatable.
    	 */
		if(usedForms != null) {
			for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
				FormBinding nextForm = (FormBinding) iter.next();
				if(nextForm.getName() == simpleName) {
					return nextForm.getStaticFormDataBinding();
				}
			}
		}
		
		for(Iterator iter = usedDataTables.iterator(); iter.hasNext();) {
			DataTableBinding usedTable = (DataTableBinding) iter.next();
			if(usedTable.getName() == simpleName) {
				return usedTable.getStaticDataTableDataBinding();
			}
        }
    	
        /*
         * 4) Search for structure item/field in (program/pagehandler/library):
         *       *Records in the global storage list
         *       *Records in the parameter list
         *       *Forms used in I/O statements
         *       *Form in the parameter list
         *       *InputForm for the Program
         *       *Tables "USED"
         *    Search for form in the main form group of the program
         *    Search for "resourceAssociation" in I/O record used by the program
         *      (note: special rules if more than 1 found!)
         * 
         *    * if allowUnqualifiedItemReferences = yes
         */
        result = (IDataBinding) getLevel4Items().get(simpleName);        
    	if(result != null) return result;
    	
    	IDataBinding resultFromParent = findDataInParent(simpleName);
    	if(IBinding.NOT_FOUND_BINDING != resultFromParent && IDataBinding.TOP_LEVEL_FUNCTION_BINDING == resultFromParent.getKind()) {
    		return resultFromParent;
    	}
    	
    	/*
    	 * 5) Search for constant, item or record in a USED library
    	 */        
        result = (IDataBinding) getLevel5Items().get(simpleName);
        
        if(result != null && IDataBinding.NESTED_FUNCTION_BINDING == result.getKind()) {
    		if(!returnFunctionContainerFunctions) {
    			result = IBinding.NOT_FOUND_BINDING;
    		}
    	}
        
        if(result != null) return result;
        
        /*
    	 * 7) Search for sysvar data 
    	 */
        if(resultFromParent != IBinding.NOT_FOUND_BINDING) {
        	if(IDataBinding.CLASS_FIELD_BINDING == resultFromParent.getKind() && !hasDLIAnnotation && isDLIVarVariable(resultFromParent)){
        		return resultFromParent;
        	}
        }
        
        result = (IDataBinding) getUsedEnumerationItems().get(simpleName);
        
        if(result != null) return result;
    	
    	/*
    	 * 8) Search DataTable
    	 */
        ITypeBinding type = findType(simpleName);
        if(type != null && type != IBinding.NOT_FOUND_BINDING) {
        	if(type.getKind() == ITypeBinding.DATATABLE_BINDING) {
        		return ((DataTableBinding) type).getStaticDataTableDataBinding();
        	}
        	else if(type.getKind() == ITypeBinding.EXTERNALTYPE_BINDING){
        		return ((ExternalTypeBinding) type).getStaticExternalTypeDataBinding();
        	}
        }
        
        /*
         * 9) Search used Libraries
         */
        for(Iterator iter = usedLibraries.iterator(); iter.hasNext();) {
			LibraryBinding usedLibrary = (LibraryBinding) iter.next();
			if(usedLibrary.getName() == simpleName) {
				return usedLibrary.getStaticLibraryDataBinding();
			}
        }
        
        if(resultFromParent != IBinding.NOT_FOUND_BINDING) {
        	return resultFromParent;
        }
        
        return IBinding.NOT_FOUND_BINDING;
    }
    
    private IDataBinding findDataInParent(String simpleName) {
    	IDataBinding binding = parentScope.findData(simpleName);
    	
    	if(IBinding.NOT_FOUND_BINDING != binding &&
    		AbstractBinder.typeIs(binding.getDeclaringPart(), new String[] {"egl", "io", "dli"}, IEGLConstants.KEYWORD_DLIVAR) &&
    		functionContainerBinding.getAnnotation(new String[] {"egl", "io", "dli"}, IEGLConstants.PROPERTY_DLI) == null) {
    		binding = IBinding.NOT_FOUND_BINDING;
    	}
    	
    	return binding;
	}

	private boolean isDLIVarVariable(IDataBinding dBinding) {
    	IPartBinding declaringPart = dBinding.isDataBindingWithImplicitQualifier() ?
    		((DataBindingWithImplicitQualifier) dBinding).getWrappedDataBinding().getDeclaringPart() :
    		dBinding.getDeclaringPart();
    		
    	if(declaringPart != null) {
    		if(InternUtil.intern(new String[] {"egl", "io", "dli"}) == declaringPart.getPackageName() &&
    		   InternUtil.intern("dlivar") == declaringPart.getName()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public IDataBinding findIOTargetData(String simpleName) {
    	/*
    	 * Attempt to find a record with the given name in the global storage
    	 */
    	IDataBinding result = functionContainerBinding.findData(simpleName);
    	if(result != IBinding.NOT_FOUND_BINDING) {
	    	int resultTypeKind =  result.getType().getBaseType().getKind();
	    	if(resultTypeKind != ITypeBinding.FIXED_RECORD_BINDING &&
			   resultTypeKind != ITypeBinding.FLEXIBLE_RECORD_BINDING) {
	    		result = IBinding.NOT_FOUND_BINDING;
	    	}
    	}
    	
    	/*
    	 * Attempt to find a form with the given name in the "main" formGroup
    	 */
    	if(usedForms != null) {
    		for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
    			FormBinding nextForm = (FormBinding) iter.next();
    			if(nextForm.getName() == simpleName) {
        			if(result == IBinding.NOT_FOUND_BINDING) {
        				result = nextForm.getStaticFormDataBinding();
        			}
        			else {
        				return AmbiguousDataBinding.getInstance();
        			}
    				break;
    			}
    		}
    	}
	    
    	return result;
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
    	ITypeBinding result = parentScope.findType(simpleName);
    	if(IBinding.NOT_FOUND_BINDING != result &&
    	   ITypeBinding.LIBRARY_BINDING == result.getKind() &&
    	   result.getPackageName() == InternUtil.intern(new String[] {"egl", "io", "dli"}) &&
    	   result.getName() == InternUtil.intern("DLIVar") &&
    	   !hasDLIAnnotation) {
    		result = IBinding.NOT_FOUND_BINDING;
    	}
        return result;
    }
    
    public void addUsedLibrary(Library usedLibraryBinding) {
    	if(usedLibraries == Collections.EMPTY_LIST) {
    		usedLibraries = new ArrayList();    		
    	}
    	usedLibraries.add(usedLibraryBinding);
    }
    
    void addUsedDataTable(ITypeBinding usedTableBinding) {
    	if(usedDataTables == Collections.EMPTY_LIST) {
    		usedDataTables = new ArrayList();    		
    	}
    	usedDataTables.add(usedTableBinding);
    }
    
    void addUsedEnumeration(ITypeBinding usedTableBinding) {
    	if(usedEnumerations == Collections.EMPTY_LIST) {
    		usedEnumerations = new ArrayList();    		
    	}
    	usedEnumerations.add(usedTableBinding);
    }
    
    public void setMainFormGroup(Set usedForms) {
    	this.usedForms = usedForms;
    }
    
    public boolean unqualifiedItemReferencesAreAllowed() {
    	return unqualifiedItemReferencesAreAllowed;
    }
    
    public boolean I4GLItemsNullableIsEnabled() {
    	return I4GLItemsNullableIsEnabled;
    }
    
    private List getRecordClassFields() {
    	if(recordClassFields == null) {
    		recordClassFields = new ArrayList();
    		for(Iterator iter = functionContainerBinding.getDeclaredData().iterator(); iter.hasNext();) {
    			IDataBinding nextVar = (IDataBinding) iter.next();
    			int nextVarTypeKind = nextVar.getType().getKind();
    			if(nextVarTypeKind == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
    			   nextVarTypeKind == ITypeBinding.FIXED_RECORD_BINDING ||
    			   nextVarTypeKind == ITypeBinding.ARRAY_TYPE_BINDING &&
				   nextVar.getType().getBaseType().getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
    				recordClassFields.add(nextVar);
    			}
    					
    		}
    	}
    	return recordClassFields;
    }
    
    private Map getLevel4Items() {
    	if(level4Items == null) {    		
    		// Level 4 items are...
    		level4Items = new HashMap();
    		
    		// Structure items/fields in...		
    		if(unqualifiedItemReferencesAreAllowed()) {
    			// Records in the global storage list
    			// Records in the parameter list
    			// Search for "resourceAssociation" in I/O record used by the program
    			for(Iterator iter = getRecordClassFields().iterator(); iter.hasNext();) {
            		IDataBinding nextRecVar = (IDataBinding) iter.next();

        			BindingUtilities.addAllToUnqualifiedBindingNameMap(level4Items, nextRecVar, nextRecVar.getType().getSimpleNamesToDataBindingsMap());        			
        			BindingUtilities.addResourceAssociationBindingToMap(level4Items, nextRecVar);
            	}
    			
    			// Forms used in I/O statements (post V6.0.1, these are the forms
    			// in the "usedForms" use statement property)
    			if(usedForms != null) {
    				for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
    					FormBinding nextForm = (FormBinding) iter.next();
        				BindingUtilities.addAllToUnqualifiedBindingNameMap(level4Items, nextForm.getStaticFormDataBinding(), nextForm.getSimpleNamesToDataBindingsMap());
    				}
    			}
    			
    			// Form in the parameter list
//    			for(Iterator iter = getFormClassFields().iterator(); iter.hasNext();) {
//    				IDataBinding nextFormVar = (IDataBinding) iter.next();
//					if(!usedForms.contains(nextFormVar.getType())) {
//						BindingUtilities.addAllToUnqualifiedBindingNameMap(level4Items, nextFormVar, ((FormBinding) nextFormVar.getType()).getSimpleNamesToDataBindingsMap());
//					}
//				}
    			
    			// InputForm for the Program (wasn't implemented in 5.12/6.0 resolver
    			// code. Is it needed here?)    			
    			
    			// Tables "USED"
    			for(Iterator iter = usedDataTables.iterator(); iter.hasNext();) {
    				DataTableBinding usedTable = (DataTableBinding) iter.next();
    				BindingUtilities.addAllToUnqualifiedBindingNameMap(level4Items, usedTable.getStaticDataTableDataBinding(), usedTable.getSimpleNamesToDataBindingsMap());
    			}
            }
    	}
    	return level4Items;
    }
    
    private Map getLevel5Items() {
    	if(level5Items == null) {    		
    		// Level 5 items are...
    		level5Items = new HashMap();
    		
    		// constants, items or records in a USED library
    		for(Iterator iter = usedLibraries.iterator(); iter.hasNext(); ) {
    			LibraryBinding nextLibrary = (LibraryBinding) iter.next();
    			for(Iterator dataIter = nextLibrary.getDeclaredData().iterator(); dataIter.hasNext();) {
    				ClassFieldBinding next = (ClassFieldBinding) dataIter.next();
    				if(!next.isPrivate()) {
    					BindingUtilities.addToUnqualifiedBindingNameMap(level5Items, nextLibrary.getStaticLibraryDataBinding(), next);
    				}
    			}
    			
    			Map functionNamesToBindings = new HashMap();
    			for(Iterator dataIter = nextLibrary.getDeclaredFunctions().iterator(); dataIter.hasNext();) {
    				NestedFunctionBinding next = (NestedFunctionBinding) dataIter.next();
    				
    				if(!next.isPrivate()) {
    					if(functionNamesToBindings.containsKey(next.getName())) {
    						IDataBinding existingFunction = (IDataBinding) functionNamesToBindings.get(next.getName());
    						OverloadedFunctionSet functionSet;
    						if(IDataBinding.NESTED_FUNCTION_BINDING == existingFunction.getKind()) {
    							functionSet = new OverloadedFunctionSet();
    							functionSet.setName(next.getCaseSensitiveName());
    							functionSet.addNestedFunctionBinding(existingFunction);    							    							
    						}
    						else {
    							functionSet = (OverloadedFunctionSet) existingFunction;    							
    						}
    						functionSet.addNestedFunctionBinding(next);
    						functionNamesToBindings.put(next.getName(), functionSet);    						
    					}
    					else {
    						functionNamesToBindings.put(next.getName(), next);
    					}
    				}
    			}
    			for(Iterator dataIter = functionNamesToBindings.values().iterator(); dataIter.hasNext();) {  					
					BindingUtilities.addToUnqualifiedBindingNameMap(level5Items, nextLibrary.getStaticLibraryDataBinding(), (IDataBinding) dataIter.next());
				}
	        }
    	}
    	return level5Items;
    }
    
    private Map getUsedEnumerationItems() {
    	if(usedEnumerationItems == null) {    		
    		// Level 5 items are...
    		usedEnumerationItems = new HashMap();
    		
    		// constants, items or records in a USED library
    		for(Iterator iter = usedEnumerations.iterator(); iter.hasNext(); ) {
    			EnumerationTypeBinding nextEnumeration = (EnumerationTypeBinding) iter.next();
    			for(Iterator dataIter = nextEnumeration.getEnumerations().iterator(); dataIter.hasNext();) {
    				EnumerationDataBinding next = (EnumerationDataBinding) dataIter.next();
    				BindingUtilities.addToUnqualifiedBindingNameMap(usedEnumerationItems, nextEnumeration.getStaticEnumerationTypeDataBinding(), next);
    			}
	        }
    	}
    	return usedEnumerationItems;
    }
    
    private Map getUsedLibraryFunctionBindings() {
    	if(usedLibraryFunctionBindings == null) {
    		usedLibraryFunctionBindings = new HashMap();
    		
    		for(Iterator iter = usedLibraries.iterator(); iter.hasNext(); ) {
    			LibraryBinding nextLibrary = (LibraryBinding) iter.next();
    			for(Iterator functionIter = nextLibrary.getDeclaredFunctions().iterator(); functionIter.hasNext();) {
    				BindingUtilities.addToUnqualifiedFunctionNameMap(usedLibraryFunctionBindings, (IFunctionBinding) ((NestedFunctionBinding) functionIter.next()).getType(), nextLibrary.getStaticLibraryDataBinding());
    			}
	        }
    	}
    	return usedLibraryFunctionBindings;
    }
    
	public Scope getScopeForKeywordThis() {
		return this;
	}
	
	public void stopReturningFunctionContainerFunctions() {
		super.stopReturningFunctionContainerFunctions();
		returnFunctionContainerFunctions = false;
	}
	
	public void startReturningFunctionContainerFunctions() {
		super.startReturningFunctionContainerFunctions();
		returnFunctionContainerFunctions = true;
	}
	
	public List getUsedLibraries() {
		return usedLibraries;
	}
	
	public IPartBinding getPartBinding() {
		return functionContainerBinding;
	}
	
	public Map getItemsWhoseNamesCanBeUnqualified() {		
		if(itemsWhoseNamesCanBeUnqualified == null) {
			itemsWhoseNamesCanBeUnqualified = new HashMap();
			
    		if(unqualifiedItemReferencesAreAllowed()) {
    			for(Iterator iter = getRecordClassFields().iterator(); iter.hasNext();) {
            		IDataBinding nextRecVar = (IDataBinding) iter.next();
            		
            		for(Iterator iter2 = nextRecVar.getType().getSimpleNamesToDataBindingsMap().values().iterator(); iter2.hasNext();) {
            			IDataBinding next = (IDataBinding) iter2.next();
            			itemsWhoseNamesCanBeUnqualified.put(next.getName(), next);
            		}
            	}
    			
    			if(usedForms != null) {
    				for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
    					FormBinding nextForm = (FormBinding) iter.next();
    					for(Iterator iter2 = nextForm.getSimpleNamesToDataBindingsMap().values().iterator(); iter2.hasNext();) {
                			IDataBinding next = (IDataBinding) iter2.next();
                			itemsWhoseNamesCanBeUnqualified.put(next.getName(), next);
                		}
    				}
    			}
    			
    			for(Iterator iter = usedDataTables.iterator(); iter.hasNext();) {
    				DataTableBinding usedTable = (DataTableBinding) iter.next();
    				for(Iterator iter2 = usedTable.getSimpleNamesToDataBindingsMap().values().iterator(); iter2.hasNext();) {
            			IDataBinding next = (IDataBinding) iter2.next();
            			itemsWhoseNamesCanBeUnqualified.put(next.getName(), next);
            		}
    			}
    		}
		}
		return itemsWhoseNamesCanBeUnqualified;
	}

	public Map getRecordsFormsAndDataTables() {
		if(recordsFormsAndDataTables == null) {
			recordsFormsAndDataTables = new HashMap();
			
			for(Iterator iter = getRecordClassFields().iterator(); iter.hasNext();) {
				IDataBinding next = (IDataBinding) iter.next();
				recordsFormsAndDataTables.put(next.getName(), next);
			}
			
			if(usedForms != null) {
				for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
					FormBinding nextForm = (FormBinding) iter.next();
					recordsFormsAndDataTables.put(nextForm.getName(), nextForm.getStaticFormDataBinding());
				}
			}
			
			for(Iterator iter = usedDataTables.iterator(); iter.hasNext();) {
				DataTableBinding usedTable = (DataTableBinding) iter.next();
				recordsFormsAndDataTables.put(usedTable.getName(), usedTable.getStaticDataTableDataBinding());
	        }
		}
		return recordsFormsAndDataTables;
	}
}
