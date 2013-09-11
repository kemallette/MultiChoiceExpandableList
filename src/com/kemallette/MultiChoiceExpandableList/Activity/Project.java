package com.kemallette.MultiChoiceExpandableList.Activity;


import java.util.ArrayList;

public class Project{

	public static int				TASK_SENTINAL	= 0;
	public static ArrayList<Task>	mTasks;

	public long						id;
	public String					title;
	public String					content;


	public Project(	long id,
					String title,
					String content){

		this.id = id;
		this.title = title;
		this.content = content;

		addTasks();
	}


	private static void addTasks(){

		mTasks = new ArrayList<Task>();

		for (int i = TASK_SENTINAL + 1; i < TASK_SENTINAL + 10; i++){

			mTasks.add(new Task(1000 + i,
								"List Requirements",
								"Remember MVP - avoid feature creep"));
			mTasks.add(new Task(2000 + i,
								"Select Enviornment, Framework and Tooling",
								"Try not to roll your own whenever possible"));
			mTasks.add(new Task(3000 + i,
								"Do work",
								"Stop browsing Hacker News, Reddit, Slashdot..."));
			mTasks.add(new Task(4000 + i,
								"Iterate!",
								"Don't give up"));
			mTasks.add(new Task(5000 + i,
								"Maintain",
								"Woohoo, feature requests, bug reports, clueless managers.."));
			mTasks.add(new Task(6000 + i,
								"Rest",
								"You know that thing you do with your head on a pillow?"));
		}

		TASK_SENTINAL += 10;
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
