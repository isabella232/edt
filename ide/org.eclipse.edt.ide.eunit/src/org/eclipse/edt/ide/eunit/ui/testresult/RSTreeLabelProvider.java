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
import java.util.List;

import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.Record_ResultSummary;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.TestResultPkgNode;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.TestResultRootNode;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class RSTreeLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {
	private List <Color> colors;
	
	private Color red;
	private Color green;
	private Color purple;
	private Color orange;
	
	private Font boldFont;
	
	public RSTreeLabelProvider(){
		red = new Color(Display.getCurrent(), new RGB(255, 0, 0));			
		green = new Color(Display.getCurrent(), new RGB(0, 128, 0));
		purple = new Color(Display.getCurrent(), new RGB(184, 0, 73));
		orange = new Color(Display.getCurrent(), new RGB(255, 127, 0));
		colors = new ArrayList<Color>();
		colors.add(red);
		colors.add(green);
		colors.add(purple);
		colors.add(orange);
		boldFont = new FontRegistry().getBold( Display.getCurrent().getSystemFont().getFontData()[0].getName() );
	}
	
	@Override
	public String getText(Object element) {			
		if(element instanceof Record_ResultSummary){
			Record_ResultSummary rs = (Record_ResultSummary)element;
			return rs.name;
		}
						
		if(element instanceof TestResultRootNode){
			return ((TestResultRootNode)element).rootname;
		}

		if(element instanceof TestResultPkgNode)	
			return ((TestResultPkgNode)element).pkgName;
		
		return element.toString();
	}

	@Override
	public Color getBackground(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground( Object element ) 
	{
		int resultCode = -1;

		if ( element instanceof Record_ResultSummary )
		{
			resultCode = ((Record_ResultSummary)element).resultCode;
		}
		else if ( element instanceof TestResultPkgNode )
		{
			resultCode = ((TestResultPkgNode)element).statisticCnts.overallResult();
		}
		else if ( element instanceof TestResultRootNode )
		{
			resultCode = ((TestResultRootNode)element).statisticCnts.overallResult();
		}
		
		switch ( resultCode )
		{
			case ConstantUtil.PASSED:
				return green;
			case ConstantUtil.FAILED:
				return red;
			case ConstantUtil.EXCEPTION:
				return purple;
			case ConstantUtil.NOT_RUN:
				return orange;
			default:
				return null;
		}
	}
	
	@Override
	public Font getFont( Object element )
	{
		int resultCode = -1;

		if ( element instanceof Record_ResultSummary )
		{
			resultCode = ((Record_ResultSummary)element).resultCode;
		}
		else if ( element instanceof TestResultPkgNode )
		{
			resultCode = ((TestResultPkgNode)element).statisticCnts.overallResult();
		}
		else if ( element instanceof TestResultRootNode )
		{
			resultCode = ((TestResultRootNode)element).statisticCnts.overallResult();
		}
		
		if ( resultCode == ConstantUtil.FAILED || resultCode == ConstantUtil.EXCEPTION )
		{
			return boldFont;
		}
		else
		{
			return null;
		}
	}
	
	public void dispose() {
		super.dispose();
		
		//dispose color resources
		if(colors != null && !colors.isEmpty()){
			for (Color color : colors) {
				color.dispose();				
			}
		}
		colors.clear();
		colors = null;
	}
}
