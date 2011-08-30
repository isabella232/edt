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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage
 * @generated
 */
public interface DeploymentFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DeploymentFactory eINSTANCE = org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Bindings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bindings</em>'.
	 * @generated
	 */
	Bindings createBindings();

	/**
	 * Returns a new object of class '<em>CICSECI Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>CICSECI Protocol</em>'.
	 * @generated
	 */
	CICSECIProtocol createCICSECIProtocol();

	/**
	 * Returns a new object of class '<em>CICSJ2C Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>CICSJ2C Protocol</em>'.
	 * @generated
	 */
	CICSJ2CProtocol createCICSJ2CProtocol();

	/**
	 * Returns a new object of class '<em>CICSSSL Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>CICSSSL Protocol</em>'.
	 * @generated
	 */
	CICSSSLProtocol createCICSSSLProtocol();

	/**
	 * Returns a new object of class '<em>CICSWS Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>CICSWS Protocol</em>'.
	 * @generated
	 */
	CICSWSProtocol createCICSWSProtocol();

	/**
	 * Returns a new object of class '<em>Deploy Ext</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deploy Ext</em>'.
	 * @generated
	 */
	DeployExt createDeployExt();

	/**
	 * Returns a new object of class '<em>Deployment</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deployment</em>'.
	 * @generated
	 */
	Deployment createDeployment();

	/**
	 * Returns a new object of class '<em>Build Descriptor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Build Descriptor</em>'.
	 * @generated
	 */
	DeploymentBuildDescriptor createDeploymentBuildDescriptor();

	/**
	 * Returns a new object of class '<em>Project</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Project</em>'.
	 * @generated
	 */
	DeploymentProject createDeploymentProject();

	/**
	 * Returns a new object of class '<em>Target</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Target</em>'.
	 * @generated
	 */
	DeploymentTarget createDeploymentTarget();

	/**
	 * Returns a new object of class '<em>EGL Binding</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EGL Binding</em>'.
	 * @generated
	 */
	EGLBinding createEGLBinding();

	/**
	 * Returns a new object of class '<em>EGL Deployment Root</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EGL Deployment Root</em>'.
	 * @generated
	 */
	EGLDeploymentRoot createEGLDeploymentRoot();

	/**
	 * Returns a new object of class '<em>IMSJ2C Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>IMSJ2C Protocol</em>'.
	 * @generated
	 */
	IMSJ2CProtocol createIMSJ2CProtocol();

	/**
	 * Returns a new object of class '<em>IMSTCP Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>IMSTCP Protocol</em>'.
	 * @generated
	 */
	IMSTCPProtocol createIMSTCPProtocol();

	/**
	 * Returns a new object of class '<em>Include</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Include</em>'.
	 * @generated
	 */
	Include createInclude();

	/**
	 * Returns a new object of class '<em>Java400 J2c Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Java400 J2c Protocol</em>'.
	 * @generated
	 */
	Java400J2cProtocol createJava400J2cProtocol();

	/**
	 * Returns a new object of class '<em>Java400 Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Java400 Protocol</em>'.
	 * @generated
	 */
	Java400Protocol createJava400Protocol();

	/**
	 * Returns a new object of class '<em>Local Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Protocol</em>'.
	 * @generated
	 */
	LocalProtocol createLocalProtocol();

	/**
	 * Returns a new object of class '<em>Native Binding</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Native Binding</em>'.
	 * @generated
	 */
	NativeBinding createNativeBinding();

	/**
	 * Returns a new object of class '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter</em>'.
	 * @generated
	 */
	Parameter createParameter();

	/**
	 * Returns a new object of class '<em>Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameters</em>'.
	 * @generated
	 */
	Parameters createParameters();

	/**
	 * Returns a new object of class '<em>Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Protocol</em>'.
	 * @generated
	 */
	Protocol createProtocol();

	/**
	 * Returns a new object of class '<em>Protocols</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Protocols</em>'.
	 * @generated
	 */
	Protocols createProtocols();

	/**
	 * Returns a new object of class '<em>Reference Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reference Protocol</em>'.
	 * @generated
	 */
	ReferenceProtocol createReferenceProtocol();

	/**
	 * Returns a new object of class '<em>Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Resource</em>'.
	 * @generated
	 */
	Resource createResource();

	/**
	 * Returns a new object of class '<em>Resource Omissions</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Resource Omissions</em>'.
	 * @generated
	 */
	ResourceOmissions createResourceOmissions();

	/**
	 * Returns a new object of class '<em>Rest Binding</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Rest Binding</em>'.
	 * @generated
	 */
	RestBinding createRestBinding();

	/**
	 * Returns a new object of class '<em>Restservice</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Restservice</em>'.
	 * @generated
	 */
	Restservice createRestservice();

	/**
	 * Returns a new object of class '<em>Restservices</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Restservices</em>'.
	 * @generated
	 */
	Restservices createRestservices();

	/**
	 * Returns a new object of class '<em>RUI Application</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>RUI Application</em>'.
	 * @generated
	 */
	RUIApplication createRUIApplication();

	/**
	 * Returns a new object of class '<em>RUI Handler</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>RUI Handler</em>'.
	 * @generated
	 */
	RUIHandler createRUIHandler();

	/**
	 * Returns a new object of class '<em>RUI Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>RUI Resource</em>'.
	 * @generated
	 */
	RUIResource createRUIResource();

	/**
	 * Returns a new object of class '<em>RUI Resource Omissions</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>RUI Resource Omissions</em>'.
	 * @generated
	 */
	RUIResourceOmissions createRUIResourceOmissions();

	/**
	 * Returns a new object of class '<em>SQL Database Binding</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>SQL Database Binding</em>'.
	 * @generated
	 */
	SQLDatabaseBinding createSQLDatabaseBinding();

	/**
	 * Returns a new object of class '<em>System IProtocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>System IProtocol</em>'.
	 * @generated
	 */
	SystemIProtocol createSystemIProtocol();

	/**
	 * Returns a new object of class '<em>TCPIP Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>TCPIP Protocol</em>'.
	 * @generated
	 */
	TCPIPProtocol createTCPIPProtocol();

	/**
	 * Returns a new object of class '<em>Web Binding</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Web Binding</em>'.
	 * @generated
	 */
	WebBinding createWebBinding();

	/**
	 * Returns a new object of class '<em>Webservice</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Webservice</em>'.
	 * @generated
	 */
	Webservice createWebservice();

	/**
	 * Returns a new object of class '<em>Webservices</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Webservices</em>'.
	 * @generated
	 */
	Webservices createWebservices();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	DeploymentPackage getDeploymentPackage();

} //DeploymentFactory
