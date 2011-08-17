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
 * A representation of the model object '<em><b>Restservice</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType <em>Impl Type</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful <em>Stateful</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getUri <em>Uri</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice()
 * @model extendedMetaData="name='Restservice' kind='elementOnly'"
 * @generated
 */
public interface Restservice extends EObject {
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_Parameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='parameters'"
	 * @generated
	 */
	Parameters getParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getParameters <em>Parameters</em>}' containment reference.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_EnableGeneration()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='enableGeneration'"
	 * @generated
	 */
	boolean isEnableGeneration();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration <em>Enable Generation</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @generated
	 */
	void unsetEnableGeneration();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration <em>Enable Generation</em>}' attribute is set.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_Implementation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='implementation'"
	 * @generated
	 */
	String getImplementation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplementation <em>Implementation</em>}' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_ImplType()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='implType'"
	 * @generated
	 */
	int getImplType();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType <em>Impl Type</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetImplType()
	 * @see #getImplType()
	 * @see #setImplType(int)
	 * @generated
	 */
	void unsetImplType();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType <em>Impl Type</em>}' attribute is set.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_Protocol()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='protocol'"
	 * @generated
	 */
	String getProtocol();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getProtocol <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol</em>' attribute.
	 * @see #getProtocol()
	 * @generated
	 */
	void setProtocol(String value);

	/**
	 * Returns the value of the '<em><b>Stateful</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stateful</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stateful</em>' attribute.
	 * @see #isSetStateful()
	 * @see #unsetStateful()
	 * @see #setStateful(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_Stateful()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='stateful'"
	 * @generated
	 */
	boolean isStateful();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful <em>Stateful</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stateful</em>' attribute.
	 * @see #isSetStateful()
	 * @see #unsetStateful()
	 * @see #isStateful()
	 * @generated
	 */
	void setStateful(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful <em>Stateful</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStateful()
	 * @see #isStateful()
	 * @see #setStateful(boolean)
	 * @generated
	 */
	void unsetStateful();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful <em>Stateful</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Stateful</em>' attribute is set.
	 * @see #unsetStateful()
	 * @see #isStateful()
	 * @see #setStateful(boolean)
	 * @generated
	 */
	boolean isSetStateful();

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestservice_Uri()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='uri'"
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

} // Restservice
