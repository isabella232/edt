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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.jface.text.ITextViewer;

public abstract class EGLAbstractPropertyReferenceCompletion extends EGLAbstractReferenceCompletion {
	protected ITextViewer viewer;
	protected int documentOffset;
	protected int partLocation;
	
	/**
	 * ONLY USEFUL IN CODE RUNNING WITHIN SPAN OF GETLOCATION METHOD BELOW
	 */
	protected ITypeBinding newTypeBinding;
	protected List settingsBlockList;
	
	protected int getLocation() {
		return getLocation(new IBoundNodeProcessor() {
			public void processBoundNode(Node node) {
			}
		});
	}
	
	protected int getLocation(final IBoundNodeProcessor additionalProcessor) {
		settingsBlockList = new ArrayList();
		final int[] result = new int[] {-1};
		getBoundASTNode(viewer, documentOffset, new String[] {"", "a};", "a", "}};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public boolean nodeIsValid(Node astNode) {
				boolean result = false;
				while(astNode != null && !result) {
					if(astNode instanceof SettingsBlock) {
						result = true;
					}
					astNode = astNode.getParent();
				}
				return result;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				if(boundNode instanceof Name && boundNode.getParent() instanceof SetValuesExpression) {
					boundNode = boundNode.getParent().getParent();
				}
			   
				//determine which property block this is.  Options are:
				while (boundNode != null) {					
					boundNode.accept(new DefaultASTVisitor() {
												
						public void endVisit(TopLevelFunction function) {
							result[0] = EGLNewPropertiesHandler.locationFunction;
							addAllSettingsBlocks(function.getContents(), settingsBlockList);
						}
						
						public void endVisit(Library library) {
							setNewTypeBindingFromPart(library);
							if(library.getName().resolveBinding().getAnnotation(EGLCORE, IEGLConstants.LIBRARY_SUBTYPE_NATIVE) != null) {
								result[0] = EGLNewPropertiesHandler.locationNativeLibrary;
							}
							else {
								result[0] = EGLNewPropertiesHandler.locationLibrary;
							}
							addAllSettingsBlocks(library.getContents(), settingsBlockList);
						}

						public void endVisit(Handler handler) {
							result[0] = EGLNewPropertiesHandler.locationHandler;
							addAllSettingsBlocks(handler.getContents(), settingsBlockList);
							setNewTypeBindingFromPart(handler);
						}
						
						public void endVisit(Program program) {
							result[0] = getProgramLocation(program);
							addAllSettingsBlocks(program.getContents(), settingsBlockList);
						}
						
						public void endVisit(Record record) {
							result[0] = getRecordLocation(record);
							addAllSettingsBlocks(record.getContents(), settingsBlockList);
						}
						
						public void endVisit(Service service) {
							result[0] = EGLNewPropertiesHandler.locationService;
							addAllSettingsBlocks(service.getContents(), settingsBlockList);
							setNewTypeBindingFromPart(service);
						}
						
						public void endVisit(Interface iFace) {
							result[0] = EGLNewPropertiesHandler.locationBasicInterface;
							addAllSettingsBlocks(iFace.getContents(), settingsBlockList);
							setNewTypeBindingFromPart(iFace);
						}
						
						public void endVisit(NestedFunction func) {
							result[0] = getNestedFunctionLocation(func);
							addAllSettingsBlocks(func.getStmts(), settingsBlockList);
						}
						
						public void endVisit(StructureItem structureItem) {
							result[0] = structureItem.isFiller() ?
								getFillerStructureItemLocation((IDataBinding) structureItem.resolveBinding()) :
								getStructureItemLocation((IDataBinding) structureItem.resolveBinding());
							settingsBlockList.add(structureItem.getSettingsBlock());
							Type type = structureItem.getType();
							if(type != null) {
								newTypeBinding = type.resolveTypeBinding();
							}
						}
						
						public void endVisit(ClassDataDeclaration classDataDeclaration) {
							result[0] = getVariableLocation(((Name) classDataDeclaration.getNames().get(0)).resolveDataBinding());
							settingsBlockList.add(classDataDeclaration.getSettingsBlockOpt());
						}
						
						public void endVisit(SetValuesExpression setValuesExpression) {
							result[0] = 0;
							newTypeBinding = setValuesExpression.resolveTypeBinding();
							settingsBlockList.add(setValuesExpression.getSettingsBlock());
						}
						
						public void endVisit(FunctionDataDeclaration dataDeclaration) {
							result[0] = getVariableLocation(((Name) dataDeclaration.getNames().get(0)).resolveDataBinding());
							settingsBlockList.add(dataDeclaration.getSettingsBlockOpt());
						}
						
						public void endVisit(CallStatement stmt) {
							result[0] = EGLNewPropertiesHandler.locationCall;
							settingsBlockList.add(stmt.getSettingsBlock());
						}

						public void endVisit(ExitStatement stmt) {
							result[0] = EGLNewPropertiesHandler.locationExit;
							settingsBlockList.add(stmt.getSettingsBlock());
						}
						
						public void endVisit(TransferStatement stmt) {
							result[0] = EGLNewPropertiesHandler.locationTransfer;
							settingsBlockList.add(stmt.getSettingsBlock());
						}
						
						public void endVisit(ShowStatement stmt) {
							result[0] = EGLNewPropertiesHandler.locationShow;
							settingsBlockList.add(stmt.getSettingsBlock());
						}
						
						public void endVisit(UseStatement useStatement) {
							result[0] = getUseStatementLocation(useStatement);
							settingsBlockList.add(useStatement.getSettingsBlock());
						}
						
						public void endVisit(NewExpression newExpression) {
							result[0] = EGLNewPropertiesHandler.locationNewExpression;
							settingsBlockList.add(newExpression.getSettingsBlock());
						}
						
						public void endVisit(ExternalType externalType) {
							result[0] = getExternalTypeLocation(externalType);
							addAllSettingsBlocks(externalType.getContents(), settingsBlockList);
						}
						
						private void setNewTypeBindingFromPart(Part part) {
							newTypeBinding = (ITypeBinding) part.getName().resolveBinding();
						}
						
					});
					
					if(result[0] != -1) {
						partLocation = result[0];
						additionalProcessor.processBoundNode(boundNode);
						return;
					}
					
					boundNode = boundNode.getParent();
				}
				
				if(result[0] != -1) {
					partLocation = result[0];
					additionalProcessor.processBoundNode(boundNode);
				}
			}
		});		
		if(result[0] != -1) {
			return result[0];
		}
		return 0;
	}
	
	private void addAllSettingsBlocks(List nodes, final List settingsBlockList) {
		for(Iterator iter = nodes.iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(new DefaultASTVisitor() {
				public boolean visit(SettingsBlock settingsBlock) {
					settingsBlockList.add(settingsBlock);
					return false;
				}
			});
		}		
	}
	
	private int getNestedFunctionLocation(NestedFunction func) {
		final int[] result = new int[] {0};
		func.getParent().accept(new DefaultASTVisitor() {
			public void endVisit(Library library) {
				boolean isNative = library.getName().resolveBinding().getAnnotation(EGLCORE, IEGLConstants.LIBRARY_SUBTYPE_NATIVE) != null;
				if(isNative) {
					result[0] = EGLNewPropertiesHandler.locationNativeLibraryFunction;
				}
			}
			public void endVisit(Service service) {
				result[0] = EGLNewPropertiesHandler.locationServiceFunction;
			}
			public void endVisit(Interface interfaceNode) {
				result[0] = EGLNewPropertiesHandler.locationBasicAbstractFunction;
			}
			public void endVisit(ExternalType externalType) {
				result[0] = EGLNewPropertiesHandler.locationExternalTypeFunction;
			}
		});
		return result[0];
	}

	private int getProgramLocation(Program program) {
		newTypeBinding = (ITypeBinding) program.getName().resolveBinding();
		if (program.isCallable()) {
			return EGLNewPropertiesHandler.locationCalledBasicProgram;
		}
		else {
			return EGLNewPropertiesHandler.locationBasicProgram;
		}
		
	}

	private int getRecordLocation(Record record) {
		newTypeBinding = (ITypeBinding) record.getName().resolveBinding();
		if(newTypeBinding.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_INDEXED) != null) {
			return EGLNewPropertiesHandler.locationIndexedRecord;
		}
		else if(newTypeBinding.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_RELATIVE) != null) {
			return EGLNewPropertiesHandler.locationRelativeRecord;
		}
		else if(newTypeBinding.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_SERIAL) != null) {
			return EGLNewPropertiesHandler.locationSerialRecord;
		}
		else if(newTypeBinding.getAnnotation(EGLIOSQL, IEGLConstants.RECORD_SUBTYPE_SQl) != null) {
			return EGLNewPropertiesHandler.locationSQLRecord;
		}
		else if(newTypeBinding.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_CSV) != null) {
			return EGLNewPropertiesHandler.locationCSVRecord;
		}
		else {
			return EGLNewPropertiesHandler.locationBasicRecord;
		}
	}

	private int getStructureItemLocation(IDataBinding structureItemBinding) {
		ITypeBinding enclosingType = getEnclosingType(structureItemBinding);
		if(enclosingType != null) {
			if(enclosingType.getAnnotation(EGLIOSQL, IEGLConstants.RECORD_SUBTYPE_SQl) != null) {
				return EGLNewPropertiesHandler.locationSqlItem;
			}
		}
		return EGLNewPropertiesHandler.locationStructureItem;
	}

	private int getExternalTypeLocation(ExternalType externalType) {
		IBinding binding = externalType.getName().resolveBinding();
		if(binding.getAnnotation(EGLIDLJAVA, IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT) != null) {
			return EGLNewPropertiesHandler.locationJavaObject;			
		}
		else if(binding.getAnnotation(EGLJAVASCRIPT, IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVASCRIPTOBJECT) != null) {
			return EGLNewPropertiesHandler.locationJavaScriptObject;
		}
		else if(binding.getAnnotation(EGLPLATFORM, IEGLConstants.EXTERNALTYPE_SUBTYPE_HOSTPROGRAM) != null) {
			return EGLNewPropertiesHandler.locationHostProgram;
		}
		return 0;
	}

	private int getFillerStructureItemLocation(IDataBinding structureItemBinding) {
		return EGLNewPropertiesHandler.locationFillerStructureItem;
	}

	private ITypeBinding getEnclosingType(IDataBinding dBinding) {
		return IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind() ?
			((StructureItemBinding) dBinding).getEnclosingStructureBinding() :
			dBinding.getDeclaringPart();
	}

	private int getUseStatementLocation(UseStatement useStatement) {
		if (useStatement.getNames().size() == 1) {
			UsedTypeBinding uBinding = useStatement.getUsedTypeBinding();
			switch(uBinding.getType().getKind()) {
				case ITypeBinding.FORMGROUP_BINDING:
					return EGLNewPropertiesHandler.locationFormGroupUseDeclaration;
				case ITypeBinding.DATATABLE_BINDING:
					return EGLNewPropertiesHandler.locationDataTableUseDeclaration;
				case ITypeBinding.FORM_BINDING:
					return EGLNewPropertiesHandler.locationFormUseDeclaration;
				case ITypeBinding.LIBRARY_BINDING:
					return EGLNewPropertiesHandler.locationLibraryUseDeclaration;
			}
		}
		else
			//properties not allowed if more than 1 use part is defined
			return 0;

		return EGLNewPropertiesHandler.locationUseDeclaration;
	}

	private int getVariableLocation(IDataBinding dataBinding) {
		if (dataBinding != null && IBinding.NOT_FOUND_BINDING != dataBinding) {
			newTypeBinding = dataBinding.getType();
			if (newTypeBinding != null && IBinding.NOT_FOUND_BINDING != newTypeBinding) {
				switch(newTypeBinding.getKind()) {
					case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					case ITypeBinding.FIXED_RECORD_BINDING:
						return getRecordtypeStaticLocation(newTypeBinding);
						
					case ITypeBinding.SERVICE_BINDING:
						return EGLNewPropertiesHandler.locationServiceDeclaration;
						
					case ITypeBinding.INTERFACE_BINDING:
						return EGLNewPropertiesHandler.locationInterfaceDeclaration;
						
					case ITypeBinding.ARRAY_TYPE_BINDING:
						return getStaticDynamicVariableLocation(dataBinding, newTypeBinding);
						
					case ITypeBinding.DICTIONARY_BINDING:
						return EGLNewPropertiesHandler.locationDictionary;
						
					case ITypeBinding.EXTERNALTYPE_BINDING:
						return EGLNewPropertiesHandler.locationNewExpression;
						
					case ITypeBinding.PRIMITIVE_TYPE_BINDING:
						if (dataBinding.getDeclaringPart().getKind() == ITypeBinding.SERVICE_BINDING)
							return EGLNewPropertiesHandler.locationServiceClassDeclaration;
						else if (dataBinding.getDeclaringPart().getKind() == ITypeBinding.EXTERNALTYPE_BINDING)
							return EGLNewPropertiesHandler.locationExternalTypeClassDeclaration;
						else
							return EGLNewPropertiesHandler.locationStaticItemDataDeclaration;
				}
			}
		
		}
		return 0;
	}
	
	private int getStaticDynamicVariableLocation(IDataBinding dBinding, ITypeBinding typeBinding) {
		ITypeBinding elementType = ((ArrayTypeBinding) typeBinding).getElementType();
		switch(elementType.getBaseType().getKind()) {
			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
				return EGLNewPropertiesHandler.locationDynamicItemDataDeclaration;
			case ITypeBinding.FIXED_RECORD_BINDING:
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				return getRecordtypeDynamicLocation(elementType);
			case ITypeBinding.HANDLER_BINDING:
				return EGLNewPropertiesHandler.locationExternalTypeArrayHandlerClassDeclaration;
		}
		return 0;
	}

	private int getRecordtypeDynamicLocation(ITypeBinding recordType) {
		if (recordType != null) {
			if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_INDEXED) != null) {
				return EGLNewPropertiesHandler.locationDynamicIndexedRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_RELATIVE) != null) {
				return EGLNewPropertiesHandler.locationDynamicRelativeRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_SERIAL) != null) {
				return EGLNewPropertiesHandler.locationDynamicSerialRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOSQL, IEGLConstants.RECORD_SUBTYPE_SQl) != null) {
				return EGLNewPropertiesHandler.locationDynamicSQLRecordDataDeclaration;
			}
			else {
				return EGLNewPropertiesHandler.locationDynamicBasicRecordDataDeclaration;
			}
		}
		return EGLNewPropertiesHandler.locationAnyRecord;
	}

	private int getRecordtypeStaticLocation(ITypeBinding recordType) {
		if (recordType != null) {
			if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_INDEXED) != null) {
				return EGLNewPropertiesHandler.locationStaticIndexedRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_RELATIVE) != null) {
				return EGLNewPropertiesHandler.locationStaticRelativeRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOFILE, IEGLConstants.RECORD_SUBTYPE_SERIAL) != null) {
				return EGLNewPropertiesHandler.locationStaticSerialRecordDataDeclaration;
			}
			else if(recordType.getAnnotation(EGLIOSQL, IEGLConstants.RECORD_SUBTYPE_SQl) != null) {
				return EGLNewPropertiesHandler.locationStaticSQLRecordDataDeclaration;
			}
			else {
				return EGLNewPropertiesHandler.locationStaticBasicRecordDataDeclaration;
			}
		}
		return EGLNewPropertiesHandler.locationAnyRecord;
	}

	/**
	 * @param viewer The viewer to set.
	 */
	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}
	/**
	 * @param documentOffset The documentOffset to set.
	 */
	public void setDocumentOffset(int documentOffset) {
		this.documentOffset = documentOffset;
	}
}
