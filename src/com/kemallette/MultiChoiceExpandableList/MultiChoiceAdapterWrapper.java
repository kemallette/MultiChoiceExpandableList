package com.kemallette.MultiChoiceExpandableList;


import android.widget.ExpandableListAdapter;


public interface MultiChoiceAdapterWrapper	extends
											ExpandableListAdapter{

	public ExpandableListAdapter getWrappedAdapter();


}
