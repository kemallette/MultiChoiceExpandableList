package com.kemallette.MultiChoiceExpandableList.Activity;


public class Task{

	public long		id;
	public String	title;
	public String	content;


	public Task(long id,
				String title,
				String content){

		this.id = id;
		this.title = title;
		this.content = content;
	}


	public long getId(){

		return id;
	}


	public void setId(long id){

		this.id = id;
	}


	public String getTitle(){

		return title;
	}


	public void setTitle(String title){

		this.title = title;
	}


	public String getContent(){

		return content;
	}


	public void setContent(String content){

		this.content = content;
	}


	@Override
	public String toString(){

		return title
				+ content;
	}
}
