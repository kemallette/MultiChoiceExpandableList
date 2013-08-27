package com.kemallette.MultiChoiceExpandableList;


import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;

public class MultiChoiceExpandableAdapter	extends
											BaseExpandableListAdapter	implements
																		MultiChoiceAdapterWrapper,
																		OnCheckedChangeListener{

	// TODO: To implement 'checking' a whole list item, instead of using a
	// compound button,
	// will need to implement checkable list item views with custom listener
	// interface


	private static final String	TAG	= "MultiChoiceExpandableAdapter";

	private static final String	GROUP_POSITION	= "groupPosition",
		CHILD_POSITION = "childPosition",
		GROUP_ID = "groupId",
		CHILD_ID = "childId";


	/**
	 * This will listen to the client adapter for data changes.
	 * 
	 * @author kemallette
	 * 
	 */
	protected class MyDataObserver	extends
									DataSetObserver{

		/*
		 * Client adapter data has changed
		 * 
		 * Called when the contents of the data set have changed. The recipient
		 * will obtain the new contents the next time it queries the data set.
		 */
		@Override
		public void onChanged(){

			mDataSetObservable.notifyChanged();
		}


		/*
		 * Called when the data set is no longer valid and cannot be queried
		 * again, such as when the data set has been closed.
		 */
		@Override
		public void onInvalidated(){

			mDataSetObservable.notifyInvalidated();
		}
	}

	class Holder{

		CompoundButton	mBox;


		void tagGroupBox(int groupPosition, long groupId){

			Bundle mData = new Bundle();
			mData.putInt(	GROUP_POSITION,
							groupPosition);
			mData.putLong(	GROUP_ID,
							groupId);
			mBox.setTag(mData);
		}


		void tagChildBox(int groupPosition,
							int childPosition,
							long groupId,
							long childId){

			Bundle mData = new Bundle();
			mData.putInt(	GROUP_POSITION,
							groupPosition);
			mData.putInt(	CHILD_POSITION,
							childPosition);
			mData.putLong(	GROUP_ID,
							groupId);
			mData.putLong(	CHILD_ID,
							childId);
			mBox.setTag(mData);
		}
	}

	private final DataSetObservable		mDataSetObservable	= new DataSetObservable();
	private final MyDataObserver		mDataObserver		= new MyDataObserver();

	private ExpandableListCheckListener	mExpandableCheckListener;
	private ExpandableListAdapter		mWrappedAdapter;


	public MultiChoiceExpandableAdapter(ExpandableListAdapter mWrappedAdapter){

		this.mWrappedAdapter = mWrappedAdapter;

		registerClientDataSetObserver(mDataObserver);
	}


	public MultiChoiceExpandableAdapter(ExpandableListAdapter mWrappedAdapter,
										ExpandableListCheckListener mExpandableCheckListener){

		this(mWrappedAdapter);
		this.mExpandableCheckListener = mExpandableCheckListener;
	}


	@Override
	public void onCheckedChanged(CompoundButton mButton, boolean isChecked){

		if (mExpandableCheckListener == null)
			Log.e(	TAG,
					"mExpandableCheckListener was null - make sure it gets set on MultiChoiceExpandableAdapter!");


		Bundle mCheckData = (Bundle) mButton.getTag(R.id.view_holder_key);

		if (mCheckData != null){

			if (mCheckData.containsKey(CHILD_ID)) // This was a child check
													// change
				mExpandableCheckListener.onChildCheckChange(mButton,
															mCheckData.getInt(GROUP_POSITION),
															mCheckData.getInt(CHILD_POSITION),
															mCheckData.getLong(CHILD_ID),
															isChecked);
			else
				mExpandableCheckListener.onGroupCheckChange(mButton,
															mCheckData.getInt(GROUP_POSITION),
															mCheckData.getLong(GROUP_ID),
															isChecked);

		}else
			Log.e(	TAG,
					"onCheckedChange mButton didn't have any tag data :( ");
	}


	@Override
	public ExpandableListAdapter getWrappedAdapter(){

		return mWrappedAdapter;
	}


	public void setWrappedAdapter(ExpandableListAdapter mClientAdapter){

		mWrappedAdapter = mClientAdapter;
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
		if (groupView.getTag(R.id.view_holder_key) == null){
			mGroupHolder = new Holder();

			mGroupHolder.mBox = (CheckBox) groupView
													.findViewById(android.R.id.checkbox);

			mGroupHolder.tagGroupBox(	groupPosition,
										getGroupId(groupPosition));
			mGroupHolder.mBox.setOnCheckedChangeListener(this);
			groupView.setTag(	R.id.view_holder_key,
								mGroupHolder);
		}else
			mGroupHolder = (Holder) groupView.getTag(R.id.view_holder_key);

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
		if (childView.getTag(R.id.view_holder_key) == null){
			mChildHolder = new Holder();

			mChildHolder.mBox = (CheckBox) childView
													.findViewById(android.R.id.checkbox);
			mChildHolder.tagChildBox(	groupPosition,
										childPosition,
										getGroupId(groupPosition),
										getChildId(	groupPosition,
													childPosition));
			mChildHolder.mBox.setOnCheckedChangeListener(this);
			childView.setTag(	R.id.view_holder_key,
								mChildHolder);
		}else
			mChildHolder = (Holder) childView.getTag(R.id.view_holder_key);

		return childView;
	}


	@Override
	public void registerDataSetObserver(DataSetObserver mObserver){

		mDataSetObservable.registerObserver(mObserver);
	}


	@Override
	public void unregisterDataSetObserver(DataSetObserver mObserver){

		mDataSetObservable.unregisterObserver(mObserver);
	}


	/*********************************************************************
	 * Delegates to client's adapter (mWrappedAdapter)
	 **********************************************************************/
	@Override
	public boolean areAllItemsEnabled(){

		return mWrappedAdapter.areAllItemsEnabled();
	}


	@Override
	public Object getChild(int groupPosition, int childPosition){

		return mWrappedAdapter.getChild(groupPosition,
										childPosition);
	}


	@Override
	public long getChildId(int groupPosition, int childPosition){

		return mWrappedAdapter.getChildId(	groupPosition,
											childPosition);
	}


	@Override
	public int getChildrenCount(int groupPosition){

		return mWrappedAdapter.getChildrenCount(groupPosition);
	}


	@Override
	public long getCombinedChildId(long groupId, long childId){

		return mWrappedAdapter.getCombinedChildId(	groupId,
													childId);
	}


	@Override
	public long getCombinedGroupId(long arg0){

		return mWrappedAdapter.getCombinedGroupId(arg0);
	}


	@Override
	public Object getGroup(int groupPosition){

		return mWrappedAdapter.getGroup(groupPosition);
	}


	@Override
	public int getGroupCount(){

		return mWrappedAdapter.getGroupCount();
	}


	@Override
	public long getGroupId(int groupPosition){

		return mWrappedAdapter.getGroupId(groupPosition);
	}


	@Override
	public boolean hasStableIds(){

		return mWrappedAdapter.hasStableIds();
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){

		return mWrappedAdapter.isChildSelectable(	groupPosition,
													childPosition);
	}


	@Override
	public boolean isEmpty(){

		return mWrappedAdapter.isEmpty();
	}


	@Override
	public void onGroupCollapsed(int groupPosition){

		mWrappedAdapter.onGroupCollapsed(groupPosition);
	}


	@Override
	public void onGroupExpanded(int groupPosition){

		mWrappedAdapter.onGroupExpanded(groupPosition);
	}


	/**
	 * This is for internal use. Set your listener on
	 * {@link MultiChoiceExpandableListView} instead.
	 * 
	 * @param mListener
	 */
	void setExpandableListCheckListener(ExpandableListCheckListener mListener){

		this.mExpandableCheckListener = mListener;
	}


	private void registerClientDataSetObserver(DataSetObserver mObserver){

		mWrappedAdapter.registerDataSetObserver(mObserver);

	}


	private void unregisterClientDataSetObserver(DataSetObserver mObserver){

		mWrappedAdapter.unregisterDataSetObserver(mObserver);

	}
}
