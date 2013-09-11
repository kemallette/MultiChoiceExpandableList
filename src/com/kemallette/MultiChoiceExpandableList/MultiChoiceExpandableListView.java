package com.kemallette.MultiChoiceExpandableList;


import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class MultiChoiceExpandableListView	extends
											ExpandableListView	implements
																MultiChoiceExpandableList{

	private static final String				TAG						= "MultiChoiceExpandableListView";


	/**
	 * Flag indicating if an item's checked state change is from a user actually
	 * touching the screen
	 */
	private boolean							isCheckChangeFromTouch	= false;

	private int								groupChoiceMode			= CHECK_MODE_NONE;
	private int								childChoiceMode			= CHECK_MODE_NONE;

	private int								groupCheckTotal,
											childCheckTotal;

	private ExpandableListCheckListener		mClientCheckListener;

	private MultiChoiceExpandableAdapter	mAdapterWrapper;

	/**
	 * CAUTION: A groupId key will still be present if any of its children are
	 * checked even if the group isn't. Use isGroupChecked() for group's check
	 * status.
	 */
	private CheckStateStore					mCheckedItems;


	public MultiChoiceExpandableListView(	Context context,
											AttributeSet attrs,
											int defStyle){

		super(	context,
				attrs,
				defStyle);

		// TODO: set check modes and checkChildrenOnGroupCheck from xml attrs

	}


	public MultiChoiceExpandableListView(	Context context,
											AttributeSet attrs){

		this(	context,
				attrs,
				-1);
	}


	public MultiChoiceExpandableListView(Context context){

		this(	context,
				null);
	}


	@Override
	public void onRestoreInstanceState(Parcelable state){

		super.onRestoreInstanceState(state);

		// TODO: restore necessary saved fields - remember that super does a few
		// things too
	}


	@Override
	public Parcelable onSaveInstanceState(){

		Parcelable mParcel = super.onSaveInstanceState();

		// TODO: save all necessary fields - remember that super does a few
		// things too
		return mParcel;
	}


	@Override
	public void setAdapter(ExpandableListAdapter adapter){

		if (adapter == null)
			throw new NullPointerException("The adapter you passed was null");

		if (mAdapterWrapper == null)
			mAdapterWrapper = new MultiChoiceExpandableAdapter(	adapter,
																this);
		else
			mAdapterWrapper.setWrappedAdapter(adapter);

		super.setAdapter(mAdapterWrapper);
	}


	@Override
	public ExpandableListAdapter getExpandableListAdapter(){

		return mAdapterWrapper.getWrappedAdapter();
	}


	/***********************************************************
	 * ExpandableListCheckListener Callbacks
	 ************************************************************/
	@Override
	public void onGroupCheckChange(Checkable checkedView, int groupPosition,
									long groupId, boolean isChecked){

		setGroupCheckedState(	groupId,
								groupPosition,
								isChecked);

		if (mClientCheckListener != null)
			mClientCheckListener.onGroupCheckChange(checkedView,
													groupPosition,
													groupId,
													isChecked);
	}


	@Override
	public void onChildCheckChange(Checkable checkedView,
									int groupPosition,
									long groupId,
									int childPosition,
									long childId,
									boolean isChecked){


		setChildCheckedState(
								groupPosition,
								groupId,
								childPosition,
								childId,
								isChecked);

		if (mClientCheckListener != null)
			mClientCheckListener.onChildCheckChange(checkedView,
													groupPosition,
													groupId,
													childPosition,
													childId,
													isChecked);
	}


	/*********************************************************************
	 * Internal utils
	 **********************************************************************/
	protected void setIsCheckChangeFromTouch(boolean isCheckChangeFromTouch){

		this.isCheckChangeFromTouch = isCheckChangeFromTouch;
	}


	protected void refreshVisibleCheckableViews(){

		if (isCheckChangeFromTouch) // This is true while a proper touch
									// initiated check change callback fires
									// which means the view is already
									// indicating the check state we have saved
			return;

		setRefreshingViewStateFlagOn(); // Letting the adapter know we're
										// refreshing a checkable view's
										// state in order to avoid
										// duplicating check change
										// callbacks


		View listItem;
		Checkable checkableView;

		int packedPositionType, groupPosition, childPosition;

		// These are both implemented in AdapterView which means
		// they're 'raw'/'flat' list positions
		int firstVis = getFirstVisiblePosition();
		int lastVis = getLastVisiblePosition();
		int count = lastVis
					- firstVis;

		long packedPosition;

		while (count >= 0){ // looping through visible list items which
							// are the only items that will need to be
							// refreshed. The adapter's getView will
							// take care of all non-visible items when
							// the list is scrolled

			listItem = getChildAt(count); // getChildAt is implemented in
											// ViewGroup, so using a
											// 'raw'/'flat' position is fine

			if (listItem != null){

				checkableView = (Checkable) listItem.findViewById(android.R.id.checkbox);

				// Returns a packed position from the 'raw'/'flat' position we
				// got above
				packedPosition = getExpandableListPosition(count
															+ firstVis);

				// ExpandableListView has static helpers to extract the type
				// (group or child position) and the group and/or child
				// positions from a packed position
				packedPositionType = getPackedPositionType(packedPosition);

				if (packedPositionType != ExpandableListView.PACKED_POSITION_TYPE_NULL){

					groupPosition = getPackedPositionGroup(packedPosition);

					if (packedPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
						childPosition = getPackedPositionChild(packedPosition);
						checkableView.setChecked(isChildChecked(groupPosition,
																childPosition));
					}else
						// Must have a group position
						checkableView.setChecked(isGroupChecked(groupPosition));

				}else
					Log.d(	TAG,
							"Packed position type was null.");
			}else
				Log.d(	TAG,
						"getChildAt didn't retrieve a non-null view");


			count--;
		}

		setRefreshingViewStateFlagOff();
	}


	public void colorVisibleItems(){

		View listItem;

		int packedPositionType, groupPosition, childPosition;

		// These are both implemented in ListView sub classes which means
		// they're 'raw'/'flat' list positions
		int firstVis = getFirstVisiblePosition();
		int lastVis = getLastVisiblePosition();
		int count = lastVis
					- firstVis;
		Log.i(	TAG,
				"firstVisPos: "
					+ firstVis
					+ "     lastVisPos: "
					+ lastVis
					+ "\nChildCount: "
					+ getChildCount()
					+ "\n mCount: "
					+ count);
		long packedPosition;

		while (count >= 0){ // looping through visible list items which
							// are the only items that will need to be
							// refreshed. The adapter's getView will
							// take care of all non-visible items when
							// the list is scrolled

			listItem = getChildAt(count); // getChildAt is implemented in
											// ViewGroup sub classes, so using a
											// 'raw'/'flat' position is fine

			if (listItem != null){

				// Returns a packed position from the 'raw'/'flat' position we
				// got above
				packedPosition = getExpandableListPosition(count
															+ firstVis);

				// ExpandableListView has static helpers to extract the type
				// (group or child position) and the group and/or child
				// positions from a packed position
				packedPositionType = getPackedPositionType(packedPosition);

				if (packedPositionType != ExpandableListView.PACKED_POSITION_TYPE_NULL){

					groupPosition = getPackedPositionGroup(packedPosition);
					if (packedPositionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
						childPosition = getPackedPositionChild(packedPosition);
						listItem.setBackgroundColor(Color.YELLOW); // pale
																	// yellow
						Log.i(	TAG,
								"CHILD\ngroupPos: "
									+ groupPosition
									+ "    childPos: "
									+ childPosition);
					}else{
						// Must have a group position
						listItem.setBackgroundColor(Color.BLUE); // pale blue
						Log.i(	TAG,
								"GROUP\ngroupPos: "
									+ groupPosition);
					}

				}else
					Log.d(	TAG,
							"Packed position type was null.");
			}else
				Log.d(	TAG,
						"getChildAt didn't retrieve a non-null view");


			count--;
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev){


		return super.onTouchEvent(ev);
	}


	@Override
	public boolean performItemClick(View v, int position, long id){

		long eLPosition = getExpandableListPosition(position);
		int type = ExpandableListView.getPackedPositionType(eLPosition);


		String typeName = "NULL";
		int gPosition = ExpandableListView.getPackedPositionGroup(eLPosition);
		int cPosition = ExpandableListView.getPackedPositionChild(eLPosition);

		switch(type){
			case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
				typeName = "Group";
				break;
			case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
				typeName = "Child";
				break;

		}

		Toast.makeText(	getContext(),
						"ViewId: "
							+ v.getId()
							+ "\nFlatPos: "
							+ position
							+ "\nType: "
							+ typeName
							+ "\ngroupPos: "
							+ gPosition
							+ "\nchildPos: "
							+ cPosition,
						Toast.LENGTH_SHORT)
				.show();

		// super does expand/collapse and fires all group/child related listener
		// callbacks
		return super.performItemClick(	v,
										position,
										id);
	}


	private void setRefreshingViewStateFlagOn(){

		mAdapterWrapper.setRefreshingCheckableViewState(true);
	}


	private void setRefreshingViewStateFlagOff(){

		mAdapterWrapper.setRefreshingCheckableViewState(false);
	}


	/*********************************************************************
	 * Public utils
	 **********************************************************************/

	/**
	 * This will return the group position for a groupId.
	 * 
	 * <b>Caution:</b> This has to loop through all group items in the list
	 * which could raise performance issues
	 * 
	 * @param groupId
	 *            - id for the group you want a position for
	 * @return group position in the list or a negative number if one was not
	 *         found
	 */
	public int getGroupPosition(long groupId){

		// loop through group positions to match groupId
		for (int i = 0; i < getExpandableListAdapter()
														.getGroupCount(); i++)
			if (getExpandableListAdapter()
											.getGroupId(i) == groupId)
				return i;

		return -1;
	}


	/**
	 * This will find the child position within the a group at groupPosition
	 * who's id matches childId.
	 * 
	 * <b>Caution:</b> This has to loop through all of this groups child items
	 * which could raise performance issues
	 * 
	 * @param groupPosition
	 *            - the group position the child falls under
	 * @param childId
	 * @return the child's position within the group at groupPosition or a
	 *         negative number if one was not found
	 */
	public int getChildPosition(int groupPosition, long childId){

		// loop through group's child positions to match child id
		for (int i = 0; i < getExpandableListAdapter()
														.getChildrenCount(groupPosition); i++)
			if (getExpandableListAdapter()
											.getChildId(groupPosition,
														i) == childId)
				return i;

		return -1;
	}


	/**
	 * This is a convenience for to avoid having to call getGroupPosition(long
	 * groupId) and getChildPosition(int groupPosition, long childId).
	 * 
	 * <b>Caution:</b> These methods have to loop through all of this groups
	 * child items which could raise performance issues
	 * 
	 * @param groupId
	 *            - id of the group the child falls under
	 * @param childId
	 * @return the child's position within the group with the specified groupId
	 *         or a negative number if one was not found
	 */
	public int getChildPosition(long groupId, long childId){

		int groupPosition = getGroupPosition(groupId);

		if (!(groupPosition < 0))
			return getChildPosition(groupPosition,
									childId);

		return -1;
	}


	/*********************************************************************
	 * Group and Child check state getters/setters
	 **********************************************************************/
	@Override
	public MultiChoiceExpandableList setGroupCheckedState(int groupPosition,
															boolean checkState){

		// if (groupChoiceMode == CHECK_MODE_NONE){
		// Log.w( TAG,
		// "Can't set group checked without enabling a group check mode.");
		// TODO

		refreshVisibleCheckableViews();
		return this;
	}


	@Override
	public MultiChoiceExpandableList setGroupCheckedState(long groupId,
															boolean checkState){

		// TODO Auto-generated method stub

		refreshVisibleCheckableViews();
		return this;
	}


	public MultiChoiceExpandableList setGroupCheckedState(long groupId,
															int groupPosition,
															boolean checkState){

		// TODO Auto-generated method stub

		refreshVisibleCheckableViews();
		return this;
	}


	@Override
	public MultiChoiceExpandableList setChildCheckedState(long childId,
															boolean checkState){

		// if (childChoiceMode == CHECK_MODE_NONE)
		// Log.w(TAG,
		// "Can't set group checked without enabling a child check mode.");
		//

		refreshVisibleCheckableViews();
		return this;
	}


	public MultiChoiceExpandableList setChildCheckedState(int groupPosition,
															long groupId,
															int childPosition,
															long childId,
															boolean checkState){

		// if (childChoiceMode == CHECK_MODE_NONE)
		// Log.w(TAG,
		// "Can't set group checked without enabling a child check mode.");
		//

		refreshVisibleCheckableViews();
		return this;
	}


	@Override
	public MultiChoiceExpandableList setChildCheckedState(int groupPosition,
															int childPosition,
															boolean checkState){

		// TODO Auto-generated method stub

		refreshVisibleCheckableViews();
		return this;
	}


	@Override
	public boolean isChildChecked(int groupPosition, int childPosition){

		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isGroupChecked(int groupPosition){

		// TODO Auto-generated method stub

		return false;
	}


	@Override
	public boolean isGroupChecked(long groupId){

		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isChildChecked(long childId){

		// TODO Auto-generated method stub
		return false;
	}


	/*********************************************************************
	 * Choice Mode getters and Setters
	 **********************************************************************/
	public MultiChoiceExpandableListView setGroupCheckMode(int groupCheckMode){

		this.groupChoiceMode = groupCheckMode;

		// if (groupChoiceMode != CHECK_MODE_NONE && mCheckedGroups == null)
		// mCheckedGroups = new BitSet(mAdapter.getGroupCount());

		return this;
	}


	public MultiChoiceExpandableListView setChildCheckMode(int childCheckMode){

		this.childChoiceMode = childCheckMode;

		// if (childChoiceMode != CHECK_MODE_NONE && mCheckedChildren == null)
		// mCheckedChildren = new HashMap<Integer, BitSet>();

		return this;
	}


	@Override
	public int getGroupChoiceMode(){

		return groupChoiceMode;
	}


	@Override
	public int getChildChoiceMode(){

		return childChoiceMode;
	}


	@Override
	public MultiChoiceExpandableList setGroupChoiceMode(int choiceMode){

		groupChoiceMode = choiceMode;
		return this;
	}


	@Override
	public MultiChoiceExpandableList setChildChoiceMode(int choiceMode){

		childChoiceMode = choiceMode;
		return this;
	}


	public MultiChoiceExpandableListView
		setCheckChildrenOnGroupCheck(
										boolean checkChildrenOnGroupCheck){

		// TODO Check that we're in a mode that allows this

		return this;
	}


	/*********************************************************************
	 * Checked Item Counts
	 **********************************************************************/

	/**
	 * Gives a count of ALL checked items in the list (groups and children
	 * both).
	 * 
	 * @return total checked items
	 */
	@Override
	public int getCheckedItemCount(){

		return getCheckedGroupCount()
				+ getCheckedChildCount();
	}


	/**
	 * Gives a count of checked groups in the list
	 * 
	 * @return the number of checked groups
	 */
	@Override
	public int getCheckedGroupCount(){

		// if (mCheckedGroups != null){
		// validateCheckedGroups();
		// groupCheckTotal = mCheckedGroups.cardinality();
		// }else
		// groupCheckTotal = 0;

		return groupCheckTotal;
	}


	/**
	 * Gives a count of ALL checked children in the list regardless of group
	 * parent.
	 * 
	 * @return total checked children
	 */
	@Override
	public int getCheckedChildCount(){

		childCheckTotal = 0;

		// validateCheckedChildren();

		// if (mCheckedChildren != null)
		// for (BitSet mChildren : mCheckedChildren.values())
		// childCheckTotal += mChildren.cardinality();

		return childCheckTotal;
	}


	/**
	 * Gives a count of all the checked children for the parent group at a
	 * specified group at groupPosition.
	 * 
	 * @param groupPosition
	 *            - position in the list of the group you want to gather the
	 *            checked child count from
	 * @return total checked child count for the specified group at
	 *         groupPosition
	 */
	@Override
	public int getCheckedChildCount(int groupPosition){

		// if (mCheckedChildren != null){
		//
		// // validateCheckedChildren(groupPosition);
		//
		// BitSet mChildren = mCheckedChildren.get(groupPosition);
		// if (mChildren != null)
		// return mChildren.cardinality();
		// else
		// return -1;
		//
		// }else
		return -1;

	}


	@Override
	public int getCheckedChildCount(long groupId){

		// TODO Auto-generated method stub
		return 0;
	}


	/*********************************************************************
	 * Clearing
	 **********************************************************************/

	/**
	 * Clears all checked items in the list and resets the all checked counts.
	 */
	@Override
	public MultiChoiceExpandableList clearAllChoices(){


		// if (mCheckedChildren != null)
		// mCheckedChildren.clear();
		//
		// if (mCheckedGroups != null)
		// mCheckedGroups.clear();

		groupCheckTotal = 0;
		childCheckTotal = 0;

		refreshVisibleCheckableViews();
		return this;
	}


	@Override
	public MultiChoiceExpandableList clearCheckedGroups(){

		refreshVisibleCheckableViews();
		return null;
	}


	@Override
	public MultiChoiceExpandableList clearCheckedChildren(){

		refreshVisibleCheckableViews();
		return null;
	}


	/**
	 * Clears all checked children for the specified parent group at
	 * groupPosition.
	 * 
	 * @param groupPosition
	 *            - the group position for the children you want to clear
	 */
	@Override
	public MultiChoiceExpandableList
		clearCheckedGroupChildren(int groupPosition){

		// TODO Auto-generated method stub

		refreshVisibleCheckableViews();
		return null;
	}


	/**
	 * Clears all checked children for the specified parent group at
	 * groupPosition.
	 * 
	 * @param groupId
	 *            - the group id for the children you want to clear
	 */
	@Override
	public MultiChoiceExpandableList clearCheckedGroupChildren(long groupId){

		// TODO Auto-generated method stub

		refreshVisibleCheckableViews();
		return null;
	}


	/*********************************************************************
	 * Checked Item id/position getters
	 **********************************************************************/

	@Override
	public long[] getCheckedGroupIds(){

		// Log.e(TAG,
		// "Can't get checked group item positions because group check mode is CHECK_MODE_NONE or mCheckedGroups is null");

		return null;
	}


	@Override
	public long[] getCheckedChildIds(int groupPosition){

		// Log.e(TAG,
		// "Can't get checked child item positions because child check mode is CHECK_MODE_NONE or mCheckedChildren is null");

		return null;
	}


	public int getCheckedGroupPosition(){

		// Log.e( TAG,
		// "Can't get checked group item position because group check mode is not GROUP_CHECK_MODE_ONE");

		return -1;
	}


	/**
	 * This checks for the single checked child. This method is ONLY for use if
	 * childChoiceMode is CHILD_CHECK_MODE_ONE
	 * 
	 * @return int[] containing {groupPosition, childPosition}
	 */
	public int[] getCheckedChildPosition(){

		// Log.e( TAG,
		// "Can't get checked child item position because group child mode is not CHILD_CHECK_MODE_ONE");
		return null;
	}


	/**
	 * @param groupPos
	 * @return checked child position in group at groupPosition
	 */
	public int getCheckedChildPosition(int groupPosition){

		// Log.e( TAG,
		// "Can't get checked child item position because group child mode is not CHILD_CHECK_MODE_ONE_PER_GROUP");
		return -1;
	}


	@Override
	public long[] getCheckedChildIds(){

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long[] getCheckedChildIds(long groupId){

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int[] getCheckedGroupPositions(){

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int[] getCheckedChildPositions(int groupPosition){

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int[] getCheckedChildPositions(long groupId){

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<int[]> getCheckedChildPositions(){

		// TODO Auto-generated method stub
		return null;
	}


	/***********************************************************
	 * Get/Set/Remove ExpandableListCheckListener
	 ************************************************************/
	@Override
	public MultiChoiceExpandableList
		setExpandableCheckListener(
									ExpandableListCheckListener listener){

		mClientCheckListener = listener;
		return this;
	}


	@Override
	public MultiChoiceExpandableList
		removeExpandableCheckListener(){

		mClientCheckListener = null;

		return this;
	}


	@Override
	public ExpandableListCheckListener getExpandableListCheckListener(){

		return mClientCheckListener;
	}


	/*********************************************************************
	 * Adapter delegates - internal use
	 **********************************************************************/
	boolean hasStableIds(){

		return mAdapterWrapper.hasStableIds();
	}


	public long getChildId(int groupPosition, int childPosition){

		return mAdapterWrapper.getChildId(	groupPosition,
											childPosition);
	}


	public long getGroupId(int groupPosition){

		return mAdapterWrapper.getGroupId(groupPosition);
	}


	public int getGroupCount(){

		return mAdapterWrapper.getGroupCount();
	}


	public int getChildrenCount(int groupPosition){

		return mAdapterWrapper.getChildrenCount(groupPosition);
	}


	/*********************************************************************
	 * Overrides from underlying ListView and AbsListView
	 **********************************************************************/

	/*
	 * For {@link MultiChoiceExpandableListView}, use clearAllChoices() instead
	 */
	@Override
	public void clearChoices(){

		Log.w(	TAG,
				"For MultiChoiceExpandableListView, use clearAllChoices() instead of clearChoices()");
	}


	/*
	 * For {@link MultiChoiceExpandableListView}, use getCheckedChildIds or
	 * getCheckedGroupIds instead.
	 */
	@Override
	public long[] getCheckedItemIds(){

		Log.e(	TAG,
				"For MultiChoiceExpandableListView, use getCheckedChildIds or getCheckedGroupIds instead.");
		return null;
	}


	/*
	 * For {@link MultiChoiceExpandableListView}, use getCheckedChildPositions
	 * or getCheckedGroupPositions instead
	 */
	@Override
	public int getCheckedItemPosition(){

		Log.e(	TAG,
				"For MultiChoiceExpandableListView, use getCheckedChildPositions or getCheckedGroupPositions instead");
		return -1;
	}


	/*
	 * For {@link MultiChoiceExpandableListView}, use getCheckedChildPositions
	 * or getCheckedGroupPositions instead
	 */
	@Override
	public SparseBooleanArray getCheckedItemPositions(){

		Log.e(	TAG,
				"For MultiChoiceExpandableListView, use getCheckedChildPositions or getCheckedGroupPositions instead");
		return null;
	}


	/*
	 * For {@link MultiChoiceExpandableListView}, use getGroupChoiceMode or
	 * getChildChoiceMode instead
	 */
	@Override
	public int getChoiceMode(){

		Log.e(	TAG,
				"For MultiChoiceExpandableListView, use getGroupChoiceMode or getChildChoiceMode instead");
		return -1;
	}


	// Searches the expandable list adapter for a group position matching the
	// given group ID. The search starts at the given seed position and then
	// alternates between moving up and moving down until 1) we find the right
	// position, or 2) we run out of time, or 3) we have looked at every
	// position
	// Returns:
	// Position of the row that matches the given row ID, or
	// AdapterView.INVALID_POSITION if it can't be found
	// See also:
	// AdapterView.findSyncPosition()

	// int findGroupPosition(long groupIdToMatch, int seedGroupPosition){
	//
	// int count = mExpandableListAdapter.getGroupCount();
	//
	// if (count == 0)
	// return AdapterView.INVALID_POSITION;
	//
	// // If there isn't a selection don't hunt for it
	// if (groupIdToMatch == AdapterView.INVALID_ROW_ID)
	// return AdapterView.INVALID_POSITION;
	//
	// // Pin seed to reasonable values
	// seedGroupPosition = Math.max( 0,
	// seedGroupPosition);
	// seedGroupPosition = Math.min( count - 1,
	// seedGroupPosition);
	//
	// long endTime = SystemClock.uptimeMillis()
	// + AdapterView.SYNC_MAX_DURATION_MILLIS;
	//
	// long rowId;
	//
	// // first position scanned so far
	// int first = seedGroupPosition;
	//
	// // last position scanned so far
	// int last = seedGroupPosition;
	//
	// // True if we should move down on the next iteration
	// boolean next = false;
	//
	// // True when we have looked at the first item in the data
	// boolean hitFirst;
	//
	// // True when we have looked at the last item in the data
	// boolean hitLast;
	//
	// // Get the item ID locally (instead of getItemIdAtPosition), so
	// // we need the adapter
	// ExpandableListAdapter adapter = getAdapter();
	// if (adapter == null)
	// return AdapterView.INVALID_POSITION;
	//
	// while (SystemClock.uptimeMillis() <= endTime){
	// rowId = adapter.getGroupId(seedGroupPosition);
	// if (rowId == groupIdToMatch)
	// // Found it!
	// return seedGroupPosition;
	//
	// hitLast = last == count - 1;
	// hitFirst = first == 0;
	//
	// if (hitLast
	// && hitFirst)
	// // Looked at everything
	// break;
	//
	// if (hitFirst
	// || (next && !hitLast)){
	// // Either we hit the top, or we are trying to move down
	// last++;
	// seedGroupPosition = last;
	// // Try going up next time
	// next = false;
	// }else if (hitLast
	// || (!next && !hitFirst)){
	// // Either we hit the bottom, or we are trying to move up
	// first--;
	// seedGroupPosition = first;
	// // Try going down next time
	// next = true;
	// }
	//
	// }
	//
	// return AdapterView.INVALID_POSITION;
	// }

}
