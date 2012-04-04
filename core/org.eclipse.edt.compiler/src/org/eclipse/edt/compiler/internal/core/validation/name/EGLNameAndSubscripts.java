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
package org.eclipse.edt.compiler.internal.core.validation.name;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author dollar
 *
 * name is an array of tokens representing names and dots
 * subscripts is an array of arrays of tokens representing subscripts.
 * Each subscript subarray is an array of tokens representing names and dots.
 * substrings is an array of arrays of tokens representing the from and to substrings.
 * Each substring subarray is an array of tokens representing names and dots.
 * 
 * Currently, we do not allow subscripts or substrings to be subscripted, but this is set up
 * so that it would work with small modifications if we do.
 * 
 * 
 * a.b.c.d[e.f.g,h,2].j[k,m:n]
 * will look like this:
 * name[0] = a token            subscripts[0] = name[0] = e token   
 * name[1] = dot token                          name[1] = dot token
 * name[2] = b token                            name[2] = f token 
 * name[3] = dot token                          name[3] = dot token
 * name[4] = c token                            name[4] = g token
 * name[5] = dot token          subscripts[1] = name[0] = h token
 * name[6] = d token            subscripts[2] = name[0] = 2 token
 * name[7] = dot token          subscripts[3] = name[0] = k token 
 * name[8] = j token            
 *                              substrings[0] = name[0] = m token
 *                              substrings[1] = name[0] = n token
 * 
 */
public class EGLNameAndSubscripts {
	ArrayList names = null;
	ArrayList subscripts = null;
	ArrayList substrings = null;


	/**
	 * Creates an EGLNameToken with text.
	 */
	public EGLNameAndSubscripts() {
		names = new ArrayList();
	    subscripts = new ArrayList();
	    substrings = new ArrayList();
	}

	public void addName(EGLNameToken token){
		names.add(token);
		return;
	}	
	
	public void newSubscript() {
		subscripts.add(new ArrayList());
		return;
	}

	public void addSubscript(EGLNameToken token){
		((ArrayList)(subscripts.get(subscripts.size()-1))).add(token);
		return;
	}
	
	public void newSubstring() {
		substrings.add(new ArrayList());
		return;
	}

	public void addSubstring(EGLNameToken token){
		((ArrayList)(substrings.get(substrings.size()-1))).add(token);
		return;
	}	
	
	public ArrayList getNames(){
		return names;
	}	

	public ArrayList getSubscripts(){
		return subscripts;
	}
	
	public ArrayList getLastSubscript(){
		return ((ArrayList)subscripts.get(subscripts.size()-1));
	}

	public ArrayList getSubstrings(){
		return substrings;
	}

	public ArrayList getFromSubstring(){
		return ((ArrayList)substrings.get(0));
	}
	
	public ArrayList getToSubstring(){
		return ((ArrayList)substrings.get(1));
	}
	
	public void copySubscriptToSubstring(){
		ArrayList lastSubscript = ((ArrayList)subscripts.get(subscripts.size()-1));
		subscripts.remove(subscripts.size()-1);
		Iterator fromNames = lastSubscript.iterator();
		while (fromNames.hasNext()) {
			addSubstring(((EGLNameToken)fromNames.next()));
		}
		return;
	}
		
	public void outputTrees(  ) {

 		Iterator nameList = names.iterator();
		Object name = null;
 		Iterator subscriptList = subscripts.iterator();
 		ArrayList subscriptNames = null;
 		Iterator substringList = substrings.iterator();
 		ArrayList substringNames = null;
		System.out.println("Names");		
		while( nameList.hasNext()) {
			
			name = nameList.next();
			System.out.println(((EGLNameToken)name).toString());
			
		}
		System.out.println("Subscripts");	
		int subcount = 1;
		while( subscriptList.hasNext()) {
			System.out.println("Subscript " + Integer.toString(subcount));			
			subscriptNames = (ArrayList)subscriptList.next();
			nameList = subscriptNames.iterator();
			while( nameList.hasNext()) {
				name = nameList.next();
				System.out.println(((EGLNameToken)name).toString());
			}
			subcount = subcount + 1; 
		}	
		System.out.println("Substrings");	
		subcount = 1;
		while( substringList.hasNext()) {
			System.out.println("Substring " + Integer.toString(subcount));			
			substringNames = (ArrayList)substringList.next();
			nameList = substringNames.iterator();
			while( nameList.hasNext()) {
				name = nameList.next();
				System.out.println(((EGLNameToken)name).toString());
			}
			subcount = subcount + 1;
		}		
		return ;	
	}

}

