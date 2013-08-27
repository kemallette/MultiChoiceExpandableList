package com.kemallette.MultiChoiceExpandableList.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Checkable;
import android.widget.Toast;

import com.kemallette.MultiChoiceExpandableList.ExampleAdapter;
import com.kemallette.MultiChoiceExpandableList.ExpandableListCheckListener;
import com.kemallette.MultiChoiceExpandableList.MultiChoiceExpandableListView;
import com.kemallette.MultiChoiceExpandableList.R;

public class MainActivity	extends
							Activity implements
									ExpandableListCheckListener{

	private MultiChoiceExpandableListView	mExpandableList;
	private ExampleAdapter					mAdapter;


	@Override
	protected void onCreate(Bundle arg0){

		super.onCreate(arg0);

		setContentView(R.layout.main_activity);

		initViews();
		initListAdapter();

	}


	@Override
	public void onGroupCheckChange(Checkable checkedView,
									int groupPosition,
									long groupId,
									boolean isChecked){

		Toast.makeText(	this,
						"Group Check Change\nid: "
							+ groupId
							+ "\n isChecked: "
							+ isChecked,
						Toast.LENGTH_SHORT)
				.show();
	}


	@Override
	public void onChildCheckChange(Checkable checkedView,
									int groupPosition,
									int childPosition,
									long childId,
									boolean isChecked){

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
	}


	private void initListAdapter(){

		mAdapter = new ExampleAdapter(this);
		mExpandableList.setAdapter(mAdapter);
		mExpandableList.setExpandableCheckListener(this);
	}


}
