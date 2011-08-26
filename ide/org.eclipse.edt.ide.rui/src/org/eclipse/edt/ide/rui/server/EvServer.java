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
package org.eclipse.edt.ide.rui.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.rui.editor.IEditorSelectAndRevealer;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.internal.nls.RUINlsStrings;
import org.eclipse.edt.ide.rui.utils.DebugFileLocator;
import org.eclipse.edt.ide.rui.utils.EGLResource;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.javart.json.TokenMgrError;
import org.eclipse.edt.javart.services.servlet.proxy.ProxyEventHandler;
import org.eclipse.edt.javart.services.servlet.proxy.RuiBrowserHttpRequest;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import eglx.http.HttpRequest;




// TODO: Issue with client locations - we need to be case sensitive in the clientsByLocation map on a case sensitive file system and case insensitive on a case insenistive file system
// TODO: Only support __ requests for the page that is set for this server (currentURL)
// TODO: Remove the need for JavaScript in the currentURL - requires parsing the domain name and port # out
// TODO: Need to capture 'important' events so that we can send them after a client is listening again - although even some events will cause all waiting events to be cleared
public class EvServer implements IClientProxy {

	private ServerSocket serverSocket = null;
	private Map contextKeyQueues = new HashMap();

	private Object contextSynchObject = new Object();
	private List<IContextResolver> contextResolvers = new ArrayList<IContextResolver>();
	private static final String EOL = "\r\n";
	private static EvServer instance = null;
	private static boolean running = true;
	private static int START_PORT_NUMBER = 5590;
	private static int portNumber = START_PORT_NUMBER;
	private int contextKeyInc = 1;
	
	public static class Event {
		public PrintStream ps = null;
		public Socket socket = null;
		public String url = null;	
		public RuiBrowserHttpRequest xmlRequest = null;
		public Integer key = null;
		public Map arguments = null;
	}

	private class ___ProxyHandler extends ProxyEventHandler implements Runnable {
		private RuiBrowserHttpRequest ruiRequest;
		private PrintStream ps;
		
		public ___ProxyHandler( RuiBrowserHttpRequest ruiRequest, final PrintStream ps ) {
			this.ruiRequest = ruiRequest;
			this.ps = ps;
		}
		
		protected HttpURLConnection getHttpProxyConnection( HttpRequest restRequest ) throws IOException {		
			String urlString = restRequest.getUri();
			Proxy proxy = getProxy( urlString );
			if( proxy != null )
			{
				return (HttpURLConnection)new URL(urlString).openConnection( proxy );
			}
			return null;
		}

		private Proxy getProxy( String urlString ) throws IOException {
			Bundle bundle = Platform.getBundle( "org.eclipse.core.net" );
			ServiceReference ref = bundle.getBundleContext().getServiceReference( IProxyService.class.getName() );
			if( ref != null ){
				Object proxyService = bundle.getBundleContext().getService( ref );
				if( proxyService instanceof IProxyService && (((IProxyService)proxyService).isProxiesEnabled() || ((IProxyService)proxyService).isSystemProxiesEnabled()) ) {
					IProxyData proxyData = getProxyData( (IProxyService)proxyService, urlString );
					if( proxyData != null ){
						int port = proxyData.getPort() == -1 ? 0 : proxyData.getPort();
						SocketAddress address = new InetSocketAddress( proxyData.getHost(), port );
						Proxy.Type proxyType;
						if( proxyData.getType().equals( IProxyData.SOCKS_PROXY_TYPE ) ){
							proxyType = Proxy.Type.SOCKS;
						}
						else{
							proxyType = Proxy.Type.HTTP;
						}
						return new Proxy( proxyType, address );
					}
				}
			}
			return null;
		}
		
		private IProxyData getProxyData( IProxyService proxyService,  String urlString ){
			IProxyData proxy = null;
			IProxyData[] proxies = proxyService.getProxyDataForHost( urlString );
			if( proxies != null && proxies.length > 0 ){
				for( int idx = 0; idx < proxies.length; idx++ ){
					/*
					 * first try to use https
					 * then http
					 * then socks
					 */
					if( proxies[idx].getType() == IProxyData.HTTPS_PROXY_TYPE ){
						proxy = proxies[idx];
						break;
					}
					else if( proxies[idx].getType() == IProxyData.HTTP_PROXY_TYPE && (proxy == null || proxy.getType() == IProxyData.SOCKS_PROXY_TYPE ) ){
						proxy = proxies[idx];
					}
					else if( proxies[idx].getType() == IProxyData.SOCKS_PROXY_TYPE && proxy == null ){
						proxy = proxies[idx];
					}
				}
			}
			return proxy;
		}
		
		public void run(){
			runProxy( ruiRequest, ps );
		}
		
		private void runProxy(RuiBrowserHttpRequest ruiRequest, final PrintStream ps) {
			String urlString = ruiRequest.getURL();
			try{
				HttpRequest innerRequest = null;
//TODO EDT service?				
//				if( ruiRequest.getHeaders() != null && ruiRequest.getHeaders().containsKey(RestServiceUtilities.EGL_SERVICE_CALL) ){
//					eglRestService(urlString, ruiRequest, ps, innerRequest);
//				}
//				else
//				{
//					innerRequest = HttpRequest.create(ruiRequest.getContent());
//					eglRestService(urlString, ruiRequest, ps, innerRequest);
//				}
			} catch (Throwable e) {
				System.err.println(urlString);
				e.printStackTrace();
				try {
					fail(ps);
				} catch (Exception ee) {
				}
			}
			finally
			{
				if(ps != null)
				{
					ps.flush();
					ps.close();
				}
			}
		}
		
//TODO EDT service	
//		protected String getWsdlFileName4Ide( String body, String projectName )
//		{
//			try
//			{
//				ObjectNode objNode = JsonParser.parse(body);
//				ObjectNode bindingNode = (ObjectNode)JsonUtilities.getValueNode( objNode, JsonUtilities.BINDING_ID, new ObjectNode() );
//				if( bindingNode != null )
//				{
//					String wsdlLocation= JsonUtilities.getValueNode(bindingNode, JsonUtilities.WEB_BINDING_WSDL_LOCATION_ID, new StringNode("",false) ).toJava();
//					File file = new File( wsdlLocation );
//					if( wsdlLocation != null && wsdlLocation.length() > 0 && !file.isAbsolute() )
//					{
//						if( wsdlLocation.charAt(0) != '/' );
//						{
//							wsdlLocation = '/' + wsdlLocation;
//						}
//						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//						if( project.isOpen() )
//						{
//							//first try to get the file from the project root
//							//if it's not found try to find it in the source folder
//							EglPathRootLocator pLocator = new EglPathRootLocator( project );
//							IFile ifile = pLocator.findFile( wsdlLocation );
//							if( ifile == null || !ifile.exists() ){
//								SourceLocator sLocator = new SourceLocator( project );
//								ifile = sLocator.findFile( wsdlLocation );
//							}
//							if( ifile != null && ifile.exists() )
//							{
//								String val = null;
//								if(!(ifile instanceof EglarResource)){
//									val = ifile.getLocation().toOSString();
//								}
//								else{
//									val = ((EglarFileResource)ifile).getFilePath();
//								}
//								bindingNode.remove( JsonUtilities.WEB_BINDING_WSDL_LOCATION_ID );
//								bindingNode.addPair( new NameValuePairNode( new StringNode(JsonUtilities.WEB_BINDING_WSDL_LOCATION_ID, false),  new StringNode( val, false) ) );
//								body = objNode.toJson();
//								//force the ide to reload the file
//								purgeWsdlCache();
//							}
//						}
//					}
//				}
//			}
//			catch( Exception e )
//			{
//				e.printStackTrace();
//			}
//			return body;
//		}
		
//TODO EDT service		
//		private void eglRestService(String urlString, RuiBrowserHttpRequest ruiRequest, final PrintStream ps, HttpRequest innerRequest) {
//			IntValue serviceKind = innerRequest != null && RestServiceUtilities.isSoapCall( innerRequest ) ? ServiceKind.WEB : ServiceKind.REST;
//			try
//			{
//				Integer intContextKey = null;
//				if (ruiRequest.getArguments().containsKey("contextKey")) {
//					intContextKey = new Integer((String)ruiRequest.getArguments().get("contextKey"));
//				}
//				else {
//					int idx = ruiRequest.getContent().indexOf( "contextKey=" );
//					if (idx != -1) {
//						try {
//							intContextKey = new Integer( ruiRequest.getContent().substring( idx + 11 /* "contextKey".length() */ ) );
//						}
//						catch ( Exception e ) {
//							// Perhaps the body contains the string "contextKey=<stuff>".
//						}
//					}
//				}
//				
//				HttpResponse response = null;
//				RUIDebugTarget debugTarget = findDebugTarget(intContextKey);
//				boolean isPreviewPane = isPreviewPane(intContextKey);
//				boolean isDedeicatedService = RestServiceUtilities.isEGLDedicatedCall(innerRequest);
//				if( debugTarget != null || isPreviewPane || (!isDesignPane(intContextKey) && isDedeicatedService ) ) {
//					if (innerRequest.getBody().length() != 0) {
//						ObjectNode node = null;
//						try {
//							node = JsonParser.parse(innerRequest.getBody());
//						}
//						catch (Throwable t) {
//							// Body for non-EGL REST services might not be sent as JSON.
//						}
//						
//						if (node != null) {
//							String bindingName = getBindingName(node);
//							if( bindingName != null )
//							{
//								final String servName = getServiceNameFromBindingName(getBindingName(node));
//								if (servName != null) {
//									
//									Service service = getService(servName, getProjectName(urlString));
//									if(service == null)
//									{
//										final String errMsg = JavartUtil.errorMessage( program(), Message.SOA_E_WS_PROXY_MISSING_SERVICE_SOURCE, new Object[] {servName});
//										CommonUtils.getDisplay().syncExec(new Runnable() {
//											public void run() {
//												ErrorDialog.openError( CommonUtils.getDisplay().getActiveShell(), 
//														RUIDebugMessages.DEBUG_SYSTEM_SERVICE_TITLE, 
//														NLS.bind(RUIDebugMessages.DEBUG_MISSING_SERVICE_SOURCE_MSG, servName),
//														new Status( IStatus.ERROR, Activator.PLUGIN_ID, errMsg ) );
//											}
//										});
//
//										response = new HttpResponse();
//
//										response.setStatus(RestServiceUtilities.HTTP_STATUS_FAILED);
//										response.setStatusMessage(RestServiceUtilities.HTTP_STATUS_MSG_FAILED);
//										JavartException jrte = RestServiceUtilities.buildServiceInvocationException( program(), Message.SOA_E_WS_PROXY_MISSING_SERVICE_SOURCE, new Object[] {servName}, null, ServiceKind.EGL );
//										response.setBody(jrte);
//									}
//									else if(service.isSystemPart() && isDedeicatedService )
//									{
//										final String errMsg = JavartUtil.errorMessage( program(), Message.SOA_E_WS_PROXY_UNSUPPORTED_OPERATION, new Object[] {service.getFullyQualifiedName()});
//										CommonUtils.getDisplay().syncExec(new Runnable() {
//											public void run() {
//												ErrorDialog.openError( CommonUtils.getDisplay().getActiveShell(), 
//														RUIDebugMessages.DEBUG_SYSTEM_SERVICE_TITLE, 
//														RUIDebugMessages.DEBUG_SYSTEM_SERVICE_MSG, 
//														new Status( IStatus.ERROR, Activator.PLUGIN_ID, errMsg ) );
//											}
//										});
//
//										response = new HttpResponse();
//
//										response.setStatus(RestServiceUtilities.HTTP_STATUS_FAILED);
//										response.setStatusMessage(RestServiceUtilities.HTTP_STATUS_MSG_FAILED);
//										JavartException jrte = RestServiceUtilities.buildServiceInvocationException( program(), Message.SOA_E_WS_PROXY_UNSUPPORTED_OPERATION, new Object[] {service.getFullyQualifiedName()}, null, ServiceKind.EGL );
//										response.setBody(jrte);
//									}
//									else if (service != null && !service.isSystemPart()) {
//										String projectName = ((IProject)((GenerateEnvironment)service.getEnv()).getProject()).getName();
//										String fileName = getFileNameFromPath(service);
//										response = invokeEGLDebugger(servName, projectName, fileName, getFunctionName(node), innerRequest.getBody(), debugTarget, RestServiceUtilities.isSoapCall(innerRequest));
//									}
//								}
//							}
//						}
//					}
//				}
//				
//				if (response == null) {
//					response = super.runProxy(urlString, ruiRequest, innerRequest );
//				}
//				if( response != null )
//				{
//					ps.print( getResponseHeader( urlString, getContentType( urlString ), !urlString.endsWith(".egl"), response.getStatus(), response.getStatusMessage()  ) );
//					String content = response.getBody( program() );
//					ps.write( content.getBytes("utf-8") );
//				}
//			}
//			catch( IOException ioe )
//			{
//				String errorMsg = RestServiceUtilities.buildJsonServiceInvocationException( program(), Message.SOA_E_WS_PROXY_COMMUNICATION, new String[]{urlString}, ioe, serviceKind );
//				ps.print( getBadResponseHeader() );
//				ps.print( errorMsg );
//			}
//			catch(Throwable t)
//			{
//				String errorMsg = RestServiceUtilities.buildJsonServiceInvocationException( program(), Message.SOA_E_WS_PROXY_UNIDENTIFIED, new Object[0], t, serviceKind );
//				ps.print( getBadResponseHeader() );
//				ps.print( errorMsg );
//			}
//		}
	
//TODO EDT service		
//		private String getProjectName(String url) {
//			int indx = url.indexOf("___proxy");
//			if (indx > 0) {
//				String name = url.substring(0, indx);
//				name = name.replace("/", "");
//				name = name.replace("\\", "");
//				name = name.trim();
//				if (name.length() == 0) {
//					return null;
//				}
//				return name;
//				
//			}
//			return null;
//		}
	
//TODO EDT service
//		private String getServiceNameFromBindingName(String binding) {
//
//			try{
//				HttpRequest innerRequest = HttpRequest.create(ruiRequest.getContent());
//				if( innerRequest.getHeaders().containsKey(RestServiceUtilities.EGL_PRIVATE_CALL))
//				{
//					ObjectNode node = JsonParser.parse(innerRequest.getBody());
//					ValueNode bindingNode = JsonUtilities.getValueNode(node, "binding", new ObjectNode());
//					if( bindingNode instanceof ObjectNode )
//					{
//						String type = JsonUtilities.getValueNode((ObjectNode)bindingNode, "type", new StringNode("",false)).toJava();
//						String serviceName = JsonUtilities.getValueNode((ObjectNode)bindingNode, "serviceName", new StringNode("",false)).toJava();
//						String protocol = JsonUtilities.getValueNode((ObjectNode)bindingNode, "protocol", new StringNode("",false)).toJava();
//						if( type.equalsIgnoreCase("EGLBinding") && 
//								protocol.equalsIgnoreCase("local") &&
//								serviceName.length() > 0 )
//						{
//							return serviceName;
//						}
//							
//					}
//				}
//			}
//			catch(Throwable e){}
//			
//			// Check the prefs for an entry
//			ServiceReferencePanel panel = new ServiceReferencePanel(EGLUIPlugin.getDefault().getPreferenceStore().getString(
//					IEGLPreferenceConstants.DEBUG_MAPPING_SERVICEREF));
//			PartPanelEntry entry = panel.findEntry(binding);
//			if (entry != null) {
//				if (entry.bypassSource()) {
//					return null;
//				}
//				return entry.getPartMapping();
//			}
//			
//			// If no entry, throw up a dialog
//			
//			final InterfaceMappingDialog dialog = new InterfaceMappingDialog( CommonUtils.getShell(), binding );
//			CommonUtils.getDisplay().syncExec( new Runnable() { public void run() { dialog.open(); } } );
//		
//
//			String[] info =  dialog.getInterfaceMappingInfo();
//
//			// If cancel pressed, then return null
//			if (info == null) {
//				return null;
//			}
//
//			// Make sure to save the changes
//			if (dialog.isSaveValueButtonChecked()) {
//				EGLDebugEngine.addPartPanelLine( info[ 0 ], info[ 1 ], info[ 2 ], IEGLPreferenceConstants.DEBUG_MAPPING_SERVICEREF );
//			}
//			
//			if (info[ 0 ] != null && !info[ 0 ].equalsIgnoreCase( "source" )) {
//				return null;
//			}
//			
//			return info[2];
//		}
//		
//		private Service getService(String qualName, String projectName) {
//			
//			if (qualName == null || projectName == null) {
//				return null;
//			}
//			
//			String name; 
//			String[] pkgName;
//			
//			String[] nameArr = NameUtil.toStringArray(qualName);
//			name = nameArr[nameArr.length - 1];
//			
//			if (nameArr.length > 1) {
//				pkgName = new String[nameArr.length - 1];
//				System.arraycopy(nameArr,0,pkgName,0,pkgName.length);
//			}
//			else {
//				pkgName = new String[0];
//			}
//						
//			HashSet searchedProjects = new HashSet();
//			
//			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//			
//			IProject project = root.getProject(projectName);
//			
//			Service service = getService(name, pkgName, project, searchedProjects, root);
//			
//			if (service != null) {
//				return service;				
//			}
//			
//			service = searchForService(qualName, name, pkgName, searchedProjects, root);
//			
//			return service;
//		}
//		
//		private Service searchForService(String qualName, String name, String[] pkgName, HashSet searchedProjects, IWorkspaceRoot root) {
//			final List projects = new ArrayList();
//	        IEGLSearchResultCollector collector = new IEGLSearchResultCollector()
//	        {
//				public IProgressMonitor getProgressMonitor() { return null; }
//				public void aboutToStart() { /* do nothing */ }
//				public void done() { /* do nothing */ }
//				public void accept( IResource resource, int start, int end, IEGLElement enclosingElement, int accuracy )
//				{
//					projects.add( resource.getProject() );
//				}
//				public void accept(IEGLElement element, int start, int end,
//						IResource resource, int accuracy) throws CoreException {
//					projects.add( resource.getProject() );
//				}
//			};
//			
//	        try {
//				(new SearchEngine()).search(
//						EGLUIPlugin.getWorkspace(),
//						SearchEngine.createSearchPattern( qualName, IEGLSearchConstants.SERVICE_PART, IEGLSearchConstants.DECLARATIONS, false ),
//						SearchEngine.createWorkspaceScope(),
//						false,
//						true,
//						collector );
//			} catch (EGLModelException e) {
//				return null;
//			}
//			
//			if (projects.size() > 0) {
//				return getService(name, pkgName, (IProject)projects.get(0), searchedProjects, root);
//			}
//
//
//			
//			return null;
//		}
//		
//		private Service getService(String name, String[] pkgName, IProject project, HashSet searchedProjects, IWorkspaceRoot root) {
//			if ( project == null || !EGLProject.hasEGLNature( project ) || searchedProjects.contains(project)) {
//				return null;
//			}
//			
//			searchedProjects.add(project);
//			GenerateEnvironment environment = GenerateEnvironmentManager.getInstance().getGenerateEnvironment(project, true);
//			try {
//				Part part = environment.findPart(InternUtil.intern(pkgName), InternUtil.intern(name));
//				if (part.getPartType() == Part.PART_SERVICE) {
//					return (Service) part;
//				}
//			} catch (PartNotFoundException e) {
//			}
//			
//			//Part was not found...must search the EGL projects that this project requires
//			
//			try
//			{
//				EGLProject eglProject = (EGLProject)EGLCore.create( project );
//				IEGLPathEntry[] entries = eglProject.getResolvedEGLPath( true );
//				for ( int i = 0; i < entries.length; i++ )
//				{
//					IEGLPathEntry entry = entries[ i ];
//					if ( entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT )
//					{
//						IResource member = root.findMember( entry.getPath() ); 
//						if ( member != null && member.getType() == IResource.PROJECT )
//						{
//							Service service = getService(name, pkgName, (IProject)member, searchedProjects, root);
//							if (service != null) {
//								return service;
//							}
//						}
//					}
//				}
//			}
//			catch ( EGLModelException emx )
//			{
//			}
//			return null;
//		}		
//		
//		private String getBindingName(ObjectNode node) {
//			List list = node.getPairs();
//			Iterator i = list.iterator();
//			while (i.hasNext()) {
//				NameValuePairNode pair = (NameValuePairNode)i.next();
//				if (RestServiceDelegate.JSON_RPC_BINDINGNAME_ID.equals(pair.getName().getJavaValue())) {
//					return pair.getValue().toJava();
//				}
//			}
//			return null;
//		}
//
//		private String getProjectNameFromPath(String pathString) {
//			IPath path = new Path(pathString);
//			path = path.removeLastSegments(path.segmentCount() - 1);
//			String str = path.toString();
//			if (str.startsWith("/")) {
//				return str.replaceFirst("/", "");
//			}
//			else {
//				return str;
//			}
//		}
//		
//		private String getFileNameFromPath(Part part) {	
//			if ( part.getAnnotation(  IEGLConstants.EGL_EGLAR ) != null ) {
//				String eglarFileName = (String)part.getAnnotation(  IEGLConstants.EGL_EGLAR ).getValue();
//				String entryName = part.getFullyQualifiedName().replaceAll( "\\.", "/" ) + ".ir";
//				return FileInEglar.EGLAR_PREFIX + eglarFileName + FileInEglar.EGLAR_SEPARATOR + entryName;
//			} else {
//				String  pathString = part.getFileName();
//				IPath path = new Path(pathString);
//				path = path.removeFirstSegments(1);
//				String str = path.toString();
//				if (str.startsWith("/")) {
//					return str.replaceFirst("/", "");
//				}
//				else {
//					return str;
//				}
//			}
//		}

//TODO EDT service 
//		private String getFunctionName(ObjectNode node) {
//			List list = node.getPairs();
//			Iterator i = list.iterator();
//			while (i.hasNext()) {
//				NameValuePairNode pair = (NameValuePairNode)i.next();
//				if (RestServiceDelegate.JSON_RPC_METHOD_ID.equals(pair.getName().getJavaValue())) {
//					return pair.getValue().toJava();
//				}
//			}
//			return null;
//		}
	}
	
	/**
	 * Process the event queue
	 */
	private class QueueProcessor extends Thread {
		//
		private LinkedList eventQueue = new LinkedList();
		private Integer contextKey = null;
		private String eventPending = null;
		private IContext context = null;
		private int timeLeft = -1;
		// once the lifeline becomes 0 the thread is killed - initialized in constructor
		private int lifeLine = -1;
		// set true once the queue has processed at least 1 __getevent
		private boolean getProcessed = false;
		private boolean active = true;

		private void resetLifeLine() {
			this.lifeLine = 25;
		}
		
		public IContext getContext() {
			return context;
		}

		public void setContext(IContext context) {
			this.context = context;
		}

		public Integer getContextKey() {
			return contextKey;
		}

		public void setContextKey(Integer contextKey) {
			this.contextKey = contextKey;
		}

		public String getEventPending() {
			return eventPending;
		}

		public void setEventPending(String eventPending) {
			this.eventPending = eventPending;
		}

		public void addEvent(Event event) {
			this.eventQueue.add(event);
		}

		private void resetTimeLeft() {
			this.timeLeft = 10;
		}
		
		public void setActive( boolean state ) {
			this.active = state;
		}
		
		/**
		 * 
		 */
		public QueueProcessor() {
			super("Visual Editor Event Queue Processor");
			resetTimeLeft();
			resetLifeLine();
		}

		/*
		 * 
		 */
		public void run() {
			while (active) {
				if (eventQueue.isEmpty() == false) {
					resetLifeLine();
					// If there is a getevent followed by another event,
					// then immediately respond to the getevent
					// --------------------------------------------------
					while (eventQueue.size() > 1
							&& ((Event)eventQueue.get(0)).url.indexOf("___getevent") >= 0) {
						sendEvent((Event)eventQueue.get(0), context, "");
						
						eventQueue.remove();
						resetTimeLeft();
					}

					Event event = (Event)eventQueue.get(0);

					// Process a getevent that is the only entry in the queue
					if (event.url.indexOf("___getevent") >= 0) {
						this.getProcessed = true;
						// Check for a pending event to be sent
						if (eventPending != null) {
							
							sendEvent(event, context, eventPending);
							eventPending = null;
							eventQueue.remove();
							resetTimeLeft();
						} else {
							// Reduce the wake-up count-down and wake up the
							// browser
							// if count down is complete
							timeLeft--;
							if (timeLeft <= 0) {
								
								sendEvent(event, context, "");
								eventQueue.remove();
								resetTimeLeft();
							} else {
								try {
									Thread.sleep(1000);
								} catch (Exception e) { }										
							}
						}				
					} else {
						// Process all other events
						try {
							
							parse4Event(event);
							eventQueue.remove();
							resetTimeLeft();
						} catch (Exception ex) {
							ex.printStackTrace();
						} catch (TokenMgrError ex) {
							ex.printStackTrace();
						} finally {
							event.ps.close();
							try {
								event.socket.close();
							} catch (IOException ex) {
							}
						}
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
					}
				} else {
					try {
						/*
						 * eventQueue is empty
						 */
						if (this.getProcessed) {
							if (this.lifeLine <= 0) {
								//debug("contextQueue stoped - key: " + this.contextKey.toString());
								//TODObreak;								
							}
							this.lifeLine--;
						}
						Thread.sleep(10);
					} catch (InterruptedException e) {
						
					}
				}
			}
		}

		/**
		 * Sends the content to the list of clients associated with the given
		 * context.
		 */
		private void sendEvent(Event event, IContext context, String content) {
			event.ps.print(getGoodResponseHeader("", getContentType(""), false));
			event.ps.print(content);
			event.ps.flush();
			if (event.ps.checkError()) {
				debug("sendEvent: error in send - content: " + content);
			} else {
				//debug("sendEvent: no error - content: " + content);
			}
			event.ps.close();

			try {
				event.socket.close();
			} catch (IOException ex) {
			}

			if (content != null && content.length() > 0)
				debug("Sent [" + content + "] for [" + event.url + "]");
		}
	}

	public static EvServer getInstance() {
		if (instance == null) {
			instance = new EvServer();
		}
		return instance;
	}

	static byte[] getResource(String name) {
		try {
			DataInputStream is = new DataInputStream(EvServer.class.getResourceAsStream(name));
			byte bytes[] = new byte[is.available()];
			is.readFully(bytes);
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	static String unescape(String msg) {
		try {
			return URLDecoder.decode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return msg;
		}
	}

	EvServer() {
		super();
		
		do {
			try {
				portNumber = START_PORT_NUMBER++;
				serverSocket = new ServerSocket(portNumber);
			} catch (Exception e) {
			}
		} while (serverSocket == null);
		debug("Server ready for business at " + serverSocket);
		
		new Thread() {
			public void run() {
				startServer();
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.etools.egl.rui.visualedtor.server.IClientProxy#addPreviewContext(
	 * com.ibm.etools.egl.rui.visualedtor.server.PreviewContext)
	 * 
	 * Function called before the first request made
	 */
	public void addContext(IContext context) {
		synchronized(contextSynchObject) {
			// shouldn't!!
			if (contextKeyQueues.containsKey(context.getKey())) {
				// QueueProcessor q = contextKeyQueues.get(context.getKey());
				// q.setContext(context);
				//debug("addContext: context key already present?");
			} else {
				QueueProcessor q = new QueueProcessor();
				contextKeyQueues.put(context.getKey(), q);
				q.setContextKey(context.getKey());
				q.setContext(context);
				q.start();
			}
		}
	}
	
	public void addContextResolver(IContextResolver resolver) {
		synchronized(contextSynchObject) {
			if (!contextResolvers.contains(resolver)) {
				contextResolvers.add(resolver);
			}
		}
	}
	
	public void removeContextResolver(IContextResolver resolver) {
		synchronized(contextSynchObject) {
			contextResolvers.remove(resolver);
		}
	}

	private void debug(String string) {
		//System.out.println(string);
	}

	public void fail(PrintStream ps) throws InterruptedException {
		ps.print(getBadResponseHeader());
		ps.close();
	}

	public String getBadResponseHeader() {
		return "HTTP/1.0 404 " + EOL + "Content-Type: " + getContentType(".txt") + EOL + EOL;
	}

	public String getContentType(String url) {
//TODO EDT service		
//		String result = (String) RestServiceUtilities.contentTypes.get(getExtension(url));
//		if (result == null) {
			String result = "text/html;charset=utf-8";
//		}
		return result;
	}

	private String getExtension(String url) {
		return url.substring(url.lastIndexOf('.') + 1).toLowerCase();
	}

	public String getGoodResponseHeader(String url, String contentType, boolean cache) {
//TODO EDT service		
//		return getResponseHeader( url, contentType, cache, RestServiceUtilities.HTTP_STATUS_OK, RestServiceUtilities.HTTP_STATUS_MSG_OK );
		return getResponseHeader( url, contentType, cache, 200, "OK" );
	}

	private String getResponseHeader(String url, String contentType, boolean cache, int status, String statusMsg ) 
	{
		return "HTTP/1.0 "
		+ String.valueOf( status ) + " " + statusMsg
		+ EOL
		+ "Server: IBM EGL UI Server"
		+ EOL
		+ (false ? ("CacheControl: max-age=3600" + EOL
				+ "Expires: Fri, 30 Oct 2007 14:19:41 GMT" + EOL)
				: ("Pragma: no-cache" + EOL + "CacheControl: no-cache"
						+ EOL + "Expires: -1" + EOL))
		+ "Content-Type: " + contentType + EOL + EOL;
	}
	public int getPortNumber() {
		return portNumber;
	}

	public int generateContextKey() {
		contextKeyInc++;
		return contextKeyInc;
	}

	private boolean checkEventTypes(String url) {
		boolean result = false;
		if (url.indexOf("__getversion") > 0) {
			result = true;
		} else if (url.indexOf("___startup") > 0) {
			result = true;
		} else if (url.indexOf("___traceEvents") > 0) {
			result = true;
		} else if (url.indexOf("___widgetPositions") > 0) {
			result = true;
		} else if (url.indexOf("___openFile") > 0) {
			result = true;
		} else {
			debug("unknown event type: " + url);
		}
		return result;
	}
	
	/**
	 * Places all browser input events on an event queue for sequential
	 * processing
	 */
	protected void handleBrowserEvent(final Socket socket) {
		//debug("EvServer.handleBrowserEvent");
		try {
			String url = null;
			PrintStream ps = new PrintStream(socket.getOutputStream());
			try {
				RuiBrowserHttpRequest xmlRequest = null;
				try {
					xmlRequest = RuiBrowserHttpRequest.createNewRequest(socket);
				} catch (SocketTimeoutException e) {
					return;
				}
				if (xmlRequest != null) {
					url = xmlRequest.getURL();
					
					// Proxy must be checked first since the context key is passed along for debug.

//TODO EDT service
//					if (url.indexOf("__proxy") != -1 || xmlRequest.getHeaders() != null && xmlRequest.getHeaders().containsKey(RestServiceUtilities.EGL_SERVICE_CALL) ){
					if (url.indexOf("__proxy") != -1 || xmlRequest.getHeaders() != null && xmlRequest.getHeaders().containsKey("EGLREST") ){
						Event event = new Event();
						event.ps = ps;
						event.socket = socket;
						event.url = xmlRequest.getURL();
						event.xmlRequest = xmlRequest;
						event.arguments = xmlRequest.getArguments();
						___ProxyHandler hop = new ___ProxyHandler( xmlRequest, ps );
						Thread thread = new Thread( hop );
						thread.start();
					}
					else {
						// Delay invoking getContextArguments() until after we check if it's a service request.
						Map args = null;
						if (xmlRequest.getArguments().containsKey("contextKey")) {
							args = xmlRequest.getArguments();
						} else if (xmlRequest.getContentArguments() != null && xmlRequest.getContentArguments().containsKey("contextKey")) {
							args = xmlRequest.getContentArguments();
						} else {
							args = xmlRequest.getArguments();
						}
						
						if (args.containsKey("contextKey")) {
							// search contextKey
							String strContextKey = (String) args.get("contextKey");
							try {
								Integer intContextKey = Integer.valueOf(strContextKey);
								//debug("handleBrowserEvent url: " + url + " - key: " + intContextKey.toString());
								IContext context = findContext(intContextKey);
								if (context instanceof IContext2) {
									Event event = new Event();
									event.ps = ps;
									event.socket = socket;
									event.url = xmlRequest.getURL();
									event.xmlRequest = xmlRequest;
									event.key = intContextKey;
									event.arguments = args;
									((IContext2)context).handleEvent(event);
								}
								else if (this.containsKey(intContextKey)) {
									QueueProcessor q = findQueueProcessor(intContextKey);
									//
									Event event = new Event();
									event.ps = ps;
									event.socket = socket;
									event.url = xmlRequest.getURL();
									event.xmlRequest = xmlRequest;
									event.key = intContextKey;
									event.arguments = args;
									//
									//debug(xmlRequest.getURL());
									//
									q.addEvent(event);
								} 
								else {
									debug("handleBrowserEvent: context key not in queue");
									ps.print(getBadResponseHeader());
									//TODO how to make this extensible?
									if (url.indexOf("___getevent") == -1 && url.indexOf("___debugTerminate") == -1) {
										// Don't send this msg for certain requests.
										String msg = RUINlsStrings.bind(RUINlsStrings.ContextKeyNotFound, intContextKey);
										try {
											ps.write(msg.getBytes("UTF-8"));
										}
										catch (UnsupportedEncodingException uee) {
											ps.write(msg.getBytes());
										}
									}
									ps.close();
								}
							} catch (NumberFormatException ex) {
								debug("contextKey not cast");
							}
						}
						else if (url.indexOf("___openFile") != -1) {
							openFile(url, (String) args.get("file"), Integer.parseInt((String) args.get("line")), ps);
						}
						else if (url.indexOf("___loadScript") != -1) {
							loadScript((String) args.get("fileName"), ps);
							ps.flush();
							ps.close();
						}
						else {
							
							//debug("no context key: " + url);
							/*
							 * 
							 */
							// initial load external browser
							if (url.indexOf(".htm") > 0) {
								// this would only occur if loading from external browser with no context key
								// ?contextKey=7
								Integer key = new Integer(this.generateContextKey());
								loadFile(url/* + "?contextKey=" + key */, null, ps);
								debug("html file loaded with context key: " + key);
							} else if (this.checkEventTypes(url)) {
								// do nothing - case handled above
							} else {
								debug("handleBrowserEvent unkown url: " + url);
								loadFile(url, null, ps);
							}
							/*
							 * debug("no contextKey - setup the event, generate new key"
							 * ); Integer key = generateContextKey(); Event event =
							 * new Event(); event.ps = ps; event.socket = socket;
							 * event.url = xmlRequest.getURL(); event.xmlRequest =
							 * xmlRequest; event.key = key; QueueProcessor q = new
							 * QueueProcessor(); contextKeyQueues.put(key, q);
							 * q.setContextKey(key); q.start(); q.addEvent(event);
							 */
						}
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
				// ignore
			} catch (Throwable e) {
				System.err.println(url);
				e.printStackTrace();
				try {
					fail(ps);
				} catch (Exception ee) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 * @param intKey
	 * @param ps
	 */
	public void loadFile(String url, Integer intKey, PrintStream ps) {
		//debug("loadFile " + url);
		IContext context = null;
		File file = null;
		try {
			String trimmedURL = url.trim();
			byte[] bytes = null;
			if (bytes == null) {
				String uri = trimmedURL.substring(trimmedURL.indexOf('/') + 1,
						trimmedURL.length());
				if (intKey != null) {
					context = findContext(intKey);
				}
				if (context != null) {
					IServerContentProvider contentProvider = context.getContentProvider();
					bytes = contentProvider.loadContent(uri);
				} 
				else {
					SavedContentProvider contentProvider = new SavedContentProvider();
					bytes = contentProvider.loadContent(uri);
				}
			}
			if (bytes != null) {
				ps.print(getGoodResponseHeader(trimmedURL, getContentType(trimmedURL), !trimmedURL.endsWith(".egl")));
				ps.write(bytes);
				ps.flush();
				ps.close();
			} else {
				ps.print(getGoodResponseHeader(url, getContentType(url), !url.endsWith(".egl")));
				if (file != null) {
					ps.println("document.write(\"Could not open " + file.getAbsolutePath().replace('\\', '/') + "\");");
				} 
//TODO EDT deployment				
//				else if (trimmedURL.endsWith(DeploymentDescriptorFileUtil.JS_SUFFIX)) {
//					String uri = trimmedURL.substring(trimmedURL.indexOf('/') + 1, trimmedURL.length());
//					ps.println("document.write(\"Could not find or open deployment descriptor " + DeploymentDescriptorFileUtil.convertToEglddFile(uri) + "\");");
//				} 
				else {
					ps.println("document.write(\"Could not open " + url + "\");");
				}
				ps.flush();
				ps.close();
			}
		} catch (Exception e) {
			ps.print(getGoodResponseHeader(url, getContentType(url), !url.endsWith(".egl")));
			ps.println("document.write(\"Could not open " + url + "\");");
			e.printStackTrace();
			ps.flush();
			ps.close();
		}
	}
	
	private boolean containsKey(Integer key) {
		synchronized(contextSynchObject) {
			return contextKeyQueues.containsKey(key);
		}
	}
	
	/**
	 * This was made synchronized because this was getting called while we were
	 * still processing the last one.
	 * 
	 * Function called when the browser makes a connection to the server
	 * 
	 */
	protected void parse4Event(Event event) throws Exception {

		// debug( "EvServer.parse4Event entry" + url );
		String url = event.url;
		PrintStream ps = event.ps;
		try {
			final Map args = event.arguments;
			Integer intContextKey = event.key;
			if (intContextKey != null) {
				if (!this.containsKey(intContextKey)) {
					debug("context key: not in queue // url: " + url);
				} else {
					// context key is present :)
					debug("context key: " + intContextKey + " // url: " + url);
					if (getExtension(url).equals("egl")) {
						debug("Egl REST service: " + url);
						// loadEGLService(url, args, ps, request);
					} else if (url.indexOf("__getversion") != -1) {
						sendVersion(ps);
					}

					// This happens every five seconds as determined by
					// ClientStore.Client.waitForEvent
					//----------------------------------------------------------
					// --------
					// ---------------
					// else if( url.indexOf( "___getevent" ) != -1 ) {
					// // debug( "About to call sendEvent" );
					// sendEvent( ps, url, intContextKey );
					// }

					// else if (url.indexOf("__println") != -1) {
					// println(ps, (String) args.get("msg"));
					// }

					else if (url.indexOf("___startup") != -1) {
						debug("parse4event: startup called");
						ps.print(getGoodResponseHeader("", "text/html", false));
						ps.flush();
						ps.close();
					}
					else if (url.indexOf("___traceEvents") != -1) {
						ps.print(getGoodResponseHeader("", getContentType(""),
								false));
						ps.print("OK");
						ps.flush();
						ps.close();
					}
					else if (url.indexOf("___widgetPositions") != -1) {
						/*
						debug("EvServer.parse4event ___widgetPositions");
						String str = event.xmlRequest.getContent().substring(
								"value=".length());
						int indexOfAmp = str.lastIndexOf('&');
						if (indexOfAmp != -1) {
							str = str.substring(0, indexOfAmp);
						}
						*/
						widgetPositions(url, intContextKey, args.get("value").toString());
						ps.print(getGoodResponseHeader("", "text/html", false));
						ps.flush();
						ps.close();
					}
					else if (url.indexOf("___openFile") != -1) {
						openFile(url, (String) args.get("file"), Integer.parseInt((String) args.get("line")), ps);
					}
					else if (url.indexOf("___showFile") != -1) {
						int offset = Integer.parseInt((String) args.get("offset")); 
						int length = Integer.parseInt((String) args.get("length")); 
						showFile(intContextKey, offset, length, ps);
					}
					else if (url.indexOf("___reloadHandler") != -1) {
						loadFile((String)args.get("fileName"), intContextKey, ps);
						ps.flush();
						ps.close();
					}
					else if (url.indexOf("..") == -1) { 
														// server, please
						// Integer key =
						// Integer.valueOf(((String)args.get("contextKey")));
						debug("parse4event: loadFile");
						loadFile(url, intContextKey, ps);
					} 
					else {
						fail(ps);
					}
				}
			}
		} finally {
			// debug( "EvServer.parse4Event exit" + url );
		}
	}

	private void loadScript(String fileName, PrintStream ps) {
		if (fileName.charAt(0) == '/')
			fileName = fileName.substring(1);
		debug("open File "+fileName);
		String projectName = fileName.substring(0, fileName.indexOf("/"));
		fileName = fileName.substring(projectName.length()+1);
		byte[] result;
		
		result = null;//(byte[])Util.RUI_RUNTIME_JAVASCRIPT_FILE_CACHE.get(fileName);
		try {
			if(result != null){
				ps.write(result);
				return;
			}	
		} catch (Exception e ) {
			
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		
		try{
			FileLocator resourceLocator = new DebugFileLocator(project);
	
			EGLResource resource = resourceLocator.findResource( fileName );
			if ( resource == null || !resource.exists() ) {
				try {
					ps.write( "throw(\"missing\");".getBytes() );
				} catch ( IOException ioe ) {
				}
				return;
			}
		
			InputStream fileContents = new BufferedInputStream(resource.getInputStream());
			try{
				result = new byte[fileContents.available()];
				fileContents.read(result);
				ps.write(result);
				Util.RUI_RUNTIME_JAVASCRIPT_FILE_CACHE.put(fileName, result);
			}finally{
				fileContents.close();
			}
		}catch(Exception e){
			try {
				ps.write( "throw(\"error\");".getBytes() );
			} catch ( IOException ioe ) {
			}
		}
	}
	
	private void openFile(String url, String fileName, final int line, PrintStream ps) {
		try{
			url = url.trim();
			if (url.charAt(0) == '/') {
				url = url.substring( 1 );
			}
			if (fileName.charAt(0) == '/') {
				fileName = fileName.substring(1);
			}
			debug("open File "+fileName);
			String projectName = url.substring(0, url.indexOf("/"));
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			final IFile file = findFileInPath(project, fileName, new HashSet<IProject>());
			
			if (file == null) {
				debug("no such file: "+fileName+" inside project "+projectName); 
			}
			else
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page = EvEditorProvider.workbenchPage;
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
					if (desc == null)
						desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("foo.txt");
					try {
						IEditorPart editorPart = page.openEditor(new FileEditorInput(file), desc.getId());
						if (editorPart instanceof IEditorSelectAndRevealer) {
							IEditorSelectAndRevealer editor = (IEditorSelectAndRevealer) editorPart;
							editor.selectAndRevealLine(line);
						}
						else if (editorPart instanceof ITextEditor) {
							ITextEditor editor = (ITextEditor) editorPart;
							IDocumentProvider provider= editor.getDocumentProvider();
							IDocument document= provider.getDocument(editor.getEditorInput());
							int start= document.getLineOffset(line-1);
							int end= document.getLineOffset(line);
							editor.selectAndReveal(start, end-start);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ps.print(getGoodResponseHeader("", "text/html", false));
			ps.close();
		}
	}
	
	private IFile findFileInPath(IProject project, String fileName, Set<IProject> seen) {
		if (seen.contains(project)) {
			return null;
		}
		seen.add(project);
		
		String[] segments = fileName.split( "/" );
		
		String name = segments[segments.length - 1];
		int dot = name.lastIndexOf('.');
		if (dot != -1) {
			name = name.substring(0, dot);
		}
		name = InternUtil.intern(name);
		
		String[] pkg = new String[segments.length - 1];
		System.arraycopy(segments, 0, pkg, 0, pkg.length);
		pkg = InternUtil.intern(pkg);
		
		ProjectInfo info = ProjectInfoManager.getInstance().getProjectInfo(project);
		if (info.hasPart(pkg, name) != ITypeBinding.NOT_FOUND_BINDING) {
			IFile file = info.getPartOrigin(pkg, name).getEGLFile();
			if (file != null && file.exists()) {
				return file;
			}
		}
		
		// Try the projects on its egl path
		ProjectBuildPath path = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
		for (IProject req : path.getRequiredProjects()) {
			IFile file = findFileInPath(req, fileName, seen);
			if (file != null) {
				return file;
			}
		}
		
		return null;
	}

	
	protected void println(PrintStream ps, String msg) {
		msg = unescape(msg);
		ps.print(getGoodResponseHeader("", getContentType(""), false));
		ps.flush();
		ps.close();
	}

	public void refreshBrowserContents(final IContext context,
			final String location, final long modificationStamp,
			final boolean force) {
		debug("refreshBrowserContents call");
		/*
		 * Display.getDefault().asyncExec(new Runnable() { public void run() {
		 * String eventPending = "egl.reloadPage('" + location + "', "+
		 * modificationStamp + ", " + force + ")"; if
		 * (contextKeyQueues.containsKey(context.getKey())) { QueueProcessor q =
		 * contextKeyQueues.get(context.getKey());
		 * q.setEventPending(eventPending); } else {
		 * debug("refreshBrowserContents: context key found no queue"
		 * ); } } });
		 */
	}

	public void refreshBrowserIncremental(IContext context) {
		if (this.containsKey(context.getKey())) {
			try {
				QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
				String result = "egl.evTerminateReloadHandler();";
				q.setEventPending(result);
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Refresh Browser Incr: Error", e));
			}
		}
	}

	public void doWidgetClick(IContext context, int x, int y) {
		if (this.containsKey(context.getKey())) {
			try {
				QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
				String result = "egl.doWidgetClick(" + x + "," + y + ");";
				q.setEventPending(result);
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Refresh Browser Incr: Error", e));
			}
		}
	}

	
	public void changeProperty(IContext context, int x, int y, int w, int h, String property, String value,int totalCharactersChanged ) {
		if (this.containsKey(context.getKey())) {
			String trimmedURL = context.getUrl().trim();
			try {
				URL jURL = new URL(trimmedURL);
				String uri = jURL.getPath();
				uri = uri.substring(uri.indexOf('/') + 1,uri.length());
				QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
				property = property.substring(0,1).toUpperCase() + property.substring( 1 );
				String result = "egl.evUpdateWidgetProperty(" + x + "," + y + "," + w + "," + h +  ", \"" + property + "\", \"" + value +"\"," + totalCharactersChanged + ");";
				q.setEventPending(result);
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Refresh Browser Incr: Error", e));
			}
		}
	}
	
	public void deleteWidget(IContext context, int x, int y, int w, int h, int totalCharactersRemoved) {
		if (this.containsKey(context.getKey())) {
			String trimmedURL = context.getUrl().trim();
			try {
				URL jURL = new URL(trimmedURL);
				String uri = jURL.getPath();
				uri = uri.substring(uri.indexOf('/') + 1,uri.length());
				QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
				String result = "egl.evDeleteWidget(" + x + "," + y + "," + w + "," + h + "," + totalCharactersRemoved + ")";
				q.setEventPending(result);
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Refresh Browser Incr: Error", e));
			}
		}
	}
	
	public void moveWidget(IContext context, int x, int y, int w, int h, int tx, int ty, int tw, int th, int oldIndex, int newIndex, int[] charactersChanged) {
		if (this.containsKey(context.getKey())) {
			String trimmedURL = context.getUrl().trim();
			try {
				URL jURL = new URL(trimmedURL);
				String uri = jURL.getPath();
				uri = uri.substring(uri.indexOf('/') + 1,uri.length());
				QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
				String result = "egl.evMoveWidget(" + x + "," + y + "," + w + "," + h + "," + tx + "," + ty + "," + tw + "," + th +  "," + oldIndex + "," + newIndex + "," + charactersChanged[0] +  "," + charactersChanged[1] + ")";
				q.setEventPending(result);
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Refresh Browser Incr: Error", e));
			}
		}
	}
	
	public void removeContext(IContext context) {
		debug("removeContext");
		synchronized(contextSynchObject) {
			QueueProcessor q = (QueueProcessor)contextKeyQueues.remove(context.getKey());
			if (q != null) {
				q.setActive(false);
			}
		}
	}

	public void requestWidgetPositions(IContext context) {
		debug("EvServer.requestWidgetPositions");
		if (this.containsKey(context.getKey())) {
			QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
			q.setEventPending("egl.getWidgetPositions();");
		} else {
			System.out
					.println("requestWidgetPosition: context key found no queue");
		}
	}

	public void terminateSession(IContext context) {
		debug("terminateSession called");
		if (this.containsKey(context.getKey())) {
			QueueProcessor q = (QueueProcessor)findQueueProcessor(context.getKey());
			q.setEventPending("if (window.egl) egl.terminateSession()");
		} else {
			debug("terminateSession: context key found no queue");
		}
	}

	public void sendVersion(PrintStream ps) {
		ps.print(getGoodResponseHeader("", getContentType(""), false));
		ps.print("1");
		ps.flush();
		ps.close();
	}

	public void startServer() {
		try {
			//
			// run with
			// http://localhost:5498/someURL?arg1=value1&arg2=value2&arg3=value3
			//
			while (running) {
				final Socket client = serverSocket.accept();
				debug("serverSocket accept");
				handleBrowserEvent(client);

				/*
				 * Job eventHandler = new Job("HTTP Event Handler") {
					protected IStatus run(IProgressMonitor monitor) {
						return Status.OK_STATUS;
					}
				};
				eventHandler.setSystem(true);
				eventHandler.schedule();
				*/
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void widgetPositions(String url, Integer intKey, String positionInfo) {
		IContext context = findContext(intKey);
		if (context instanceof DesignContext) {
			IServerListener listener = ((DesignContext)context).getServerListener();
			if (listener != null) {
				//System.out.println(positionInfo);
				listener.acceptWidgetPositions(positionInfo);
			}
		}
	}
	
	private IContext findContext(Integer contextKey) {
		synchronized(contextSynchObject) {
			QueueProcessor q = (QueueProcessor)contextKeyQueues.get(contextKey);
			if (q != null) {
				return q.getContext();
			}
			
			if (contextResolvers.size() > 0) {
				for (IContextResolver resolver : contextResolvers) {
					IContext context = resolver.findContext(contextKey);
					if (context != null) {
						return context;
					}
				}
			}
		}
		return null;
	}
	
	private QueueProcessor findQueueProcessor(Integer contextKey) {
		synchronized(contextSynchObject) {
			return (QueueProcessor)contextKeyQueues.get(contextKey);
		}
	}
	
	private boolean isDesignPane(Integer contextKey){
		IContext context = findContext( contextKey );
		return(context instanceof DesignContext);
	}
	private boolean isPreviewPane(Integer contextKey){
		IContext context = findContext( contextKey );
		return(context instanceof PreviewContext);
	}
	
	private synchronized void showFile(Integer intKey, final int offset, final int length, PrintStream ps) {
		try{
			AbstractPreviewContext context = (AbstractPreviewContext)findContext(intKey);
			if (context != null) {
				final IServerListener listener = context.getServerListener();
				if (listener != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							listener.selectTextInEditor(offset, length);
						}
					});
				}
			}
		}finally{
			ps.print(getGoodResponseHeader("", "text/html", false));
			ps.close();
		}
	}
}
