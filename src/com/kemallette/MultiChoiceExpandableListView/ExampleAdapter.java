package com.kemallette.MultiChoiceExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemallette.MultiChoiceExpandableList.Activity.Project;
import com.kemallette.MultiChoiceExpandableList.Activity.Task;
import com.kemallette.multichoiceexpandablelist.R;

public class ExampleAdapter extends BaseExpandableListAdapter {

	/**
	 * A map of sample projects with tasks.
	 */
	public static List<Project> mProjects = new ArrayList<Project>();

	private Context ctx;

	public ExampleAdapter(Activity context) {

		this.ctx = context;

		{
			// Add sample projects.
			mProjects.add(new Project(10, "<b>List Requirements</b>",
					"<br><i>Remember MVP - avoid feature creep</i>"));

			mProjects.add(new Project(20,
					"<b>Select Enviornment, Framework and Tooling</b>",
					"<br><i>Try not to roll your own whenever possible</i>"));

			mProjects
					.add(new Project(30, "<b>Do work</b>",
							"<br><i>Stop browsing Hacker News, Reddit, Slashdot...</i>"));

			mProjects.add(new Project(40, "<b>Iterate!</b>",
					"<br><i>Don't give up</i>"));

			mProjects
					.add(new Project(50, "<b>Maintain</b>",
							"<br><i>Woohoo, feature requests, bug reports, clueless managers..</i>"));

			mProjects
					.add(new Project(60, "<b>Rest</b>",
							"<br><i>You know that thing you do with your head on a pillow?</i>"));
		}

	}

	public Object getChild(int groupPosition, int childPosition) {
		return laptopCollections.get(laptops.get(groupPosition)).get(
				childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String laptop = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_item, null);
		}

		TextView item = (TextView) convertView.findViewById(R.id.laptop);

		ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Do you want to remove?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								List<String> child = laptopCollections
										.get(laptops.get(groupPosition));
								child.remove(childPosition);
								notifyDataSetChanged();
							}
						});
				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		});

		item.setText(laptop);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return laptopCollections.get(laptops.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return laptops.get(groupPosition);
	}

	public int getGroupCount() {
		return laptops.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String laptopName = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.group_item, null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.laptop);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(laptopName);
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
