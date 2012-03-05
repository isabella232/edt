/*******************************************************************************
 * Copyright 漏 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.services.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.Services;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLFileOperation;
import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;

public class ServiceOperation extends EGLFileOperation {
	
	private ServiceConfiguration configuration;
	private List interfaces;
	private List calledPrograms;

	/**
     * @param configuration
     */
    public ServiceOperation(ServiceConfiguration configuration, List interfaces, List programs) {
        super(configuration);
        this.configuration = configuration; 
        this.interfaces = interfaces;
        this.calledPrograms = programs;
    }
    
    public ServiceOperation(ServiceConfiguration configuration, List interfaces, List programs, ISchedulingRule rule) {
		super(configuration, rule);
        this.configuration = configuration; 
        this.interfaces = interfaces;
        this.calledPrograms = programs;
    }
    
    private String getFileContentsFromTemplate()  throws PartTemplateException {        
		String serviceOutputString = ""; //$NON-NLS-1$
		String templateName = "service"; //$NON-NLS-1$
		String templateid = "org.eclipse.edt.ide.ui.templates.service"; //$NON-NLS-1$
		
		String templateVariableInterfaceName = "${serviceName}"; //$NON-NLS-1$
		
		//Determine the template to use
		if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_CUSTOM){
		    serviceOutputString = TemplateEngine.getCustomizedTemplateString(templateName, templateid);	
		} else if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT){
		    serviceOutputString = TemplateEngine.getDefaultTemplateString(templateName, templateid);
		}
		
		//Check for error condition (no template found / template disabled)
		if(serviceOutputString.compareTo("")!=0) { //$NON-NLS-1$    
			//Find and replace variables
			int variableBegin;
		
			String firstHalfOutputString;
			String secondHalfOutputString;
		
			String newVariableValue = configuration.getServiceName();
			variableBegin = serviceOutputString.indexOf(templateVariableInterfaceName);
			if(variableBegin == -1) {
				throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_CORRUPTED);
			}
			
			firstHalfOutputString = serviceOutputString.substring(0, variableBegin);
			secondHalfOutputString = serviceOutputString.substring(variableBegin + templateVariableInterfaceName.length(), serviceOutputString.length());
		
			serviceOutputString = firstHalfOutputString + newVariableValue + secondHalfOutputString;
			serviceOutputString = RemoveRemainingTemplateTags(serviceOutputString, firstHalfOutputString, secondHalfOutputString);
			
			return serviceOutputString;
		} else {
			throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_NOT_FOUND);
		}
			
	}
    
    private String getServiceStringFromInterfaces() {
        StringBuffer strBuf = new StringBuffer();
        
        StringBuffer strImportSection = new StringBuffer();
        StringBuffer strServiceName = new StringBuffer();
        StringBuffer strServiceImpl = new StringBuffer();
        StringBuffer strServiceBody = new StringBuffer();
        
		String myServicePkgName = configuration.getFPackage();
		
		strServiceName.append(newLine);
		strServiceName.append(IEGLConstants.KEYWORD_SERVICE); //$NON-NLS-1$
		strServiceName.append(" "); //$NON-NLS-1$
		strServiceName.append(configuration.getServiceName());

        int iSize = interfaces.size();
        if(iSize>0) {
			strServiceImpl.append(" "); //$NON-NLS-1$
			strServiceImpl.append(IEGLConstants.KEYWORD_IMPLEMENTS);
			strServiceImpl.append(" "); //$NON-NLS-1$
        }
        
		Iterator it = interfaces.iterator();
		HashSet<String> functionSet = new HashSet<String>();
		while (it.hasNext()) {
		    String interfaceFQName = (String)it.next();
		    Object interfaceobj = configuration.getInterface(interfaceFQName);
		    if(interfaceobj != null && interfaceobj instanceof IPart) {	//if found
		        IPart interfacepart = (IPart)interfaceobj;
		        
		        //------ import section if differs from the current package
		        if(!myServicePkgName.equalsIgnoreCase(interfacepart.getPackageFragment().getElementName())) {
		            strImportSection.append(IEGLConstants.KEYWORD_IMPORT);
		            strImportSection.append(" "); //$NON-NLS-1$
		            strImportSection.append(interfaceFQName);
		            strImportSection.append(";"); //$NON-NLS-1$
		            strImportSection.append(newLine);
		        }
		        
		        //------ implements section
		        strServiceImpl.append(interfacepart.getElementName());
		        if(it.hasNext())
		            strServiceImpl.append(", "); //$NON-NLS-1$
		        else
		            strServiceImpl.append(newLine);
		        
		        //------- function body section
		        if(interfacepart instanceof BinaryPart) {
		        	try {
						IProperty[] properties = ((BinaryPart)interfacepart).getProperties("XML");
						if(properties != null) {
							strServiceImpl.append(getXMLAnnotationString(properties));
						}
					} catch (EGLModelException e) {
						e.printStackTrace();
					}
		        	strServiceBody.append(geAllFunctionsBody(interfacepart, functionSet));
		        } else {
			        Interface boundInterfacePart = getBoundInterfacePart(interfacepart);
			        strServiceBody.append(geAllFunctionsBody(boundInterfacePart, functionSet));		 
			        
			        if(iSize == 1) {
			            //add the xml annotation of the interface to the service, 
			            //if the service implements more than one interface, then do not create the wsdl annotation for service.		            
			            IBinding interfaceNameBinding = boundInterfacePart.getName().resolveBinding();
			            if(interfaceNameBinding != null && interfaceNameBinding != IBinding.NOT_FOUND_BINDING) {		            	
			            	StringBuffer nameVal = new StringBuffer();
			            	StringBuffer namespaceVal = new StringBuffer();
			            	EGLFileConfiguration.getXMLAnnotationValueFromBinding(interfaceNameBinding, nameVal, namespaceVal);		            
			                strServiceImpl.append(getXMLAnnotationString(namespaceVal.toString(), nameVal.toString()));
			            }
			        }
		        }
		    }
		}
		
		strServiceBody.append(getProgramCallFunctionContent(myServicePkgName, functionSet));
				
		strBuf.append(strImportSection);
		strBuf.append(newLine);
		strBuf.append(strServiceName);
		strBuf.append(strServiceImpl);
		strBuf.append(strServiceBody);
		strBuf.append(newLine);
		strBuf.append(IEGLConstants.KEYWORD_END);
		
		return strBuf.toString();
    }
    
    private StringBuffer getProgramCallFunctionContent(String myServicePkgName, HashSet<String> functionSet)  {
        StringBuffer strbuf = new StringBuffer();
        Iterator itpgms = calledPrograms.iterator();
		while(itpgms.hasNext()) {		   
		    String programFQName = (String)(itpgms.next());
		    IPart programpart = configuration.getCalledBasicPgm(programFQName);
		    if(programpart != null) {	//if found    
		        //create program call function
		    	if(programpart instanceof SourcePart) {
		    		Program pgmProgram = configuration.getBoundCalledBasicProgramPgm(programFQName);
		    		strbuf.append(getProgramCallFunction(programFQName, pgmProgram, myServicePkgName, 
		    				programpart, functionSet));
		    	} else if(programpart instanceof BinaryPart) {
		    		BinaryPart pgmProgram = configuration.getBinaryCalledBasicProgramPgm(programFQName);
		    		strbuf.append(getProgramCallFunction(programFQName, pgmProgram, myServicePkgName, 
		    				programpart, functionSet));
		    		
		    	}
		    }
		}
		return strbuf;
    }
    
    private String getUniqueFunctionName(HashSet<String> functionSet, String proposedFunctionName) {
        int iCnt = 0; 
        String uniqueName = proposedFunctionName;
        while(functionSet.contains(uniqueName)) {
            iCnt++;
            uniqueName = proposedFunctionName + Integer.toString(iCnt); 
        }
        
        return uniqueName;
    }
    
    private StringBuffer getProgramCallFunction(String programFQName, Program pgmProgram, 
    		final String currFilePkg, IPart programpart, HashSet<String> functionSet) {
    	
        final StringBuffer strbuf = new StringBuffer();
        final StringBuffer strbufParamList = new StringBuffer();
        strbuf.append(newLine);
        strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append(IEGLConstants.KEYWORD_FUNCTION);
	    strbuf.append(" "); //$NON-NLS-1$

	    String functionname = pgmProgram.getName().getIdentifier();		//get the program simple name
	    functionname += "_function"; //$NON-NLS-1$
	    functionname = getUniqueFunctionName(functionSet, functionname);			//get the unique name in case there are same propgram name(but different package name)	    
	    strbuf.append(functionname);
	    functionSet.add(functionname);
	    
	    final boolean[] first = new boolean[]{true};
	    pgmProgram.accept(new DefaultASTVisitor() {
	    	public boolean visit(Program program) {
	    		strbuf.append("("); //$NON-NLS-1$	    		
	    		return true;
	    	}
	    	
	    	public boolean visit(ProgramParameter programParameter) {
	    		if(!first[0]) {
                    strbuf.append(", "); //$NON-NLS-1$
                    strbufParamList.append(", "); //$NON-NLS-1$
                }
	    		
                String paramName = programParameter.getName().getIdentifier();		//get the simple name	                
                String paramFQTypeName = ExtractInterfaceConfiguration.getQualifiedTypeString(programParameter, currFilePkg, true);
                strbuf.append(paramName);
                strbuf.append(" "); //$NON-NLS-1$
                strbuf.append(paramFQTypeName);
                
                strbufParamList.append(paramName);
                first[0] = false;
                return false;
	    	}
	    	
	    	public void endVisit(Program program) {
	    		strbuf.append(")"); //$NON-NLS-1$
	    	}
	    });

	    strbuf.append(newLine);
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append(IEGLConstants.KEYWORD_CALL);
	    strbuf.append(" "); //$NON-NLS-1$
	    if(!currFilePkg.equalsIgnoreCase(programpart.getPackageFragment().getElementName()))	    	           
	        strbuf.append(programFQName);				//use fully qualified program name
	    else
	        strbuf.append(pgmProgram.getIdentifier());		//use the program simple name since it's the same pacakge
	    strbuf.append("("); //$NON-NLS-1$
	    strbuf.append(strbufParamList);
	    strbuf.append(")");	//$NON-NLS-1$
	    strbuf.append(';');
	    strbuf.append(newLine);
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append(IEGLConstants.KEYWORD_END);
	    strbuf.append(newLine);
	    return strbuf;
    }
    
    private StringBuffer getProgramCallFunction(String programFQName, BinaryPart pgmProgram,
    		final String currFilePkg, IPart programpart, HashSet<String> functionSet){
    	final StringBuffer strbuf = new StringBuffer();
    	final StringBuffer strbufParamList = new StringBuffer();
    	strbuf.append(newLine);
        strbuf.append("\t"); //$NON-NLS-1$
        strbuf.append(IEGLConstants.KEYWORD_FUNCTION);
	    strbuf.append(" "); //$NON-NLS-1$
	    
	    String functionname = pgmProgram.getElementName();		//get the program simple name
	    functionname += "_function"; //$NON-NLS-1$
	    functionname = getUniqueFunctionName(functionSet, functionname);			//get the unique name in case there are same propgram name(but different package name)	    
	    strbuf.append(functionname);
	    functionSet.add(functionname);	    
	    strbuf.append("(");
	    SourcePartElementInfo pgmElementInfo;
		try {
			pgmElementInfo = (SourcePartElementInfo)(pgmProgram.getElementInfo());
			if(pgmElementInfo.getParameterNames() != null){
				for(int i=0; i<pgmElementInfo.getParameterNames().length; i++) {
					if(i > 0) {
						strbuf.append(", ");
						strbufParamList.append(", ");
					}
					strbuf.append(pgmElementInfo.getParameterNames()[i]);
					strbufParamList.append(pgmElementInfo.getParameterNames()[i]);
					strbuf.append(" ");
					strbuf.append(pgmElementInfo.getParameterTypeNames()[i]);
				}
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}	 
	    strbuf.append(")");
    
	    strbuf.append(newLine);
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append(IEGLConstants.KEYWORD_CALL);
	    strbuf.append(" "); //$NON-NLS-1$
	    if(!currFilePkg.equalsIgnoreCase(programpart.getPackageFragment().getElementName()))	    	           
	        strbuf.append(programFQName);				//use fully qualified program name
	    else
	        strbuf.append(pgmProgram.getElementName());		//use the program simple name since it's the same pacakge
	    strbuf.append("("); //$NON-NLS-1$
	    strbuf.append(strbufParamList);
	    strbuf.append(")");	//$NON-NLS-1$
	    strbuf.append(';');
	    strbuf.append(newLine);
	    strbuf.append("\t"); //$NON-NLS-1$
	    strbuf.append(IEGLConstants.KEYWORD_END);
	    strbuf.append(newLine);
    	return strbuf;
    }
    
    private StringBuffer geAllFunctionsBody(Interface boundInterface, final HashSet<String> functionSet) {
        final StringBuffer strFuncs = new StringBuffer();
        
        boundInterface.accept(new DefaultASTVisitor() {
        	public boolean visit(Interface interfaceNode) {return true;}
        	
        	public boolean visit(NestedFunction nestedFunction) {
        		if(!nestedFunction.isPrivate()) { //interface can only have public functions
        			strFuncs.append(getFunctionBody(nestedFunction, functionSet));
        			strFuncs.append(newLine);
        		}
        		return false;
        	}
        });
                
        return strFuncs;
    }
    
    private StringBuffer geAllFunctionsBody(IPart interfacePart, final HashSet<String> functionSet) {
    	final StringBuffer strFuncs = new StringBuffer();
    	 
    	IFunction[] functions;
		try {
			functions = interfacePart.getFunctions();
	    	for(IFunction func: functions) {
	    		if(func.isPublic()) {
	    			strFuncs.append(getFunctionBody(func, functionSet));
	    			strFuncs.append(newLine);
	    		}
	    	}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
    	
    	return strFuncs;
    }
    
    private StringBuffer getFunctionBody(NestedFunction func, HashSet<String> functionSet) {
        StringBuffer strBuf = new StringBuffer();
        String funcSignature = ExtractInterfaceOperation.getFunctionFullSignature(func, configuration.getFPackage());
        
        //do not add another function if it already existed
        if(!functionSet.contains(funcSignature)) {
            //add to the collection
            functionSet.add(funcSignature);
            
	        strBuf.append(newLine);
	        strBuf.append("\t");        //indentation //$NON-NLS-1$
	        strBuf.append(funcSignature);
	        strBuf.append(newLine);
	        strBuf.append("\t// 	TODO Auto-generated function"); //$NON-NLS-1$
	        strBuf.append(newLine);
	        strBuf.append("\t");         //$NON-NLS-1$
	        strBuf.append(IEGLConstants.KEYWORD_END);
        }
        return strBuf;
    }
    
    private StringBuffer getFunctionBody(IFunction func, HashSet<String> functionSet) throws EGLModelException {
    	StringBuffer strBuf = new StringBuffer();
    	String funcSignature = ExtractInterfaceOperation.getFunctionFullSignature(func, configuration.getFPackage());
    	
    	//do not add another function if it already existed
        if(!functionSet.contains(funcSignature)) {
            //add to the collection
            functionSet.add(funcSignature);
            
	        strBuf.append(newLine);
	        strBuf.append("\t");        //indentation //$NON-NLS-1$
	        strBuf.append(funcSignature);
	        strBuf.append(newLine);
	        strBuf.append("\t// 	TODO Auto-generated function"); //$NON-NLS-1$
	        strBuf.append(newLine);
	        strBuf.append("\t");         //$NON-NLS-1$
	        strBuf.append(IEGLConstants.KEYWORD_END);
        }
    	return strBuf;
    }
    
    private Interface getBoundInterfacePart(IPart interfacepart) {
        if(interfacepart.getParent() instanceof IEGLFile) {
            IEGLFile eglFile = (IEGLFile)(interfacepart.getParent());
            Part boundPart = EGLFileConfiguration.getBoundPart(eglFile, interfacepart.getElementName());
            if(boundPart != null)
            	return (Interface)boundPart;
        }
        return null;
    }
        
	public String getFileContents() throws PartTemplateException {		
		if(interfaces.isEmpty() && calledPrograms.isEmpty()) {
		    return getFileContentsFromTemplate();		    
		} else {
		    return getServiceStringFromInterfaces();
		}
	}  
	
	protected void execute(IProgressMonitor monitor) throws CoreException, 
	        InvocationTargetException, InterruptedException {
		super.execute(monitor) ;
		
		if(configuration.IsGenAsWebService() || configuration.isGenAsRestService()) {
			//we need to add the service to the deployment descriptor as web service
			//by default, we'll use the <projectName>.egldd 
			IFile eglddFile = CoreUtility.getOrCreateEGLDDFileHandle(configuration); //getEGLDDFileHandle();		
			if(eglddFile != null) {
				EGLDeploymentRoot deploymentRoot = null;			
				try {
					if(eglddFile.exists()) {
						String fullyQualifiedServiceName = configuration.getFPackage();
						if(fullyQualifiedServiceName.length()>0)
							fullyQualifiedServiceName += '.';
						fullyQualifiedServiceName += configuration.getFileName();
						
						deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(eglddFile, false);
						//add this service to deployment descriptor
						Deployment deployment = deploymentRoot.getDeployment();
						DeploymentFactory factory = DeploymentFactory.eINSTANCE;
						if(configuration.IsGenAsWebService()) {
							//TODO not yet supported. will need to be changed like REST below for new model.
//							Webservices wss = deployment.getWebservices();
//							if(wss == null){
//								wss = factory.createWebservices();
//								deployment.setWebservices(wss);
//							}
//							
//							Webservice newWS = factory.createWebservice();							
//							newWS.setImplementation(fullyQualifiedServiceName);
//							newWS.setEnableGeneration(true);
//							newWS.setStyle(StyleTypes.get(StyleTypes.DOCUMENT_WRAPPED_VALUE));
//							wss.getWebservice().add(newWS);
						}
						
						if(configuration.isGenAsRestService()) {
							Services rss = deployment.getServices();
							if(rss == null){
								rss = factory.createServices();
								deployment.setServices(rss);
							}
							
							org.eclipse.edt.ide.ui.internal.deployment.Service newRS = factory.createService();
							Parameters params = factory.createParameters();
							newRS.setParameters(params);
							newRS.setType(org.eclipse.edt.ide.deployment.core.model.Service.SERVICE_REST);
							newRS.setImplementation(fullyQualifiedServiceName);
							EGLDDRootHelper.addOrUpdateParameter(params, Restservice.ATTRIBUTE_SERVICE_REST_enableGeneration, true);
							EGLDDRootHelper.addOrUpdateParameter(params, Restservice.ATTRIBUTE_SERVICE_REST_uriFragment, EGLDDRootHelper.getValidURI(deployment, configuration.getServiceName()));
							rss.getService().add(newRS);
						}
						
						//persist the file if we're the only client 
						if(!EGLDDRootHelper.isWorkingModelSharedByUserClients(eglddFile))
							EGLDDRootHelper.saveEGLDDFile(eglddFile, deploymentRoot);
						
					}
				}
				finally{
					if(deploymentRoot != null)
						EGLDDRootHelper.releaseSharedWorkingModel(eglddFile, false);
				}
			}
			
		}
	}
	
	@Override
    protected void updateExistingFile(IPackageFragmentRoot root,
            IPackageFragment frag, IFile file, IProgressMonitor monitor)
            throws CoreException, InterruptedException {
	    IEGLElement eglElem = EGLCore.create(file);	
	    if(eglElem instanceof IEGLFile) {
	        IEGLFile eglLibFile = (IEGLFile)eglElem;	        
	        IPart servicePart = eglLibFile.getPart(configuration.getFileName());	//get the service part
	        if(servicePart.exists()) {
	            //check for duplicates
	            IFunction[] functions = servicePart.getFunctions();
		        StringBuffer functionBuffer = new StringBuffer();
		        HashSet functionSet = new HashSet();
		        for(int i=0; i<functions.length; i++)
		            functionSet.add(functions[i].getElementName());
		        String servicePkgName = configuration.getFPackage();		        
		        functionBuffer = getProgramCallFunctionContent(servicePkgName, functionSet);
	            insertMemeberPart(eglLibFile, servicePart, functionBuffer.toString());	            	            
	        }
	        saveExistingFileWithNewBuffer(frag, eglLibFile, file, monitor);
	    }
    }
    
    
   @Override
    protected int getInsertPosition(IEGLFile eglfile, IPart sourcePart,
            StringBuffer insertStringBuf) throws EGLModelException {
        final int[] insertposition= new int[]{0};
        
        try {
	        File fileast = EGLModelUtility.getEGLFileAST(eglfile, EGLUI.getBufferFactory());
	        
	        fileast.accept(new DefaultASTVisitor(){
	        	public boolean visit(File file) {return true;}
	
	        	public void endVisit(Service service) {
	        		//insert position is before the "END" keyward in service
	        		insertposition[0] = service.getOffset() + service.getLength() - IEGLConstants.KEYWORD_END.length();        		
	        	};
	        });
        } catch(Exception e) {
        	if(e instanceof EGLModelException)
        		throw (EGLModelException)e;
        	else
        		e.printStackTrace();
        }
        // always insert a tab after the name and before the property block
        // EGLTODO This isn't right.  This assumes that there isn't a tab between the name and
        // the first element in the page handler, which there might be.
        insertStringBuf.insert(0, "\t"); //$NON-NLS-1$

        return insertposition[0];

    }
}
