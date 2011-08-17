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
package org.eclipse.edt.ide.ui.internal.deployment.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class EGLDeploymentRootItemProvider
	extends ItemProviderAdapter
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGLDeploymentRootItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR);
			childrenFeatures.add(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns EGLDeploymentRoot.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/EGLDeploymentRoot"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		return getString("_UI_EGLDeploymentRoot_type");
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(EGLDeploymentRoot.class)) {
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT,
				 DeploymentFactory.eINSTANCE.createDeployment()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI,
				 DeploymentFactory.eINSTANCE.createCICSECIProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C,
				 DeploymentFactory.eINSTANCE.createCICSJ2CProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL,
				 DeploymentFactory.eINSTANCE.createCICSSSLProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS,
				 DeploymentFactory.eINSTANCE.createCICSWSProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C,
				 DeploymentFactory.eINSTANCE.createIMSJ2CProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP,
				 DeploymentFactory.eINSTANCE.createIMSTCPProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400,
				 DeploymentFactory.eINSTANCE.createJava400Protocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C,
				 DeploymentFactory.eINSTANCE.createJava400J2cProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL,
				 DeploymentFactory.eINSTANCE.createLocalProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF,
				 DeploymentFactory.eINSTANCE.createReferenceProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL,
				 DeploymentFactory.eINSTANCE.createSystemIProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP,
				 DeploymentFactory.eINSTANCE.createTCPIPProtocol()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR,
				 DeploymentFactory.eINSTANCE.createDeploymentBuildDescriptor()));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT,
				 DeploymentFactory.eINSTANCE.createDeploymentProject()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return EglddEditPlugin.INSTANCE;
	}

}
