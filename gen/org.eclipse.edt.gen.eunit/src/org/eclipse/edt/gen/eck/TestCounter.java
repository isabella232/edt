package org.eclipse.edt.gen.eck;

public class TestCounter {
	private int count=0;
	
	public void increment(){
		count++;
	}
	
	public void increment(int c){
		count += c;
	}
	
	public int getCount(){
		return count;
	}
	
}
