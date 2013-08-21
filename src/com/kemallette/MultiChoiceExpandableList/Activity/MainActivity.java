package com.kemallette.MultiChoiceExpandableList.Activity;


import android.app.Activity;
import android.os.Bundle;

import com.kemallette.MultiChoiceExpandableList.R;
import com.kemallette.MultiChoiceExpandableListView.MultiChoiceExpandableListView;

public class MainActivity	extends
							Activity{


	@Override
	protected void onCreate(Bundle arg0){

		super.onCreate(arg0);

		setContentView(R.layout.main_activity);

		initViews();
		initListAdapter();
	}


	private void initViews(){

		MultiChoiceExpandableListView mExpandableList = (MultiChoiceExpandableListView) findViewById(R.id.list);
	}


	private void initListAdapter(){

	}


}
