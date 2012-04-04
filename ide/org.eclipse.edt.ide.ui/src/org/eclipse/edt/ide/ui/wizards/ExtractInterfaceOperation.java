/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.binding.DataBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IFunction;

public class ExtractInterfaceOperation extends EGLFileOperation {
	private ExtractInterfaceConfiguration configuration;

    /**
     * @param configuration
     */
    public ExtractInterfaceOperation(ExtractInterfaceConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }
    
    public ExtractInterfaceOperation(ExtractInterfaceConfiguration configuration, ISchedulingRule rule) {
        super(configuration, rule);
        this.configuration = configuration;
    }
    
    /**
     * returns the string of function's full signature, but it does NOT have "end", ";" or newLine at the end, 
     * caller needs to close it
     * @param eglfunc
     * @param currFilePkg
     * @return
     */
	static public String getFunctionFullSignature(NestedFunction eglfunc, String currFilePkg) {
	    StringBuffer strbuf = new StringBuffer();
	    strbuf.append(IEGLConstants.KEYWORD_FUNCTION);
	    strbuf.append(" "); //$NON-NLS-1$
	    strbuf.append(ExtractInterfaceConfiguration.getFunctionSimpleSignature(eglfunc, true, currFilePkg));
	    
        final int[] outparamCnt = new int[]{0};
	    if(eglfunc.hasReturnType()) {
	        strbuf.append(" "); //$NON-NLS-1$
	        strbuf.append(IEGLConstants.KEYWORD_RETURNS);
	        strbuf.append(" "); //$NON-NLS-1$
	        strbuf.append("("); //$NON-NLS-1$
	        
	        //get fully qualifed return type if necessary
	        Type returnType = eglfunc.getReturnType();       
	        String qualifiedReturnTypeString = ExtractInterfaceConfiguration.getQualifiedTypeString(returnType, currFilePkg, true);
	        strbuf.append(qualifiedReturnTypeString);
	        strbuf.append(")"); //$NON-NLS-1$   
	    } else {	//no return type
	    	eglfunc.accept(new DefaultASTVisitor(){
	    		public boolean visit(NestedFunction nestedFunction) {return true;}
	    		
	    		public boolean visit(FunctionParameter functionParameter) {
	    			if(functionParameter.getUseType() == FunctionParameter.UseType.OUT)
	    				outparamCnt[0]++;
	    			return false;
	    		};
	    	});	    	
	    }
	        
	    IBinding nestedFuncBinding = eglfunc.getName().resolveBinding();
	    if(nestedFuncBinding != null && nestedFuncBinding != IBinding.NOT_FOUND_BINDING) {
	    	StringBuffer nameVal = new StringBuffer();
	    	StringBuffer namespaceVal = new StringBuffer();
	    	ITypeBinding nestedFuncTypeBinding = ((DataBinding)nestedFuncBinding).getType();
	    	//use the function type binding, because annotation information is stored there
	    	EGLFileConfiguration.getXMLAnnotationValueFromBinding(nestedFuncTypeBinding, nameVal, namespaceVal);
	    	
	        //here is a senario, if the function has exact 1 out parameter(you could have more than one in, in/out params) and no return
	        //this out parameter should be at the end (validation makes sure of that)
	        //we need to make isLastParamReturnValue = yes		            
            strbuf.append(getXMLAnnotationString(namespaceVal.toString(), nameVal.toString()));
        } else {
		    if(outparamCnt[0] == 1) {            
		        strbuf.append(getXMLAnnotationString(null, null));
		    }
        }
	    return strbuf.toString();
	}
	
	static public String getFunctionFullSignature(IFunction func, String currFilePkg) throws EGLModelException {
	    StringBuffer strbuf = new StringBuffer();
	    strbuf.append(IEGLConstants.KEYWORD_FUNCTION);
	    strbuf.append(" "); //$NON-NLS-1$
	    strbuf.append(ExtractInterfaceConfiguration.getFunctionSimpleSignature(func, true, currFilePkg));	    
        final int[] outparamCnt = new int[]{0};
	    if(func.getReturnTypeName() != null) {
	        strbuf.append(" "); //$NON-NLS-1$
	        strbuf.append(IEGLConstants.KEYWORD_RETURNS);
	        strbuf.append(" "); //$NON-NLS-1$
	        strbuf.append("("); //$NON-NLS-1$
	        
	        //get fully qualifed return type if necessary
	        String returnTypeString = func.getReturnTypeName();
	        String returnTypePkg = func.getReturnTypePackage();
	        String qualifiedReturnTypeString = ExtractInterfaceConfiguration.getQualifiedTypeString(returnTypeString, returnTypePkg, currFilePkg);
	        strbuf.append(qualifiedReturnTypeString);
	        strbuf.append(")");  
	    }
	   
	    return strbuf.toString();
	}
	
	private String getAbstractFunctionDefinitionString(NestedFunction eglfunc, String currFilePkg) {
	    StringBuffer strbuf = new StringBuffer();
	    strbuf.append(getFunctionFullSignature(eglfunc, currFilePkg));
        strbuf.append(";"); //$NON-NLS-1$
        strbuf.append(newLine);
        return strbuf.toString();
	}
		
    private StringBuffer getInterfaceString() {
        StringBuffer strInterface = new StringBuffer(IEGLConstants.KEYWORD_INTERFACE);        
        strInterface.append(" "); //$NON-NLS-1$
        strInterface.append(configuration.getInterfaceName());
        
        Part boundServicePart = configuration.getTheBoundPart();
        //need to get the annotation values
        IBinding serviceNameBinding = boundServicePart.getName().resolveBinding();
        if(serviceNameBinding != null && serviceNameBinding != IBinding.NOT_FOUND_BINDING) {
        	StringBuffer nameVal = new StringBuffer();
        	StringBuffer namespaceVal = new StringBuffer();
        	EGLFileConfiguration.getXMLAnnotationValueFromBinding(serviceNameBinding, nameVal, namespaceVal);        	
        	strInterface.append(getXMLAnnotationString(namespaceVal.toString(), nameVal.toString()));
        } else {
            String serviceSimpleName = configuration.getTheBoundPart().getIdentifier();
            //use the service name as the element name
            //senario:  service => wsdl file(portType is the service name)
            //			service => extract to interface, the interface name is prepended with "I", 
            //			in order to match portType in the wsdl file
            //					   set the element name of the interface to be service name
            //TODO: add SOAP feature
            String servicePackage = configuration.getOriginalServicePackage();            
            strInterface.append(getXMLAnnotationString(createNamespace(servicePackage), serviceSimpleName));
        }

	    strInterface.append(newLine);
	    strInterface.append(newLine);
	    
	    //now interface functions definitions
	    List funcstates = configuration.getFFunctionsSelectionState();
	    List funcs = configuration.getFFunctions();
	    for(int i=0; i<funcstates.size(); i++)
	    {
	        //only generate the ones used selected
	        if(configuration.getFunctionSelectionState(i))
	        {
	            strInterface.append("\t");		//create indentation	             //$NON-NLS-1$
	            strInterface.append(getAbstractFunctionDefinitionString((NestedFunction)(funcs.get(i)), configuration.getFPackage()));
	        }
	    }
	    
	    strInterface.append(IEGLConstants.KEYWORD_END);		//end for interface definition
	    strInterface.append(newLine);
	    strInterface.append(newLine);
	    return strInterface;    
    }
    
    @Override
    protected String getFileContents() {
        StringBuffer buf = new StringBuffer();
        buf.append(getInterfaceString());
        return buf.toString();
    }
    
    private String createNamespace(String ePackage) {
        StringBuffer sbuffer;
        if (ePackage == null || ePackage.length() < 1) {
            sbuffer = new StringBuffer("http://default");
        } else {
            StringTokenizer tokenizer = new StringTokenizer(ePackage, ".", true);
            sbuffer = new StringBuffer();
            while (tokenizer.countTokens() > 0) {
                sbuffer.insert(0, tokenizer.nextToken());
            }
            sbuffer.insert(0, "http://");
        }

        return sbuffer.toString();         
    }
}
