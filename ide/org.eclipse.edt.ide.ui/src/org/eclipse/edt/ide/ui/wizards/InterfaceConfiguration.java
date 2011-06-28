/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
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

//import com.ibm.etools.egl.wsdl.ui.model.EInterface;
//import com.ibm.etools.egl.wsdl.ui.model.Function;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class InterfaceConfiguration extends InterfaceListConfiguration {
	
	public final static int BASIC_INTERFACE = 0;
	public final static int JAVAOBJ_INTERFACE = 1;
	
	/** The type of program */
	private int interfaceType;	
    
    /** The name of the EGL Interface */
	private String interfaceName;
	
	/*
	 * this is the datamodel came out of the wsdl parser for a portType in the wsdl
	 * this also contains the complex type info
	 */
// TODO EDT Uncomment when wsdl is ready	
//	private EInterface wsdlInterface;
//	private EInterface wsdlRuiInterface;
	
	/*
	 * each element of the list is EGLFunction - the datamodel came out of the wsdl parser
	 * the operations in a portType 
	 */
	private boolean[] functionsSelectionStates;
	
	public InterfaceConfiguration(){
		super();
		// TODO EDT Uncomment when wsdl is ready		
//		setDefaultAttributes();
	}
    
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		// TODO EDT Uncomment when wsdl is ready		
//		setDefaultAttributes();			
	}
    
    /**
     * @return Returns the interface EGL Name.
     */
    public String getInterfaceName() {
        return interfaceName;
    }
    /**
     * @param serviceName The serviceName to set.
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
 // TODO EDT Uncomment when wsdl is ready    
//    public String getInterfaceWSDLName()
//    {
//        return wsdlInterface.getXML().getName();
//    }
//    
//	private void setDefaultAttributes() {
//	    if(wsdlInterface != null)
//	        interfaceName = wsdlInterface.getName();
//	    else
//	    	interfaceName = ""; //$NON-NLS-1$
//	}
//	
//	public void setInterface(int index, EInterface wsdlInterfaceIn, EInterface wsdlRuiInterfaceIn )
//	{
//	    wsdlInterface = wsdlInterfaceIn;
//	    wsdlRuiInterface = wsdlRuiInterfaceIn;
//	    setDefaultAttributes();
//	    initFunctionList();
//	}
//	
//	private void initFunctionList()
//	{
//	    Function[] functionsInInterface = wsdlInterface.getFunctions();
//	    functionsSelectionStates = new boolean[functionsInInterface.length];
//	    //init it all true, means selected
//	    for(int i=0; i<functionsSelectionStates.length; i++){
//	    	DataTypeStatus funcStatus = functionsInInterface[i].getDataTypeStatus();
//	    	if(funcStatus.getStatus() == DataTypeStatus.STATUS_OK)
//	    		functionsSelectionStates[i] = true;
//	    	else
//	    		functionsSelectionStates[i] = false;
//	    }
//	}
//
//	public Function[] getFunctionsInInterface(boolean is4RuiProject)
//	{
//		if( is4RuiProject )
//			return wsdlRuiInterface.getFunctions();
//		else
//			return wsdlInterface.getFunctions();
//	}
//	
//	public boolean[] getFunctionSelectionStates()
//	{
//	    return functionsSelectionStates;
//	}
//	
//    public void setFunctionsSelectionState(int index, boolean selected) {
//        functionsSelectionStates[index] = selected;
//    }
//	
//    /**
//     * @return Returns the wsdlInterface.
//     */
//    public EInterface getWsdlInterface( boolean is4RuiProject ) {
//    	if( !is4RuiProject )
//    		return wsdlInterface;
//    	else
//    		return wsdlRuiInterface;
//    }
//    /**
//     * @return Returns the interfaceType.
//     */
//    public int getInterfaceType() {
//        return interfaceType;
//    }
//    /**
//     * @param interfaceType The interfaceType to set.
//     */
//    public void setInterfaceType(int interfaceType) {
//        this.interfaceType = interfaceType;
//    }
}
