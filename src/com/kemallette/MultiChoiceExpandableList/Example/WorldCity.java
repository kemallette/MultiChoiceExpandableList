package com.kemallette.MultiChoiceExpandableList.Example;

import com.kemallette.MultiChoiceExpandableList.R;

public class WorldCity{
	private final int id;
	private final String name;
	
	
	public WorldCity(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId(){
	
		return id;
	}

	public String getName(){
	
		return name;
	}

	@Override
	public String toString(){
		return name;
	}
	
}