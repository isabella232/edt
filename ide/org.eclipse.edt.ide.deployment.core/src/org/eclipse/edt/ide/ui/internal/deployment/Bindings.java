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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bindings</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getEglBinding <em>Egl Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getWebBinding <em>Web Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getNativeBinding <em>Native Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getRestBinding <em>Rest Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getSqlDatabaseBinding <em>Sql Database Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings()
 * @model extendedMetaData="name='Bindings' kind='elementOnly'"
 * @generated
 */
public interface Bindings extends EObject {
	/**
	 * Returns the value of the '<em><b>Egl Binding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Egl Binding</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Egl Binding</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings_EglBinding()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='eglBinding'"
	 * @generated
	 */
	EList<EGLBinding> getEglBinding();

	/**
	 * Returns the value of the '<em><b>Web Binding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Web Binding</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Web Binding</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings_WebBinding()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='webBinding'"
	 * @generated
	 */
	EList<WebBinding> getWebBinding();

	/**
	 * Returns the value of the '<em><b>Native Binding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Native Binding</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Native Binding</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings_NativeBinding()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='nativeBinding'"
	 * @generated
	 */
	EList<NativeBinding> getNativeBinding();

	/**
	 * Returns the value of the '<em><b>Rest Binding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rest Binding</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rest Binding</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings_RestBinding()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='restBinding'"
	 * @generated
	 */
	EList<RestBinding> getRestBinding();

	/**
	 * Returns the value of the '<em><b>Sql Database Binding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql Database Binding</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Database Binding</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getBindings_SqlDatabaseBinding()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sqlDatabaseBinding'"
	 * @generated
	 */
	EList<SQLDatabaseBinding> getSqlDatabaseBinding();

} // Bindings
