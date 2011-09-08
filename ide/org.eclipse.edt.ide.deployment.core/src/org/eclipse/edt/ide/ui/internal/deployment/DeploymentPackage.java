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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
public interface DeploymentPackage extends EPackage {
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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl <em>Bindings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getBindings()
	 * @generated
	 */
	int BINDINGS = 0;

	/**
	 * The feature id for the '<em><b>Egl Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__EGL_BINDING = 0;

	/**
	 * The feature id for the '<em><b>Web Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__WEB_BINDING = 1;

	/**
	 * The feature id for the '<em><b>Native Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__NATIVE_BINDING = 2;

	/**
	 * The feature id for the '<em><b>Rest Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__REST_BINDING = 3;

	/**
	 * The feature id for the '<em><b>Sql Database Binding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS__SQL_DATABASE_BINDING = 4;

	/**
	 * The number of structural features of the '<em>Bindings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINDINGS_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolImpl <em>Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getProtocol()
	 * @generated
	 */
	int PROTOCOL = 21;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROTOCOL__NAME = 0;

	/**
	 * The number of structural features of the '<em>Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROTOCOL_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl <em>CICSECI Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSECIProtocol()
	 * @generated
	 */
	int CICSECI_PROTOCOL = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__CONVERSION_TABLE = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Ctg Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__CTG_LOCATION = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ctg Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__CTG_PORT = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Server ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL__SERVER_ID = PROTOCOL_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>CICSECI Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSECI_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl <em>CICSJ2C Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSJ2CProtocol()
	 * @generated
	 */
	int CICSJ2C_PROTOCOL = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSJ2C_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSJ2C_PROTOCOL__CONVERSION_TABLE = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSJ2C_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>CICSJ2C Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSJ2C_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSSSLProtocolImpl <em>CICSSSL Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSSSLProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSSSLProtocol()
	 * @generated
	 */
	int CICSSSL_PROTOCOL = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__CONVERSION_TABLE = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Ctg Key Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__CTG_KEY_STORE = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ctg Key Store Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__CTG_KEY_STORE_PASSWORD = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Ctg Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__CTG_LOCATION = PROTOCOL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Ctg Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__CTG_PORT = PROTOCOL_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Server ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL__SERVER_ID = PROTOCOL_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>CICSSSL Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSSSL_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl <em>CICSWS Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSWSProtocol()
	 * @generated
	 */
	int CICSWS_PROTOCOL = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSWS_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Transaction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSWS_PROTOCOL__TRANSACTION = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSWS_PROTOCOL__USER_ID = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>CICSWS Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CICSWS_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl <em>Deploy Ext</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeployExtImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeployExt()
	 * @generated
	 */
	int DEPLOY_EXT = 5;

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
	int DEPLOYMENT = 6;

	/**
	 * The feature id for the '<em><b>Bindings</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__BINDINGS = 0;

	/**
	 * The feature id for the '<em><b>Protocols</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__PROTOCOLS = 1;

	/**
	 * The feature id for the '<em><b>Webservices</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__WEBSERVICES = 2;

	/**
	 * The feature id for the '<em><b>Restservices</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__RESTSERVICES = 3;

	/**
	 * The feature id for the '<em><b>Ruiapplication</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__RUIAPPLICATION = 4;

	/**
	 * The feature id for the '<em><b>Resource Omissions</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__RESOURCE_OMISSIONS = 5;

	/**
	 * The feature id for the '<em><b>Target Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__TARGET_GROUP = 6;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__TARGET = 7;

	/**
	 * The feature id for the '<em><b>Include</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__INCLUDE = 8;

	/**
	 * The feature id for the '<em><b>Deploy Ext Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__DEPLOY_EXT_GROUP = 9;

	/**
	 * The feature id for the '<em><b>Deploy Ext</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__DEPLOY_EXT = 10;

	/**
	 * The feature id for the '<em><b>Webservice Runtime</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__WEBSERVICE_RUNTIME = 11;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT__ALIAS = 12;

	/**
	 * The number of structural features of the '<em>Deployment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_FEATURE_COUNT = 13;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl <em>Target</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentTargetImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentTarget()
	 * @generated
	 */
	int DEPLOYMENT_TARGET = 9;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentBuildDescriptorImpl <em>Build Descriptor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentBuildDescriptorImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentBuildDescriptor()
	 * @generated
	 */
	int DEPLOYMENT_BUILD_DESCRIPTOR = 7;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_BUILD_DESCRIPTOR__PARAMETERS = DEPLOYMENT_TARGET__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_BUILD_DESCRIPTOR__NAME = DEPLOYMENT_TARGET__NAME;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_BUILD_DESCRIPTOR__FILE_NAME = DEPLOYMENT_TARGET_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Build Descriptor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYMENT_BUILD_DESCRIPTOR_FEATURE_COUNT = DEPLOYMENT_TARGET_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentProjectImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentProject()
	 * @generated
	 */
	int DEPLOYMENT_PROJECT = 8;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl <em>EGL Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getEGLBinding()
	 * @generated
	 */
	int EGL_BINDING = 10;

	/**
	 * The feature id for the '<em><b>Protocol Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING__PROTOCOL_GROUP = 0;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING__PROTOCOL = 1;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING__ALIAS = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING__NAME = 3;

	/**
	 * The feature id for the '<em><b>Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING__SERVICE_NAME = 4;

	/**
	 * The number of structural features of the '<em>EGL Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_BINDING_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl <em>EGL Deployment Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getEGLDeploymentRoot()
	 * @generated
	 */
	int EGL_DEPLOYMENT_ROOT = 11;

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
	 * The feature id for the '<em><b>Protocol</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL = 5;

	/**
	 * The feature id for the '<em><b>Protocol Cicseci</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI = 6;

	/**
	 * The feature id for the '<em><b>Protocol Cicsj2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C = 7;

	/**
	 * The feature id for the '<em><b>Protocol Cicsssl</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL = 8;

	/**
	 * The feature id for the '<em><b>Protocol Cicsws</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS = 9;

	/**
	 * The feature id for the '<em><b>Protocol Imsj2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C = 10;

	/**
	 * The feature id for the '<em><b>Protocol Imstcp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP = 11;

	/**
	 * The feature id for the '<em><b>Protocol Java400</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400 = 12;

	/**
	 * The feature id for the '<em><b>Protocol Java400j2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C = 13;

	/**
	 * The feature id for the '<em><b>Protocol Local</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL = 14;

	/**
	 * The feature id for the '<em><b>Protocol Ref</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_REF = 15;

	/**
	 * The feature id for the '<em><b>Protocol System ILocal</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL = 16;

	/**
	 * The feature id for the '<em><b>Protocol Tcpip</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP = 17;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__TARGET = 18;

	/**
	 * The feature id for the '<em><b>Target Build Descriptor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR = 19;

	/**
	 * The feature id for the '<em><b>Target Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT__TARGET_PROJECT = 20;

	/**
	 * The number of structural features of the '<em>EGL Deployment Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_DEPLOYMENT_ROOT_FEATURE_COUNT = 21;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IMSJ2CProtocolImpl <em>IMSJ2C Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IMSJ2CProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getIMSJ2CProtocol()
	 * @generated
	 */
	int IMSJ2C_PROTOCOL = 12;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSJ2C_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Attribute1</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSJ2C_PROTOCOL__ATTRIBUTE1 = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>IMSJ2C Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSJ2C_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IMSTCPProtocolImpl <em>IMSTCP Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IMSTCPProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getIMSTCPProtocol()
	 * @generated
	 */
	int IMSTCP_PROTOCOL = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSTCP_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Attribute1</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSTCP_PROTOCOL__ATTRIBUTE1 = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>IMSTCP Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMSTCP_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl <em>Include</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IncludeImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getInclude()
	 * @generated
	 */
	int INCLUDE = 14;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl <em>Java400 J2c Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getJava400J2cProtocol()
	 * @generated
	 */
	int JAVA400_J2C_PROTOCOL = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__CONVERSION_TABLE = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Current Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Libraries</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__LIBRARIES = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__PASSWORD = PROTOCOL_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL__USER_ID = PROTOCOL_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Java400 J2c Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_J2C_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400ProtocolImpl <em>Java400 Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.Java400ProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getJava400Protocol()
	 * @generated
	 */
	int JAVA400_PROTOCOL = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__CONVERSION_TABLE = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__LIBRARY = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__PASSWORD = PROTOCOL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL__USER_ID = PROTOCOL_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Java400 Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA400_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.LocalProtocolImpl <em>Local Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.LocalProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getLocalProtocol()
	 * @generated
	 */
	int LOCAL_PROTOCOL = 17;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The number of structural features of the '<em>Local Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl <em>Native Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getNativeBinding()
	 * @generated
	 */
	int NATIVE_BINDING = 18;

	/**
	 * The feature id for the '<em><b>Protocol Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NATIVE_BINDING__PROTOCOL_GROUP = 0;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NATIVE_BINDING__PROTOCOL = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NATIVE_BINDING__NAME = 2;

	/**
	 * The number of structural features of the '<em>Native Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NATIVE_BINDING_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParameterImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 19;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl <em>Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ParametersImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getParameters()
	 * @generated
	 */
	int PARAMETERS = 20;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl <em>Protocols</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getProtocols()
	 * @generated
	 */
	int PROTOCOLS = 22;

	/**
	 * The feature id for the '<em><b>Protocol Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROTOCOLS__PROTOCOL_GROUP = 0;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROTOCOLS__PROTOCOL = 1;

	/**
	 * The number of structural features of the '<em>Protocols</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROTOCOLS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ReferenceProtocolImpl <em>Reference Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ReferenceProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getReferenceProtocol()
	 * @generated
	 */
	int REFERENCE_PROTOCOL = 23;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_PROTOCOL__REF = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Reference Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl <em>Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ResourceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getResource()
	 * @generated
	 */
	int RESOURCE = 24;

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
	int RESOURCE_OMISSIONS = 25;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl <em>Rest Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestBinding()
	 * @generated
	 */
	int REST_BINDING = 26;

	/**
	 * The feature id for the '<em><b>Base URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING__BASE_URI = 0;

	/**
	 * The feature id for the '<em><b>Enable Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING__ENABLE_GENERATION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING__NAME = 2;

	/**
	 * The feature id for the '<em><b>Preserve Request Headers</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING__PRESERVE_REQUEST_HEADERS = 3;

	/**
	 * The feature id for the '<em><b>Session Cookie Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING__SESSION_COOKIE_ID = 4;

	/**
	 * The number of structural features of the '<em>Rest Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REST_BINDING_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl <em>Restservice</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestservice()
	 * @generated
	 */
	int RESTSERVICE = 27;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Enable Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__ENABLE_GENERATION = 1;

	/**
	 * The feature id for the '<em><b>Implementation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__IMPLEMENTATION = 2;

	/**
	 * The feature id for the '<em><b>Impl Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__IMPL_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__PROTOCOL = 4;

	/**
	 * The feature id for the '<em><b>Stateful</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__STATEFUL = 5;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE__URI = 6;

	/**
	 * The number of structural features of the '<em>Restservice</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICE_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestservicesImpl <em>Restservices</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestservicesImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestservices()
	 * @generated
	 */
	int RESTSERVICES = 28;

	/**
	 * The feature id for the '<em><b>Restservice</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICES__RESTSERVICE = 0;

	/**
	 * The number of structural features of the '<em>Restservices</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESTSERVICES_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl <em>RUI Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIApplication()
	 * @generated
	 */
	int RUI_APPLICATION = 29;

	/**
	 * The feature id for the '<em><b>Ruihandler</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__RUIHANDLER = 0;

	/**
	 * The feature id for the '<em><b>Resource Omissions</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__RESOURCE_OMISSIONS = 1;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__PARAMETERS = 2;

	/**
	 * The feature id for the '<em><b>Deploy All Handlers</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__DEPLOY_ALL_HANDLERS = 3;

	/**
	 * The feature id for the '<em><b>Support Dynamic Loading</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING = 4;

	/**
	 * The number of structural features of the '<em>RUI Application</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_APPLICATION_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl <em>RUI Handler</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIHandlerImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIHandler()
	 * @generated
	 */
	int RUI_HANDLER = 30;

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
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceImpl <em>RUI Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIResource()
	 * @generated
	 */
	int RUI_RESOURCE = 31;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_RESOURCE__ID = 0;

	/**
	 * The number of structural features of the '<em>RUI Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_RESOURCE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceOmissionsImpl <em>RUI Resource Omissions</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceOmissionsImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIResourceOmissions()
	 * @generated
	 */
	int RUI_RESOURCE_OMISSIONS = 32;

	/**
	 * The feature id for the '<em><b>Resource</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_RESOURCE_OMISSIONS__RESOURCE = 0;

	/**
	 * The number of structural features of the '<em>RUI Resource Omissions</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUI_RESOURCE_OMISSIONS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl <em>SQL Database Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSQLDatabaseBinding()
	 * @generated
	 */
	int SQL_DATABASE_BINDING = 33;

	/**
	 * The feature id for the '<em><b>Dbms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__DBMS = 0;

	/**
	 * The feature id for the '<em><b>Jar List</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__JAR_LIST = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__NAME = 2;

	/**
	 * The feature id for the '<em><b>Sql DB</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_DB = 3;

	/**
	 * The feature id for the '<em><b>Sql ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_ID = 4;

	/**
	 * The feature id for the '<em><b>Sql JDBC Driver Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS = 5;

	/**
	 * The feature id for the '<em><b>Sql JNDI Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_JNDI_NAME = 6;

	/**
	 * The feature id for the '<em><b>Sql Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_PASSWORD = 7;

	/**
	 * The feature id for the '<em><b>Sql Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_SCHEMA = 8;

	/**
	 * The feature id for the '<em><b>Sql Validation Connection URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL = 9;

	/**
	 * The number of structural features of the '<em>SQL Database Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_DATABASE_BINDING_FEATURE_COUNT = 10;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl <em>System IProtocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSystemIProtocol()
	 * @generated
	 */
	int SYSTEM_IPROTOCOL = 34;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_IPROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Binddir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_IPROTOCOL__BINDDIR = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_IPROTOCOL__LIBRARY = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>System IProtocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_IPROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl <em>TCPIP Protocol</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getTCPIPProtocol()
	 * @generated
	 */
	int TCPIP_PROTOCOL = 35;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TCPIP_PROTOCOL__NAME = PROTOCOL__NAME;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TCPIP_PROTOCOL__LOCATION = PROTOCOL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Server ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TCPIP_PROTOCOL__SERVER_ID = PROTOCOL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>TCPIP Protocol</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TCPIP_PROTOCOL_FEATURE_COUNT = PROTOCOL_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl <em>Web Binding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebBinding()
	 * @generated
	 */
	int WEB_BINDING = 36;

	/**
	 * The feature id for the '<em><b>Enable Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__ENABLE_GENERATION = 0;

	/**
	 * The feature id for the '<em><b>Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__INTERFACE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__NAME = 2;

	/**
	 * The feature id for the '<em><b>Soap Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__SOAP_VERSION = 3;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__URI = 4;

	/**
	 * The feature id for the '<em><b>Wsdl Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__WSDL_LOCATION = 5;

	/**
	 * The feature id for the '<em><b>Wsdl Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__WSDL_PORT = 6;

	/**
	 * The feature id for the '<em><b>Wsdl Service</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING__WSDL_SERVICE = 7;

	/**
	 * The number of structural features of the '<em>Web Binding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEB_BINDING_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl <em>Webservice</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebservice()
	 * @generated
	 */
	int WEBSERVICE = 37;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Enable Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__ENABLE_GENERATION = 1;

	/**
	 * The feature id for the '<em><b>Implementation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__IMPLEMENTATION = 2;

	/**
	 * The feature id for the '<em><b>Impl Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__IMPL_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__PROTOCOL = 4;

	/**
	 * The feature id for the '<em><b>Soap Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__SOAP_VERSION = 5;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__STYLE = 6;

	/**
	 * The feature id for the '<em><b>Transaction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__TRANSACTION = 7;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__URI = 8;

	/**
	 * The feature id for the '<em><b>Use Existing WSDL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__USE_EXISTING_WSDL = 9;

	/**
	 * The feature id for the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__USER_ID = 10;

	/**
	 * The feature id for the '<em><b>Wsdl Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__WSDL_LOCATION = 11;

	/**
	 * The feature id for the '<em><b>Wsdl Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__WSDL_PORT = 12;

	/**
	 * The feature id for the '<em><b>Wsdl Service</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE__WSDL_SERVICE = 13;

	/**
	 * The number of structural features of the '<em>Webservice</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICE_FEATURE_COUNT = 14;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebservicesImpl <em>Webservices</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebservicesImpl
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebservices()
	 * @generated
	 */
	int WEBSERVICES = 38;

	/**
	 * The feature id for the '<em><b>Webservice</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICES__WEBSERVICE = 0;

	/**
	 * The number of structural features of the '<em>Webservices</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEBSERVICES_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType <em>SOAP Version Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSOAPVersionType()
	 * @generated
	 */
	int SOAP_VERSION_TYPE = 39;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.StyleTypes <em>Style Types</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getStyleTypes()
	 * @generated
	 */
	int STYLE_TYPES = 40;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType <em>Webservice Runtime Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebserviceRuntimeType()
	 * @generated
	 */
	int WEBSERVICE_RUNTIME_TYPE = 41;

	/**
	 * The meta object id for the '<em>SOAP Version Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSOAPVersionTypeObject()
	 * @generated
	 */
	int SOAP_VERSION_TYPE_OBJECT = 42;

	/**
	 * The meta object id for the '<em>Style Types Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getStyleTypesObject()
	 * @generated
	 */
	int STYLE_TYPES_OBJECT = 43;

	/**
	 * The meta object id for the '<em>Webservice Runtime Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
	 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebserviceRuntimeTypeObject()
	 * @generated
	 */
	int WEBSERVICE_RUNTIME_TYPE_OBJECT = 44;


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
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getEglBinding <em>Egl Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Egl Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getEglBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_EglBinding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getWebBinding <em>Web Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Web Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getWebBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_WebBinding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getNativeBinding <em>Native Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Native Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getNativeBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_NativeBinding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getRestBinding <em>Rest Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rest Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getRestBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_RestBinding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings#getSqlDatabaseBinding <em>Sql Database Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sql Database Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings#getSqlDatabaseBinding()
	 * @see #getBindings()
	 * @generated
	 */
	EReference getBindings_SqlDatabaseBinding();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol <em>CICSECI Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CICSECI Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol
	 * @generated
	 */
	EClass getCICSECIProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getConversionTable <em>Conversion Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conversion Table</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getConversionTable()
	 * @see #getCICSECIProtocol()
	 * @generated
	 */
	EAttribute getCICSECIProtocol_ConversionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getCtgLocation <em>Ctg Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getCtgLocation()
	 * @see #getCICSECIProtocol()
	 * @generated
	 */
	EAttribute getCICSECIProtocol_CtgLocation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getCtgPort <em>Ctg Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Port</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getCtgPort()
	 * @see #getCICSECIProtocol()
	 * @generated
	 */
	EAttribute getCICSECIProtocol_CtgPort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getLocation()
	 * @see #getCICSECIProtocol()
	 * @generated
	 */
	EAttribute getCICSECIProtocol_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getServerID <em>Server ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol#getServerID()
	 * @see #getCICSECIProtocol()
	 * @generated
	 */
	EAttribute getCICSECIProtocol_ServerID();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol <em>CICSJ2C Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CICSJ2C Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol
	 * @generated
	 */
	EClass getCICSJ2CProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol#getConversionTable <em>Conversion Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conversion Table</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol#getConversionTable()
	 * @see #getCICSJ2CProtocol()
	 * @generated
	 */
	EAttribute getCICSJ2CProtocol_ConversionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol#getLocation()
	 * @see #getCICSJ2CProtocol()
	 * @generated
	 */
	EAttribute getCICSJ2CProtocol_Location();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol <em>CICSSSL Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CICSSSL Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol
	 * @generated
	 */
	EClass getCICSSSLProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getConversionTable <em>Conversion Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conversion Table</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getConversionTable()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_ConversionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStore <em>Ctg Key Store</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Key Store</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStore()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_CtgKeyStore();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStorePassword <em>Ctg Key Store Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Key Store Password</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStorePassword()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_CtgKeyStorePassword();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgLocation <em>Ctg Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgLocation()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_CtgLocation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgPort <em>Ctg Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ctg Port</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgPort()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_CtgPort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getLocation()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getServerID <em>Server ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getServerID()
	 * @see #getCICSSSLProtocol()
	 * @generated
	 */
	EAttribute getCICSSSLProtocol_ServerID();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol <em>CICSWS Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CICSWS Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol
	 * @generated
	 */
	EClass getCICSWSProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getTransaction <em>Transaction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getTransaction()
	 * @see #getCICSWSProtocol()
	 * @generated
	 */
	EAttribute getCICSWSProtocol_Transaction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getUserID <em>User ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>User ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getUserID()
	 * @see #getCICSWSProtocol()
	 * @generated
	 */
	EAttribute getCICSWSProtocol_UserID();

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
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getProtocols <em>Protocols</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocols</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getProtocols()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Protocols();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getWebservices <em>Webservices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Webservices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getWebservices()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Webservices();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRestservices <em>Restservices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Restservices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getRestservices()
	 * @see #getDeployment()
	 * @generated
	 */
	EReference getDeployment_Restservices();

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
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getWebserviceRuntime <em>Webservice Runtime</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Webservice Runtime</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getWebserviceRuntime()
	 * @see #getDeployment()
	 * @generated
	 */
	EAttribute getDeployment_WebserviceRuntime();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment#getAlias()
	 * @see #getDeployment()
	 * @generated
	 */
	EAttribute getDeployment_Alias();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor <em>Build Descriptor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Build Descriptor</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor
	 * @generated
	 */
	EClass getDeploymentBuildDescriptor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor#getFileName <em>File Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor#getFileName()
	 * @see #getDeploymentBuildDescriptor()
	 * @generated
	 */
	EAttribute getDeploymentBuildDescriptor_FileName();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding <em>EGL Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGL Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding
	 * @generated
	 */
	EClass getEGLBinding();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocolGroup <em>Protocol Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Protocol Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocolGroup()
	 * @see #getEGLBinding()
	 * @generated
	 */
	EAttribute getEGLBinding_ProtocolGroup();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocol()
	 * @see #getEGLBinding()
	 * @generated
	 */
	EReference getEGLBinding_Protocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getAlias()
	 * @see #getEGLBinding()
	 * @generated
	 */
	EAttribute getEGLBinding_Alias();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getName()
	 * @see #getEGLBinding()
	 * @generated
	 */
	EAttribute getEGLBinding_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getServiceName <em>Service Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Service Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getServiceName()
	 * @see #getEGLBinding()
	 * @generated
	 */
	EAttribute getEGLBinding_ServiceName();

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
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocol()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_Protocol();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicseci <em>Protocol Cicseci</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Cicseci</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicseci()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolCicseci();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsj2c <em>Protocol Cicsj2c</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Cicsj2c</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsj2c()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolCicsj2c();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsssl <em>Protocol Cicsssl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Cicsssl</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsssl()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolCicsssl();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsws <em>Protocol Cicsws</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Cicsws</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsws()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolCicsws();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImsj2c <em>Protocol Imsj2c</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Imsj2c</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImsj2c()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolImsj2c();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImstcp <em>Protocol Imstcp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Imstcp</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImstcp()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolImstcp();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400 <em>Protocol Java400</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Java400</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolJava400();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400j2c <em>Protocol Java400j2c</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Java400j2c</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400j2c()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolJava400j2c();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolLocal <em>Protocol Local</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Local</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolLocal()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolLocal();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolRef <em>Protocol Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Ref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolRef()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolRef();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolSystemILocal <em>Protocol System ILocal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol System ILocal</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolSystemILocal()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolSystemILocal();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolTcpip <em>Protocol Tcpip</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol Tcpip</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolTcpip()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_ProtocolTcpip();

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
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetBuildDescriptor <em>Target Build Descriptor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Target Build Descriptor</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetBuildDescriptor()
	 * @see #getEGLDeploymentRoot()
	 * @generated
	 */
	EReference getEGLDeploymentRoot_TargetBuildDescriptor();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol <em>IMSJ2C Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IMSJ2C Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol
	 * @generated
	 */
	EClass getIMSJ2CProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol#getAttribute1 <em>Attribute1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute1</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol#getAttribute1()
	 * @see #getIMSJ2CProtocol()
	 * @generated
	 */
	EAttribute getIMSJ2CProtocol_Attribute1();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol <em>IMSTCP Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IMSTCP Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol
	 * @generated
	 */
	EClass getIMSTCPProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol#getAttribute1 <em>Attribute1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute1</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol#getAttribute1()
	 * @see #getIMSTCPProtocol()
	 * @generated
	 */
	EAttribute getIMSTCPProtocol_Attribute1();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol <em>Java400 J2c Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Java400 J2c Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol
	 * @generated
	 */
	EClass getJava400J2cProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getConversionTable <em>Conversion Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conversion Table</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getConversionTable()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_ConversionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getCurrentLibrary <em>Current Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Current Library</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getCurrentLibrary()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_CurrentLibrary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLibraries <em>Libraries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Libraries</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLibraries()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_Libraries();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLocation()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Password</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getPassword()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_Password();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getUserID <em>User ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>User ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getUserID()
	 * @see #getJava400J2cProtocol()
	 * @generated
	 */
	EAttribute getJava400J2cProtocol_UserID();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol <em>Java400 Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Java400 Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol
	 * @generated
	 */
	EClass getJava400Protocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getConversionTable <em>Conversion Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conversion Table</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getConversionTable()
	 * @see #getJava400Protocol()
	 * @generated
	 */
	EAttribute getJava400Protocol_ConversionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLibrary <em>Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Library</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLibrary()
	 * @see #getJava400Protocol()
	 * @generated
	 */
	EAttribute getJava400Protocol_Library();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLocation()
	 * @see #getJava400Protocol()
	 * @generated
	 */
	EAttribute getJava400Protocol_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Password</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getPassword()
	 * @see #getJava400Protocol()
	 * @generated
	 */
	EAttribute getJava400Protocol_Password();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getUserID <em>User ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>User ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getUserID()
	 * @see #getJava400Protocol()
	 * @generated
	 */
	EAttribute getJava400Protocol_UserID();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol <em>Local Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol
	 * @generated
	 */
	EClass getLocalProtocol();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding <em>Native Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Native Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.NativeBinding
	 * @generated
	 */
	EClass getNativeBinding();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getProtocolGroup <em>Protocol Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Protocol Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getProtocolGroup()
	 * @see #getNativeBinding()
	 * @generated
	 */
	EAttribute getNativeBinding_ProtocolGroup();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getProtocol()
	 * @see #getNativeBinding()
	 * @generated
	 */
	EReference getNativeBinding_Protocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.NativeBinding#getName()
	 * @see #getNativeBinding()
	 * @generated
	 */
	EAttribute getNativeBinding_Name();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocol
	 * @generated
	 */
	EClass getProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocol#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocol#getName()
	 * @see #getProtocol()
	 * @generated
	 */
	EAttribute getProtocol_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols <em>Protocols</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Protocols</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocols
	 * @generated
	 */
	EClass getProtocols();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocolGroup <em>Protocol Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Protocol Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocolGroup()
	 * @see #getProtocols()
	 * @generated
	 */
	EAttribute getProtocols_ProtocolGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocol()
	 * @see #getProtocols()
	 * @generated
	 */
	EReference getProtocols_Protocol();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol <em>Reference Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol
	 * @generated
	 */
	EClass getReferenceProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol#getRef()
	 * @see #getReferenceProtocol()
	 * @generated
	 */
	EAttribute getReferenceProtocol_Ref();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding <em>Rest Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rest Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding
	 * @generated
	 */
	EClass getRestBinding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getBaseURI <em>Base URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base URI</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getBaseURI()
	 * @see #getRestBinding()
	 * @generated
	 */
	EAttribute getRestBinding_BaseURI();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration <em>Enable Generation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enable Generation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isEnableGeneration()
	 * @see #getRestBinding()
	 * @generated
	 */
	EAttribute getRestBinding_EnableGeneration();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getName()
	 * @see #getRestBinding()
	 * @generated
	 */
	EAttribute getRestBinding_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders <em>Preserve Request Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Preserve Request Headers</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding#isPreserveRequestHeaders()
	 * @see #getRestBinding()
	 * @generated
	 */
	EAttribute getRestBinding_PreserveRequestHeaders();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getSessionCookieId <em>Session Cookie Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Session Cookie Id</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding#getSessionCookieId()
	 * @see #getRestBinding()
	 * @generated
	 */
	EAttribute getRestBinding_SessionCookieId();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice <em>Restservice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Restservice</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice
	 * @generated
	 */
	EClass getRestservice();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#getParameters()
	 * @see #getRestservice()
	 * @generated
	 */
	EReference getRestservice_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration <em>Enable Generation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enable Generation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#isEnableGeneration()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_EnableGeneration();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplementation <em>Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Implementation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplementation()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_Implementation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType <em>Impl Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Impl Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#getImplType()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_ImplType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#getProtocol()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_Protocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful <em>Stateful</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stateful</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#isStateful()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_Stateful();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice#getUri <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uri</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice#getUri()
	 * @see #getRestservice()
	 * @generated
	 */
	EAttribute getRestservice_Uri();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservices <em>Restservices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Restservices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservices
	 * @generated
	 */
	EClass getRestservices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservices#getRestservice <em>Restservice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Restservice</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservices#getRestservice()
	 * @see #getRestservices()
	 * @generated
	 */
	EReference getRestservices_Restservice();

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
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getResourceOmissions <em>Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Resource Omissions</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication#getResourceOmissions()
	 * @see #getRUIApplication()
	 * @generated
	 */
	EReference getRUIApplication_ResourceOmissions();

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
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResource <em>RUI Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RUI Resource</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResource
	 * @generated
	 */
	EClass getRUIResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResource#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResource#getId()
	 * @see #getRUIResource()
	 * @generated
	 */
	EAttribute getRUIResource_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions <em>RUI Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RUI Resource Omissions</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions
	 * @generated
	 */
	EClass getRUIResourceOmissions();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions#getResource <em>Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resource</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions#getResource()
	 * @see #getRUIResourceOmissions()
	 * @generated
	 */
	EReference getRUIResourceOmissions_Resource();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding <em>SQL Database Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>SQL Database Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding
	 * @generated
	 */
	EClass getSQLDatabaseBinding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getDbms <em>Dbms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dbms</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getDbms()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_Dbms();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getJarList <em>Jar List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Jar List</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getJarList()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_JarList();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getName()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlDB <em>Sql DB</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql DB</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlDB()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlDB();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlID <em>Sql ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlID()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlID();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJDBCDriverClass <em>Sql JDBC Driver Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql JDBC Driver Class</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJDBCDriverClass()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlJDBCDriverClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJNDIName <em>Sql JNDI Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql JNDI Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJNDIName()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlJNDIName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlPassword <em>Sql Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql Password</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlPassword()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlPassword();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlSchema <em>Sql Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql Schema</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlSchema()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlValidationConnectionURL <em>Sql Validation Connection URL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql Validation Connection URL</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlValidationConnectionURL()
	 * @see #getSQLDatabaseBinding()
	 * @generated
	 */
	EAttribute getSQLDatabaseBinding_SqlValidationConnectionURL();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol <em>System IProtocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>System IProtocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol
	 * @generated
	 */
	EClass getSystemIProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getBinddir <em>Binddir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Binddir</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getBinddir()
	 * @see #getSystemIProtocol()
	 * @generated
	 */
	EAttribute getSystemIProtocol_Binddir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getLibrary <em>Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Library</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getLibrary()
	 * @see #getSystemIProtocol()
	 * @generated
	 */
	EAttribute getSystemIProtocol_Library();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol <em>TCPIP Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>TCPIP Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol
	 * @generated
	 */
	EClass getTCPIPProtocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol#getLocation()
	 * @see #getTCPIPProtocol()
	 * @generated
	 */
	EAttribute getTCPIPProtocol_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol#getServerID <em>Server ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol#getServerID()
	 * @see #getTCPIPProtocol()
	 * @generated
	 */
	EAttribute getTCPIPProtocol_ServerID();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding <em>Web Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Web Binding</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding
	 * @generated
	 */
	EClass getWebBinding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration <em>Enable Generation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enable Generation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#isEnableGeneration()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_EnableGeneration();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getInterface <em>Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Interface</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getInterface()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_Interface();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getName()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion <em>Soap Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Soap Version</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getSoapVersion()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_SoapVersion();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getUri <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uri</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getUri()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_Uri();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlLocation <em>Wsdl Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlLocation()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_WsdlLocation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlPort <em>Wsdl Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Port</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlPort()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_WsdlPort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlService <em>Wsdl Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Service</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding#getWsdlService()
	 * @see #getWebBinding()
	 * @generated
	 */
	EAttribute getWebBinding_WsdlService();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice <em>Webservice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Webservice</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice
	 * @generated
	 */
	EClass getWebservice();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getParameters()
	 * @see #getWebservice()
	 * @generated
	 */
	EReference getWebservice_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration <em>Enable Generation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enable Generation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#isEnableGeneration()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_EnableGeneration();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplementation <em>Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Implementation</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplementation()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_Implementation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType <em>Impl Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Impl Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getImplType()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_ImplType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Protocol</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getProtocol()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_Protocol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion <em>Soap Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Soap Version</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getSoapVersion()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_SoapVersion();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getStyle()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getTransaction <em>Transaction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getTransaction()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_Transaction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUri <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uri</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUri()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_Uri();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL <em>Use Existing WSDL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Use Existing WSDL</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#isUseExistingWSDL()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_UseExistingWSDL();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUserID <em>User ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>User ID</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getUserID()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_UserID();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlLocation <em>Wsdl Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlLocation()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_WsdlLocation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlPort <em>Wsdl Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Port</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlPort()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_WsdlPort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlService <em>Wsdl Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wsdl Service</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice#getWsdlService()
	 * @see #getWebservice()
	 * @generated
	 */
	EAttribute getWebservice_WsdlService();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservices <em>Webservices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Webservices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservices
	 * @generated
	 */
	EClass getWebservices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservices#getWebservice <em>Webservice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Webservice</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservices#getWebservice()
	 * @see #getWebservices()
	 * @generated
	 */
	EReference getWebservices_Webservice();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType <em>SOAP Version Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>SOAP Version Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @generated
	 */
	EEnum getSOAPVersionType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.edt.ide.ui.internal.deployment.StyleTypes <em>Style Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Style Types</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @generated
	 */
	EEnum getStyleTypes();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType <em>Webservice Runtime Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Webservice Runtime Type</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
	 * @generated
	 */
	EEnum getWebserviceRuntimeType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType <em>SOAP Version Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>SOAP Version Type Object</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
	 * @model instanceClass="org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType"
	 *        extendedMetaData="name='SOAPVersionType:Object' baseType='SOAPVersionType'"
	 * @generated
	 */
	EDataType getSOAPVersionTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.edt.ide.ui.internal.deployment.StyleTypes <em>Style Types Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Style Types Object</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
	 * @model instanceClass="org.eclipse.edt.ide.ui.internal.deployment.StyleTypes"
	 *        extendedMetaData="name='StyleTypes:Object' baseType='StyleTypes'"
	 * @generated
	 */
	EDataType getStyleTypesObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType <em>Webservice Runtime Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Webservice Runtime Type Object</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
	 * @model instanceClass="org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType"
	 *        extendedMetaData="name='WebserviceRuntimeType:Object' baseType='WebserviceRuntimeType'"
	 * @generated
	 */
	EDataType getWebserviceRuntimeTypeObject();

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
	interface Literals {
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
		 * The meta object literal for the '<em><b>Egl Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__EGL_BINDING = eINSTANCE.getBindings_EglBinding();

		/**
		 * The meta object literal for the '<em><b>Web Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__WEB_BINDING = eINSTANCE.getBindings_WebBinding();

		/**
		 * The meta object literal for the '<em><b>Native Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__NATIVE_BINDING = eINSTANCE.getBindings_NativeBinding();

		/**
		 * The meta object literal for the '<em><b>Rest Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__REST_BINDING = eINSTANCE.getBindings_RestBinding();

		/**
		 * The meta object literal for the '<em><b>Sql Database Binding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINDINGS__SQL_DATABASE_BINDING = eINSTANCE.getBindings_SqlDatabaseBinding();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl <em>CICSECI Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSECIProtocol()
		 * @generated
		 */
		EClass CICSECI_PROTOCOL = eINSTANCE.getCICSECIProtocol();

		/**
		 * The meta object literal for the '<em><b>Conversion Table</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSECI_PROTOCOL__CONVERSION_TABLE = eINSTANCE.getCICSECIProtocol_ConversionTable();

		/**
		 * The meta object literal for the '<em><b>Ctg Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSECI_PROTOCOL__CTG_LOCATION = eINSTANCE.getCICSECIProtocol_CtgLocation();

		/**
		 * The meta object literal for the '<em><b>Ctg Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSECI_PROTOCOL__CTG_PORT = eINSTANCE.getCICSECIProtocol_CtgPort();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSECI_PROTOCOL__LOCATION = eINSTANCE.getCICSECIProtocol_Location();

		/**
		 * The meta object literal for the '<em><b>Server ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSECI_PROTOCOL__SERVER_ID = eINSTANCE.getCICSECIProtocol_ServerID();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl <em>CICSJ2C Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSJ2CProtocol()
		 * @generated
		 */
		EClass CICSJ2C_PROTOCOL = eINSTANCE.getCICSJ2CProtocol();

		/**
		 * The meta object literal for the '<em><b>Conversion Table</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSJ2C_PROTOCOL__CONVERSION_TABLE = eINSTANCE.getCICSJ2CProtocol_ConversionTable();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSJ2C_PROTOCOL__LOCATION = eINSTANCE.getCICSJ2CProtocol_Location();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSSSLProtocolImpl <em>CICSSSL Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSSSLProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSSSLProtocol()
		 * @generated
		 */
		EClass CICSSSL_PROTOCOL = eINSTANCE.getCICSSSLProtocol();

		/**
		 * The meta object literal for the '<em><b>Conversion Table</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__CONVERSION_TABLE = eINSTANCE.getCICSSSLProtocol_ConversionTable();

		/**
		 * The meta object literal for the '<em><b>Ctg Key Store</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__CTG_KEY_STORE = eINSTANCE.getCICSSSLProtocol_CtgKeyStore();

		/**
		 * The meta object literal for the '<em><b>Ctg Key Store Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__CTG_KEY_STORE_PASSWORD = eINSTANCE.getCICSSSLProtocol_CtgKeyStorePassword();

		/**
		 * The meta object literal for the '<em><b>Ctg Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__CTG_LOCATION = eINSTANCE.getCICSSSLProtocol_CtgLocation();

		/**
		 * The meta object literal for the '<em><b>Ctg Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__CTG_PORT = eINSTANCE.getCICSSSLProtocol_CtgPort();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__LOCATION = eINSTANCE.getCICSSSLProtocol_Location();

		/**
		 * The meta object literal for the '<em><b>Server ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSSSL_PROTOCOL__SERVER_ID = eINSTANCE.getCICSSSLProtocol_ServerID();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl <em>CICSWS Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getCICSWSProtocol()
		 * @generated
		 */
		EClass CICSWS_PROTOCOL = eINSTANCE.getCICSWSProtocol();

		/**
		 * The meta object literal for the '<em><b>Transaction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSWS_PROTOCOL__TRANSACTION = eINSTANCE.getCICSWSProtocol_Transaction();

		/**
		 * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CICSWS_PROTOCOL__USER_ID = eINSTANCE.getCICSWSProtocol_UserID();

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
		 * The meta object literal for the '<em><b>Protocols</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__PROTOCOLS = eINSTANCE.getDeployment_Protocols();

		/**
		 * The meta object literal for the '<em><b>Webservices</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__WEBSERVICES = eINSTANCE.getDeployment_Webservices();

		/**
		 * The meta object literal for the '<em><b>Restservices</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPLOYMENT__RESTSERVICES = eINSTANCE.getDeployment_Restservices();

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
		 * The meta object literal for the '<em><b>Webservice Runtime</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT__WEBSERVICE_RUNTIME = eINSTANCE.getDeployment_WebserviceRuntime();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT__ALIAS = eINSTANCE.getDeployment_Alias();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentBuildDescriptorImpl <em>Build Descriptor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentBuildDescriptorImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getDeploymentBuildDescriptor()
		 * @generated
		 */
		EClass DEPLOYMENT_BUILD_DESCRIPTOR = eINSTANCE.getDeploymentBuildDescriptor();

		/**
		 * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPLOYMENT_BUILD_DESCRIPTOR__FILE_NAME = eINSTANCE.getDeploymentBuildDescriptor_FileName();

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
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl <em>EGL Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getEGLBinding()
		 * @generated
		 */
		EClass EGL_BINDING = eINSTANCE.getEGLBinding();

		/**
		 * The meta object literal for the '<em><b>Protocol Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_BINDING__PROTOCOL_GROUP = eINSTANCE.getEGLBinding_ProtocolGroup();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_BINDING__PROTOCOL = eINSTANCE.getEGLBinding_Protocol();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_BINDING__ALIAS = eINSTANCE.getEGLBinding_Alias();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_BINDING__NAME = eINSTANCE.getEGLBinding_Name();

		/**
		 * The meta object literal for the '<em><b>Service Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_BINDING__SERVICE_NAME = eINSTANCE.getEGLBinding_ServiceName();

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
		 * The meta object literal for the '<em><b>Protocol</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL = eINSTANCE.getEGLDeploymentRoot_Protocol();

		/**
		 * The meta object literal for the '<em><b>Protocol Cicseci</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI = eINSTANCE.getEGLDeploymentRoot_ProtocolCicseci();

		/**
		 * The meta object literal for the '<em><b>Protocol Cicsj2c</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C = eINSTANCE.getEGLDeploymentRoot_ProtocolCicsj2c();

		/**
		 * The meta object literal for the '<em><b>Protocol Cicsssl</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL = eINSTANCE.getEGLDeploymentRoot_ProtocolCicsssl();

		/**
		 * The meta object literal for the '<em><b>Protocol Cicsws</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS = eINSTANCE.getEGLDeploymentRoot_ProtocolCicsws();

		/**
		 * The meta object literal for the '<em><b>Protocol Imsj2c</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C = eINSTANCE.getEGLDeploymentRoot_ProtocolImsj2c();

		/**
		 * The meta object literal for the '<em><b>Protocol Imstcp</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP = eINSTANCE.getEGLDeploymentRoot_ProtocolImstcp();

		/**
		 * The meta object literal for the '<em><b>Protocol Java400</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400 = eINSTANCE.getEGLDeploymentRoot_ProtocolJava400();

		/**
		 * The meta object literal for the '<em><b>Protocol Java400j2c</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C = eINSTANCE.getEGLDeploymentRoot_ProtocolJava400j2c();

		/**
		 * The meta object literal for the '<em><b>Protocol Local</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL = eINSTANCE.getEGLDeploymentRoot_ProtocolLocal();

		/**
		 * The meta object literal for the '<em><b>Protocol Ref</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_REF = eINSTANCE.getEGLDeploymentRoot_ProtocolRef();

		/**
		 * The meta object literal for the '<em><b>Protocol System ILocal</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL = eINSTANCE.getEGLDeploymentRoot_ProtocolSystemILocal();

		/**
		 * The meta object literal for the '<em><b>Protocol Tcpip</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP = eINSTANCE.getEGLDeploymentRoot_ProtocolTcpip();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__TARGET = eINSTANCE.getEGLDeploymentRoot_Target();

		/**
		 * The meta object literal for the '<em><b>Target Build Descriptor</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR = eINSTANCE.getEGLDeploymentRoot_TargetBuildDescriptor();

		/**
		 * The meta object literal for the '<em><b>Target Project</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_DEPLOYMENT_ROOT__TARGET_PROJECT = eINSTANCE.getEGLDeploymentRoot_TargetProject();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IMSJ2CProtocolImpl <em>IMSJ2C Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IMSJ2CProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getIMSJ2CProtocol()
		 * @generated
		 */
		EClass IMSJ2C_PROTOCOL = eINSTANCE.getIMSJ2CProtocol();

		/**
		 * The meta object literal for the '<em><b>Attribute1</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMSJ2C_PROTOCOL__ATTRIBUTE1 = eINSTANCE.getIMSJ2CProtocol_Attribute1();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IMSTCPProtocolImpl <em>IMSTCP Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.IMSTCPProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getIMSTCPProtocol()
		 * @generated
		 */
		EClass IMSTCP_PROTOCOL = eINSTANCE.getIMSTCPProtocol();

		/**
		 * The meta object literal for the '<em><b>Attribute1</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMSTCP_PROTOCOL__ATTRIBUTE1 = eINSTANCE.getIMSTCPProtocol_Attribute1();

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
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl <em>Java400 J2c Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getJava400J2cProtocol()
		 * @generated
		 */
		EClass JAVA400_J2C_PROTOCOL = eINSTANCE.getJava400J2cProtocol();

		/**
		 * The meta object literal for the '<em><b>Conversion Table</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__CONVERSION_TABLE = eINSTANCE.getJava400J2cProtocol_ConversionTable();

		/**
		 * The meta object literal for the '<em><b>Current Library</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY = eINSTANCE.getJava400J2cProtocol_CurrentLibrary();

		/**
		 * The meta object literal for the '<em><b>Libraries</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__LIBRARIES = eINSTANCE.getJava400J2cProtocol_Libraries();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__LOCATION = eINSTANCE.getJava400J2cProtocol_Location();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__PASSWORD = eINSTANCE.getJava400J2cProtocol_Password();

		/**
		 * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_J2C_PROTOCOL__USER_ID = eINSTANCE.getJava400J2cProtocol_UserID();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400ProtocolImpl <em>Java400 Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.Java400ProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getJava400Protocol()
		 * @generated
		 */
		EClass JAVA400_PROTOCOL = eINSTANCE.getJava400Protocol();

		/**
		 * The meta object literal for the '<em><b>Conversion Table</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_PROTOCOL__CONVERSION_TABLE = eINSTANCE.getJava400Protocol_ConversionTable();

		/**
		 * The meta object literal for the '<em><b>Library</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_PROTOCOL__LIBRARY = eINSTANCE.getJava400Protocol_Library();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_PROTOCOL__LOCATION = eINSTANCE.getJava400Protocol_Location();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_PROTOCOL__PASSWORD = eINSTANCE.getJava400Protocol_Password();

		/**
		 * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA400_PROTOCOL__USER_ID = eINSTANCE.getJava400Protocol_UserID();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.LocalProtocolImpl <em>Local Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.LocalProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getLocalProtocol()
		 * @generated
		 */
		EClass LOCAL_PROTOCOL = eINSTANCE.getLocalProtocol();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl <em>Native Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getNativeBinding()
		 * @generated
		 */
		EClass NATIVE_BINDING = eINSTANCE.getNativeBinding();

		/**
		 * The meta object literal for the '<em><b>Protocol Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NATIVE_BINDING__PROTOCOL_GROUP = eINSTANCE.getNativeBinding_ProtocolGroup();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NATIVE_BINDING__PROTOCOL = eINSTANCE.getNativeBinding_Protocol();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NATIVE_BINDING__NAME = eINSTANCE.getNativeBinding_Name();

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
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolImpl <em>Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getProtocol()
		 * @generated
		 */
		EClass PROTOCOL = eINSTANCE.getProtocol();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROTOCOL__NAME = eINSTANCE.getProtocol_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl <em>Protocols</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getProtocols()
		 * @generated
		 */
		EClass PROTOCOLS = eINSTANCE.getProtocols();

		/**
		 * The meta object literal for the '<em><b>Protocol Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROTOCOLS__PROTOCOL_GROUP = eINSTANCE.getProtocols_ProtocolGroup();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROTOCOLS__PROTOCOL = eINSTANCE.getProtocols_Protocol();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ReferenceProtocolImpl <em>Reference Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.ReferenceProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getReferenceProtocol()
		 * @generated
		 */
		EClass REFERENCE_PROTOCOL = eINSTANCE.getReferenceProtocol();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_PROTOCOL__REF = eINSTANCE.getReferenceProtocol_Ref();

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
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl <em>Rest Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestBinding()
		 * @generated
		 */
		EClass REST_BINDING = eINSTANCE.getRestBinding();

		/**
		 * The meta object literal for the '<em><b>Base URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REST_BINDING__BASE_URI = eINSTANCE.getRestBinding_BaseURI();

		/**
		 * The meta object literal for the '<em><b>Enable Generation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REST_BINDING__ENABLE_GENERATION = eINSTANCE.getRestBinding_EnableGeneration();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REST_BINDING__NAME = eINSTANCE.getRestBinding_Name();

		/**
		 * The meta object literal for the '<em><b>Preserve Request Headers</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REST_BINDING__PRESERVE_REQUEST_HEADERS = eINSTANCE.getRestBinding_PreserveRequestHeaders();

		/**
		 * The meta object literal for the '<em><b>Session Cookie Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REST_BINDING__SESSION_COOKIE_ID = eINSTANCE.getRestBinding_SessionCookieId();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl <em>Restservice</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestservice()
		 * @generated
		 */
		EClass RESTSERVICE = eINSTANCE.getRestservice();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESTSERVICE__PARAMETERS = eINSTANCE.getRestservice_Parameters();

		/**
		 * The meta object literal for the '<em><b>Enable Generation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__ENABLE_GENERATION = eINSTANCE.getRestservice_EnableGeneration();

		/**
		 * The meta object literal for the '<em><b>Implementation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__IMPLEMENTATION = eINSTANCE.getRestservice_Implementation();

		/**
		 * The meta object literal for the '<em><b>Impl Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__IMPL_TYPE = eINSTANCE.getRestservice_ImplType();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__PROTOCOL = eINSTANCE.getRestservice_Protocol();

		/**
		 * The meta object literal for the '<em><b>Stateful</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__STATEFUL = eINSTANCE.getRestservice_Stateful();

		/**
		 * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESTSERVICE__URI = eINSTANCE.getRestservice_Uri();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestservicesImpl <em>Restservices</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RestservicesImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRestservices()
		 * @generated
		 */
		EClass RESTSERVICES = eINSTANCE.getRestservices();

		/**
		 * The meta object literal for the '<em><b>Restservice</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESTSERVICES__RESTSERVICE = eINSTANCE.getRestservices_Restservice();

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
		 * The meta object literal for the '<em><b>Resource Omissions</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUI_APPLICATION__RESOURCE_OMISSIONS = eINSTANCE.getRUIApplication_ResourceOmissions();

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
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceImpl <em>RUI Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIResource()
		 * @generated
		 */
		EClass RUI_RESOURCE = eINSTANCE.getRUIResource();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUI_RESOURCE__ID = eINSTANCE.getRUIResource_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceOmissionsImpl <em>RUI Resource Omissions</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.RUIResourceOmissionsImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getRUIResourceOmissions()
		 * @generated
		 */
		EClass RUI_RESOURCE_OMISSIONS = eINSTANCE.getRUIResourceOmissions();

		/**
		 * The meta object literal for the '<em><b>Resource</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUI_RESOURCE_OMISSIONS__RESOURCE = eINSTANCE.getRUIResourceOmissions_Resource();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl <em>SQL Database Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSQLDatabaseBinding()
		 * @generated
		 */
		EClass SQL_DATABASE_BINDING = eINSTANCE.getSQLDatabaseBinding();

		/**
		 * The meta object literal for the '<em><b>Dbms</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__DBMS = eINSTANCE.getSQLDatabaseBinding_Dbms();

		/**
		 * The meta object literal for the '<em><b>Jar List</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__JAR_LIST = eINSTANCE.getSQLDatabaseBinding_JarList();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__NAME = eINSTANCE.getSQLDatabaseBinding_Name();

		/**
		 * The meta object literal for the '<em><b>Sql DB</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_DB = eINSTANCE.getSQLDatabaseBinding_SqlDB();

		/**
		 * The meta object literal for the '<em><b>Sql ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_ID = eINSTANCE.getSQLDatabaseBinding_SqlID();

		/**
		 * The meta object literal for the '<em><b>Sql JDBC Driver Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS = eINSTANCE.getSQLDatabaseBinding_SqlJDBCDriverClass();

		/**
		 * The meta object literal for the '<em><b>Sql JNDI Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_JNDI_NAME = eINSTANCE.getSQLDatabaseBinding_SqlJNDIName();

		/**
		 * The meta object literal for the '<em><b>Sql Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_PASSWORD = eINSTANCE.getSQLDatabaseBinding_SqlPassword();

		/**
		 * The meta object literal for the '<em><b>Sql Schema</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_SCHEMA = eINSTANCE.getSQLDatabaseBinding_SqlSchema();

		/**
		 * The meta object literal for the '<em><b>Sql Validation Connection URL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL = eINSTANCE.getSQLDatabaseBinding_SqlValidationConnectionURL();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl <em>System IProtocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSystemIProtocol()
		 * @generated
		 */
		EClass SYSTEM_IPROTOCOL = eINSTANCE.getSystemIProtocol();

		/**
		 * The meta object literal for the '<em><b>Binddir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYSTEM_IPROTOCOL__BINDDIR = eINSTANCE.getSystemIProtocol_Binddir();

		/**
		 * The meta object literal for the '<em><b>Library</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYSTEM_IPROTOCOL__LIBRARY = eINSTANCE.getSystemIProtocol_Library();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl <em>TCPIP Protocol</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getTCPIPProtocol()
		 * @generated
		 */
		EClass TCPIP_PROTOCOL = eINSTANCE.getTCPIPProtocol();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TCPIP_PROTOCOL__LOCATION = eINSTANCE.getTCPIPProtocol_Location();

		/**
		 * The meta object literal for the '<em><b>Server ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TCPIP_PROTOCOL__SERVER_ID = eINSTANCE.getTCPIPProtocol_ServerID();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl <em>Web Binding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebBinding()
		 * @generated
		 */
		EClass WEB_BINDING = eINSTANCE.getWebBinding();

		/**
		 * The meta object literal for the '<em><b>Enable Generation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__ENABLE_GENERATION = eINSTANCE.getWebBinding_EnableGeneration();

		/**
		 * The meta object literal for the '<em><b>Interface</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__INTERFACE = eINSTANCE.getWebBinding_Interface();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__NAME = eINSTANCE.getWebBinding_Name();

		/**
		 * The meta object literal for the '<em><b>Soap Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__SOAP_VERSION = eINSTANCE.getWebBinding_SoapVersion();

		/**
		 * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__URI = eINSTANCE.getWebBinding_Uri();

		/**
		 * The meta object literal for the '<em><b>Wsdl Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__WSDL_LOCATION = eINSTANCE.getWebBinding_WsdlLocation();

		/**
		 * The meta object literal for the '<em><b>Wsdl Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__WSDL_PORT = eINSTANCE.getWebBinding_WsdlPort();

		/**
		 * The meta object literal for the '<em><b>Wsdl Service</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEB_BINDING__WSDL_SERVICE = eINSTANCE.getWebBinding_WsdlService();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl <em>Webservice</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebservice()
		 * @generated
		 */
		EClass WEBSERVICE = eINSTANCE.getWebservice();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WEBSERVICE__PARAMETERS = eINSTANCE.getWebservice_Parameters();

		/**
		 * The meta object literal for the '<em><b>Enable Generation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__ENABLE_GENERATION = eINSTANCE.getWebservice_EnableGeneration();

		/**
		 * The meta object literal for the '<em><b>Implementation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__IMPLEMENTATION = eINSTANCE.getWebservice_Implementation();

		/**
		 * The meta object literal for the '<em><b>Impl Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__IMPL_TYPE = eINSTANCE.getWebservice_ImplType();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__PROTOCOL = eINSTANCE.getWebservice_Protocol();

		/**
		 * The meta object literal for the '<em><b>Soap Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__SOAP_VERSION = eINSTANCE.getWebservice_SoapVersion();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__STYLE = eINSTANCE.getWebservice_Style();

		/**
		 * The meta object literal for the '<em><b>Transaction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__TRANSACTION = eINSTANCE.getWebservice_Transaction();

		/**
		 * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__URI = eINSTANCE.getWebservice_Uri();

		/**
		 * The meta object literal for the '<em><b>Use Existing WSDL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__USE_EXISTING_WSDL = eINSTANCE.getWebservice_UseExistingWSDL();

		/**
		 * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__USER_ID = eINSTANCE.getWebservice_UserID();

		/**
		 * The meta object literal for the '<em><b>Wsdl Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__WSDL_LOCATION = eINSTANCE.getWebservice_WsdlLocation();

		/**
		 * The meta object literal for the '<em><b>Wsdl Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__WSDL_PORT = eINSTANCE.getWebservice_WsdlPort();

		/**
		 * The meta object literal for the '<em><b>Wsdl Service</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEBSERVICE__WSDL_SERVICE = eINSTANCE.getWebservice_WsdlService();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebservicesImpl <em>Webservices</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.WebservicesImpl
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebservices()
		 * @generated
		 */
		EClass WEBSERVICES = eINSTANCE.getWebservices();

		/**
		 * The meta object literal for the '<em><b>Webservice</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WEBSERVICES__WEBSERVICE = eINSTANCE.getWebservices_Webservice();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType <em>SOAP Version Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSOAPVersionType()
		 * @generated
		 */
		EEnum SOAP_VERSION_TYPE = eINSTANCE.getSOAPVersionType();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.StyleTypes <em>Style Types</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getStyleTypes()
		 * @generated
		 */
		EEnum STYLE_TYPES = eINSTANCE.getStyleTypes();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType <em>Webservice Runtime Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebserviceRuntimeType()
		 * @generated
		 */
		EEnum WEBSERVICE_RUNTIME_TYPE = eINSTANCE.getWebserviceRuntimeType();

		/**
		 * The meta object literal for the '<em>SOAP Version Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getSOAPVersionTypeObject()
		 * @generated
		 */
		EDataType SOAP_VERSION_TYPE_OBJECT = eINSTANCE.getSOAPVersionTypeObject();

		/**
		 * The meta object literal for the '<em>Style Types Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.StyleTypes
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getStyleTypesObject()
		 * @generated
		 */
		EDataType STYLE_TYPES_OBJECT = eINSTANCE.getStyleTypesObject();

		/**
		 * The meta object literal for the '<em>Webservice Runtime Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType
		 * @see org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentPackageImpl#getWebserviceRuntimeTypeObject()
		 * @generated
		 */
		EDataType WEBSERVICE_RUNTIME_TYPE_OBJECT = eINSTANCE.getWebserviceRuntimeTypeObject();

	}

} //DeploymentPackage
