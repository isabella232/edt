/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.editor.CodeConstants;
import org.eclipse.edt.ide.ui.internal.util.CapabilityFilterUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLPropertyNameProposalHandler extends EGLAbstractProposalHandler {
	protected static final String NUMERIC_LITERAL = "numericLiteral"; //$NON-NLS-1$
	protected static final String LINE = "line"; //$NON-NLS-1$
	protected static final String LINES = "lines"; //$NON-NLS-1$
	protected static final String COLUMN = "column"; //$NON-NLS-1$
	protected static final String LENGTH = "length"; //$NON-NLS-1$
	protected static final String COLUMNS = "columns"; //$NON-NLS-1$
	protected static final String LOW_VALUE = "lowValue"; //$NON-NLS-1$
	protected static final String HIGH_VALUE = "highValue"; //$NON-NLS-1$
	protected static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
	protected static final String VARIABLE_NAME = "variableName"; //$NON-NLS-1$
	protected static final String TABLE_LABEL = "tableLabel"; //$NON-NLS-1$
	protected static final String VALIDATIONBYPASSKEYS_PFN_LIST_PROPOSAL = "pfn"; //$NON-NLS-1$

	protected static final String ROW_CONTENTS = "rowContents"; //$NON-NLS-1$
	protected static final String SQL_CONDITION = "condition"; //$NON-NLS-1$
	
	private boolean isAnnotationSetting;
	private boolean useAnnIcon;

	public EGLPropertyNameProposalHandler(ITextViewer viewer, int documentOffset, String prefix, boolean isAnnotationSetting, boolean useAnnIcon) {
		super(viewer, documentOffset, prefix, null);
		this.isAnnotationSetting = isAnnotationSetting;
		this.useAnnIcon = useAnnIcon;
	}

	public List getProposals(int location, List propertyBlockList) {
		Collection propertyRules = CapabilityFilterUtility.filterPropertyRules(EGLNewPropertiesHandler.getPropertyRules(location));
		return getProposals(location, propertyRules, propertyBlockList);
	}
	
	public List getProposals(Collection propertyRules, List propertyBlockList) {
		return getProposals(0, propertyRules, propertyBlockList);
	}
	
	public List getProposals(int location, Collection propertyRules, List propertyBlockList) {
		boolean vagCompatibility = EGLBasePlugin.getPlugin().getPreferenceStore().getBoolean(EGLBasePlugin.VAGCOMPATIBILITY_OPTION);
		List proposals = new ArrayList();		
		if (propertyRules != null) {
			for(Iterator iter = propertyRules.iterator(); iter.hasNext();) {
				EGLPropertyRule propertyRule = (EGLPropertyRule) iter.next();
				String propertyName = propertyRule.getName();
				if (propertyName.toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if (checkVagCompatibility(propertyName, vagCompatibility)) {
						if (!containsProperty(propertyName, propertyBlockList)) {
							proposals.add(createProposal(location, propertyRule));
						}
					}
				}
			}
		}
		return proposals;
	}

	private EGLCompletionProposal createProposal(int location, EGLPropertyRule propertyRule) {
		int cursorPosition = 0;
		int selectionLength = 0;
		
		StringBuffer buffer = new StringBuffer();
		if ( (propertyRule.isValueless()&&!isAnnotationSetting)|| 
			(propertyRule.isComplex()
			&& !isAnnotationSetting
			&& !propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_PRINTFLOATINGAREA)
			&& !propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENFLOATINGAREA))) {
			buffer.append("@"); //$NON-NLS-1$
		}
			
		buffer.append(propertyRule.getName());		
		if (propertyRule.isComplex() || propertyRule.hasType(EGLNewPropertiesHandler.nestedValue) || isAnnotationSetting) {
			if(!propertyRule.isValueless()){
				buffer.append(" {"); //$NON-NLS-1$
				cursorPosition = buffer.length();
				buffer.append("}"); //$NON-NLS-1$
			}
		}
		else {
			if (!propertyRule.isValueless()) {
				buffer.append(" = "); //$NON-NLS-1$
				
				//nameValue
				if (propertyRule.hasType(EGLNewPropertiesHandler.nameValue)) {
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_HELPKEY)) {
						buffer.append(VALIDATIONBYPASSKEYS_PFN_LIST_PROPOSAL);
						cursorPosition = buffer.length()-1;
						selectionLength = 1;
					}
					else
						cursorPosition = buffer.length();
				}
	
				//specificValue -  this has to be before quotedvalue for properties that have both values to work (ie dateFormat)
				else if (propertyRule.hasType(EGLNewPropertiesHandler.specificValue)) {
					//do not give a value because there are multiple possibilities
					cursorPosition = buffer.length();
				}
				
				//quotedValue
				else if (propertyRule.hasType(EGLNewPropertiesHandler.quotedValue)) {
					cursorPosition = buffer.length()+1;
					buffer.append("\"\""); //$NON-NLS-1$
				}
				
				//integerValue
				else if (propertyRule.hasType(EGLNewPropertiesHandler.integerValue)) {
					cursorPosition = buffer.length();
					buffer.append(NUMERIC_LITERAL);
					selectionLength = NUMERIC_LITERAL.length();
				}
				//literalValue
				else if (propertyRule.hasType(EGLNewPropertiesHandler.literalValue)) {
					//do not give a value because it can be string value or integer literal
					cursorPosition = buffer.length();
				}
				
				//listValue
				else if (propertyRule.hasType(EGLNewPropertiesHandler.listValue)) {
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_OUTLINE)) {
						cursorPosition = buffer.length();
					}
					else {
						buffer.append("["); //$NON-NLS-1$
						cursorPosition = buffer.length();
						if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_KEYITEMS)) {}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_PAGESIZE)) {
							buffer.append(LINES);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMNS);
							selectionLength = LINES.length();
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_POSITION)) {
							buffer.append(LINE);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMN);
							selectionLength = LINE.length();
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDVALUES)) {
							buffer.append(LOW_VALUE);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(HIGH_VALUE);
							selectionLength = LOW_VALUE.length();
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZE)) {
							buffer.append(LINES);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMNS);
							selectionLength = LINES.length();
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES)) {
							buffer.append("["); //$NON-NLS-1$
							cursorPosition = buffer.length();
							buffer.append(LINES);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMNS);
							selectionLength = LINES.length();
							buffer.append("]"); //$NON-NLS-1$
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_FORMSIZE)) {
							buffer.append(LINES);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMNS);
							selectionLength = LINES.length();
						}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS)) {}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS)) {}
						else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SEGMENTS)) {
							buffer.append("["); //$NON-NLS-1$
							cursorPosition = buffer.length();
							buffer.append(LINE);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(COLUMN);
							buffer.append(","); //$NON-NLS-1$
							buffer.append(LENGTH);
							selectionLength = LINE.length();
							buffer.append("]"); //$NON-NLS-1$
						}
						buffer.append("]"); //$NON-NLS-1$
					}
				}
				
				//contents
				else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_CONTENTS)) {
					String value = ""; //$NON-NLS-1$
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_CONTENTS))
						value = ROW_CONTENTS;
					buffer.append("[["); //$NON-NLS-1$
					cursorPosition = buffer.length();
					buffer.append(value);
					buffer.append("]]"); //$NON-NLS-1$
					selectionLength = value.length();
				}
				
				//sqlValue
				else if (propertyRule.hasType(EGLNewPropertiesHandler.sqlValue)) {
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_DEFAULTSELECTCONDITION)) {
						buffer.append(CodeConstants.EGL_SQL_CONDITION_PARTITION_START);
						buffer.append(" "); //$NON-NLS-1$
						cursorPosition = buffer.length();
						buffer.append(SQL_CONDITION);
						buffer.append(" }"); //$NON-NLS-1$
						selectionLength = SQL_CONDITION.length();
					}
				}
				
				//arrayOfArrays
				else if (propertyRule.hasType(EGLNewPropertiesHandler.arrayOfArrays)) {
					String value1 = ""; //$NON-NLS-1$
					String value2 = ""; //$NON-NLS-1$
					buffer.append("[["); //$NON-NLS-1$
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES)) {}  //screenSizes does not have double-quotes
					else
						buffer.append("\""); //$NON-NLS-1$
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMES)) {
						value1 = TABLE_NAME;
						value2 = TABLE_LABEL;
						selectionLength = TABLE_NAME.length();
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMEVARIABLES)) {
						value1 = VARIABLE_NAME;
						value2 = TABLE_LABEL;
						selectionLength = VARIABLE_NAME.length();
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES)) {
						value1 = LINES;
						value2 = COLUMNS;
						selectionLength = LINES.length();
					}
					cursorPosition = buffer.length();
					buffer.append(value1);
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES)) { //screenSizes does not have double-quotes
						buffer.append(", "); //$NON-NLS-1$
						buffer.append(value2);
					}
					else {
						buffer.append("\", \""); //$NON-NLS-1$
						buffer.append(value2);
						buffer.append("\""); //$NON-NLS-1$
					}
					buffer.append("]]"); //$NON-NLS-1$
				}
	
				//arrayOf
				else if (propertyRule.hasType(EGLNewPropertiesHandler.arrayOf)) {
					String value = ""; //$NON-NLS-1$
					buffer.append("[@"); //$NON-NLS-1$
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_LINKPARMS))
						value = IEGLConstants.PROPERTY_LINKPARAMETER;
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_HIERARCHY))
						value = IEGLConstants.PROPERTY_RELATIONSHIP;
					else {
						IAnnotationTypeBinding elementAnnotationType = propertyRule.getElementAnnotationType();
						if(Binding.isValidBinding(elementAnnotationType)) {
							value = elementAnnotationType.getCaseSensitiveName();
						}
					}
					buffer.append(value);
					buffer.append(" {"); //$NON-NLS-1$
					cursorPosition = buffer.length();
					buffer.append("}]"); //$NON-NLS-1$
				}
			}
		}
		
		String img_src = useAnnIcon ? PluginImages.IMG_OBJS_ANNOTATION : PluginImages.IMG_OBJS_ENV_VAR;
		return
			new EGLCompletionProposal(viewer,
				propertyRule.getName(),
				buffer.toString(),
				getAdditionalInfo(location, propertyRule.getName()),
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				cursorPosition,
				EGLCompletionProposal.RELEVANCE_MEDIUM,
				selectionLength,
				img_src);
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private boolean checkVagCompatibility(String propertyName, boolean vagCompatibility) {
		if (vagCompatibility)
			return true;
		return !(propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_DELETEAFTERUSE)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SQLDATACODE));
	}

	/**
	 * @param property
	 * @return
	 */
	private String getAdditionalInfo(int location, String propertyName) {
		if (location == EGLNewPropertiesHandler.locationUseDeclaration) {
			//give unique additional information to tell customer which part
			//types allow each program property
			if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HELPGROUP))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HELPKEY))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_PFKEYEQUATE))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_DELETEAFTERUSE))
				return UINlsStrings.CAProposal_PropertyNameDataTables;
			else
				return UINlsStrings.CAProposal_PropertyName;
		}
		else
			return UINlsStrings.CAProposal_PropertyName;
	}
}
