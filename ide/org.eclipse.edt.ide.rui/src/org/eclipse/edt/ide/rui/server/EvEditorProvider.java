/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.document.utils.DeleteWidgetDefinitionOperation;
import org.eclipse.edt.ide.rui.document.utils.DeleteWidgetReferenceOperation;
import org.eclipse.edt.ide.rui.document.utils.DocumentCache;
import org.eclipse.edt.ide.rui.document.utils.GetEventHandlerFunctionsOperation;
import org.eclipse.edt.ide.rui.document.utils.GetEventValueOperation;
import org.eclipse.edt.ide.rui.document.utils.GridLayoutOperation;
import org.eclipse.edt.ide.rui.document.utils.IVEConstants;
import org.eclipse.edt.ide.rui.document.utils.InsertEventHandlingFunctionOperation;
import org.eclipse.edt.ide.rui.document.utils.InsertFieldOperation;
import org.eclipse.edt.ide.rui.document.utils.InsertFunctionOperation;
import org.eclipse.edt.ide.rui.document.utils.InsertWidgetOperation;
import org.eclipse.edt.ide.rui.document.utils.InsertWidgetReferenceOperation;
import org.eclipse.edt.ide.rui.document.utils.MoveWidgetReferenceOperation;
import org.eclipse.edt.ide.rui.document.utils.NameGenerator;
import org.eclipse.edt.ide.rui.document.utils.RemoveEventValueOperation;
import org.eclipse.edt.ide.rui.document.utils.RemoveLayoutPropertyValueOperation;
import org.eclipse.edt.ide.rui.document.utils.RemovePropertyValueOperation;
import org.eclipse.edt.ide.rui.document.utils.SetEventValueOperation;
import org.eclipse.edt.ide.rui.document.utils.SetLayoutPropertyValueOperation;
import org.eclipse.edt.ide.rui.document.utils.SetPropertyValueOperation;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.utils.JavaScriptPreviewGenerator;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;

/**
 * This class provides an interface for the visual editor so it can ask he EGL editor's model classes to:
 * <ul>
 * <li>Request changes to the EGL model, such as adding, moving and creating a widget</li>
 * <li>Get and set widget property values</li>
 * <li>Obtain widget metadata</li>
 * <li>Obtain the names of the functions in the EGL source</li>
 * <li>Generate the web page from the EGL source</li>
 * <li>Obtain the HTML web page URL string</li>
 * <li>Obtain whether the web page exists for viewing</li>
 * <li>Adding a RUI handler</li>
 * </ul>
 */
public class EvEditorProvider {

	private static int						PROVIDER_KEY				= 0;
	public static final IPath				ROOT_GENERATION_DIRECTORY	= Activator.getDefault().getStateLocation();

	protected IEGLDocument					_currentDocument			= null;
	protected IFile							_currentFile				= null;
	protected EGLEditor						_eglEditor					= null;
	protected IPath							_generationDirectory		= null;
	protected WorkingCopyGenerationResult	_generationResult			= null;
	protected JavaScriptPreviewGenerator	_generator					= null;
	protected DocumentCache                 _documentCache              = null;
	protected IDocumentPartitioner 			_documentPartitioner		= null;
	
	public static IWorkbenchPage workbenchPage;

	public EvEditorProvider( EGLEditor eglEditor ) {
		PROVIDER_KEY += 1;
		_eglEditor = eglEditor;
		_currentFile = ( (IFileEditorInput)_eglEditor.getEditorInput() ).getFile();
		_currentDocument = (IEGLDocument)_eglEditor.getDocumentProvider().getDocument( _eglEditor.getEditorInput() );
		_generator = new JavaScriptPreviewGenerator( _currentFile );
		_generationDirectory = ROOT_GENERATION_DIRECTORY.append( Integer.toString( PROVIDER_KEY ) );
		_documentCache = new DocumentCache( _currentDocument, _currentFile );
		
		workbenchPage = eglEditor.getSite().getPage();
		
		// Cleanup this directory, in case it was being used before a product crash
		cleanupGeneratedJavaScript();
	}

	public void eglpathUpdated(){
		 // Create a new generator so that its eglpath is updated
	       _generator = new JavaScriptPreviewGenerator(_currentFile);
	}	

	/**
	 * Called by the editor after hasRUIHandler has returned false.  If a RUI handler is allowed, then true is returned.
	 * If false is returned, the editor will disable some of its user interface.
	 */
	public boolean canCreateRUIHandler() {
		//TODO Remove this
		return false;
	}

	public void cleanupGeneratedJavaScript() {
		File genDir = _generationDirectory.toFile();
		if( genDir.exists() ) {
			deleteFiles( genDir );
			genDir.delete();
		}
	}
	
	public IPath getGenerationDirectory(){
		return _generationDirectory;
	}
	
	/**
	 * Called by the editor to create a widget of a given type.
	 * A new widget of the specified type is inserted as a child of the specified parent statement offset and length at the specified index position.
	 * It is assumed that only valid widgets are passed to this API (e.g. passing a widget in a default package to a RUIHandler that is not in the default package
	 * will result in an error.)
	 */
	public void create( IProject widgetProject, String strWidgetPackage, String strWidgetType, int iStatementOffset, int iStatementLength, int iIndex, String widgetTemplate, String functionTemplate, String strWidgetName ) {
		_documentCache.clear();
		String fieldName = strWidgetName;
		
		if(fieldName == null){
			// The user wants the field name generated automatically
			NameGenerator generator = new NameGenerator( _currentFile );
			fieldName = generator.generateFieldName( strWidgetType );
		}

		if( fieldName != null ) {
			int insertIndex = 0;  // default to the 0th index
			try{
				IEGLFile modelFile = (IEGLFile)EGLCore.create( _currentFile );
				IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
				sharedWorkingCopy.reconcile(false, null);
			
				try{
					final String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
					IPart part = sharedWorkingCopy.getPart( partName );
					if(part.exists()){
						// Insert this field at the end of all existing fields
						insertIndex = part.getFields().length;
						
						if(iStatementOffset == 0 && iStatementLength == 0){
							// This field is being inserted at the Handler level
							// We need to find the offset and length of the handler so we can insert the reference in the Handler's Children field
							iStatementOffset = part.getSourceRange().getOffset();
							iStatementLength = part.getSourceRange().getLength();
						}
					}
				}finally{
					sharedWorkingCopy.destroy();
				}
			} catch (EGLModelException e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Create: Error creating new variable", e));
			}
			
			try {
				clearDocumentPartitioner();
				// Insert a reference to the widget that is about to be created.
				InsertWidgetReferenceOperation operation = new InsertWidgetReferenceOperation( _currentDocument, _currentFile );
				operation.insertWidgetReference( fieldName, iStatementOffset, iStatementLength, iIndex );
	
				// Insert the new field declaration
				InsertWidgetOperation insertWidgetOp = new InsertWidgetOperation( _currentDocument, _currentFile );
				insertWidgetOp.insertWidget( fieldName, strWidgetPackage, strWidgetType, widgetTemplate, insertIndex);
				
				// Insert FormManger function
				if(functionTemplate != null){
					InsertFunctionOperation insertFunctionOperation = new InsertFunctionOperation( _currentDocument, _currentFile );
					insertFunctionOperation.insertFunction(functionTemplate);
				}
			} finally {
				restoreDocumentPartitioner();
			}
		}
	}
	
	public void createField(String fieldTypePackage, String fieldName, String fieldType, String template){
		_documentCache.clear();
	
		int insertIndex = 0;  // default to the 0th index
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create( _currentFile );
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
			
			try{
				final String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
				IPart part = sharedWorkingCopy.getPart( partName );
				if(part.exists()){
					// Insert this field at the end of all existing fields
					insertIndex = part.getFields().length;
				}
			}finally{
				sharedWorkingCopy.destroy();
			}
		} catch (EGLModelException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Create: Error creating new variable", e));
		}
		
		InsertFieldOperation insertFieldOperation = new InsertFieldOperation(_currentDocument, _currentFile);
		insertFieldOperation.insertField(fieldTypePackage, fieldName, fieldType, template, insertIndex);
	}

	/**
	 * Called by the editor to create an event handling function with the given name. 
	 */
	public NestedFunction createEventHandlingFunction( String strName ) {
		//TODO Cleanup API
		InsertEventHandlingFunctionOperation operation = new InsertEventHandlingFunctionOperation( _currentDocument, _currentFile );
		operation.insertFunction( strName );

		return null;
	}

	/**
	 * Called by the editor to create a RUI handler after the editor has determined
	 * whether a RUI handler can be created via the canCreateRUIHandler method. 
	 */
	public void createRUIHandler() {
		//TODO Remove this
	}

	/**
	 * Called by the editor to delete a widget from the source.
	 * A parent with zero offset and length is a RUI handler.
	 */
	public int delete( int iParentStatementOffset, int iParentStatementLength, int iWidgetIndex ) {
		_documentCache.clear();
		if(iParentStatementOffset == 0 && iParentStatementLength == 0){
			try{
				IEGLFile modelFile = (IEGLFile)EGLCore.create( _currentFile );
				IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
				sharedWorkingCopy.reconcile(false, null);
			
				try{
					final String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
					IPart part = sharedWorkingCopy.getPart( partName );
					if(part.exists()){
						iParentStatementOffset = part.getSourceRange().getOffset();
						iParentStatementLength = part.getSourceRange().getLength();
					}
				}finally{
					sharedWorkingCopy.destroy();
				}
			} catch (EGLModelException e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Delete: Error deleting variable", e));
			}						
		}
		try {
			clearDocumentPartitioner();
			DeleteWidgetReferenceOperation deleteReferenceOperation = new DeleteWidgetReferenceOperation( _currentDocument, _currentFile );
			int deleted = deleteReferenceOperation.deleteWidgetReference( iParentStatementOffset, iParentStatementLength, iWidgetIndex );
			DeleteWidgetDefinitionOperation deleteDefinitionOperation = new DeleteWidgetDefinitionOperation( _currentDocument, _currentFile );
			String deletedWidgetName = deleteReferenceOperation.getDeletedWidgetName();
			if(deletedWidgetName.contains(DeleteWidgetDefinitionOperation.DELETE_WIDGET_NAME_SAPERATOR)){
				String[] deletedWidgetNames = deletedWidgetName.split(DeleteWidgetDefinitionOperation.DELETE_WIDGET_NAME_SAPERATOR);
				for(int i=0; i<deletedWidgetNames.length; i++){
					deleteDefinitionOperation.deleteWidgetDefinition( deletedWidgetNames[i] );
				}
			}else{
				deleteDefinitionOperation.deleteWidgetDefinition( deleteReferenceOperation.getDeletedWidgetName() );
			}
			return deleted;
		} finally {
			restoreDocumentPartitioner();
		}
	}

	private void deleteFiles( File directory ) {
		File[] files = directory.listFiles();
		
		for( int i = 0; i < files.length; i++ ) {
			if( files[ i ].isDirectory() ) {
				deleteFiles( files[ i ] );
			}
			files[ i ].delete();
		}
	}

	/**
	 * Generates the web page from the EGL source.
	 */
	public void generateJavaScript() {
		_generationResult = _generator.generateJavaScript( _generationDirectory );
	}

	/**
	 * Returns a default name for a new event handling function.
	 */
	public String getEventDefaultFunctionName( String strEventName ){
		NameGenerator generator = new NameGenerator( _currentFile );
		return generator.generateFieldName( strEventName );
	}
	
	/**
	 * Returns a list of available function names that could be the value of an event attribute.
	 */
	public String[] getEventHandlingFunctionNames() {
		GetEventHandlerFunctionsOperation operation = new GetEventHandlerFunctionsOperation( _currentDocument, _currentFile );
		return operation.getFunctions();
	}
	
	/**
	 * Returns the offset of the first line in the function.
	 */
	public int getFunctionFirstLineOffset( String functionName ) {
		GetEventHandlerFunctionsOperation operation = new GetEventHandlerFunctionsOperation( _currentDocument, _currentFile );
		return operation.getFunctionFirstLineOffset( functionName );
	}
	
	/**
	 * Returns range of the function name;
	 * The size of the return array is 2:
	 * The first value is the offset of the function name;
	 * The second value is the length of the function name.
	 */
	public int[] getFunctionNameRange( String functionName ) {
		GetEventHandlerFunctionsOperation operation = new GetEventHandlerFunctionsOperation( _currentDocument, _currentFile );
		return operation.getFunctionNameRange( functionName );
	}

	public WorkingCopyGenerationResult getLastGenerationResult() {
		return _generationResult;
	}

	/**
	 * Obtains the name of the function associated with a widget's event.  If the returned value has a length of zero or is null, then it
	 * is assumed that there is no function associated with the widget's event. 
	 */
	public EventValue getEventValue( int iStatementOffset, int iStatementLength, String strEventName ) {
		GetEventValueOperation operation = new GetEventValueOperation( _currentDocument, _currentFile );
		return operation.getEventValue( strEventName, iStatementOffset, iStatementLength );
	}

	/**
	 * Obtains the value of a property as a string.  If the returned value has a length of zero or is null, then it
	 * is assumed that the property is not currently specified. 
	 */
	public PropertyValue getPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType ) {
		return _documentCache.getPropertyValue(iStatementOffset, iStatementLength, strPropertyName, strPropertyType);
	}
	
	/**
	 * Obtains the value of the Layout property as a string.  If the returned value has a length of zero or is null, then it
	 * is assumed that the property is not currently specified. 
	 */
	public PropertyValue getLayoutPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType ) {
		return _documentCache.getLayoutPropertyValue(iStatementOffset, iStatementLength, strPropertyName, strPropertyType);
	}

	/**
	 * Returns a URL string that is the location of the web page that will be given to the design page and preview page browsers.
	 * http://localhost:5598/com.ibm.etools.egl.rui.samples/buttons/ManyButtons.html  
	 */
	public String getWebPageURL( String strPortNumber ) {
		StringBuffer result = new StringBuffer();
		IEGLFile modelFile = (IEGLFile)EGLCore.create( _currentFile );
		
		if(modelFile.exists()){
			String[] packageName = ((EGLFile)modelFile).getPackageName();
			result.append("http://localhost:");
			result.append(strPortNumber);  								// append the port number
			result.append("/");
			result.append(modelFile.getEGLProject().getElementName());  // append the project name
			result.append("/");
			result.append(JavaScriptAliaser.packageNameAlias(packageName, '/'));  // alias and append the package name
			result.append("/");
			// Alias and append the file name.  Replace .egl with .html
			result.append(JavaScriptAliaser.getAlias(new Path(modelFile.getElementName()).removeFileExtension().addFileExtension("html").toString()));
		}
		return result.toString().replaceAll( " ", "%20" );
	}

	/**
	 * Given a widget type, returns the default name for a new widget to be created
	 */
	public String getWidgetDefaultName( String strWidgetType ){
		NameGenerator generator = new NameGenerator( _currentFile );
		return generator.generateFieldName( strWidgetType );
	}
	
	/**
	 * Returns the name of the project that the widget is declared in, given the widget's package name and type name.
	 * This is to be implemented when we have more time.
	 */
	public String getWidgetProjectName( String strWidgetPackageName, String strWidgetTypeName ){
		IEGLProject project = EGLCore.create(_currentFile.getProject());
		
		try {
			IPart part = project.findPart(strWidgetPackageName, strWidgetTypeName);
			if(part != null && part.exists()){
				return part.getEGLProject().getElementName();
			}
		} catch (EGLModelException e) {
			// do nothing
		}
		return "";
	}

	/**
	 * Returns a list of widget names that are already in use.
	 */
	public List getWidgetNames(){
		List result = new ArrayList();
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(_currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				IField[] fields = modelPart.getFields();
				
				for (int i = 0; i < fields.length; i++) {
					IField field = fields[i];
					result.add(field.getElementName());
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Widget Namesr: Error getting field name", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Widget Names: Error building names list", e));
		}
		return result;
	}
	
	/**
	 * Returns a list of Function names that are already in use.
	 */
	public List getExistingFunctionNames(){
		List result = new ArrayList();
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(_currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				IFunction[] functions = modelPart.getFunctions();
				
				for (int i = 0; i < functions.length; i++) {
					IFunction function = functions[i];
					result.add(function.getElementName());
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "getFunctionNames: Error generating function name", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Function Names: Error building names list", e));
		}
		return result;
	}
	
	/**
	 * Called by the editor to determine whether a RUI handler exists.  If this method returns false,
	 * the editor will call canCreateRUIHandler.
	 */
	public boolean isVESupportType() {
		return Util.isVESupportType( _currentFile, _currentDocument );
	}
	
	public boolean isRUIWidget() {
		return Util.isRUIWidget( _currentFile );
	}
	
	public String getRUIWidgetId(){
		return Util.getRUIWidgetId( _currentFile );
	}

	
	/**
	 * Returns whether the given name can be a valid name for a widget (non reserved word)
	 */
	public boolean isValidName( String strName ){
		if ((strName == null) || (strName.length() == 0)){
			return false;
		}
		if (strName.length() > 128){
			return false;
		}
		ICompilerOptions compilerOptions = new ICompilerOptions() {
			public boolean isVAGCompatible() {
				return false;
			}
			public boolean isAliasJSFNames() {
				return false;
			}            
		};
		AccumulatingProblemrRequestor pRequestor = new AccumulatingProblemrRequestor();
		EGLNameValidator.validate(strName, EGLNameValidator.PART, pRequestor, compilerOptions);
		if(pRequestor.getProblems().size() != 0){
			return false;
		};
		return true;
	}
	
	/**
	 * Called by the editor to relocate a widget in the source from a given document position to a child index
	 * of a parent widget.  The parent could be the same as, or different from the existing parent.
	 * A parent that has zero offset and length is a RUI handler.
	 */
	public int[] move( int iSourceParentStatementOffset, int iSourceParentStatementLength, int iSourceChildIndex, int iTargetParentStatementOffset, int iTargetParentStatementLength, int iTargetChildIndex ) {
		_documentCache.clear();
		if( (iSourceParentStatementOffset == 0 && iSourceParentStatementLength == 0) || (iTargetParentStatementOffset == 0 && iTargetParentStatementLength == 0)){
			try{
				IEGLFile modelFile = (IEGLFile)EGLCore.create( _currentFile );
				IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
				sharedWorkingCopy.reconcile(false, null);
			
				try{
					final String partName = new Path(_currentFile.getName()).removeFileExtension().toString();
					IPart part = sharedWorkingCopy.getPart( partName );
					if(part.exists()){
						if(iSourceParentStatementOffset == 0 && iSourceParentStatementLength == 0){
							iSourceParentStatementOffset = part.getSourceRange().getOffset();
							iSourceParentStatementLength = part.getSourceRange().getLength();							
						}
						
						if(iTargetParentStatementOffset == 0 && iTargetParentStatementLength == 0){
							iTargetParentStatementOffset = part.getSourceRange().getOffset();
							iTargetParentStatementLength = part.getSourceRange().getLength();
						}						
					}
				}finally{
					sharedWorkingCopy.destroy();
				}
			} catch (EGLModelException e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Move: Error moving variable", e));
			}
		}
		
		try {
			clearDocumentPartitioner();
			MoveWidgetReferenceOperation operation = new MoveWidgetReferenceOperation( _currentDocument, _currentFile );
			return operation.moveWidget( iSourceParentStatementOffset, iSourceParentStatementLength, iSourceChildIndex, iTargetParentStatementOffset, iTargetParentStatementLength, iTargetChildIndex );
		} finally {
			restoreDocumentPartitioner();
		}
	}

	public void gridlayoutOpertion( int iSourceParentStatementOffset, int iSourceParentStatementLength, int row, int column, int operationType ) {
		_documentCache.clear();
		try {
			clearDocumentPartitioner();
			GridLayoutOperation operation = new GridLayoutOperation( _currentDocument, _currentFile );
			operation.doOpeation( iSourceParentStatementOffset, iSourceParentStatementLength, row, column, operationType );
		} finally {
			restoreDocumentPartitioner();
		}
	}

	/**
	 * Returns whether additional project dependencies need to be added to the project, given a widget type
	 * The method returns true if the widget type is defined in a project that is not being referenced by this project.
	 */
	public boolean requiresDependentProjects( IProject projectEditorInput, IProject projectDependency ){
		EGLProject eglProject = (EGLProject)EGLCore.create(projectEditorInput);
		return !(projectEditorInput.equals(projectDependency)) && !eglProject.isOnEGLPath(projectDependency);
	}
	
	/**
	 * Sets the event value of the given event name to the given value.  If the event value has a length of zero, or is null,
	 * then the event is removed. 
	 */
	public void setEventValue( int iStatementOffset, int iStatementLength, String strEventName, String strEventvalue ) {
		_documentCache.clear();
		if( strEventvalue != null && !"".equals( strEventvalue ) ) {
			SetEventValueOperation operation = new SetEventValueOperation( _currentDocument, _currentFile );
			operation.setEventValue( strEventName, strEventvalue, iStatementOffset, iStatementLength );
		}
		else {
			RemoveEventValueOperation operation = new RemoveEventValueOperation( _currentDocument, _currentFile );
			operation.removeEventValue( strEventName, iStatementOffset, iStatementLength );
		}
	}

	/**
	 * Sets the property value of the given property name to the given value.  If the property value has a length of zero, or is null,
	 * then the property is removed. 
	 */
	public int setPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType, PropertyValue propertyValue ) {
		_documentCache.clear();
		// Should only be called on editable properties...
		StringBuffer thePropertyValue = new StringBuffer();
		ArrayList values = propertyValue.values;
		if(IVEConstants.STRING_ARRAY_TYPE.equals(strPropertyType)){
			thePropertyValue.append("[");
			for (Iterator iterator = values.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				thePropertyValue.append("\"" + escape(value) + "\"");
				if(iterator.hasNext()){
					thePropertyValue.append(",");
				}
			}
			thePropertyValue.append("]");
		}else{
			if(IVEConstants.STRING_TYPE.equals(strPropertyType)){
				String theValue = (String)values.get(0);
				if(theValue.length() > 0){
					thePropertyValue.append("\"" + escape(theValue) + "\"");
				}
			}else if(IVEConstants.COLOR_TYPE.equals(strPropertyType)){
				String theValue = (String)values.get(0);
				if(theValue.length() > 0){
					thePropertyValue.append("\"" + theValue + "\"");
				}
			}else{
				// All other properties are returned as is
				thePropertyValue.append((String)values.get(0));
			}
		}
		int charactersChanged = 0;
		if(thePropertyValue.length() > 0){
			SetPropertyValueOperation operation = new SetPropertyValueOperation( _currentDocument, _currentFile );
			charactersChanged = operation.setPropertyValue( strPropertyName, thePropertyValue.toString(), strPropertyType, iStatementOffset, iStatementLength );
		}else {
			RemovePropertyValueOperation operation = new RemovePropertyValueOperation( _currentDocument, _currentFile );
			charactersChanged = operation.removePropertyValue( strPropertyName, iStatementOffset, iStatementLength );
		}
		return charactersChanged;
	}
	
	/**
	 * Sets the property value of the given property name to the given value.  If the property value has a length of zero, or is null,
	 * then the property is removed. 
	 */
	public int setLayoutPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType, PropertyValue propertyValue) {
		_documentCache.clear();
		// Should only be called on editable properties...
		StringBuffer thePropertyValue = new StringBuffer();
		ArrayList values = propertyValue.values;
		if(IVEConstants.STRING_ARRAY_TYPE.equals(strPropertyType)){
			thePropertyValue.append("[");
			for (Iterator iterator = values.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				thePropertyValue.append("\"" + escape(value) + "\"");
				if(iterator.hasNext()){
					thePropertyValue.append(",");
				}
			}
			thePropertyValue.append("]");
		}else{
			if(IVEConstants.STRING_TYPE.equals(strPropertyType)){
				String theValue = (String)values.get(0);
				if(theValue.length() > 0){
					thePropertyValue.append("\"" + escape(theValue) + "\"");
				}
			}else if(IVEConstants.COLOR_TYPE.equals(strPropertyType)){
				String theValue = (String)values.get(0);
				if(theValue.length() > 0){
					thePropertyValue.append("\"" + theValue + "\"");
				}
			}else{
				// All other properties are returned as is
				thePropertyValue.append((String)values.get(0));
			}
		}
		int charactersChanged = 0;
		if(thePropertyValue.length() > 0){
			SetLayoutPropertyValueOperation operation = new SetLayoutPropertyValueOperation( _currentDocument, _currentFile );
			charactersChanged = operation.setPropertyValue( strPropertyName, thePropertyValue.toString(), strPropertyType, iStatementOffset, iStatementLength );
		}else {
			RemoveLayoutPropertyValueOperation operation = new RemoveLayoutPropertyValueOperation( _currentDocument, _currentFile );
			charactersChanged = operation.removePropertyValue( strPropertyName, iStatementOffset, iStatementLength );
		}
		return charactersChanged;
	}
	
	public void clearCache() {
		_documentCache.clear();
	}

	private void clearDocumentPartitioner() {
		_documentPartitioner = _currentDocument.getDocumentPartitioner();
		_documentPartitioner.disconnect();
		_currentDocument.setDocumentPartitioner( null );
	}

	private void restoreDocumentPartitioner() {
		_currentDocument.setDocumentPartitioner( _documentPartitioner );
		_documentPartitioner.connect( _currentDocument );
	}
	
	private String escape(String value){
		String str = value;
		str = str.replaceAll("\\\\", "\\\\\\\\");
		str = str.replaceAll("\\\"", "\\\\\"");
		str = str.replaceAll("\\\b", "\\\\b");
		str = str.replaceAll("\\\f", "\\\\f");
		str = str.replaceAll("\\\n", "\\\\n");
		str = str.replaceAll("\\\r", "\\\\r");
		str = str.replaceAll("\\\t", "\\\\t");
		return str;
	}
}
