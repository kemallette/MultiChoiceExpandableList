package com.kemallette.MultiChoiceExpandableList.Activity;


import android.app.Activity;
import android.os.Bundle;

import com.kemallette.MultiChoiceExpandableList.ExampleAdapter;
import com.kemallette.MultiChoiceExpandableList.MultiChoiceExpandableListView;
import com.kemallette.MultiChoiceExpandableList.R;

public class MainActivity	extends
							Activity{

	private MultiChoiceExpandableListView	mExpandableList;


	@Override
	protected void onCreate(Bundle arg0){

		super.onCreate(arg0);

		setContentView(R.layout.main_activity);

		initViews();
		initListAdapter();
	}


	private void initViews(){

		mExpandableList = (MultiChoiceExpandableListView) findViewById(R.id.list);
	}


	private void initListAdapter(){

		ExampleAdapter mAdapter = new ExampleAdapter(this);
		mExpandableList.setAdapter(mAdapter);
	}


}
