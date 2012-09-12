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
package org.eclipse.edt.ide.ui.internal.record.conversion;

import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.ide.ui.templates.parts.Field;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;

public abstract class PartsUtil {
	static java.util.Map<String,String> _reservedConversionNames = new java.util.HashMap<String,String>();
	static {
		_reservedConversionNames.put("egl_value", "egl_value");//$NON-NLS-1$
	}
	static final String subChar = "";
	
	private java.util.HashMap<String,String> _partNames = new java.util.HashMap<String,String>();
	private int _aliasNumber = 0;
	protected IMessageHandler _msgHandler = null;

	protected PartsUtil( IMessageHandler msgHandler ) {
		_msgHandler = msgHandler;
	}
	private class NameValidatorProblemRequestor extends DefaultProblemRequestor
	{
		int _problemKind = 0;
		int _severity = 0;
		String[] _inserts = new String[0];
		ResourceBundle _bundle;
		
		public NameValidatorProblemRequestor(){
		}
		
		@Override
		public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
			_bundle = bundle;
			if (_problemKind ==0) {
				_inserts = shiftInsertsIfNeccesary(problemKind, inserts);
				_problemKind = problemKind;
				_severity = severity;
			}
			if (_severity > IMarker.SEVERITY_WARNING) {
				throw (new RuntimeException(getMessageFromBundle(problemKind, inserts, bundle)));
			}
		}
		
		public int getProblemKind()
		{
			return _problemKind;
		}
		
		public void setProblemKind( int problemKind)
		{
			_problemKind = problemKind;
		}
		
		public String[] getInserts()
		{
			return _inserts;
		}
		
		public ResourceBundle getBundle()
		{
			return _bundle;
		}
	}

	private String getName(String str, DefaultProblemRequestor problemRequestor) {
	
		EGLNameValidator.validate(str, EGLNameValidator.PART, problemRequestor, DefaultCompilerOptions.getInstance());
	
		return str;
	
	}

	public String getTypeName(String str) {
		NameValidatorProblemRequestor problemRequestor = new NameValidatorProblemRequestor();
		String name = fixName(str, null); // replace invalid characters with subChar
		boolean isValidName = false;
		while (!isValidName) {
			try {
				name = getName(name, problemRequestor);
				isValidName = true;
			}
			catch (RuntimeException ex) {
				if (problemRequestor.getProblemKind() == IProblemRequestor.RESERVED_WORD_NOT_ALLOWED) {
					String newName = name + subChar + (++_aliasNumber);
					_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_reservedWordRenameMessage,new String[] {name, getFinalType(newName)}));
					name = newName;
					problemRequestor.setProblemKind(0);
				} else {
					throw (new RuntimeException(DefaultProblemRequestor.getMessageFromBundle(problemRequestor.getProblemKind(), problemRequestor.getInserts(), problemRequestor.getBundle())));
				}
			}
			if (problemRequestor.getProblemKind() != 0) {
				throw (new RuntimeException(DefaultProblemRequestor.getMessageFromBundle(problemRequestor.getProblemKind(), problemRequestor.getInserts(), problemRequestor.getBundle())));
			}
		}
		return getFinalType(name);
	}

	protected void mergeRecords( Record oldrec, Record newrec ) {
		// add fields from oldrec to new rec marking the fields as nullable
		Field[] oldFields = oldrec.getFields();
		for (int i=0; i<oldFields.length; i++) {
			Field newField = newrec.getField(oldFields[i].getName());
			if (newField == null) {
				if (!oldFields[i].getType().isNullable() && !oldFields[i].getType().isReferenceType())
					_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_definedAsNullableMessage,new String[] {newrec.getName(), oldFields[i].getName()}));
				oldFields[i].getType().setNullable(true);
				newrec.addField(oldFields[i]);
			}
			else // propagate the old nullable setting to the new record field
				newField.getType().setNullable(oldFields[i].getType().isNullable());
		}
		
		// set nullable new record fields that are missing from the old record
		Field[] newFields = newrec.getFields();
		for (int i=0; i<newFields.length; i++) {
			if (oldrec.getField(newFields[i].getName()) == null) {
				if (!newFields[i].getType().isNullable() && !newFields[i].getType().isReferenceType())
					_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_definedAsNullableMessage,new String[] {newrec.getName(), newFields[i].getName()}));
				newFields[i].getType().setNullable(true);
			}
		}
	}

	public abstract Part[] process(Object node, Record wrapRec);

	private boolean isNameCollision( String name, boolean isOriginalName, java.util.HashMap<String,String> fieldNames )
	{
		boolean isCollision = false;
		// if (name has changed and the new name is the same as another field in the record
		// or the name collides with another name already processed
		// or the name collides with a reserved conversion name
		isCollision =  ((!isOriginalName && fieldNames.containsKey(name.toLowerCase())) ||
			_partNames.containsKey(getFinalName(name).toLowerCase()) || 
			_reservedConversionNames.containsKey(getFinalName(name).toLowerCase()));
			
		return isCollision;
	}
	
	protected String getFieldName(String str, String recordName, java.util.HashMap<String,String> fieldNames ) 
	{
		NameValidatorProblemRequestor problemRequestor = new NameValidatorProblemRequestor();
		String name = fixName(str, recordName); // replace invalid characters with subChar
		boolean isValidName = false;
		while (!isValidName) 
		{
			try 
			{
				if (isNameCollision(name, name.equals(str), fieldNames))
				{
					String newName = name + subChar + (++_aliasNumber);
					_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_renameFieldInUseMessage,new String[] {name, getFinalName(newName), recordName}));
					name = newName;
				}
				name = getName(name, problemRequestor);
				isValidName = true;
			}
			catch (RuntimeException ex) 
			{
				if (problemRequestor.getProblemKind() == IProblemRequestor.RESERVED_WORD_NOT_ALLOWED) 
				{
					String newName = name + subChar + (++_aliasNumber);
					_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_reservedWordRename2Message,new String[] {name, getFinalName(newName), recordName}));
					name = newName;
					problemRequestor.setProblemKind(0);
				} 
				else 
				{
					throw (new RuntimeException(DefaultProblemRequestor.getMessageFromBundle(problemRequestor.getProblemKind(), problemRequestor.getInserts(), problemRequestor.getBundle())));
				}
			}
			if (problemRequestor.getProblemKind() != 0) 
			{
				throw (new RuntimeException(DefaultProblemRequestor.getMessageFromBundle(problemRequestor.getProblemKind(), problemRequestor.getInserts(), problemRequestor.getBundle())));
			}
		}
		name = getFinalName(name);
		_partNames.put(name.toLowerCase(), name);
		return name;
	}

	private String getFinalName(String name) {
		if (name.equals(name.toUpperCase())) { // if all uppercase
			return name.toLowerCase(); // lowercase the whole thing
		}
		// for mixed case or all lowercase, lowercase the first letter.
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	private String getFinalType(String name) 
	{
		if (!name.equals(name.toUpperCase()))
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}

	private String fixName(String name, String recordName) {
		boolean isVAGCompatibility = false; //EGLVAGCompatibilitySetting.isVAGCompatibility();
	
		StringBuffer eName = new StringBuffer(name);
	
		// more special characters are allowed if VAGCompatibility is enabled
		if (!isVAGCompatibility) {
			if (!Character.isJavaIdentifierStart(eName.charAt(0))) {
				// '$' and '_' are already allowed by isJavaIdentifierStart
				eName.replace(1, 2, eName.substring(1, 2).toUpperCase());
				eName.replace(0, 1, subChar);
			}
	
			for (int i = 1; i < eName.length(); i++) {
				if (!Character.isJavaIdentifierPart(eName.charAt(i))) {
					// '$' and '_' are already allowed by isJavaIdentifierPart
					eName.replace(i+1, i + 2, eName.substring(i+1, i+2).toUpperCase());
					eName.replace(i, i + 1, subChar);
					i--; // backup and reprocess this character
				}
			}
		} else { // in VAGCompatibility mode
			if (!Character.isJavaIdentifierStart(eName.charAt(0))) {
				// '$' and '_' are already allowed by isJavaIdentifierStart
				eName.replace(1, 2, eName.substring(1, 2).toUpperCase());
				eName.replace(0, 1, subChar);
			}
	
			for (int i = 1; i < eName.length(); i++)  {
				if (!Character.isJavaIdentifierPart(eName.charAt(i))) {
					// '$' and '_' are already allowed by isJavaIdentifierPart
					if (eName.charAt(i) != '-' && eName.charAt(i) != '@' 
						  && eName.charAt(i) != '#' && eName.charAt(i) != ' ') {
						eName.replace(i+1, i + 2, eName.substring(i+1, i+2).toUpperCase());
						eName.replace(i, i + 1, subChar);
						i--; // reprocess that character
					}
				}
			}
		}
		String newName = eName.toString();
		if (!name.equalsIgnoreCase(newName))
		{
			_msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_invalidCharactersRenameMessage,new String[] {name, getFinalName(newName), ((recordName==null)?"":NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsUtil_invalidCharactersRename2Message,new String[] {recordName}))}));
		}
	
		return newName;
	}
}
