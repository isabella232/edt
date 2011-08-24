package org.eclipse.edt.ide.eck.ui.testresult;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.eck.ui.testresult.ResultSummaryBlock.Record_ResultSummary;
import org.eclipse.edt.ide.eck.ui.testresult.ResultSummaryBlock.TestResultPkgNode;
import org.eclipse.edt.ide.eck.ui.testresult.ResultSummaryBlock.TestResultRootNode;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class RSTreeLabelProvider extends LabelProvider implements IColorProvider{
	private List <Color> colors;
	
	private Color red;
	private Color green;
	
	public RSTreeLabelProvider(){
		red = new Color(Display.getCurrent(), new RGB(255, 0, 0));			
		green = new Color(Display.getCurrent(), new RGB(0, 128, 0));
		colors = new ArrayList<Color>();
		colors.add(red);
		colors.add(green);
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
	public Image getImage(Object element) {
		if(element instanceof Record_ResultSummary){
			Record_ResultSummary rs = (Record_ResultSummary)element;
			//if(rs.resultCode == ConstantUtil.SPASSED)
				//ImageDescriptor.createFromURL(url)
		}
		return super.getImage(element);
	}

	@Override
	public Color getBackground(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if(element instanceof Record_ResultSummary){
			Record_ResultSummary rs = (Record_ResultSummary)element;
			return rs.isSuccessful ? green : red;

		}
						
		if(element instanceof TestResultRootNode){
			return ((TestResultRootNode)element).isSuccessful ? green : red;
		}

		if(element instanceof TestResultPkgNode)	
			return ((TestResultPkgNode)element).isSuccessful ? green : red;
		return null;
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