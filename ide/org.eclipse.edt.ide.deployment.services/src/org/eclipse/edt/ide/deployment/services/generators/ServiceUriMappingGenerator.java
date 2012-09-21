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
package org.eclipse.edt.ide.deployment.services.generators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.rui.internal.util.WebUtilities;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceUriMappingGenerator
{
	private DeploymentContext context;
	private String uriMappingFileName;
	private Document document;
	private IProject project;
	private Element root;
	private String projectName;
	private LogicAndDataPart logicPart;
//	private String contextRoot;
	private boolean isHostProgramService;
	private Restservice restService;
	
	private static String gatewayServiceName = "UIGatewayService";

/**	
    <servicemappings>
    	<contextroot>MyTestProject</contextroot>
    	<classpath>cp1;cp2;</classpath>
		<servicemapping classname="rest.services.Employee">
		   	<uri eglfunction="$func_xxxx" httpFunction="GET" encoding="XML">/Employee</uri>
		</servicemapping>	
    </servicemappings>
*/	
	public ServiceUriMappingGenerator( DeploymentContext context )
	{
		this.context = context;
	}

	public boolean visit( Service service, Restservice restService )
	{
		isHostProgramService = false;
		gen( service, restService );
		
		return false;
	}

	public boolean visit( ExternalType externalType, Restservice restService )
	{
		isHostProgramService = true;
		gen( externalType, restService );
		return false;
	}

	private void gen( LogicAndDataPart part, Restservice restService )
	{
		logicPart = part;
		project = context.getTargetProject();
		projectName = project.getName();
		uriMappingFileName = ServiceUtilities.getUriMappingFileName( projectName );
		this.restService = restService;
		
		genUriMapping();
	}
	private void genUriMapping()
	{
		try
		{
			getRoot();
		}
		catch( Exception e )
		{
			//throw new JavaGenException( new Exception(Messages.bind(Messages.J2EEDeploymentSolution_87, uriMappingFileName),  e) );
		}
		//String fullyQualifiedName = Aliaser.packageNameAlias( logicPart.getPackageName(), '.' ) + '.' + Utilities.getServiceCoreName( logicPart );
		String fullyQualifiedName = JavaAliaser.packageNameAlias( logicPart.getCaseSensitivePackageName() ) + '.' + logicPart.getCaseSensitiveName();
		removeServiceContextNode( RestServiceUtilities.CONTEXT_ROOT_ELEM );
		removeServiceContextNode( RestServiceUtilities.CLASSPATH_ELEM );
		removeServiceNode( fullyQualifiedName );
		addServiceNode( fullyQualifiedName );
		addContextNode();
		try
		{
			addClasspathNode();
			writeDocument();
		}
		catch( Exception e )
		{
			//throw new JavaGenException( new Exception(Messages.bind(Messages.J2EEDeploymentSolution_88, uriMappingFileName),  e) );
		}
	}
	
	private void removeServiceNode( String fullyQualifiedName )
	{
		NodeList mappings = root.getElementsByTagName( RestServiceUtilities.SERVICE_MAPPING_ELEM );
		Element node = null;
		ArrayList nodes2Remove = new ArrayList();
		String className;
		for( int idx = 0; idx < mappings.getLength(); idx++ )
		{
			node = (Element)mappings.item( idx );
			className = node.getAttribute( RestServiceUtilities.CLASSNAME_ATTR );
			if( className == null )
			{
				nodes2Remove.add( node );
			}
			else if( className.equals( fullyQualifiedName ) )
			{
				nodes2Remove.add( node );
			}
		}
		for( int idx = 0; idx < nodes2Remove.size(); idx++ )
		{
			root.removeChild( (Node)nodes2Remove.get( idx ) );
		}
	}

	private void removeServiceContextNode( String nodeName )
	{
		NodeList nodeList = root.getElementsByTagName( nodeName );
		ArrayList nodes2Remove = new ArrayList();
		for( int idx = 0; idx < nodeList.getLength(); idx++ )
		{
			nodes2Remove.add( (Element)nodeList.item( idx ) );
		}
		for( int idx = 0; idx < nodes2Remove.size(); idx++ )
		{
			root.removeChild( (Node)nodes2Remove.get( idx ) );
		}
	}

	private void addContextNode()
	{
		Element serviceContextNode = document.createElement( RestServiceUtilities.CONTEXT_ROOT_ELEM );
		root.appendChild( serviceContextNode );
		serviceContextNode.appendChild( document.createTextNode( '/' + WebUtilities.getContextRoot(project) + '/' + ServiceUtilities.getRestServiceRoot() ) );
	}
	
	private void addClasspathNode() throws CoreException
	{
//		if( !context.getBuildDescriptor().getJ2EE() )
//		{
//			Element serviceContextNode = document.createElement( RestServiceUtilities.CLASSPATH_ELEM );
//			root.appendChild( serviceContextNode );
//			serviceContextNode.appendChild( document.createTextNode( getClasspath( project.getName() ) ) );
//		}
	}
	
	private void addServiceNode( String fullyQualifiedName )
	{
		/*
		<servicemapping classname="rest.services.Employee">
		   	<uri eglfunction="$func_xxxx" httpFunction="GET" encoding"XML">/Employee</uri>
		</servicemapping>	
		 */
		Element serviceMappingNode = document.createElement( RestServiceUtilities.SERVICE_MAPPING_ELEM );
		root.appendChild( serviceMappingNode );
		serviceMappingNode.setAttribute( RestServiceUtilities.CLASSNAME_ATTR, fullyQualifiedName );
		if( ServiceUtilities.isStateful( logicPart ) )
		{
			serviceMappingNode.setAttribute( RestServiceUtilities.STATEFUL_SERVICE_ATTR, RestServiceUtilities.TRUE_VALUE );
		}
		if( isHostProgramService )
		{
			serviceMappingNode.setAttribute( RestServiceUtilities.HOST_PROGRAM_SERVICE_ATTR, RestServiceUtilities.TRUE_VALUE );
		}
		Element uriNode = document.createElement( RestServiceUtilities.URI_ELEM );
		uriNode.setAttribute(RestServiceUtilities.HTTP_FUNCTION_ATTR, RestServiceUtilities.HTTP_POST_METHOD);
//		uriNode.setAttribute(RestServiceUtilities.IN_ENCODING_ATTR, org.eclipse.edt.javart.services.servlet.rest.rpc.RestServiceProjectInfo.ENCODING_STR_JSON);
//		uriNode.setAttribute(RestServiceUtilities.OUT_ENCODING_ATTR, RestServiceProjectInfo.ENCODING_STR_JSON);
		String nodeValue = ServiceUtilities.getUri( logicPart, restService );
		uriNode.appendChild( document.createTextNode( nodeValue ) );
		serviceMappingNode.appendChild( uriNode );
	}
	
	private void getRoot() throws Exception
	{
    	String fullyQualifiedUriMappingFileName = project.getLocation().toPortableString();
		List<IResource> folders = new ArrayList<IResource>();
    	org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities.getJavaSourceFolders(project, folders);
		if ( folders.size() == 0 ) {
			return;
		}
		String sourceFolder = folders.get(0).getFullPath().removeFirstSegments( 1 ).toOSString();
		fullyQualifiedUriMappingFileName += '/' + sourceFolder + '/' + uriMappingFileName;
		File file = new File( fullyQualifiedUriMappingFileName );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if( file.exists() )
        {
        	try
        	{
        		document = builder.parse( file );
        	}
        	catch( Exception e ){}
        }
        
        if( document == null )
        {
	        document = builder.newDocument();
        }
        
        NodeList roots = document.getElementsByTagName( RestServiceUtilities.SERVICES_MAPPINGS_ELEM );
        if( roots == null || roots.getLength() == 0 )
        {
        	root = document.createElement( RestServiceUtilities.SERVICES_MAPPINGS_ELEM );
        	document.appendChild( root );
        }
        else
        {
        	root = (Element)roots.item( 0 );
        }
    }    
	
	private void writeDocument() throws Exception
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        cleanupDocument();
        DOMSource source = new DOMSource(document);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult( os );
        transformer.transform(source, result);
        InputStream bis = new ByteArrayInputStream( os.toByteArray() );
//		ServiceFileWriter updater = 
//			new ServiceFileWriter( bis, uriMappingFileName, "", context.getBuildDescriptor(), null );
//
//		//Run the updater.  If something goes wrong, an exception will be thrown.
//		EclipseUtilities.runUpdater( updater );
        String javaSourceFolder = EclipseUtilities.getJavaSourceFolderName( context.getTargetProject() );
        
		IPath targetFilePath = new Path( "/" + context.getTargetProject().getName() + "/" + javaSourceFolder + "/" + uriMappingFileName ); 
		
		IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);

		if( targetFile.exists() ) {
			targetFile.setContents(bis, true, true, context.getMonitor());
		} else {
			targetFile.create(bis, true, context.getMonitor());
//			targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
		}
        os.close();
        bis.close();
	}
	
	private void cleanupDocument()
	{
		document.normalize();
		NodeList nodes = root.getChildNodes();
		Node node;
		for( int idx = 0; idx < nodes.getLength(); idx++ )
		{
			node = nodes.item( idx );
			if( node.getNodeType() != Node.ELEMENT_NODE )
			{
				root.removeChild(node);
			}
		}
	}
	private String getClasspath( String projectName ) throws CoreException
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		IJavaProject javaProject = (IJavaProject)project.getNature( JavaCore.NATURE_ID );

		String classpath = "";
		try 
		{
			if( javaProject != null )
			{
				classpath = createClasspath(javaProject);
			}
		} 
		catch (IOException e1) 
		{
//			throw new JavaGenException( new Exception( Messages.bind(Messages.J2EEDeploymentSolution_86, projectName), e1 ));
		}
		catch( URISyntaxException use )
		{
//			throw new JavaGenException( new Exception( Messages.bind(Messages.J2EEDeploymentSolution_86, projectName), use ));
		}
		return classpath;
	}
	private static String createClasspath(IJavaProject javaProject ) throws JavaModelException, MalformedURLException, URISyntaxException
	{
		StringBuffer classpath = new StringBuffer();
		String path;
		IClasspathEntry[] resolvedClasspath = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < resolvedClasspath.length; i++) {
			IClasspathEntry entry = resolvedClasspath[i];
			if(entry.getEntryKind() != IClasspathEntry.CPE_SOURCE)
			{
				path = entry.getPath().toPortableString();
			}
			else
			{
				if(entry.getOutputLocation() != null)
				{
					path = javaProject.getProject().getLocation().removeLastSegments(1).append(entry.getOutputLocation()).toPortableString();
				}
				else
				{
					path = javaProject.getProject().getLocation().removeLastSegments(1).append(javaProject.getOutputLocation()).toPortableString();
				}
			}
			classpath.append(path);
			classpath.append( ";" );
		}
		return classpath.toString();
	}
	
//	public static void mapUIGatewayService( Context context )
//	{
//		ElementFactoryImpl factory = new ElementFactoryImpl();
//		Service service= factory.createService( factory.createName(gatewayServiceName));
//		service.setPackageName(gatewayServicePackage);
//		CommonUtilities.addAnnotation(service, context, Constants.SERVICE_STATEFUL_ANNOTATION, new Boolean( true ) );
//		CommonUtilities.addAnnotation(service, context, Constants.SERVICE_URI_ANNOTATION, "/" + gatewayServiceName);
//		service.accept( new ServiceUriMappingGenerator( context ) );
//		CommonUtilities.removeAnnotation(service, Constants.SERVICE_STATEFUL_ANNOTATION);
//		CommonUtilities.removeAnnotation(service, Constants.SERVICE_URI_ANNOTATION);
//	}
}
