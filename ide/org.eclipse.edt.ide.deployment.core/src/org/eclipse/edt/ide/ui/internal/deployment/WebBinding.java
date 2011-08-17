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
 * A representation of the model object '<em><b>Web Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion <em>Soap Version</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlLocation <em>Wsdl Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlPort <em>Wsdl Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlService <em>Wsdl Service</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding()
 * @model extendedMetaData="name='WebBinding' kind='empty'"
 * @generated
 */
public interface WebBinding extends EObject {
	/**
	 * Returns the value of the '<em><b>Enable Generation</b></em>' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_EnableGeneration()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='enableGeneration'"
	 * @generated
	 */
	boolean isEnableGeneration();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration <em>Enable Generation</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @generated
	 */
	void unsetEnableGeneration();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration <em>Enable Generation</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interface</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interface</em>' attribute.
	 * @see #setInterface(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_Interface()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='interface'"
	 * @generated
	 */
	String getInterface();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getInterface <em>Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interface</em>' attribute.
	 * @see #getInterface()
	 * @generated
	 */
	void setInterface(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_SoapVersion()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='soapVersion'"
	 * @generated
	 */
	SOAPVersionType getSoapVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion <em>Soap Version</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion <em>Soap Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSoapVersion()
	 * @see #getSoapVersion()
	 * @see #setSoapVersion(SOAPVersionType)
	 * @generated
	 */
	void unsetSoapVersion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion <em>Soap Version</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uri</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uri</em>' attribute.
	 * @see #setUri(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_Uri()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='uri'"
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_WsdlLocation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI" required="true"
	 *        extendedMetaData="kind='attribute' name='wsdlLocation'"
	 * @generated
	 */
	String getWsdlLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlLocation <em>Wsdl Location</em>}' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_WsdlPort()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI" required="true"
	 *        extendedMetaData="kind='attribute' name='wsdlPort'"
	 * @generated
	 */
	String getWsdlPort();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlPort <em>Wsdl Port</em>}' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getWebBinding_WsdlService()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='wsdlService'"
	 * @generated
	 */
	String getWsdlService();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlService <em>Wsdl Service</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wsdl Service</em>' attribute.
	 * @see #getWsdlService()
	 * @generated
	 */
	void setWsdlService(String value);

} // WebBinding
