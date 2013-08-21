package com.kemallette.MultiChoiceExpandableListView;


import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;


public class MultiChoiceExpandableAdapter	implements
											MultiChoiceAdapterWrapper{

	private static final String	TAG	= "MultiChoiceExpandableAdapter";


	private class Holder{

		CompoundButton	mBox;
	}

	private ExpandableListAdapter	mWrappedAdapter;


	public MultiChoiceExpandableAdapter(ExpandableListAdapter mWrappedAdapter){

		this.mWrappedAdapter = mWrappedAdapter;
	}


	@Override
	public ExpandableListAdapter getWrappedAdapter(){

		return mWrappedAdapter;
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
								View convertView, ViewGroup parent){

		View groupView = mWrappedAdapter.getGroupView(	groupPosition,
														isExpanded,
														convertView,
														parent);
		if (groupView == null)
			Log.e(	TAG,
					"Users adapter returned null for getGroupView");

		Holder mGroupHolder;
		if (groupView.getTag() == null){
			mGroupHolder = new Holder();

			mGroupHolder.mBox = (CheckBox) groupView.findViewById(android.R.id.checkbox);

			groupView.setTag(mGroupHolder);
		}else
			mGroupHolder = (Holder) groupView.getTag();


		return groupView;
	}


	@Override
	public View getChildView(final int groupPosition,
								final int childPosition,
								boolean isLastChild,
								View convertView,
								ViewGroup parent){

		View childView = mWrappedAdapter.getChildView(	groupPosition,
														childPosition,
														isLastChild,
														convertView,
														parent);


		if (childView == null)
			Log.e(	TAG,
					"Users adapter returned null for getChildView");

		Holder mChildHolder;
		if (childView.getTag() == null){
			mChildHolder = new Holder();

			mChildHolder.mBox = (CheckBox) childView.findViewById(android.R.id.checkbox);

			childView.setTag(mChildHolder);
		}else
			mChildHolder = (Holder) childView.getTag();


		return childView;
	}


	@Override
	public boolean areAllItemsEnabled(){

		return mWrappedAdapter.areAllItemsEnabled();
	}


	@Override
	public Object getChild(int arg0, int arg1){

		return mWrappedAdapter.getChild(arg0,
										arg1);
	}


	@Override
	public long getChildId(int arg0, int arg1){

		return mWrappedAdapter.getChildId(	arg0,
											arg1);
	}


	@Override
	public int getChildrenCount(int arg0){

		return mWrappedAdapter.getChildrenCount(arg0);
	}


	@Override
	public long getCombinedChildId(long arg0, long arg1){

		return mWrappedAdapter.getCombinedChildId(	arg0,
													arg1);
	}


	@Override
	public long getCombinedGroupId(long arg0){

		return mWrappedAdapter.getCombinedGroupId(arg0);
	}


	@Override
	public Object getGroup(int arg0){

		return mWrappedAdapter.getGroup(arg0);
	}


	@Override
	public int getGroupCount(){

		return mWrappedAdapter.getGroupCount();
	}


	@Override
	public long getGroupId(int arg0){

		return mWrappedAdapter.getGroupId(arg0);
	}


	@Override
	public boolean hasStableIds(){

		return mWrappedAdapter.hasStableIds();
	}


	@Override
	public boolean isChildSelectable(int arg0, int arg1){

		return mWrappedAdapter.isChildSelectable(	arg0,
													arg1);
	}


	@Override
	public boolean isEmpty(){

		return mWrappedAdapter.isEmpty();
	}


	@Override
	public void onGroupCollapsed(int arg0){

		mWrappedAdapter.onGroupCollapsed(arg0);
	}


	@Override
	public void onGroupExpanded(int arg0){

		mWrappedAdapter.onGroupExpanded(arg0);
	}


	@Override
	public void registerDataSetObserver(DataSetObserver arg0){

		mWrappedAdapter.registerDataSetObserver(arg0);
	}


	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0){

		mWrappedAdapter.unregisterDataSetObserver(arg0);
	}


}
