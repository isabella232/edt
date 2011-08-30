/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.util;

import org.eclipse.edt.ide.ui.internal.deployment.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage
 * @generated
 */
public class DeploymentAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static DeploymentPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = DeploymentPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeploymentSwitch<Adapter> modelSwitch =
		new DeploymentSwitch<Adapter>() {
			@Override
			public Adapter caseBindings(Bindings object) {
				return createBindingsAdapter();
			}
			@Override
			public Adapter caseCICSECIProtocol(CICSECIProtocol object) {
				return createCICSECIProtocolAdapter();
			}
			@Override
			public Adapter caseCICSJ2CProtocol(CICSJ2CProtocol object) {
				return createCICSJ2CProtocolAdapter();
			}
			@Override
			public Adapter caseCICSSSLProtocol(CICSSSLProtocol object) {
				return createCICSSSLProtocolAdapter();
			}
			@Override
			public Adapter caseCICSWSProtocol(CICSWSProtocol object) {
				return createCICSWSProtocolAdapter();
			}
			@Override
			public Adapter caseDeployExt(DeployExt object) {
				return createDeployExtAdapter();
			}
			@Override
			public Adapter caseDeployment(Deployment object) {
				return createDeploymentAdapter();
			}
			@Override
			public Adapter caseDeploymentBuildDescriptor(DeploymentBuildDescriptor object) {
				return createDeploymentBuildDescriptorAdapter();
			}
			@Override
			public Adapter caseDeploymentProject(DeploymentProject object) {
				return createDeploymentProjectAdapter();
			}
			@Override
			public Adapter caseDeploymentTarget(DeploymentTarget object) {
				return createDeploymentTargetAdapter();
			}
			@Override
			public Adapter caseEGLBinding(EGLBinding object) {
				return createEGLBindingAdapter();
			}
			@Override
			public Adapter caseEGLDeploymentRoot(EGLDeploymentRoot object) {
				return createEGLDeploymentRootAdapter();
			}
			@Override
			public Adapter caseIMSJ2CProtocol(IMSJ2CProtocol object) {
				return createIMSJ2CProtocolAdapter();
			}
			@Override
			public Adapter caseIMSTCPProtocol(IMSTCPProtocol object) {
				return createIMSTCPProtocolAdapter();
			}
			@Override
			public Adapter caseInclude(Include object) {
				return createIncludeAdapter();
			}
			@Override
			public Adapter caseJava400J2cProtocol(Java400J2cProtocol object) {
				return createJava400J2cProtocolAdapter();
			}
			@Override
			public Adapter caseJava400Protocol(Java400Protocol object) {
				return createJava400ProtocolAdapter();
			}
			@Override
			public Adapter caseLocalProtocol(LocalProtocol object) {
				return createLocalProtocolAdapter();
			}
			@Override
			public Adapter caseNativeBinding(NativeBinding object) {
				return createNativeBindingAdapter();
			}
			@Override
			public Adapter caseParameter(Parameter object) {
				return createParameterAdapter();
			}
			@Override
			public Adapter caseParameters(Parameters object) {
				return createParametersAdapter();
			}
			@Override
			public Adapter caseProtocol(Protocol object) {
				return createProtocolAdapter();
			}
			@Override
			public Adapter caseProtocols(Protocols object) {
				return createProtocolsAdapter();
			}
			@Override
			public Adapter caseReferenceProtocol(ReferenceProtocol object) {
				return createReferenceProtocolAdapter();
			}
			@Override
			public Adapter caseResource(Resource object) {
				return createResourceAdapter();
			}
			@Override
			public Adapter caseResourceOmissions(ResourceOmissions object) {
				return createResourceOmissionsAdapter();
			}
			@Override
			public Adapter caseRestBinding(RestBinding object) {
				return createRestBindingAdapter();
			}
			@Override
			public Adapter caseRestservice(Restservice object) {
				return createRestserviceAdapter();
			}
			@Override
			public Adapter caseRestservices(Restservices object) {
				return createRestservicesAdapter();
			}
			@Override
			public Adapter caseRUIApplication(RUIApplication object) {
				return createRUIApplicationAdapter();
			}
			@Override
			public Adapter caseRUIHandler(RUIHandler object) {
				return createRUIHandlerAdapter();
			}
			@Override
			public Adapter caseRUIResource(RUIResource object) {
				return createRUIResourceAdapter();
			}
			@Override
			public Adapter caseRUIResourceOmissions(RUIResourceOmissions object) {
				return createRUIResourceOmissionsAdapter();
			}
			@Override
			public Adapter caseSQLDatabaseBinding(SQLDatabaseBinding object) {
				return createSQLDatabaseBindingAdapter();
			}
			@Override
			public Adapter caseSystemIProtocol(SystemIProtocol object) {
				return createSystemIProtocolAdapter();
			}
			@Override
			public Adapter caseTCPIPProtocol(TCPIPProtocol object) {
				return createTCPIPProtocolAdapter();
			}
			@Override
			public Adapter caseWebBinding(WebBinding object) {
				return createWebBindingAdapter();
			}
			@Override
			public Adapter caseWebservice(Webservice object) {
				return createWebserviceAdapter();
			}
			@Override
			public Adapter caseWebservices(Webservices object) {
				return createWebservicesAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Bindings <em>Bindings</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Bindings
	 * @generated
	 */
	public Adapter createBindingsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol <em>CICSECI Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol
	 * @generated
	 */
	public Adapter createCICSECIProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol <em>CICSJ2C Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol
	 * @generated
	 */
	public Adapter createCICSJ2CProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol <em>CICSSSL Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol
	 * @generated
	 */
	public Adapter createCICSSSLProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol <em>CICSWS Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol
	 * @generated
	 */
	public Adapter createCICSWSProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeployExt <em>Deploy Ext</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeployExt
	 * @generated
	 */
	public Adapter createDeployExtAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Deployment <em>Deployment</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Deployment
	 * @generated
	 */
	public Adapter createDeploymentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor <em>Build Descriptor</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor
	 * @generated
	 */
	public Adapter createDeploymentBuildDescriptorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject
	 * @generated
	 */
	public Adapter createDeploymentProjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget
	 * @generated
	 */
	public Adapter createDeploymentTargetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding <em>EGL Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLBinding
	 * @generated
	 */
	public Adapter createEGLBindingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot <em>EGL Deployment Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot
	 * @generated
	 */
	public Adapter createEGLDeploymentRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol <em>IMSJ2C Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol
	 * @generated
	 */
	public Adapter createIMSJ2CProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol <em>IMSTCP Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol
	 * @generated
	 */
	public Adapter createIMSTCPProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Include <em>Include</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Include
	 * @generated
	 */
	public Adapter createIncludeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol <em>Java400 J2c Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol
	 * @generated
	 */
	public Adapter createJava400J2cProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol <em>Java400 Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol
	 * @generated
	 */
	public Adapter createJava400ProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol <em>Local Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol
	 * @generated
	 */
	public Adapter createLocalProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding <em>Native Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.NativeBinding
	 * @generated
	 */
	public Adapter createNativeBindingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameter
	 * @generated
	 */
	public Adapter createParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Parameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Parameters
	 * @generated
	 */
	public Adapter createParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocol
	 * @generated
	 */
	public Adapter createProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols <em>Protocols</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Protocols
	 * @generated
	 */
	public Adapter createProtocolsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol <em>Reference Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol
	 * @generated
	 */
	public Adapter createReferenceProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Resource <em>Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Resource
	 * @generated
	 */
	public Adapter createResourceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions <em>Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions
	 * @generated
	 */
	public Adapter createResourceOmissionsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.RestBinding <em>Rest Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RestBinding
	 * @generated
	 */
	public Adapter createRestBindingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservice <em>Restservice</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservice
	 * @generated
	 */
	public Adapter createRestserviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Restservices <em>Restservices</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Restservices
	 * @generated
	 */
	public Adapter createRestservicesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIApplication <em>RUI Application</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIApplication
	 * @generated
	 */
	public Adapter createRUIApplicationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIHandler <em>RUI Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIHandler
	 * @generated
	 */
	public Adapter createRUIHandlerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResource <em>RUI Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResource
	 * @generated
	 */
	public Adapter createRUIResourceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions <em>RUI Resource Omissions</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions
	 * @generated
	 */
	public Adapter createRUIResourceOmissionsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding <em>SQL Database Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding
	 * @generated
	 */
	public Adapter createSQLDatabaseBindingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol <em>System IProtocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol
	 * @generated
	 */
	public Adapter createSystemIProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol <em>TCPIP Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol
	 * @generated
	 */
	public Adapter createTCPIPProtocolAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.WebBinding <em>Web Binding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.WebBinding
	 * @generated
	 */
	public Adapter createWebBindingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservice <em>Webservice</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservice
	 * @generated
	 */
	public Adapter createWebserviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.edt.ide.ui.internal.deployment.Webservices <em>Webservices</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.Webservices
	 * @generated
	 */
	public Adapter createWebservicesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //DeploymentAdapterFactory
