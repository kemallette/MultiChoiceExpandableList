package com.kemallette.MultiChoiceExpandableList.Example;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Checkable;
import android.widget.Toast;

import com.kemallette.MultiChoiceExpandableList.ExampleAdapter;
import com.kemallette.MultiChoiceExpandableList.MultiCheckListener;
import com.kemallette.MultiChoiceExpandableList.MultiCheckable;
import com.kemallette.MultiChoiceExpandableList.MultiChoiceExpandableListView;
import com.kemallette.MultiChoiceExpandableList.R;

public class MainActivity	extends
							Activity implements
									MultiCheckListener{

	private MultiChoiceExpandableListView	mExpandableList;
	private ExampleAdapter					mAdapter;


	@Override
	protected void onCreate(final Bundle arg0){

		super.onCreate(arg0);

		setContentView(R.layout.main_activity);

		initViews();
		initListAdapter();

	}


	@Override
	public void onGroupCheckChange(final Checkable checkedView,
									final int groupPosition,
									final long groupId,
									final boolean isChecked){

		Toast.makeText(	this,
						"Group Check Change\nid: "
							+ groupId
							+ "\n isChecked: "
							+ isChecked,
						Toast.LENGTH_SHORT)
				.show();
	}


	@Override
	public void onChildCheckChange(final Checkable checkedView,
									final int groupPosition,
									final long groupId,
									final int childPosition,
									final long childId,
									final boolean isChecked){

		Toast.makeText(	this,
						"Child Check Change\nid: "
							+ childId
							+ "\n isChecked: "
							+ isChecked,
						Toast.LENGTH_SHORT)
				.show();

	}


	private void initViews(){

		mExpandableList = (MultiChoiceExpandableListView) findViewById(R.id.list);
		mExpandableList.setExpandableCheckListener(this);
	}


	private void initListAdapter(){

		mAdapter = new ExampleAdapter(this);
		mExpandableList.setAdapter(mAdapter);
		mExpandableList.setGroupChoiceMode(MultiCheckable.CHECK_MODE_MULTI)
						.setChildChoiceMode(MultiCheckable.CHECK_MODE_MULTI);
	}


}
