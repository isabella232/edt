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

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getBindings <em>Bindings</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRuiapplication <em>Ruiapplication</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getResourceOmissions <em>Resource Omissions</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTargetGroup <em>Target Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getInclude <em>Include</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExtGroup <em>Deploy Ext Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExt <em>Deploy Ext</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment()
 * @model extendedMetaData="name='Deployment' kind='elementOnly'"
 * @generated
 */
public interface Deployment extends EObject
{
	/**
	 * Returns the value of the '<em><b>Bindings</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bindings</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bindings</em>' containment reference.
	 * @see #setBindings(Bindings)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_Bindings()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='bindings'"
	 * @generated
	 */
	Bindings getBindings();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getBindings <em>Bindings</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bindings</em>' containment reference.
	 * @see #getBindings()
	 * @generated
	 */
	void setBindings(Bindings value);

	/**
	 * Returns the value of the '<em><b>Services</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Services</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Services</em>' containment reference.
	 * @see #setServices(Services)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_Services()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='services'"
	 * @generated
	 */
	Services getServices();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getServices <em>Services</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Services</em>' containment reference.
	 * @see #getServices()
	 * @generated
	 */
	void setServices(Services value);

	/**
	 * Returns the value of the '<em><b>Ruiapplication</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ruiapplication</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ruiapplication</em>' containment reference.
	 * @see #setRuiapplication(RUIApplication)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_Ruiapplication()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ruiapplication'"
	 * @generated
	 */
	RUIApplication getRuiapplication();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRuiapplication <em>Ruiapplication</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ruiapplication</em>' containment reference.
	 * @see #getRuiapplication()
	 * @generated
	 */
	void setRuiapplication(RUIApplication value);

	/**
	 * Returns the value of the '<em><b>Resource Omissions</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource Omissions</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource Omissions</em>' containment reference.
	 * @see #setResourceOmissions(ResourceOmissions)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_ResourceOmissions()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='resource-omissions'"
	 * @generated
	 */
	ResourceOmissions getResourceOmissions();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getResourceOmissions <em>Resource Omissions</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Omissions</em>' containment reference.
	 * @see #getResourceOmissions()
	 * @generated
	 */
	void setResourceOmissions(ResourceOmissions value);

	/**
	 * Returns the value of the '<em><b>Target Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Group</em>' attribute list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_TargetGroup()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="false"
	 *        extendedMetaData="kind='group' name='target:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getTargetGroup();

	/**
	 * Returns the value of the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_Target()
	 * @model containment="true" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='target' namespace='##targetNamespace' group='target:group'"
	 * @generated
	 */
	DeploymentTarget getTarget();

	/**
	 * Returns the value of the '<em><b>Include</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.Include}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Include</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Include</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_Include()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='include'"
	 * @generated
	 */
	EList<Include> getInclude();

	/**
	 * Returns the value of the '<em><b>Deploy Ext Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy Ext Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy Ext Group</em>' attribute list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_DeployExtGroup()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='deploy-ext:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getDeployExtGroup();

	/**
	 * Returns the value of the '<em><b>Deploy Ext</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.DeployExt}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy Ext</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy Ext</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeployment_DeployExt()
	 * @model containment="true" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deploy-ext' namespace='##targetNamespace' group='deploy-ext:group'"
	 * @generated
	 */
	EList<DeployExt> getDeployExt();

} // Deployment
