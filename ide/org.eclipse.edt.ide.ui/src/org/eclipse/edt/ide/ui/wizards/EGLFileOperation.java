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
package org.eclipse.edt.ide.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EGLFileOperation extends WorkspaceModifyOperation {
    static protected String newLine = System.getProperty("line.separator"); //$NON-NLS-1$
	private EGLFileConfiguration configuration;
	protected IProject project;
	
	public EGLFileOperation(EGLFileConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
	
	public EGLFileOperation(EGLFileConfiguration configuration, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
		
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getProjectName());
		IEGLProject eproject = EGLCore.create(project);
		IPath sourcePath = new Path(configuration.getContainerName());
		IPackageFragmentRoot root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
		String packName = configuration.getFPackage();
		IPackageFragment frag = root.getPackageFragment(packName);
		
		IContainer container = (IContainer) frag.getResource();

		final IFile file = container.getFile(new Path(configuration.getFileName() + ".egl")); //$NON-NLS-1$
		
		if(file.exists())
		    updateExistingFile(root, frag, file, monitor);
		else
		    writeFileWithNewContent(root, frag, file, monitor);
		
		configuration.setFile(file);
		
		updateEGLPathIfNeeded(monitor);
	}
	
	/**
	 * overwrite file with new content wether or not the file already existed
	 * 
	 * @param root
	 * @param frag
	 * @param file
	 * @param monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	protected void writeFileWithNewContent(IPackageFragmentRoot root, IPackageFragment frag, IFile file, IProgressMonitor monitor)
		throws CoreException, InterruptedException
	{
		try {
			createFile(root, frag);
		}
		catch(CoreException e) {
			EGLLogger.log(this, e);
			throw e;
		}		
		catch(InterruptedException e0) {
			EGLLogger.log(this, e0);
		}

		try {
			String fileOutputString;
			String fileHeader = getFileHeader(configuration.getFPackage());
			String fileContents = getFileContents();
		
			fileOutputString = fileHeader + fileContents;
			
			String encoding = null;
			if(file.exists())
				encoding = file.getCharset();
			else
			{
				IContainer folder = (IContainer)frag.getResource();
				encoding = folder.getDefaultCharset();
			}
		
			InputStream stream  = new ByteArrayInputStream(encoding == null ? fileOutputString.getBytes():fileOutputString.getBytes(encoding));
			
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e1) {
			EGLLogger.log(this, e1);
		}	    
	}
	
	/**
	 * child class can override this method to do what it wants, this implmentation just calls the writeFileWithNewContent method
	 * so it will overwrite the existing file with the new content
	 * 
	 * @param root
	 * @param frag
	 * @param file
	 * @param monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	protected void updateExistingFile(IPackageFragmentRoot root, IPackageFragment frag, IFile file, IProgressMonitor monitor)
			throws CoreException, InterruptedException
	{
	    writeFileWithNewContent(root, frag, file, monitor);
	}
	
	protected String getFileHeader(String packName) {
		String fileContents = ""; //$NON-NLS-1$
		
		if(packName.compareTo("")!=0){ //$NON-NLS-1$
			fileContents = fileContents.concat("package " + packName + ";\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else{
			fileContents = ""; //$NON-NLS-1$
		}
		
		return fileContents;
	}
	
	protected String getFileContents() throws PartTemplateException {
		return NewWizardMessages.NewEGLFileWizardPageFilecontents;
	}
	  
	private void createFile(IPackageFragmentRoot root, IPackageFragment pack) throws CoreException, InterruptedException {	
		IEGLFile createdWorkingCopy= null;
		try {
			if (pack == null) {
				pack= root.getPackageFragment(""); //$NON-NLS-1$
			}
		
			if (!pack.exists()) {
				String packName= pack.getElementName();
				pack= root.createPackageFragment(packName, true, null);
			}			
			
			String fileOutputString=""; //$NON-NLS-1$
			pack.createEGLFile(configuration.getFileName() + ".egl", fileOutputString, true, new NullProgressMonitor()); //$NON-NLS-1$

		} finally {
			if (createdWorkingCopy != null) {
				createdWorkingCopy.destroy();
			}
		}
	}
	
	//utility function
	protected String RemoveRemainingTemplateTags(String outputString, String firstHalfOutputString, String secondHalfOutputString)
	{
		//Remove remaining template tags
		int tagStart;
		int tagEnd;
	
		while(outputString.indexOf("${")!=-1){ //$NON-NLS-1$
			tagStart = outputString.indexOf("${"); //$NON-NLS-1$
			tagEnd = outputString.indexOf("}", tagStart); //$NON-NLS-1$
	
			firstHalfOutputString = outputString.substring(0, tagStart);
			secondHalfOutputString = outputString.substring(tagStart + 2, outputString.length());
	
			outputString = firstHalfOutputString + secondHalfOutputString;
	
			firstHalfOutputString = outputString.substring(0, tagEnd -2);
			secondHalfOutputString = outputString.substring(tagEnd - 1, outputString.length());
			
			//Check for (and remove) cursor variable
			if(firstHalfOutputString.endsWith("cursor")) //$NON-NLS-1$
				firstHalfOutputString = firstHalfOutputString.substring(0, firstHalfOutputString.length()-6);
	
			outputString = firstHalfOutputString + secondHalfOutputString;
		}
		return outputString;
	}
	
	/**
	 * utiltiy method
	 * @param strPropName
	 * @param strPropValue
	 * @param addCommaAtBeginning
	 * @param addNewLine
	 * @return
	 */
    static protected boolean getPropertyValueString(boolean first, String strPropName, String strPropValue, boolean addNewLine, boolean addQuote, StringBuffer str)
    {
        if(strPropValue != null && strPropValue.length()>0)
        {
            if(!first)
                str.append(", "); //$NON-NLS-1$
            first = false;
            
            if(addNewLine)
                str.append(newLine);

            str.append(strPropName);
            str.append("="); //$NON-NLS-1$
            if(addQuote)
                str.append("\"");	//open quote //$NON-NLS-1$
            	
            str.append(strPropValue);
            
            if(addQuote)
                str.append("\"");	//close quote                 //$NON-NLS-1$
        }
        return first;
    }	
    
    static protected boolean getPropertyQuoteValueString(boolean first, String strPropName, String strPropValue, boolean addNewLine, StringBuffer str)
    {
        return getPropertyValueString(first, strPropName, strPropValue, addNewLine, true, str);
    }
    static protected boolean getPropertyBooleanValueString(boolean first, String strPropName, boolean booleanvalue, boolean addNewLine, StringBuffer str)
    {
        String strValue = Boolean.toString(booleanvalue);
        return getPropertyValueString(first, strPropName, strValue, addNewLine, false, str);
    }
    static protected boolean getPropertyBoolValueString(boolean first, String strPropName, boolean booleanvalue, boolean addNewLine, StringBuffer str)
    {
        String strYesNoValue = booleanvalue ? IEGLConstants.KEYWORD_YES : IEGLConstants.KEYWORD_NO;
        return getPropertyValueString(first, strPropName, strYesNoValue, addNewLine, false, str);
    }
        
    /**
     * utility method
     * @param strNameSpace
     * @param strElemName
     * @return
     */
    static protected StringBuffer getXMLAnnotationString(String strNameSpace, String strName)
    {    	
        StringBuffer str = new StringBuffer();
        if((strNameSpace != null && strNameSpace.length()>0) || (strName != null && strName.length()>0))
        {
	        str.append("{@"); //$NON-NLS-1$
	        str.append(IEGLConstants.PROPERTY_XML); //TODO add XML property constant
	        str.append(" {");	//open brace //$NON-NLS-1$
	        boolean firstProp = true;
	        firstProp = getPropertyQuoteValueString(firstProp, IEGLConstants.PROPERTY_NAME, strName, false, str);
	        firstProp = getPropertyQuoteValueString(firstProp, IEGLConstants.PROPERTY_NAMESPACE, strNameSpace, false, str);
		   	str.append("}}"); //closing brace //$NON-NLS-1$
        }
        return str;
    }
    
    static protected StringBuffer getXMLAnnotationString(IProperty[] xmlProperties){
    	StringBuffer str = new StringBuffer();
    	str.append("{");
    	str.append("@XML");
    	str.append("{");
    	str.append(getProperty(xmlProperties));
    	str.append("}");
    	str.append("}");
    	return str;
    }
    
    static private StringBuffer getProperty(IProperty[] properties){
    	StringBuffer str = new StringBuffer();
    	for(IProperty prop: properties){
    		boolean isNestedProp = false;
        	try {
    			if(prop.getValue() instanceof IProperty[]){
    				isNestedProp = true;
    			}
    			if(isNestedProp){
    				str.append("@");
    			}
    			str.append(prop.getElementName());
    			if(isNestedProp){
    				str.append("{");
    				str.append(getProperty((IProperty[])prop.getValue()));
    				str.append("}");
    			}
    			else{
    				str.append("=");
    				if(prop.getValueType() == IProperty.VALUE_TYPE_STRING){
    					str.append("\"");
    					str.append(prop.getValue());
    					str.append("\"");
    				}
    				else{
    					str.append(prop.getValue());
    				}
    			}
    		} catch (EGLModelException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	 	
    	return str;
    }
    
    protected void updateEGLPathIfNeeded(IProgressMonitor monitor) throws EGLModelException
    {
        if(configuration.isNeed2UpdateEGLPath())
        {
	        String currProjName = configuration.getProjectName();
	        String initialProjName = configuration.getInitialProjectName();
	        //will update the current project's EGL Path, add the initial project as a referenced project  
	        //if the current project differs from the initial project
	        //AND user wants to update egl path(by default, yes)
	        if(!currProjName.equals(initialProjName) && configuration.isUpdateEGLPath())
	        {
	            //get the current project's EGL Path
	            IProject currProject = configuration.fWorkspaceRoot.getProject(currProjName);
	            IEGLProject currEGLProj = EGLCore.create(currProject);
	            	            
	            IProject initialProj = configuration.fWorkspaceRoot.getProject(initialProjName);
	            IEGLPathEntry newEntry = EGLCore.newProjectEntry(initialProj.getFullPath());	    
	            //if it's not there, and by adding it won't cause circular link, we'll add to the egl path
	            if(!currEGLProj.isOnEGLPath(initialProj) && !currEGLProj.hasEGLPathCycle(new IEGLPathEntry[]{newEntry}))
	            {	                
		            IEGLPathEntry[] eglPathEntries = currEGLProj.getRawEGLPath();
		            int oldLen = eglPathEntries.length;
		            IEGLPathEntry[] newEGLPathEntries = new IEGLPathEntry[oldLen+1];
		            for(int i=0; i<oldLen; i++)
		                newEGLPathEntries[i] = eglPathEntries[i];		            
		            newEGLPathEntries[oldLen] = newEntry;
		            
		            //update the egl path
		            currEGLProj.setRawEGLPath(newEGLPathEntries, monitor);
	            }	            
	        }
        }
    }
	
	private void doUpdateEGLBuffer(IEGLFile file, int position, int length, String text) throws EGLModelException, CoreException
	{
		IBuffer buffer = file.getBuffer();
		buffer.replace(position, length, text);
	}
	
	protected void saveExistingFileWithNewBuffer(IPackageFragment frag, IEGLFile eglfile, IFile file, IProgressMonitor monitor) throws CoreException
	{
	    IBuffer buffer = eglfile.getBuffer();
	    String newFileContents = buffer.getContents();
		buffer.close();
				
		frag.createEGLFile(file.getName(), newFileContents, true, new NullProgressMonitor()); //$NON-NLS-1$
		
		String encoding=null;
		if(file.exists())
			encoding = file.getCharset();
		else		//get the folder's default char set
		{
			IContainer folder = (IContainer) frag.getResource();
			encoding = folder.getDefaultCharset();
		}
		try {
			InputStream stream = new ByteArrayInputStream(encoding == null ? newFileContents.getBytes() : newFileContents.getBytes(encoding));
			if(file.exists())
			    file.setContents(stream, true, true, monitor);		
            stream.close();
        } catch (IOException e) {
			EGLLogger.log(this, e);	                
        }	        
	}
    
	/**
	 * This method will check for duplicate imports, will not add duplicate imports
	 * @param file
	 * @param lstImportElemName		- list of String, each element is the import element name without ; at the end
	 * 									i.e. for "import a.b.c;" the element is "a.b.c" 								
	 * @throws EGLModelException
	 * @throws CoreException
	 */
	protected void addImports(IEGLFile file, List lstImportElemName) throws EGLModelException, CoreException
	{
		StringBuffer importStringBuffer = new StringBuffer();
		if (file != null)
		{
			IImportDeclaration[] imports = file.getImports();
			ISourceRange range = null;
			IEGLElement[] elements = file.getChildren();
			IEGLElement importBlock = null;
			IEGLElement packageBlock = null;
			
			//Make sure not to add dupes
			Iterator it = lstImportElemName.iterator();
			while(it.hasNext())
			{
			    String newImportElemName = (String)(it.next());
			    boolean fndDup = false;
				for(int i=0; i< imports.length && !fndDup; i++)
				{
					String existingElementName = imports[i].getElementName();
					if(newImportElemName.equalsIgnoreCase(existingElementName))
					    fndDup = true;
				}
				if(!fndDup)
				{
				    importStringBuffer.append(IEGLConstants.KEYWORD_IMPORT);
				    importStringBuffer.append(" "); //$NON-NLS-1$
				    importStringBuffer.append(newImportElemName);
				    importStringBuffer.append(";"); //$NON-NLS-1$
				    importStringBuffer.append(newLine);
				}			        
			}
											
			// Try to find the property block
			for(int i=0; i<elements.length; i++)
			{
				if(elements[i].getElementType() == IEGLFile.PACKAGE_DECLARATION)
				{
					packageBlock = elements[i];
					continue;
				}
				if(elements[i].getElementType() == IEGLFile.IMPORT_CONTAINER)
				{
					importBlock = elements[i];
					break;
				}
			}
			int rangeOffset = 0;
			if(importBlock != null)
			{
				// The block was found
				range = ((IImportContainer)importBlock).getSourceRange();
				rangeOffset = (range != null) ? range.getOffset() : 0;
			}
			else if (packageBlock != null)
			{
				// We have no imports
				// insert after the package
				range = ((ISourceReference)packageBlock).getSourceRange();
				rangeOffset = (range != null) ? range.getOffset() + range.getLength() : 0;
			}
			
			String importString = importStringBuffer.toString(); 
			if(importString.length()>0)
			    doUpdateEGLBuffer(file, rangeOffset, 0, importString);
		}
	}    
	
	/**
	 * The strategy here is to always insert after the property block or teh name (if no property block is found)
	 * @param insertionString
	 * @throws CoreException
	 */	
	protected void insertMemeberPart(IEGLFile eglfile, IPart sourcePart, String insertionString) throws CoreException 
	{		
		if(sourcePart != null)
		{
			StringBuffer insertStringBuf = new StringBuffer(insertionString); 
			try {
				ISourceRange range = null;
				IEGLElement[] elements = sourcePart.getChildren();
				IEGLElement propBlock = null;
						
				// Try to find the property block
				for(int i=0; i<elements.length; i++)
				{
					if(elements[i].getElementType() == IEGLFile.PROPERTY_BLOCK)
					{
						propBlock = elements[i];
						break;
					}
				}
				int insertposition = 0;
				if(propBlock != null)
				{
					// The block was found
					range = ((IMember)propBlock).getSourceRange();
					String propBlockSource = ((IMember)propBlock).getSource(); 
	
					// whatever whitespace is between the property block and the first element in the CBF is going to appear
					// between the property block and our newly inserted field.  Make sure we insert at least one tab between the two
					// if none is found at the end.
					if(propBlockSource.charAt(propBlockSource.length() - 1) != '\t')
					{
						insertStringBuf.insert(0, "\t"); //$NON-NLS-1$
					}
					insertposition = range.getOffset() + range.getLength();
				}
				else
				{
					insertposition = getInsertPosition(eglfile, sourcePart, insertStringBuf);				
				}
				
				// If we are inserting the first field, put a new line after it.
				//if(fields.length == 0)
				{
					insertStringBuf.append("\n");  //$NON-NLS-1$
				}
				
				// Insert a trailing tab if we are not inserting the first element in the page handler
				// (other than the property block).  Since we are always inserting at the top, if there
				// are elements other than the property block in the page handler, they will be below this
				// one after insertion. Do not insert the trailing tab if we don't have a property block,
				// since we are using the name range to figure out the insertion and it doesn't eat the trailing
				// tabs.
				if(propBlock != null && elements.length > 1)
				{
					insertStringBuf.append("\t"); //$NON-NLS-1$
				}
				
				//if(range != null)
				if(insertposition > 0)
				{
				    doUpdateEGLBuffer(eglfile, insertposition, 0, insertStringBuf.toString());
				}
			} catch (EGLModelException e) {
			}
		}
	}

	/**
	 * Child class should overwrite this method
	 * @param eglfile
	 * @param sourcePart
	 * @param insertStringBuf
	 * @return
	 * @throws EGLModelException
	 */
	protected int getInsertPosition(IEGLFile eglfile, IPart sourcePart, StringBuffer insertStringBuf) throws EGLModelException {
	    return 0;
	}
	
 }
