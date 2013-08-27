package com.kemallette.MultiChoiceExpandableList.Activity;


import java.util.ArrayList;

public class Project{

	public long				id;
	public String			title;
	public String			content;

	public ArrayList<Task>	mTasks;


	public Project(	long id,
					String title,
					String content){

		this.id = id;
		this.title = title;
		this.content = content;

		addTasks();
	}


	private void addTasks(){

		mTasks = new ArrayList<Task>();

		mTasks.add(new Task(100,
							"List Requirements",
							"Remember MVP - avoid feature creep"));
		mTasks.add(new Task(200,
							"Select Enviornment, Framework and Tooling",
							"Try not to roll your own whenever possible"));
		mTasks.add(new Task(300,
							"Do work",
							"Stop browsing Hacker News, Reddit, Slashdot..."));
		mTasks.add(new Task(400,
							"Iterate!",
							"Don't give up"));
		mTasks.add(new Task(500,
							"Maintain",
							"Woohoo, feature requests, bug reports, clueless managers.."));
		mTasks.add(new Task(600,
							"Rest",
							"You know that thing you do with your head on a pillow?"));

	}


	public ArrayList<Task> getTasks(){

		return mTasks;
	}


	public long getId(){

		return id;
	}


	public String getTitle(){

		return title;
	}


	public String getContent(){

		return content;
	}


	public ArrayList<Task> getmTasks(){

		return mTasks;
	}


	public void setmTasks(ArrayList<Task> mTasks){

		this.mTasks = mTasks;
	}


	public void setId(long id){

		this.id = id;
	}


	public void setTitle(String title){

		this.title = title;
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
