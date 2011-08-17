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
 * A representation of the model object '<em><b>Rest Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getBaseURI <em>Base URI</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders <em>Preserve Request Headers</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getSessionCookieId <em>Session Cookie Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding()
 * @model extendedMetaData="name='RestBinding' kind='empty'"
 * @generated
 */
public interface RestBinding extends EObject {
	/**
	 * Returns the value of the '<em><b>Base URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base URI</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Base URI</em>' attribute.
	 * @see #setBaseURI(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding_BaseURI()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='baseURI'"
	 * @generated
	 */
	String getBaseURI();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getBaseURI <em>Base URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base URI</em>' attribute.
	 * @see #getBaseURI()
	 * @generated
	 */
	void setBaseURI(String value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding_EnableGeneration()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='enableGeneration'"
	 * @generated
	 */
	boolean isEnableGeneration();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration <em>Enable Generation</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEnableGeneration()
	 * @see #isEnableGeneration()
	 * @see #setEnableGeneration(boolean)
	 * @generated
	 */
	void unsetEnableGeneration();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration <em>Enable Generation</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Preserve Request Headers</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Preserve Request Headers</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Preserve Request Headers</em>' attribute.
	 * @see #isSetPreserveRequestHeaders()
	 * @see #unsetPreserveRequestHeaders()
	 * @see #setPreserveRequestHeaders(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding_PreserveRequestHeaders()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='preserveRequestHeaders'"
	 * @generated
	 */
	boolean isPreserveRequestHeaders();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders <em>Preserve Request Headers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Preserve Request Headers</em>' attribute.
	 * @see #isSetPreserveRequestHeaders()
	 * @see #unsetPreserveRequestHeaders()
	 * @see #isPreserveRequestHeaders()
	 * @generated
	 */
	void setPreserveRequestHeaders(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders <em>Preserve Request Headers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPreserveRequestHeaders()
	 * @see #isPreserveRequestHeaders()
	 * @see #setPreserveRequestHeaders(boolean)
	 * @generated
	 */
	void unsetPreserveRequestHeaders();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders <em>Preserve Request Headers</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Preserve Request Headers</em>' attribute is set.
	 * @see #unsetPreserveRequestHeaders()
	 * @see #isPreserveRequestHeaders()
	 * @see #setPreserveRequestHeaders(boolean)
	 * @generated
	 */
	boolean isSetPreserveRequestHeaders();

	/**
	 * Returns the value of the '<em><b>Session Cookie Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Session Cookie Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Session Cookie Id</em>' attribute.
	 * @see #setSessionCookieId(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRestBinding_SessionCookieId()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sessionCookieId'"
	 * @generated
	 */
	String getSessionCookieId();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getSessionCookieId <em>Session Cookie Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Session Cookie Id</em>' attribute.
	 * @see #getSessionCookieId()
	 * @generated
	 */
	void setSessionCookieId(String value);

} // RestBinding
