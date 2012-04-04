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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory
 * @model kind="package"
 * @generated
 */
public interface DeploymentPackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "deployment";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/xmlns/edt/deployment/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "egl";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DeploymentPackage eINSTANCE = org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingImpl <em>Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.BindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getBinding()
	 * @generated
	 */
	int BINDING = 0;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING__NAME = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING__TYPE = 2;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING__URI = 3;

	/**
	 * The feature id for the '<em><b>Use URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING__USE_URI = 4;

	/**
	 * The number of structural features of the '<em>Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDING_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl <em>Bindings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getBindings()
	 * @generated
	 */
	int BINDINGS = 1;

	/**
	 * The feature id for the '<em><b>Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__BINDING = 0;

	/**
	 * The number of structural features of the '<em>Bindings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl <em>Deploy Ext</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeployExt()
	 * @generated
	 */
	int DEPLOY_EXT = 2;

	/**
	 * The number of structural features of the '<em>Deploy Ext</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_EXT_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl <em>Deployment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeployment()
	 * @generated
	 */
	int DEPLOYMENT = 3;

	/**
	 * The feature id for the '<em><b>Bindings</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__BINDINGS = 0;

	/**
	 * The feature id for the '<em><b>Services</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__SERVICES = 1;

	/**
	 * The feature id for the '<em><b>Ruiapplication</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__RUIAPPLICATION = 2;

	/**
	 * The feature id for the '<em><b>Resource Omissions</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__RESOURCE_OMISSIONS = 3;

	/**
	 * The feature id for the '<em><b>Target Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__TARGET_GROUP = 4;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__TARGET = 5;

	/**
	 * The feature id for the '<em><b>Include</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__INCLUDE = 6;

	/**
	 * The feature id for the '<em><b>Deploy Ext Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__DEPLOY_EXT_GROUP = 7;

	/**
	 * The feature id for the '<em><b>Deploy Ext</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__DEPLOY_EXT = 8;

	/**
	 * The number of structural features of the '<em>Deployment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl <em>Target</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentTarget()
	 * @generated
	 */
	int DEPLOYMENT_TARGET = 5;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_TARGET__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_TARGET__NAME = 1;

	/**
	 * The number of structural features of the '<em>Target</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_TARGET_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentProject()
	 * @generated
	 */
	int DEPLOYMENT_PROJECT = 4;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_PROJECT__PARAMETERS = DEPLOYMENT_TARGET__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_PROJECT__NAME = DEPLOYMENT_TARGET__NAME;

	/**
	 * The number of structural features of the '<em>Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_PROJECT_FEATURE_COUNT = DEPLOYMENT_TARGET_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl <em>EGL Deployment Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getEGLDeploymentRoot()
	 * @generated
	 */
	int EGL_DEPLOYMENT_ROOT = 6;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Deploy Ext</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__DEPLOY_EXT = 3;

	/**
	 * The feature id for the '<em><b>Deployment</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__DEPLOYMENT = 4;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__TARGET = 5;

	/**
	 * The feature id for the '<em><b>Target Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__TARGET_PROJECT = 6;

	/**
	 * The number of structural features of the '<em>EGL Deployment Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl <em>Include</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getInclude()
	 * @generated
	 */
	int INCLUDE = 7;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INCLUDE__LOCATION = 0;

	/**
	 * The number of structural features of the '<em>Include</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INCLUDE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__TYPE = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl <em>Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameters()
	 * @generated
	 */
	int PARAMETERS = 9;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERS__PARAMETER = 0;

	/**
	 * The number of structural features of the '<em>Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl <em>Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getResource()
	 * @generated
	 */
	int RESOURCE = 10;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE__ID = 0;

	/**
	 * The number of structural features of the '<em>Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceOmissionsImpl <em>Resource Omissions</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceOmissionsImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getResourceOmissions()
	 * @generated
	 */
	int RESOURCE_OMISSIONS = 11;

	/**
	 * The feature id for the '<em><b>Resource</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_OMISSIONS__RESOURCE = 0;

	/**
	 * The number of structural features of the '<em>Resource Omissions</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_OMISSIONS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl <em>RUI Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIApplication()
	 * @generated
	 */
	int RUI_APPLICATION = 12;

	/**
	 * The feature id for the '<em><b>Ruihandler</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__RUIHANDLER = 0;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__PARAMETERS = 1;

	/**
	 * The feature id for the '<em><b>Deploy All Handlers</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__DEPLOY_ALL_HANDLERS = 2;

	/**
	 * The feature id for the '<em><b>Support Dynamic Loading</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING = 3;

	/**
	 * The number of structural features of the '<em>RUI Application</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl <em>RUI Handler</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIHandler()
	 * @generated
	 */
	int RUI_HANDLER = 13;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_HANDLER__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Enable Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_HANDLER__ENABLE_GENERATION = 1;

	/**
	 * The feature id for the '<em><b>Implementation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_HANDLER__IMPLEMENTATION = 2;

	/**
	 * The number of structural features of the '<em>RUI Handler</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_HANDLER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ServiceImpl <em>Service</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ServiceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getService()
	 * @generated
	 */
	int SERVICE = 14;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Implementation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE__IMPLEMENTATION = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE__TYPE = 2;

	/**
	 * The number of structural features of the '<em>Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ServicesImpl <em>Services</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ServicesImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getServices()
	 * @generated
	 */
	int SERVICES = 15;

	/**
	 * The feature id for the '<em><b>Service</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES__SERVICE = 0;

	/**
	 * The number of structural features of the '<em>Services</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding <em>Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding
	 * @generated
	 */
	EClass getBinding();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding#getParameters()
	 * @see #getBinding()
	 * @generated
	 */
	EReference getBinding_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding#getName()
	 * @see #getBinding()
	 * @generated
	 */
	EAttribute getBinding_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding#getType()
	 * @see #getBinding()
	 * @generated
	 */
	EAttribute getBinding_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#getUri <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uri</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding#getUri()
	 * @see #getBinding()
	 * @generated
	 */
	EAttribute getBinding_Uri();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI <em>Use URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Use URI</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Binding#isUseURI()
	 * @see #getBinding()
	 * @generated
	 */
	EAttribute getBinding_UseURI();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings <em>Bindings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bindings</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings
	 * @generated
	 */
	EClass getBindings();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getBinding <em>Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_Binding();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeployExt <em>Deploy Ext</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deploy Ext</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeployExt
	 * @generated
	 */
	EClass getDeployExt();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment <em>Deployment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deployment</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment
	 * @generated
	 */
	EClass getDeployment();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getBindings <em>Bindings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Bindings</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getBindings()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Bindings();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Services</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getServices()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Services();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRuiapplication <em>Ruiapplication</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Ruiapplication</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRuiapplication()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Ruiapplication();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getResourceOmissions <em>Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Resource Omissions</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getResourceOmissions()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_ResourceOmissions();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTargetGroup <em>Target Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Target Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTargetGroup()
	 * @see #getDeployment()
	 * @generated
	 */
	EAttribute getDeployment_TargetGroup();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Target</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getTarget()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Target();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getInclude <em>Include</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Include</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getInclude()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Include();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExtGroup <em>Deploy Ext Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Deploy Ext Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExtGroup()
	 * @see #getDeployment()
	 * @generated
	 */
	EAttribute getDeployment_DeployExtGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExt <em>Deploy Ext</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Deploy Ext</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getDeployExt()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_DeployExt();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject
	 * @generated
	 */
	EClass getDeploymentProject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Target</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget
	 * @generated
	 */
	EClass getDeploymentTarget();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget#getParameters()
	 * @see #getDeploymentTarget()
	 * @generated
	 */
	EReference getDeploymentTarget_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget#getName()
	 * @see #getDeploymentTarget()
	 * @generated
	 */
	EAttribute getDeploymentTarget_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot <em>EGL Deployment Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGL Deployment Root</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot
	 * @generated
	 */
	EClass getEGLDeploymentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getMixed()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EAttribute getEGLDeploymentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXMLNSPrefixMap()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXSISchemaLocation()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployExt <em>Deploy Ext</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Deploy Ext</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployExt()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_DeployExt();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployment <em>Deployment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Deployment</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployment()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_Deployment();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Target</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTarget()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_Target();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetProject <em>Target Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Target Project</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetProject()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_TargetProject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Include <em>Include</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Include</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Include
	 * @generated
	 */
	EClass getInclude();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Include#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Include#getLocation()
	 * @see #getInclude()
	 * @generated
	 */
	EAttribute getInclude_Location();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameter#getName()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameter#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameter#getType()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameter#getValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameters
	 * @generated
	 */
	EClass getParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameters#getParameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameters#getParameter()
	 * @see #getParameters()
	 * @generated
	 */
	EReference getParameters_Parameter();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Resource <em>Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Resource
	 * @generated
	 */
	EClass getResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Resource#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Resource#getId()
	 * @see #getResource()
	 * @generated
	 */
	EAttribute getResource_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions <em>Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Omissions</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions
	 * @generated
	 */
	EClass getResourceOmissions();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions#getResource <em>Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resource</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions#getResource()
	 * @see #getResourceOmissions()
	 * @generated
	 */
	EReference getResourceOmissions_Resource();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication <em>RUI Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RUI Application</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication
	 * @generated
	 */
	EClass getRUIApplication();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getRuihandler <em>Ruihandler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Ruihandler</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getRuihandler()
	 * @see #getRUIApplication()
	 * @generated
	 */
	EReference getRUIApplication_Ruihandler();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getParameters()
	 * @see #getRUIApplication()
	 * @generated
	 */
	EReference getRUIApplication_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers <em>Deploy All Handlers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deploy All Handlers</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isDeployAllHandlers()
	 * @see #getRUIApplication()
	 * @generated
	 */
	EAttribute getRUIApplication_DeployAllHandlers();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading <em>Support Dynamic Loading</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Support Dynamic Loading</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#isSupportDynamicLoading()
	 * @see #getRUIApplication()
	 * @generated
	 */
	EAttribute getRUIApplication_SupportDynamicLoading();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler <em>RUI Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RUI Handler</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIHandler
	 * @generated
	 */
	EClass getRUIHandler();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#getParameters()
	 * @see #getRUIHandler()
	 * @generated
	 */
	EReference getRUIHandler_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#isEnableGeneration <em>Enable Generation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enable Generation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#isEnableGeneration()
	 * @see #getRUIHandler()
	 * @generated
	 */
	EAttribute getRUIHandler_EnableGeneration();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#getImplementation <em>Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Implementation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIHandler#getImplementation()
	 * @see #getRUIHandler()
	 * @generated
	 */
	EAttribute getRUIHandler_Implementation();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Service <em>Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Service</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Service
	 * @generated
	 */
	EClass getService();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Service#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Service#getParameters()
	 * @see #getService()
	 * @generated
	 */
	EReference getService_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Service#getImplementation <em>Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Implementation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Service#getImplementation()
	 * @see #getService()
	 * @generated
	 */
	EAttribute getService_Implementation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Service#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Service#getType()
	 * @see #getService()
	 * @generated
	 */
	EAttribute getService_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Services <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Services</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Services
	 * @generated
	 */
	EClass getServices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Services#getService <em>Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Service</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Services#getService()
	 * @see #getServices()
	 * @generated
	 */
	EReference getServices_Service();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DeploymentFactory getDeploymentFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals
	{
		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingImpl <em>Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.BindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getBinding()
		 * @generated
		 */
		EClass BINDING = eINSTANCE.getBinding();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDING__PARAMETERS = eINSTANCE.getBinding_Parameters();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINDING__NAME = eINSTANCE.getBinding_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINDING__TYPE = eINSTANCE.getBinding_Type();

		/**
		 * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINDING__URI = eINSTANCE.getBinding_Uri();

		/**
		 * The meta object literal for the '<em><b>Use URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINDING__USE_URI = eINSTANCE.getBinding_UseURI();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl <em>Bindings</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getBindings()
		 * @generated
		 */
		EClass BINDINGS = eINSTANCE.getBindings();

		/**
		 * The meta object literal for the '<em><b>Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__BINDING = eINSTANCE.getBindings_Binding();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl <em>Deploy Ext</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeployExt()
		 * @generated
		 */
		EClass DEPLOY_EXT = eINSTANCE.getDeployExt();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl <em>Deployment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeployment()
		 * @generated
		 */
		EClass DEPLOYMENT = eINSTANCE.getDeployment();

		/**
		 * The meta object literal for the '<em><b>Bindings</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__BINDINGS = eINSTANCE.getDeployment_Bindings();

		/**
		 * The meta object literal for the '<em><b>Services</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__SERVICES = eINSTANCE.getDeployment_Services();

		/**
		 * The meta object literal for the '<em><b>Ruiapplication</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__RUIAPPLICATION = eINSTANCE.getDeployment_Ruiapplication();

		/**
		 * The meta object literal for the '<em><b>Resource Omissions</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__RESOURCE_OMISSIONS = eINSTANCE.getDeployment_ResourceOmissions();

		/**
		 * The meta object literal for the '<em><b>Target Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT__TARGET_GROUP = eINSTANCE.getDeployment_TargetGroup();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__TARGET = eINSTANCE.getDeployment_Target();

		/**
		 * The meta object literal for the '<em><b>Include</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__INCLUDE = eINSTANCE.getDeployment_Include();

		/**
		 * The meta object literal for the '<em><b>Deploy Ext Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT__DEPLOY_EXT_GROUP = eINSTANCE.getDeployment_DeployExtGroup();

		/**
		 * The meta object literal for the '<em><b>Deploy Ext</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__DEPLOY_EXT = eINSTANCE.getDeployment_DeployExt();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl <em>Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentProject()
		 * @generated
		 */
		EClass DEPLOYMENT_PROJECT = eINSTANCE.getDeploymentProject();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl <em>Target</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentTarget()
		 * @generated
		 */
		EClass DEPLOYMENT_TARGET = eINSTANCE.getDeploymentTarget();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT_TARGET__PARAMETERS = eINSTANCE.getDeploymentTarget_Parameters();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT_TARGET__NAME = eINSTANCE.getDeploymentTarget_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl <em>EGL Deployment Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getEGLDeploymentRoot()
		 * @generated
		 */
		EClass EGL_DEPLOYMENT_ROOT = eINSTANCE.getEGLDeploymentRoot();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_DEPLOYMENT_ROOT__MIXED = eINSTANCE.getEGLDeploymentRoot_Mixed();

		/**
		 * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getEGLDeploymentRoot_XMLNSPrefixMap();

		/**
		 * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getEGLDeploymentRoot_XSISchemaLocation();

		/**
		 * The meta object literal for the '<em><b>Deploy Ext</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__DEPLOY_EXT = eINSTANCE.getEGLDeploymentRoot_DeployExt();

		/**
		 * The meta object literal for the '<em><b>Deployment</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__DEPLOYMENT = eINSTANCE.getEGLDeploymentRoot_Deployment();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__TARGET = eINSTANCE.getEGLDeploymentRoot_Target();

		/**
		 * The meta object literal for the '<em><b>Target Project</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__TARGET_PROJECT = eINSTANCE.getEGLDeploymentRoot_TargetProject();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl <em>Include</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getInclude()
		 * @generated
		 */
		EClass INCLUDE = eINSTANCE.getInclude();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INCLUDE__LOCATION = eINSTANCE.getInclude_Location();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__TYPE = eINSTANCE.getParameter_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl <em>Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameters()
		 * @generated
		 */
		EClass PARAMETERS = eINSTANCE.getParameters();

		/**
		 * The meta object literal for the '<em><b>Parameter</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETERS__PARAMETER = eINSTANCE.getParameters_Parameter();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl <em>Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getResource()
		 * @generated
		 */
		EClass RESOURCE = eINSTANCE.getResource();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE__ID = eINSTANCE.getResource_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceOmissionsImpl <em>Resource Omissions</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceOmissionsImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getResourceOmissions()
		 * @generated
		 */
		EClass RESOURCE_OMISSIONS = eINSTANCE.getResourceOmissions();

		/**
		 * The meta object literal for the '<em><b>Resource</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_OMISSIONS__RESOURCE = eINSTANCE.getResourceOmissions_Resource();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl <em>RUI Application</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIApplication()
		 * @generated
		 */
		EClass RUI_APPLICATION = eINSTANCE.getRUIApplication();

		/**
		 * The meta object literal for the '<em><b>Ruihandler</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUI_APPLICATION__RUIHANDLER = eINSTANCE.getRUIApplication_Ruihandler();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUI_APPLICATION__PARAMETERS = eINSTANCE.getRUIApplication_Parameters();

		/**
		 * The meta object literal for the '<em><b>Deploy All Handlers</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUI_APPLICATION__DEPLOY_ALL_HANDLERS = eINSTANCE.getRUIApplication_DeployAllHandlers();

		/**
		 * The meta object literal for the '<em><b>Support Dynamic Loading</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING = eINSTANCE.getRUIApplication_SupportDynamicLoading();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl <em>RUI Handler</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIHandler()
		 * @generated
		 */
		EClass RUI_HANDLER = eINSTANCE.getRUIHandler();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUI_HANDLER__PARAMETERS = eINSTANCE.getRUIHandler_Parameters();

		/**
		 * The meta object literal for the '<em><b>Enable Generation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUI_HANDLER__ENABLE_GENERATION = eINSTANCE.getRUIHandler_EnableGeneration();

		/**
		 * The meta object literal for the '<em><b>Implementation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUI_HANDLER__IMPLEMENTATION = eINSTANCE.getRUIHandler_Implementation();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ServiceImpl <em>Service</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ServiceImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getService()
		 * @generated
		 */
		EClass SERVICE = eINSTANCE.getService();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE__PARAMETERS = eINSTANCE.getService_Parameters();

		/**
		 * The meta object literal for the '<em><b>Implementation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVICE__IMPLEMENTATION = eINSTANCE.getService_Implementation();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVICE__TYPE = eINSTANCE.getService_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ServicesImpl <em>Services</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ServicesImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getServices()
		 * @generated
		 */
		EClass SERVICES = eINSTANCE.getServices();

		/**
		 * The meta object literal for the '<em><b>Service</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICES__SERVICE = eINSTANCE.getServices_Service();

	}

} //DeploymentPackage
