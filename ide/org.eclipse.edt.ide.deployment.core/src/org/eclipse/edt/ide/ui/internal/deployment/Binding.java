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
package org.eclipse.edt.ide.ui.internal.deployment;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI <em>Use URI</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding()
 * @model extendedMetaData="name='Binding' kind='elementOnly'"
 * @generated
 */
public interface Binding extends EObject
{
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding_Parameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='parameters'"
	 * @generated
	 */
	Parameters getParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getParameters <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameters</em>' containment reference.
	 * @see #getParameters()
	 * @generated
	 */
	void setParameters(Parameters value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding_Type()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='type'"
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding_Uri()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='uri'"
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

	/**
	 * Returns the value of the '<em><b>Use URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Use URI</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Use URI</em>' attribute.
	 * @see #isSetUseURI()
	 * @see #unsetUseURI()
	 * @see #setUseURI(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBinding_UseURI()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='useURI'"
	 * @generated
	 */
	boolean isUseURI();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI <em>Use URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Use URI</em>' attribute.
	 * @see #isSetUseURI()
	 * @see #unsetUseURI()
	 * @see #isUseURI()
	 * @generated
	 */
	void setUseURI(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI <em>Use URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetUseURI()
	 * @see #isUseURI()
	 * @see #setUseURI(boolean)
	 * @generated
	 */
	void unsetUseURI();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI <em>Use URI</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Use URI</em>' attribute is set.
	 * @see #unsetUseURI()
	 * @see #isUseURI()
	 * @see #setUseURI(boolean)
	 * @generated
	 */
	boolean isSetUseURI();

} // Binding
