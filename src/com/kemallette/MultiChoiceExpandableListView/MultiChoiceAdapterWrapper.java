package com.kemallette.MultiChoiceExpandableListView;


import android.widget.ExpandableListAdapter;


public interface MultiChoiceAdapterWrapper	extends
											ExpandableListAdapter{

	public ExpandableListAdapter getWrappedAdapter();


}
