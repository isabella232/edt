/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;

import java.util.List;

/**
 * A Container is an interface implementors of which contain Members
 *
 */
public interface Container extends Element {
	
	public List<Member> getMembers();
	
	public void addMember(Member mbr);
	
	List<Member> getAllMembers();

}
