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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>RUI Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getRuihandler <em>Ruihandler</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers <em>Deploy All Handlers</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading <em>Support Dynamic Loading</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIApplication()
 * @model extendedMetaData="name='RUIApplication' kind='elementOnly'"
 * @generated
 */
public interface RUIApplication extends EObject
{
	/**
	 * Returns the value of the '<em><b>Ruihandler</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ruihandler</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ruihandler</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIApplication_Ruihandler()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ruihandler'"
	 * @generated
	 */
	EList<RUIHandler> getRuihandler();

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIApplication_Parameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='parameters'"
	 * @generated
	 */
	Parameters getParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getParameters <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameters</em>' containment reference.
	 * @see #getParameters()
	 * @generated
	 */
	void setParameters(Parameters value);

	/**
	 * Returns the value of the '<em><b>Deploy All Handlers</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy All Handlers</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy All Handlers</em>' attribute.
	 * @see #isSetDeployAllHandlers()
	 * @see #unsetDeployAllHandlers()
	 * @see #setDeployAllHandlers(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIApplication_DeployAllHandlers()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='deployAllHandlers'"
	 * @generated
	 */
	boolean isDeployAllHandlers();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers <em>Deploy All Handlers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deploy All Handlers</em>' attribute.
	 * @see #isSetDeployAllHandlers()
	 * @see #unsetDeployAllHandlers()
	 * @see #isDeployAllHandlers()
	 * @generated
	 */
	void setDeployAllHandlers(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers <em>Deploy All Handlers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDeployAllHandlers()
	 * @see #isDeployAllHandlers()
	 * @see #setDeployAllHandlers(boolean)
	 * @generated
	 */
	void unsetDeployAllHandlers();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers <em>Deploy All Handlers</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Deploy All Handlers</em>' attribute is set.
	 * @see #unsetDeployAllHandlers()
	 * @see #isDeployAllHandlers()
	 * @see #setDeployAllHandlers(boolean)
	 * @generated
	 */
	boolean isSetDeployAllHandlers();

	/**
	 * Returns the value of the '<em><b>Support Dynamic Loading</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Support Dynamic Loading</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Support Dynamic Loading</em>' attribute.
	 * @see #isSetSupportDynamicLoading()
	 * @see #unsetSupportDynamicLoading()
	 * @see #setSupportDynamicLoading(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIApplication_SupportDynamicLoading()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='supportDynamicLoading'"
	 * @generated
	 */
	boolean isSupportDynamicLoading();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading <em>Support Dynamic Loading</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Support Dynamic Loading</em>' attribute.
	 * @see #isSetSupportDynamicLoading()
	 * @see #unsetSupportDynamicLoading()
	 * @see #isSupportDynamicLoading()
	 * @generated
	 */
	void setSupportDynamicLoading(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading <em>Support Dynamic Loading</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSupportDynamicLoading()
	 * @see #isSupportDynamicLoading()
	 * @see #setSupportDynamicLoading(boolean)
	 * @generated
	 */
	void unsetSupportDynamicLoading();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading <em>Support Dynamic Loading</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Support Dynamic Loading</em>' attribute is set.
	 * @see #unsetSupportDynamicLoading()
	 * @see #isSupportDynamicLoading()
	 * @see #setSupportDynamicLoading(boolean)
	 * @generated
	 */
	boolean isSetSupportDynamicLoading();

} // RUIApplication
