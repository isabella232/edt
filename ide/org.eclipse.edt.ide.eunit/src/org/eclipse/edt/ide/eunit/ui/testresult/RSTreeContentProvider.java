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
package org.eclipse.edt.ide.eunit.ui.testresult;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.Record_ResultSummary;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.ResultStatisticCnts;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.TestResultPkgNode;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.TestResultRootNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.forms.IManagedForm;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RSTreeContentProvider implements ITreeContentProvider{
	private HashMap<String, TestResultPkgNode> map;
	
	public RSTreeContentProvider(){			
	}
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}
	
	@Override
	public Object getParent(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		Object[] children = getChildren(arg0);
		if(children == null)
			return false;
		return (children.length > 0);
	}	
	
	@Override
	public Object[] getElements(Object arg0) {
		// TODO Auto-generated method stub
		return getChildren(arg0);
	}
	
	@Override
	public Object[] getChildren(Object inputElem) {
		if(inputElem instanceof IManagedForm){
			IManagedForm form = (IManagedForm)inputElem;
			return new Object[]{form.getInput()};
		}
		if(inputElem instanceof TestResultRootNode){
			populateTree((TestResultRootNode)inputElem);
			return map.values().toArray();
		}
		if(inputElem instanceof TestResultPkgNode){
			TestResultPkgNode pkgNode = (TestResultPkgNode)inputElem;				
			ArrayList<Record_ResultSummary> rsList = pkgNode.listRS;
			return rsList.toArray();
		}
		return null;
	}
	
	private void populateTree(TestResultRootNode rootNode){
		if(map == null){
			map = new HashMap<String, TestResultPkgNode>();
			rootNode.expectedTotalTestVariationCnt = Integer.parseInt(rootNode.elemRoot.getAttribute(ConstantUtil.ATTRIB_expCnt));
			rootNode.startTS = rootNode.elemRoot.getAttribute(ConstantUtil.ATTRIB_startTS);
			rootNode.endRunTS = rootNode.elemRoot.getAttribute(ConstantUtil.ATTRIB_endRunTS);
			rootNode.finalTS = rootNode.elemRoot.getAttribute(ConstantUtil.ATTRIB_finalTS);
			NodeList nl = rootNode.elemRoot.getElementsByTagName(ConstantUtil.ELEM_trSummary);
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {

					//get the TRSummary element
					Element el = (Element)nl.item(i);

					Record_ResultSummary rs = getRecordResultSummary(el);						
					
					//add it to map
					TestResultPkgNode pkgNode = map.get(rs.pkgName);
					if(pkgNode == null){
						//create new
						pkgNode = new TestResultPkgNode();
						pkgNode.pkgName = rs.pkgName;
					}
					if(pkgNode.listRS == null)
						pkgNode.listRS = new ArrayList<Record_ResultSummary>();
					pkgNode.listRS.add(rs);					
						
					if(!rs.isSuccessful){
						pkgNode.isSuccessful = false;
						rootNode.isSuccessful = false;
					}
					
					map.put(rs.pkgName, pkgNode);
					pkgNode.statisticCnts = pkgNode.statisticCnts.plus(rs.statisticCnts);
					rootNode.statisticCnts = rootNode.statisticCnts.plus(rs.statisticCnts);
				}
			}
		}
	}		
	
	/**
	 * I take an trSummary element and read the values in, create
	 * an trSummary object and return it
	 */
	private Record_ResultSummary getRecordResultSummary(Element el) {
		String name = el.getAttribute(ConstantUtil.ATTRIB_name);
		String pkgName = el.getAttribute(ConstantUtil.ATTRIB_pkgName);
		String sResult = el.getAttribute(ConstantUtil.ATTRIB_resultCode);
		int resultCode = Integer.parseInt(sResult);
		
		int testCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_testCnt));
		int expectedCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_expCnt));
		int passedCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_passedCnt));
		int failedCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_failedCnt));
		int exCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_errCnt));
		int badCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_badCnt));
		int notRunCnt = Integer.parseInt(el.getAttribute(ConstantUtil.ATTRIB_notRunCnt));
		
		ResultStatisticCnts statisticCnt = new ResultStatisticCnts(testCnt, expectedCnt, passedCnt, failedCnt, exCnt, badCnt, notRunCnt);
		
		Record_ResultSummary rs = new Record_ResultSummary(pkgName, name, resultCode, statisticCnt); 
		return rs;
	}

}
