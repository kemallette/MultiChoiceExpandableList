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
							"<b>List Requirements</b>",
							"<br><i>Remember MVP - avoid feature creep</i>"));
		mTasks.add(new Task(200,
							"<b>Select Enviornment, Framework and Tooling</b>",
							"<br><i>Try not to roll your own whenever possible</i>"));
		mTasks.add(new Task(300,
							"<b>Do work</b>",
							"<br><i>Stop browsing Hacker News, Reddit, Slashdot...</i>"));
		mTasks.add(new Task(400,
							"<b>Iterate!</b>",
							"<br><i>Don't give up</i>"));
		mTasks.add(new Task(500,
							"<b>Maintain</b>",
							"<br><i>Woohoo, feature requests, bug reports, clueless managers..</i>"));
		mTasks.add(new Task(600,
							"<b>Rest</b>",
							"<br><i>You know that thing you do with your head on a pillow?</i>"));

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
