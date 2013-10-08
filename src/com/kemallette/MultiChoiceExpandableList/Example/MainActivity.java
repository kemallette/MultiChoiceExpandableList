package com.kemallette.MultiChoiceExpandableList.Example;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kemallette.MultiChoiceExpandableList.ExampleAdapter;
import com.kemallette.MultiChoiceExpandableList.MultiCheckListener;
import com.kemallette.MultiChoiceExpandableList.MultiCheckable;
import com.kemallette.MultiChoiceExpandableList.MultiChoiceExpandableListView;
import com.kemallette.MultiChoiceExpandableList.R;

public class MainActivity	extends
							Activity implements
									MultiCheckListener,
									OnItemSelectedListener{

	private static final String				TAG			= "MainActivity";

	private int								childMode	= MultiCheckable.CHECK_MODE_MULTI;
	private int								groupMode	= MultiCheckable.CHECK_MODE_MULTI;

	private ToggleButton					onlyOneItem;
	private MultiChoiceExpandableListView	mExpandableList;
	private ExampleAdapter					mAdapter;


	@Override
	protected void onCreate(final Bundle arg0){

		super.onCreate(arg0);

		setContentView(R.layout.main_activity);

		initViews();
		initListAdapter();

	}


	/*********************************************************************
	 * MultiCheckListner Callbacks
	 **********************************************************************/
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


	/*********************************************************************
	 * Spinner Callbacks
	 **********************************************************************/
	@Override
	public void onItemSelected(AdapterView<?> parent,
								View view,
								int position,
								long id){

		onlyOneItem.setChecked(false);
		mExpandableList.enableChoice(	groupMode,
										childMode);

		switch(parent.getId()){

			case R.id.groupChoiceModes:

				switch(position){

					case 0:
						Log.i(	TAG,
								"group to multi");
						groupMode = MultiCheckable.CHECK_MODE_MULTI;
						break;

					case 1:
						groupMode = MultiCheckable.CHECK_MODE_ONE;
						break;

					case 2:
						groupMode = MultiCheckable.CHECK_MODE_NONE;
						break;
				}
				mExpandableList.setGroupChoiceMode(groupMode);
				break;

			case R.id.childChoiceModes:

				switch(position){

					case 0:
						Log.i(	TAG,
								"child to multi");

						childMode = MultiCheckable.CHECK_MODE_MULTI;
						break;

					case 1:
						childMode = MultiCheckable.CHILD_CHECK_MODE_ONE_PER_GROUP;
						break;

					case 2:
						childMode = MultiCheckable.CHECK_MODE_ONE;
						break;

					case 3:
						childMode = MultiCheckable.CHECK_MODE_NONE;
						break;
				}
				mExpandableList.setChildChoiceMode(childMode);
				break;
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent){


	}


	private void initViews(){

		Spinner groupChoiceModes = (Spinner) findViewById(R.id.groupChoiceModes);
		Spinner childChoiceModes = (Spinner) findViewById(R.id.childChoiceModes);
		groupChoiceModes.setOnItemSelectedListener(this);
		childChoiceModes.setOnItemSelectedListener(this);

		onlyOneItem = (ToggleButton) findViewById(R.id.onlyOneItem);
		onlyOneItem.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
											boolean isChecked){

				if (isChecked)
					mExpandableList.enableOnlyOneItemChoice();
				else
					mExpandableList.disableOnlyOneItemChoice();

			}
		});

		mExpandableList = (MultiChoiceExpandableListView) findViewById(R.id.list);
		mExpandableList.setExpandableCheckListener(this);
	}


	private void initListAdapter(){

		mAdapter = new ExampleAdapter(this);
		mExpandableList.setAdapter(mAdapter);
	}
}
