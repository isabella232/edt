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

import java.util.List;

import org.eclipse.edt.ide.ui.internal.deployment.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage
 * @generated
 */
public class DeploymentSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static DeploymentPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentSwitch() {
		if (modelPackage == null) {
			modelPackage = DeploymentPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case DeploymentPackage.BINDINGS: {
				Bindings bindings = (Bindings)theEObject;
				T result = caseBindings(bindings);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.CICSECI_PROTOCOL: {
				CICSECIProtocol cicseciProtocol = (CICSECIProtocol)theEObject;
				T result = caseCICSECIProtocol(cicseciProtocol);
				if (result == null) result = caseProtocol(cicseciProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.CICSJ2C_PROTOCOL: {
				CICSJ2CProtocol cicsj2CProtocol = (CICSJ2CProtocol)theEObject;
				T result = caseCICSJ2CProtocol(cicsj2CProtocol);
				if (result == null) result = caseProtocol(cicsj2CProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.CICSSSL_PROTOCOL: {
				CICSSSLProtocol cicssslProtocol = (CICSSSLProtocol)theEObject;
				T result = caseCICSSSLProtocol(cicssslProtocol);
				if (result == null) result = caseProtocol(cicssslProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.CICSWS_PROTOCOL: {
				CICSWSProtocol cicswsProtocol = (CICSWSProtocol)theEObject;
				T result = caseCICSWSProtocol(cicswsProtocol);
				if (result == null) result = caseProtocol(cicswsProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.DEPLOY_EXT: {
				DeployExt deployExt = (DeployExt)theEObject;
				T result = caseDeployExt(deployExt);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.DEPLOYMENT: {
				Deployment deployment = (Deployment)theEObject;
				T result = caseDeployment(deployment);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.DEPLOYMENT_BUILD_DESCRIPTOR: {
				DeploymentBuildDescriptor deploymentBuildDescriptor = (DeploymentBuildDescriptor)theEObject;
				T result = caseDeploymentBuildDescriptor(deploymentBuildDescriptor);
				if (result == null) result = caseDeploymentTarget(deploymentBuildDescriptor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.DEPLOYMENT_PROJECT: {
				DeploymentProject deploymentProject = (DeploymentProject)theEObject;
				T result = caseDeploymentProject(deploymentProject);
				if (result == null) result = caseDeploymentTarget(deploymentProject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.DEPLOYMENT_TARGET: {
				DeploymentTarget deploymentTarget = (DeploymentTarget)theEObject;
				T result = caseDeploymentTarget(deploymentTarget);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.EGL_BINDING: {
				EGLBinding eglBinding = (EGLBinding)theEObject;
				T result = caseEGLBinding(eglBinding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT: {
				EGLDeploymentRoot eglDeploymentRoot = (EGLDeploymentRoot)theEObject;
				T result = caseEGLDeploymentRoot(eglDeploymentRoot);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.IMSJ2C_PROTOCOL: {
				IMSJ2CProtocol imsj2CProtocol = (IMSJ2CProtocol)theEObject;
				T result = caseIMSJ2CProtocol(imsj2CProtocol);
				if (result == null) result = caseProtocol(imsj2CProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.IMSTCP_PROTOCOL: {
				IMSTCPProtocol imstcpProtocol = (IMSTCPProtocol)theEObject;
				T result = caseIMSTCPProtocol(imstcpProtocol);
				if (result == null) result = caseProtocol(imstcpProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.INCLUDE: {
				Include include = (Include)theEObject;
				T result = caseInclude(include);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.JAVA400_J2C_PROTOCOL: {
				Java400J2cProtocol java400J2cProtocol = (Java400J2cProtocol)theEObject;
				T result = caseJava400J2cProtocol(java400J2cProtocol);
				if (result == null) result = caseProtocol(java400J2cProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.JAVA400_PROTOCOL: {
				Java400Protocol java400Protocol = (Java400Protocol)theEObject;
				T result = caseJava400Protocol(java400Protocol);
				if (result == null) result = caseProtocol(java400Protocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.LOCAL_PROTOCOL: {
				LocalProtocol localProtocol = (LocalProtocol)theEObject;
				T result = caseLocalProtocol(localProtocol);
				if (result == null) result = caseProtocol(localProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.NATIVE_BINDING: {
				NativeBinding nativeBinding = (NativeBinding)theEObject;
				T result = caseNativeBinding(nativeBinding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.PARAMETER: {
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.PARAMETERS: {
				Parameters parameters = (Parameters)theEObject;
				T result = caseParameters(parameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.PROTOCOL: {
				Protocol protocol = (Protocol)theEObject;
				T result = caseProtocol(protocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.PROTOCOLS: {
				Protocols protocols = (Protocols)theEObject;
				T result = caseProtocols(protocols);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.REFERENCE_PROTOCOL: {
				ReferenceProtocol referenceProtocol = (ReferenceProtocol)theEObject;
				T result = caseReferenceProtocol(referenceProtocol);
				if (result == null) result = caseProtocol(referenceProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RESOURCE: {
				Resource resource = (Resource)theEObject;
				T result = caseResource(resource);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RESOURCE_OMISSIONS: {
				ResourceOmissions resourceOmissions = (ResourceOmissions)theEObject;
				T result = caseResourceOmissions(resourceOmissions);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.REST_BINDING: {
				RestBinding restBinding = (RestBinding)theEObject;
				T result = caseRestBinding(restBinding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RESTSERVICE: {
				Restservice restservice = (Restservice)theEObject;
				T result = caseRestservice(restservice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RESTSERVICES: {
				Restservices restservices = (Restservices)theEObject;
				T result = caseRestservices(restservices);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RUI_APPLICATION: {
				RUIApplication ruiApplication = (RUIApplication)theEObject;
				T result = caseRUIApplication(ruiApplication);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RUI_HANDLER: {
				RUIHandler ruiHandler = (RUIHandler)theEObject;
				T result = caseRUIHandler(ruiHandler);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RUI_RESOURCE: {
				RUIResource ruiResource = (RUIResource)theEObject;
				T result = caseRUIResource(ruiResource);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.RUI_RESOURCE_OMISSIONS: {
				RUIResourceOmissions ruiResourceOmissions = (RUIResourceOmissions)theEObject;
				T result = caseRUIResourceOmissions(ruiResourceOmissions);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.SYSTEM_IPROTOCOL: {
				SystemIProtocol systemIProtocol = (SystemIProtocol)theEObject;
				T result = caseSystemIProtocol(systemIProtocol);
				if (result == null) result = caseProtocol(systemIProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.TCPIP_PROTOCOL: {
				TCPIPProtocol tcpipProtocol = (TCPIPProtocol)theEObject;
				T result = caseTCPIPProtocol(tcpipProtocol);
				if (result == null) result = caseProtocol(tcpipProtocol);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.WEB_BINDING: {
				WebBinding webBinding = (WebBinding)theEObject;
				T result = caseWebBinding(webBinding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.WEBSERVICE: {
				Webservice webservice = (Webservice)theEObject;
				T result = caseWebservice(webservice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DeploymentPackage.WEBSERVICES: {
				Webservices webservices = (Webservices)theEObject;
				T result = caseWebservices(webservices);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bindings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bindings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBindings(Bindings object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CICSECI Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CICSECI Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCICSECIProtocol(CICSECIProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CICSJ2C Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CICSJ2C Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCICSJ2CProtocol(CICSJ2CProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CICSSSL Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CICSSSL Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCICSSSLProtocol(CICSSSLProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CICSWS Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CICSWS Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCICSWSProtocol(CICSWSProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Deploy Ext</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Deploy Ext</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeployExt(DeployExt object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Deployment</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Deployment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeployment(Deployment object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Build Descriptor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Build Descriptor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeploymentBuildDescriptor(DeploymentBuildDescriptor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Project</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Project</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeploymentProject(DeploymentProject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Target</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Target</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeploymentTarget(DeploymentTarget object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EGL Binding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EGL Binding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEGLBinding(EGLBinding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EGL Deployment Root</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EGL Deployment Root</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEGLDeploymentRoot(EGLDeploymentRoot object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IMSJ2C Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IMSJ2C Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIMSJ2CProtocol(IMSJ2CProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IMSTCP Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IMSTCP Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIMSTCPProtocol(IMSTCPProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Include</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Include</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInclude(Include object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Java400 J2c Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Java400 J2c Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJava400J2cProtocol(Java400J2cProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Java400 Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Java400 Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJava400Protocol(Java400Protocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLocalProtocol(LocalProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Native Binding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Native Binding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNativeBinding(NativeBinding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameters(Parameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProtocol(Protocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Protocols</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Protocols</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProtocols(Protocols object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceProtocol(ReferenceProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Resource</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Resource</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResource(Resource object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Resource Omissions</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Resource Omissions</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResourceOmissions(ResourceOmissions object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rest Binding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rest Binding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRestBinding(RestBinding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Restservice</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Restservice</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRestservice(Restservice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Restservices</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Restservices</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRestservices(Restservices object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>RUI Application</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>RUI Application</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRUIApplication(RUIApplication object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>RUI Handler</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>RUI Handler</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRUIHandler(RUIHandler object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>RUI Resource</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>RUI Resource</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRUIResource(RUIResource object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>RUI Resource Omissions</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>RUI Resource Omissions</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRUIResourceOmissions(RUIResourceOmissions object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>System IProtocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>System IProtocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSystemIProtocol(SystemIProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>TCPIP Protocol</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>TCPIP Protocol</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTCPIPProtocol(TCPIPProtocol object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Web Binding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Web Binding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWebBinding(WebBinding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Webservice</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Webservice</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWebservice(Webservice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Webservices</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Webservices</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWebservices(Webservices object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //DeploymentSwitch
