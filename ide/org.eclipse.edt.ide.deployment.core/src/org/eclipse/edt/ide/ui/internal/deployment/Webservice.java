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
package org.eclipse.edt.ide.ui.internal.deployment;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Webservice</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType <em>Impl Type</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion <em>Soap Version</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getTransaction <em>Transaction</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL <em>Use Existing WSDL</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUserID <em>User ID</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlLocation <em>Wsdl Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlPort <em>Wsdl Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlService <em>Wsdl Service</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice()
 * @model extendedMetaData="name='Webservice' kind='elementOnly'"
 * @generated
 */
public interface Webservice extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference.
	 * @see #setParameters(Parameters)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Parameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='parameters'"
	 * @generated
	 */
	Parameters getParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getParameters <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameters</em>' containment reference.
	 * @see #getParameters()
	 * @generated
	 */
	void setParameters(Parameters value);

	/**
	 * Returns the value of the '<em><b>Enable Generation</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Enable Generation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enable Generation</em>' attribute.
	 * @see #isSetEnableGeneration()
	 * @see #unsetEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_EnableGeneration()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='enableGeneration'"
	 * @generated
	 */
	boolean isEnableGeneration();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enable Generation</em>' attribute.
	 * @see #isSetEnableGeneration()
	 * @see #unsetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @generated
	 */
	void setEnableGeneration(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @generated
	 */
	void unsetEnableGeneration();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration <em>Enable Generation</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Enable Generation</em>' attribute is set.
	 * @see #unsetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @generated
	 */
	boolean isSetEnableGeneration();

	/**
	 * Returns the value of the '<em><b>Implementation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Implementation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Implementation</em>' attribute.
	 * @see #setImplementation(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Implementation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='implementation'"
	 * @generated
	 */
	String getImplementation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplementation <em>Implementation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Implementation</em>' attribute.
	 * @see #getImplementation()
	 * @generated
	 */
	void setImplementation(String value);

	/**
	 * Returns the value of the '<em><b>Impl Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Impl Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Impl Type</em>' attribute.
	 * @see #isSetImplType()
	 * @see #unsetImplType()
	 * @see #setImplType(int)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_ImplType()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='implType'"
	 * @generated
	 */
	int getImplType();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Impl Type</em>' attribute.
	 * @see #isSetImplType()
	 * @see #unsetImplType()
	 * @see #getImplType()
	 * @generated
	 */
	void setImplType(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetImplType()
	 * @see #getImplType()
	 * @see #setImplType(int)
	 * @generated
	 */
	void unsetImplType();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType <em>Impl Type</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Impl Type</em>' attribute is set.
	 * @see #unsetImplType()
	 * @see #getImplType()
	 * @see #setImplType(int)
	 * @generated
	 */
	boolean isSetImplType();

	/**
	 * Returns the value of the '<em><b>Protocol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol</em>' attribute.
	 * @see #setProtocol(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Protocol()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='protocol'"
	 * @generated
	 */
	String getProtocol();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getProtocol <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol</em>' attribute.
	 * @see #getProtocol()
	 * @generated
	 */
	void setProtocol(String value);

	/**
	 * Returns the value of the '<em><b>Soap Version</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Soap Version</em>' attribute.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @see #isSetSoapVersion()
	 * @see #unsetSoapVersion()
	 * @see #setSoapVersion(SOAPVersionType)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_SoapVersion()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='soapVersion'"
	 * @generated
	 */
	SOAPVersionType getSoapVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion <em>Soap Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Soap Version</em>' attribute.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @see #isSetSoapVersion()
	 * @see #unsetSoapVersion()
	 * @see #getSoapVersion()
	 * @generated
	 */
	void setSoapVersion(SOAPVersionType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion <em>Soap Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSoapVersion()
	 * @see #getSoapVersion()
	 * @see #setSoapVersion(SOAPVersionType)
	 * @generated
	 */
	void unsetSoapVersion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion <em>Soap Version</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Soap Version</em>' attribute is set.
	 * @see #unsetSoapVersion()
	 * @see #getSoapVersion()
	 * @see #setSoapVersion(SOAPVersionType)
	 * @generated
	 */
	boolean isSetSoapVersion();

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The default value is <code>"document-wrapped"</code>.
	 * The literals are from the enumeration {@link org.eclipse.edt.ide.ui.internal.deployment.StyleTypes}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Style</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @see #isSetStyle()
	 * @see #unsetStyle()
	 * @see #setStyle(StyleTypes)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Style()
	 * @model default="document-wrapped" unsettable="true"
	 *        extendedMetaData="kind='attribute' name='style'"
	 * @generated
	 */
	StyleTypes getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @see #isSetStyle()
	 * @see #unsetStyle()
	 * @see #getStyle()
	 * @generated
	 */
	void setStyle(StyleTypes value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStyle()
	 * @see #getStyle()
	 * @see #setStyle(StyleTypes)
	 * @generated
	 */
	void unsetStyle();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle <em>Style</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Style</em>' attribute is set.
	 * @see #unsetStyle()
	 * @see #getStyle()
	 * @see #setStyle(StyleTypes)
	 * @generated
	 */
	boolean isSetStyle();

	/**
	 * Returns the value of the '<em><b>Transaction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transaction</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transaction</em>' attribute.
	 * @see #setTransaction(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Transaction()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='transaction'"
	 * @generated
	 */
	String getTransaction();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getTransaction <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transaction</em>' attribute.
	 * @see #getTransaction()
	 * @generated
	 */
	void setTransaction(String value);

	/**
	 * Returns the value of the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uri</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uri</em>' attribute.
	 * @see #setUri(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_Uri()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='uri'"
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

	/**
	 * Returns the value of the '<em><b>Use Existing WSDL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Use Existing WSDL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Use Existing WSDL</em>' attribute.
	 * @see #isSetUseExistingWSDL()
	 * @see #unsetUseExistingWSDL()
	 * @see #setUseExistingWSDL(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_UseExistingWSDL()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='useExistingWSDL'"
	 * @generated
	 */
	boolean isUseExistingWSDL();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL <em>Use Existing WSDL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Use Existing WSDL</em>' attribute.
	 * @see #isSetUseExistingWSDL()
	 * @see #unsetUseExistingWSDL()
	 * @see #isUseExistingWSDL()
	 * @generated
	 */
	void setUseExistingWSDL(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL <em>Use Existing WSDL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetUseExistingWSDL()
	 * @see #isUseExistingWSDL()
	 * @see #setUseExistingWSDL(boolean)
	 * @generated
	 */
	void unsetUseExistingWSDL();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL <em>Use Existing WSDL</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Use Existing WSDL</em>' attribute is set.
	 * @see #unsetUseExistingWSDL()
	 * @see #isUseExistingWSDL()
	 * @see #setUseExistingWSDL(boolean)
	 * @generated
	 */
	boolean isSetUseExistingWSDL();

	/**
	 * Returns the value of the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User ID</em>' attribute.
	 * @see #setUserID(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_UserID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='userID'"
	 * @generated
	 */
	String getUserID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUserID <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User ID</em>' attribute.
	 * @see #getUserID()
	 * @generated
	 */
	void setUserID(String value);

	/**
	 * Returns the value of the '<em><b>Wsdl Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wsdl Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wsdl Location</em>' attribute.
	 * @see #setWsdlLocation(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_WsdlLocation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI" required="true"
	 *        extendedMetaData="kind='attribute' name='wsdlLocation'"
	 * @generated
	 */
	String getWsdlLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlLocation <em>Wsdl Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wsdl Location</em>' attribute.
	 * @see #getWsdlLocation()
	 * @generated
	 */
	void setWsdlLocation(String value);

	/**
	 * Returns the value of the '<em><b>Wsdl Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wsdl Port</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wsdl Port</em>' attribute.
	 * @see #setWsdlPort(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_WsdlPort()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI" required="true"
	 *        extendedMetaData="kind='attribute' name='wsdlPort'"
	 * @generated
	 */
	String getWsdlPort();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlPort <em>Wsdl Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wsdl Port</em>' attribute.
	 * @see #getWsdlPort()
	 * @generated
	 */
	void setWsdlPort(String value);

	/**
	 * Returns the value of the '<em><b>Wsdl Service</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wsdl Service</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wsdl Service</em>' attribute.
	 * @see #setWsdlService(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebservice_WsdlService()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='wsdlService'"
	 * @generated
	 */
	String getWsdlService();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlService <em>Wsdl Service</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wsdl Service</em>' attribute.
	 * @see #getWsdlService()
	 * @generated
	 */
	void setWsdlService(String value);

} // Webservice
