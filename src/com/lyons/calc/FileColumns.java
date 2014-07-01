package com.lyons.calc;

public enum FileColumns {
	PRIMARY_LAT(9), 
	PRIMARY_LONG(10),
	SECONDARY_LAT(13),
	SECONDARY_LONG(14);
	
	private int pos;
	private FileColumns(int pos){
		this.pos = pos;
	}
	
	public int getPosition(){
		return pos;
	}
}
