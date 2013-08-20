package com.kemallette.MultiChoiceExpandableList.Activity;


public class Task {

	public int id;
	public String title;
	public String content;

	public Task(int id, String title, String content) {

		this.id = id;
		this.title = title;
		this.content = content;
	}

	@Override
	public String toString() {

		return title + content;
	}
}
