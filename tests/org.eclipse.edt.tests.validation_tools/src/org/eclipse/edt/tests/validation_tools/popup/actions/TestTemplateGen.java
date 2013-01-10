/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation_tools.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class TestTemplateGen implements IObjectActionDelegate {
	
	private IWorkbenchWindow window;
	IStructuredSelection sel = null;
	String errorMsg = null;
	
	private Map allMessages = new TreeMap();
	
	private static class UsageInfo {
		int numMessagesIssued;
		Set messagesIssued;
		String className;
		
		UsageInfo(int numMessagesIssued, Set messagesIssued, String className) {
			this.numMessagesIssued = numMessagesIssued;
			this.messagesIssued = messagesIssued;
			this.className = className;
		}
	}

	/**
	 * Constructor for Action1.
	 */
	public TestTemplateGen() {
		super();
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.window = targetPart.getSite().getWorkbenchWindow();
	}
	
	List filenamesAndMessageCounts = new ArrayList();
	Map filenamesToUsageInfo = new HashMap();
	int totalMessageCount = 0;
	
	Map classNamesToTestNames = new HashMap();
	
	class StringAndInt implements Comparable {
		String s;
		int i;
		
		StringAndInt( String s, int i ) {
			this.s = s;
			this.i = i;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			return s.compareTo( ((StringAndInt) o).s );
		}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		errorMsg = null;
		
		filenamesAndMessageCounts = new ArrayList();
		filenamesToUsageInfo = new HashMap();
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (sel == null)
					return;

				monitor.beginTask("Writing annotated file templates to d:\\temp...", IProgressMonitor.UNKNOWN);
				
				IJavaProject project = null;
				
				for (Iterator iter = sel.iterator(); iter.hasNext() && errorMsg == null;) {					
					project = (IJavaProject) iter.next();
					
					IFolder folder = project.getProject().getFolder( "eglvalidation" );
										
					if( folder != null ) {
						try {
							folder.accept( new IResourceVisitor() {
								String lastFolderName = null;
								public boolean visit(IResource resource) throws CoreException {
									if( resource instanceof IFile ) {
										IFile file = (IFile) resource;
										if( file.getFileExtension() != null && file.getFileExtension().equalsIgnoreCase( "java" ) ) {
											genTestFile( file, lastFolderName + "/" + getTestName( file.getName() ), file.getName() );
											collectErrorUsages(file, lastFolderName + "/" + getTestName( file.getName() ));
										}
									}
									if( resource instanceof IFolder ) {
										lastFolderName = ((IFolder) resource).getName();
									}
									
									return true;
								}
								
								String getTestName( String s ) {
									if( s.equals( "EGLStatementValidator.java" ) ) {
										return "statementValidator.egl";
									}
									if( s.equals( "EGLUIPropertiesValidationUtility.java" ) ) {
										return "uiPropertiesValidationUtility.egl";
									}
									if( s.equals( "EGLBindingResolverAndValidator.java" ) ) {
										return "bindingResolverAndValidator.egl";
									}
									s = s.replaceAll( ".java", ".egl" ).replaceAll( "Validator", "" ).replaceFirst( "EGL", "" );
									return Character.toLowerCase( s.charAt(0) ) + s.substring(1);
								}
								
								void genTestFile( IFile file, String testFilename, String className ) throws CoreException {
									
									ICompilationUnit cUnit = JavaCore.createCompilationUnitFrom( file );
									
									Map functionNamesToMessageUsageLists = getFunctionNamesToMessageUsageLists( cUnit.getAllTypes() );
									Set keySet = new TreeSet( functionNamesToMessageUsageLists.keySet() );
									if( !keySet.isEmpty() ) {
										BufferedWriter writer = null;
										try {
											File newFile = new File( "d:/temp/" + testFilename );
											Set usedMessages = new TreeSet();
											if( !newFile.exists() ) {
												newFile.getParentFile().mkdirs();
											}
											writer = new BufferedWriter( new FileWriter( newFile ) );
											int numMessageUsages = 0;
											for( Iterator iter = keySet.iterator(); iter.hasNext(); ) {
												String funcName = (String) iter.next();
												List messageUsages = (List) functionNamesToMessageUsageLists.get( funcName );
												writer.write( "// " + funcName + ":" + System.getProperty( "line.separator" ) );
												for( Iterator iter2 = messageUsages.iterator(); iter2.hasNext(); ) {													
													String next = (String) iter2.next();
													numMessageUsages += 1;
													totalMessageCount += 1;
													writer.write( "// 1 " + getErrorLineContent( next ).replaceAll("^([0-9][0-9][0-9][0-9]\\*?):(.*)\\{(.*)", "$2{$1, $3").trim() + System.getProperty( "line.separator" ) );
													usedMessages.add(Integer.toString(getMessageNumber(next)));
												}
												writer.write( System.getProperty( "line.separator" ) );
											}
											filenamesToUsageInfo.put(testFilename, new UsageInfo(numMessageUsages, usedMessages, className.replaceAll(".java", "")));
											filenamesAndMessageCounts.add( new StringAndInt( testFilename, numMessageUsages ) );
										}
										catch( Exception e ) {
											e.printStackTrace();
										}
										finally {
											try {
												writer.close();
											}
											catch( Exception e2 ) {												
											}
										}
									}
								}
								
								void collectErrorUsages( IFile file, String testFilename ) throws CoreException {
									
									ICompilationUnit cUnit = JavaCore.createCompilationUnitFrom( file );
									
									Map functionNamesToMessageUsageLists = getFunctionNamesToMessageUsageLists( cUnit.getAllTypes() );
									Set keySet = new TreeSet( functionNamesToMessageUsageLists.keySet() );
									if( !keySet.isEmpty() ) {
										
										for( Iterator iter = keySet.iterator(); iter.hasNext(); ) {
											String funcName = (String) iter.next();
											List messageUsages = (List) functionNamesToMessageUsageLists.get( funcName );
											
											for( Iterator iter2 = messageUsages.iterator(); iter2.hasNext(); ) {													
												String next = (String) iter2.next();
												int number = getMessageNumber( next );
												String content = getErrorLineContent( next );
												totalMessageCount += 1;
												
												if(!allMessages.containsKey(new Integer(number))) {													
													allMessages.put(new Integer(number), content);
												}
											}
										
										}
										//filenamesAndMessageCounts.add( new StringAndInt( testFilename, numMessageUsages ) );
									}
								}
								
								String getErrorLineContent( String str ) {
									StringBuffer sb = new StringBuffer();
									int number = getMessageNumber( str );
									String messageText = getMessageText( number );
									

//									sb.append("// 1 " );
//									sb.append( messageText + " " );
//									sb.append( "{" + Integer.toString( number ) + ", " + str + "}" );
									sb.append( Integer.toString(number));
									if( messageText.startsWith( "{0} - " ) && messageText.indexOf("At line {") != -1) {
										sb.append("*:");
										messageText = messageText.substring(0, messageText.indexOf("At line {"));
									}
									else {
										sb.append(": ");
									}
									sb.append( " " + messageText + " {" + str.substring("EGLMESSAGE_".length()) + "}");
									
									return sb.toString();
								}
								
								Map cachedMessageNumbers = new HashMap();
								
								int getMessageNumber( String str ) {
									if(cachedMessageNumbers.containsKey(str)) { return ((Integer) cachedMessageNumbers.get(str)).intValue(); };
									
									BufferedReader reader = null;
									int number = 0;
									try {
										reader = new BufferedReader( new FileReader( "d:/temp/EGLValidationMessages.java" ) );
										String line = reader.readLine();
										while( line != null && number == 0) {
											int index = line.indexOf( str );
											if( index != -1 ) {
												number = Integer.parseInt( new StringTokenizer( line.substring( line.indexOf( '=' ) + 1 ).trim(), "\"" ).nextToken() );
											}
											
											line = reader.readLine();
										}
										
									}
									catch( IOException e ) {
										e.printStackTrace();
									}
									finally {
										try {
											reader.close();
										}
										catch( Exception e ) {											
										}
									}
									cachedMessageNumbers.put(str, new Integer(number));
									return number;
								}
								
								String getMessageText( int msgNumber ) {
									BufferedReader reader = null;
									String result = null;
									try {
										reader = new BufferedReader( new FileReader( "d:/temp/EGLValidationResources.properties" ) );
										String line = reader.readLine();
										while( line != null && result == null) {
											if( line.startsWith( Integer.toString( msgNumber ) ) ) {
												result = line.substring( line.indexOf( "=" ) + 1 ).trim();
												if( result.startsWith( "{0} - " ) ) {
													//result = result.substring( "{0} - ".length() );
												}
												int index = result.indexOf( "At line {" );
												if( index != -1 ) {
													//result = result.substring( 0, index );
												}
												result = result.trim();
											}
											
											line = reader.readLine();
										}
										
									}
									catch( IOException e ) {
										e.printStackTrace();
									}
									finally {
										try {
											reader.close();
										}
										catch( Exception e ) {											
										}
									}
									return result;
								}
								
								Map getFunctionNamesToMessageUsageLists( IType[] types ) throws CoreException {									
									Map result = new HashMap();
									BufferedReader reader = null;
									List messageUsageList = new ArrayList();

									for( int i = 0; i < types.length; i++ ) {
										IType type = types[i];
										
										IMethod[] methods = type.getMethods();
										for( int j = 0; j < methods.length; j++ ) {
											IMethod method = methods[j];	
											String currentFuncName = method.getElementName();
											
											String source = method.getSource();
											
											StringTokenizer st = new StringTokenizer( source, System.getProperty( "line.separator" ) );
											while( st.hasMoreTokens() ) {
												String line = st.nextToken();
												
												if(line.trim().startsWith("//")) {
													continue;
												}

												int index = line.indexOf( "EGLValidationMessages.EGLMESSAGE_" );
												if( index != -1 ) {
													index = index + "EGLValidationMessages.".length();
													char ch = line.charAt( index );
													StringBuffer sb = new StringBuffer();
													while( ch == '_' || Character.isLetter( ch ) || Character.isDigit( ch ) ) {
														sb.append( ch );
														if( index + 1 == line.length() ) {
															break;
														}
														ch = line.charAt( ++index );
													}
													messageUsageList.add( sb.toString() );
												}
											}
											
											if( currentFuncName != null ) {													
												if( result.get( currentFuncName ) != null ) {
													int k = 1;
													while( result.get( currentFuncName + " (" + Integer.toString( k ) + ")" ) != null ) {
														k += 1;
													}
													currentFuncName = currentFuncName + " (" + Integer.toString( k ) + ")";
												}
												if( !messageUsageList.isEmpty() ) {
													result.put( currentFuncName, messageUsageList );
												}
												messageUsageList = new ArrayList();
											}
										}
									}
								
									return result;
								}
							} );
						}
						catch( CoreException e ) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		try {
		
			new ProgressMonitorDialog(window.getShell()).run(true, true, runnable);
			
			Collections.sort( filenamesAndMessageCounts );
			for( Iterator iter = filenamesAndMessageCounts.iterator(); iter.hasNext(); ) {
				StringAndInt next = (StringAndInt) iter.next();
				System.out.println( next.s + "\t" + next.i );
			}
			System.out.println();
			System.out.println( "total message usages = " + totalMessageCount );
			
			writeAllMessagesFile();
			writeUsageSummaryFile();

		} catch (InvocationTargetException e) {
			//throw new RuntimeException(e);
		} catch (InterruptedException e) {
			//throw new RuntimeException(e);
		}	
	}
	
	Map messageNumberToCanonicalNumber = new HashMap();

	private void writeAllMessagesFile() {
		BufferedWriter writer = null;
		try {
			File newFile = new File( "d:/temp/allMessages.txt" );
			if( !newFile.exists() ) {
				newFile.getParentFile().mkdirs();
			}
			writer = new BufferedWriter( new FileWriter( newFile ) );
			
			int i = 1;
			
			for( Iterator iter = allMessages.keySet().iterator(); iter.hasNext(); ) {
				Object next = iter.next();
				StringBuffer sb = new StringBuffer();
				sb.append( Integer.toString(i) + ". " + (String) allMessages.get(next));
				messageNumberToCanonicalNumber.put(next, new Integer(i));
				
				List messages = new ArrayList();
				StringTokenizer st = new StringTokenizer(sb.toString());
				sb = new StringBuffer();
				while(st.hasMoreTokens()) {
					String s = st.nextToken();
					
					if(sb.length() + s.length() + 1 >= 120) {
						messages.add(sb.toString());
						sb = new StringBuffer();
						sb.append("            " + s);
					}
					else {
						sb.append( (sb.length()==0?"":" ") + ((s.endsWith(":")?(s.endsWith("*:")?s:s+" "):s)));
					}
				}
				messages.add(sb.toString());
				
				for(int j=0; j<messages.size(); j++) {
					if(j==0) {
						if(i<10) {
							writer.write("  ");
						}
						else if(i<100) {
							writer.write(" ");
						}
					}
					writer.write((String)messages.get(j));					
					writer.write( System.getProperty( "line.separator" ) );
				}
				
				i+=1;
			}			
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch( Exception e2 ) {												
			}
		}
	}
	
	private void writeUsageSummaryFile() {
		BufferedWriter writer = null;
		try {
			File newFile = new File( "d:/temp/usageSummary.txt" );
			if( !newFile.exists() ) {
				newFile.getParentFile().mkdirs();
			}
			writer = new BufferedWriter( new FileWriter( newFile ) );
			
			List filenames = new ArrayList(filenamesToUsageInfo.keySet());
			Collections.sort(filenames);
			for(Iterator iter = filenames.iterator(); iter.hasNext();) {
				String fName = (String) iter.next();
				UsageInfo uInfo = (UsageInfo) filenamesToUsageInfo.get(fName);
				
				writer.write(fName);
				writer.write("\t");
				writer.write(uInfo.className);
				writer.write("\t");
				writer.write(Integer.toString(uInfo.numMessagesIssued));				
				writer.write("\t");
				writer.write(getMessageListString(uInfo.messagesIssued));
				writer.write("\t");
				writer.write(System.getProperty( "line.separator" ));
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch( Exception e2 ) {												
			}
		}
	}
	
	private String getMessageListString(Set messagesIssued) {
		if(messagesIssued.isEmpty()) {
			return "n/a";
		}
		
		List canonicalNumbers = new ArrayList();
		for(Iterator iter = messagesIssued.iterator(); iter.hasNext();) {
			canonicalNumbers.add(messageNumberToCanonicalNumber.get(new Integer((String) iter.next())));
		}
		StringBuffer sb = new StringBuffer();
		Collections.sort(canonicalNumbers);
		
		if(canonicalNumbers.size() == 1) {
			sb.append(((Integer) canonicalNumbers.get(0)).toString());
		}
		else {
			int previousNumber = -1;
			int lastSkipped = -1;
			for(Iterator iter = canonicalNumbers.iterator(); iter.hasNext();) {
				int currentNumber = ((Integer) iter.next()).intValue();
				if(currentNumber == previousNumber + 1) {
					if(iter.hasNext()) {
						lastSkipped = currentNumber;	
					}
					else {
						sb.append(currentNumber);
						sb.append(")");
					}					
				}
				else {
					if(lastSkipped != -1) {
						sb.append(lastSkipped);
						lastSkipped = -1;
						sb.append("),");
					}
					sb.append(currentNumber);
					if(iter.hasNext()) {
						sb.append(",");
					}
				}
				
				previousNumber = currentNumber;
			}
			
			sb = new StringBuffer(sb.toString().replaceAll("([0-9]*,[0-9]*\\))", "($1"));
		}
		
		return sb.toString();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;

		sel = (IStructuredSelection) selection;
	}

}
