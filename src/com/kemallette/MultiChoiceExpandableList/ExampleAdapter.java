package com.kemallette.MultiChoiceExpandableList;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kemallette.MultiChoiceExpandableList.Activity.Project;
import com.kemallette.MultiChoiceExpandableList.Activity.Task;

public class ExampleAdapter	extends
							BaseExpandableListAdapter{

	private class ExampleHolder{

		TextView	mTitle, mContent;
	}

	public static List<Project>	mProjects	= new ArrayList<Project>();

	private ExampleHolder		mHolder;

	private Activity			ctx;
	private LayoutInflater		inflater;


	public ExampleAdapter(Activity context){

		this.ctx = context;

		populateProjects();
	}


	private void populateProjects(){

		// Add sample projects.
		mProjects.add(new Project(	10,
									"<b>List Requirements</b>",
									"<br><i>Remember MVP - avoid feature creep</i>"));

		mProjects.add(new Project(	20,
									"<b>Select Enviornment, Framework and Tooling</b>",
									"<br><i>Try not to roll your own whenever possible</i>"));

		mProjects.add(new Project(	30,
									"<b>Do work</b>",
									"<br><i>Stop browsing Hacker News, Reddit, Slashdot...</i>"));

		mProjects.add(new Project(	40,
									"<b>Iterate!</b>",
									"<br><i>Don't give up</i>"));

		mProjects
					.add(new Project(	50,
										"<b>Maintain</b>",
										"<br><i>Woohoo, feature requests, bug reports, clueless managers..</i>"));

		mProjects
					.add(new Project(	60,
										"<b>Rest</b>",
										"<br><i>You know that thing you do with your head on a pillow?</i>"));

	}


	@Override
	public Object getGroup(int groupPosition){

		return mProjects.get(groupPosition);
	}


	@Override
	public Object getChild(int groupPosition, int childPosition){

		return mProjects.get(groupPosition)
						.getTasks()
						.get(childPosition);
	}


	@Override
	public long getGroupId(int groupPosition){

		return mProjects.get(groupPosition)
						.getId();
	}


	@Override
	public long getChildId(int groupPosition, int childPosition){

		return ((Task) getChild(groupPosition,
								childPosition)).getId();
	}


	@Override
	public int getGroupCount(){

		return mProjects.size();
	}


	@Override
	public int getChildrenCount(int groupPosition){

		return ((Project) getGroup(groupPosition)).getTasks()
													.size();
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
								View convertView, ViewGroup parent){

		if (convertView == null){

			if (inflater == null)
				inflater = ctx.getLayoutInflater();

			convertView = inflater.inflate(	R.layout.list_item,
											null);
		}

		if (convertView.getTag() == null){
			mHolder = new ExampleHolder();

			mHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
			mHolder.mContent = (TextView) convertView
														.findViewById(R.id.content);

			convertView.setTag(mHolder);
		}else
			mHolder = (ExampleHolder) convertView.getTag();

		mHolder.mTitle.setText(Html
									.fromHtml(((Project) getGroup(groupPosition)).getTitle()));
		mHolder.mContent.setText(Html
										.fromHtml(((Project) getGroup(groupPosition)).getContent()));

		return convertView;
	}


	@Override
	public View getChildView(	final int groupPosition,
								final int childPosition,
								boolean isLastChild,
								View convertView,
								ViewGroup parent){

		if (convertView == null){

			if (inflater == null)
				inflater = ctx.getLayoutInflater();

			convertView = inflater.inflate(	R.layout.list_item,
											null);
		}

		if (convertView.getTag() == null){
			mHolder = new ExampleHolder();

			mHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
			mHolder.mContent = (TextView) convertView
														.findViewById(R.id.content);

			convertView.setTag(mHolder);
		}else
			mHolder = (ExampleHolder) convertView.getTag();

		mHolder.mTitle.setText(Html
									.fromHtml(((Task) getChild(	groupPosition,
																childPosition)).getTitle()));
		mHolder.mContent.setText(Html
										.fromHtml(((Task) getChild(	groupPosition,
																	childPosition)).getContent()));

		return convertView;
	}


	@Override
	public boolean hasStableIds(){

		return true;
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){

		return true;
	}

}
