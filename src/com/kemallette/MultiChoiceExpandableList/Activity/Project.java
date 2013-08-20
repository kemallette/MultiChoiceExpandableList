package com.kemallette.MultiChoiceExpandableList.Activity;

import java.util.ArrayList;

public class Project {

	public int id;
	public String title;
	public String content;

	public ArrayList<Task> mTasks;

	public Project(int id, String title, String content) {

		this.id = id;
		this.title = title;
		this.content = content;

		addTasks();
	}

	private void addTasks() {

		mTasks = new ArrayList<Task>();

		mTasks.add(new Task(100, "<b>List Requirements</b>",
				"<br><i>Remember MVP - avoid feature creep</i>"));
		mTasks.add(new Task(200,
				"<b>Select Enviornment, Framework and Tooling</b>",
				"<br><i>Try not to roll your own whenever possible</i>"));
		mTasks.add(new Task(300, "<b>Do work</b>",
				"<br><i>Stop browsing Hacker News, Reddit, Slashdot...</i>"));
		mTasks.add(new Task(400, "<b>Iterate!</b>", "<br><i>Don't give up</i>"));
		mTasks.add(new Task(500, "<b>Maintain</b>",
				"<br><i>Woohoo, feature requests, bug reports, clueless managers..</i>"));
		mTasks.add(new Task(600, "<b>Rest</b>",
				"<br><i>You know that thing you do with your head on a pillow?</i>"));

	}

	public ArrayList<Task> getTasks() {
		return mTasks;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {

		return title + content;
	}
}
