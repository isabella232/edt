/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
dependencies = {
	action: "release",
	cssOptimize: "comments",
	optimize:"shrinksafe",
	selectorEngine: "acme",
	version: "1.7.1",
	layers: [
		{
			name: "dojo.js",
			boot: true,
			dependencies: [
							"dojox.mobile.app._base",
							"dojox.mobile.app._Widget",
							"dojox.mobile.app._event",
							"dojox.mobile.app._FormWidget",
							"dojox.mobile.deviceTheme",
							"dojox.mobile._base",
							"dojox.mobile.Button",
							"dojox.mobile.View",
							"dojox.mobile.EdgeToEdgeList",
							"dojox.mobile.RoundRectList",
							"dojox.mobile.EdgeToEdgeCategory",
							"dojox.mobile.RoundRectCategory",
							"dojox.mobile.ListItem",
							"dojox.mobile.ProgressIndicator",
							"dojox.mobile.Switch",
							"dojox.mobile.TabBar",
							"dojox.mobile.TabBarButton",
							"dojox.mobile.CheckBox",
							"dojox.mobile.Slider",
							"dojox.mobile.SpinWheel",
							"dojox.mobile.SpinWheelDatePicker",
							"dojox.mobile.SpinWheelTimePicker",
							"dojox.mobile.ScrollableView",
							"dojox.mobile.SwapView",
							"dojox.mobile.IconContainer",
							"dojox.mobile.Opener",
							"dojox.mobile.Overlay"
			]
		}
	],
	prefixes: [
		[ "dijit", "../dijit" ],
		[ "dojox", "../dojox" ]
	]
}