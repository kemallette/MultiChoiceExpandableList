package com.kemallette.MultiChoiceExpandableList;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Checkable;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class MultiChoiceExpandableListView	extends
											ExpandableListView	implements
																MultiCheckable{

	private static final String				TAG						= "MultiChoiceExpandableListView";


	/**
	 * Flag indicating if an item's checked state change is from a user actually
	 * touching the screen
	 */
	private boolean							isCheckChangeFromTouch	= false;
	private boolean							isOneItemChoice			= false;
	private boolean							isChoiceOn				= true;
	/**
	 * If true, on a group check change, that group's children will match the
	 * group's check state. In other words, if you check a group, all its
	 * children will also be checked and the reverse. If a group is unchecked,
	 * all its children will be unchecked.
	 */
	private boolean							checkChildrenWithGroup	= false;


	private int								groupChoiceMode			= CHECK_MODE_MULTI;
	private int								childChoiceMode			= CHECK_MODE_MULTI;

	private CheckStateStore					mCheckStore;
	private MultiCheckListener				mClientCheckListener;

	private MultiChoiceExpandableAdapter	mAdapterWrapper;


	public MultiChoiceExpandableListView(	final Context context,
											final AttributeSet attrs,
											final int defStyle){

		super(	context,
				attrs,
				defStyle);

		if (attrs != null){
			TypedArray a = getContext()
										.obtainStyledAttributes(attrs,
																R.styleable.MultiChoiceExpandableListView,
																0,
																0);

			groupChoiceMode = a.getInt(	R.styleable.MultiChoiceExpandableListView_groupChoiceMode,
										CHECK_MODE_MULTI);

			childChoiceMode = a.getInt(	R.styleable.MultiChoiceExpandableListView_childChoiceMode,
										CHECK_MODE_MULTI);

			checkChildrenWithGroup = a.getBoolean(	R.styleable.MultiChoiceExpandableListView_checkChildrenWithGroup,
													false);

			isOneItemChoice = a.getBoolean(	R.styleable.MultiChoiceExpandableListView_oneItemChoice,
											false);

			// default on instantiation is true so if both modes are NONE we
			// need to set to false
			if (groupChoiceMode == CHECK_MODE_NONE
				&& childChoiceMode == CHECK_MODE_NONE)
				isChoiceOn = false;


			if (isOneItemChoice){
				groupChoiceMode = CHECK_MODE_ONE_ALL;
				childChoiceMode = CHECK_MODE_ONE_ALL;
				checkChildrenWithGroup = false;
				isChoiceOn = true;
			}

			a.recycle();
		}

	}


	public MultiChoiceExpandableListView(	final Context context,
											final AttributeSet attrs){

		this(	context,
				attrs,
				-1);
	}


	public MultiChoiceExpandableListView(final Context context){

		this(	context,
				null);
	}


	@Override
	public void onRestoreInstanceState(final Parcelable state){

		super.onRestoreInstanceState(state);

		// TODO: restore necessary saved fields - remember that super does a few
		// things too
	}


	@Override
	public Parcelable onSaveInstanceState(){

		final Parcelable mParcel = super.onSaveInstanceState();

		// TODO: save all necessary fields - remember that super does a few
		// things too
		return mParcel;
	}


	@Override
	public void setAdapter(final ExpandableListAdapter adapter){

		if (adapter == null)
			throw new NullPointerException("The adapter you passed was null");


		if (adapter instanceof MultiChoiceExpandableAdapter)
			this.mAdapterWrapper = (MultiChoiceExpandableAdapter) adapter;

		else if (mAdapterWrapper == null)
			mAdapterWrapper = new MultiChoiceExpandableAdapter(	adapter,
																this);
		else
			mAdapterWrapper.setWrappedAdapter(adapter);

		super.setAdapter(mAdapterWrapper);

		mCheckStore = new CheckStateStore(this); // Must do this to ensure
													// hasStableIds stays
													// current
	}


	@Override
	public ExpandableListAdapter getExpandableListAdapter(){

		return mAdapterWrapper.getWrappedAdapter();
	}


	/*********************************************************************
	 * Choice Mode Related
	 **********************************************************************/

	@Override
	public MultiCheckable
		enableChoice(int groupChoiceMode, int childChoiceMode){

		setGroupChoiceMode(groupChoiceMode);
		setChildChoiceMode(childChoiceMode);

		isOneItemChoice = false;
		isChoiceOn = true;

		mAdapterWrapper.enableChoice();

		return this;
	}


	@Override
	public MultiCheckable disableChoice(){

		isChoiceOn = false;
		isOneItemChoice = false;

		groupChoiceMode = CHOICE_MODE_NONE;
		childChoiceMode = CHOICE_MODE_NONE;
		return this;
	}


	@Override
	public boolean isChoiceOn(){

		return isChoiceOn;
	}


	@Override
	public MultiCheckable setGroupChoiceMode(final int choiceMode){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}

		if (choiceMode != CHECK_MODE_MULTI)
			clearGroups();

		groupChoiceMode = choiceMode;
		return this;
	}


	@Override
	public MultiCheckable setChildChoiceMode(final int choiceMode){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}

		if (choiceMode != CHECK_MODE_MULTI)
			clearChildren();

		childChoiceMode = choiceMode;
		return this;
	}


	@Override
	public MultiCheckable
		checkChildrenWithGroup(final boolean checkChildrenWithGroup){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}

		this.checkChildrenWithGroup = checkChildrenWithGroup;

		return this;
	}


	@Override
	public boolean checkChildrenWithGroup(){

		return checkChildrenWithGroup;
	}


	/*
	 * Enables one item choice mode. Only one item at a time throughout the list
	 * (including groups and children) can be checked. isChoiceOn() will return
	 * true.
	 * 
	 * If checkChildrenWithGroup is enabled, it will be disabled until
	 * checkChildrenWithGroup is set to true again.
	 */
	@Override
	public MultiCheckable enableOnlyOneItemChoice(){

		isOneItemChoice = true;
		isChoiceOn = true;

		checkChildrenWithGroup = false;

		groupChoiceMode = CHECK_MODE_ONE_ALL;
		childChoiceMode = CHECK_MODE_ONE_ALL;

		mAdapterWrapper.enableChoice();
		return this;
	}


	/*
	 * This disables one item choice mode. isChoiceOn() will return false until
	 * either enableChoice(int groupChoiceMode, int childChoiceMode) or
	 * enableOnlyOneItemChoice() are called.
	 */
	@Override
	public MultiCheckable disableOnlyOneItemChoice(){

		groupChoiceMode = CHOICE_MODE_NONE;
		childChoiceMode = CHOICE_MODE_NONE;

		isOneItemChoice = false;
		isChoiceOn = false;

		return this;
	}


	@Override
	public boolean isOneItemChoiceOn(){

		return isOneItemChoice;
	}


	@Override
	public int getGroupChoiceMode(){

		return groupChoiceMode;
	}


	@Override
	public int getChildChoiceMode(){

		return childChoiceMode;
	}


	/***********************************************************
	 * MultiCheckListener Callbacks
	 ************************************************************/
	@Override
	public void onGroupCheckChange(final Checkable checkedView,
									final int groupPosition,
									final long groupId,
									final boolean isChecked){

		if (isChoiceOn){
			if (groupChoiceMode != CHOICE_MODE_NONE)
				setGroupCheckedState(	groupPosition,
										isChecked);
			else
				Log.i(	TAG,
						"onGroupCheckChange called, but groupChoice mode is NONE");

			if (mClientCheckListener != null)
				mClientCheckListener.onGroupCheckChange(checkedView,
														groupPosition,
														groupId,
														isChecked);
		}
	}


	@Override
	public void onChildCheckChange(final Checkable checkedView,
									final int groupPosition,
									final long groupId,
									final int childPosition,
									final long childId,
									final boolean isChecked){

		if (isChoiceOn){
			if (childChoiceMode != CHOICE_MODE_NONE)
				setChildCheckedState(	groupPosition,
										childPosition,
										isChecked);
			else
				Log.i(	TAG,
						"onChildCheckChange called, but childChoice mode is NONE");

			if (mClientCheckListener != null)
				mClientCheckListener.onChildCheckChange(checkedView,
														groupPosition,
														groupId,
														childPosition,
														childId,
														isChecked);
		}
	}


	/*********************************************************************
	 * Internal utils
	 **********************************************************************/
	protected void
		setIsCheckChangeFromTouch(final boolean isCheckChangeFromTouch){

		this.isCheckChangeFromTouch = isCheckChangeFromTouch;
	}


	protected void refreshVisibleCheckableViews(){

		if (isCheckChangeFromTouch) // This is true while a proper touch
									// initiated check change callback fires
									// which means the view is already
									// indicating the check state we have saved
			return;

		isRefreshingView(true); // Letting the adapter know we're
								// refreshing a checkable view's
								// state in order to avoid
								// duplicating check change
								// callbacks


		View listItem;
		Checkable checkableView;

		int packedPositionType, groupPosition, childPosition;

		// These are both implemented in ListView sub classes which means
		// they're 'raw'/'flat' list positions
		final int firstVis = getFirstVisiblePosition();
		final int lastVis = getLastVisiblePosition();
		int count = firstVis;

		long packedPosition;

		while (count <= lastVis){ // looping through visible list items which
									// are the only items that will need to be
									// refreshed. The adapter's getView will
									// take care of all non-visible items when
									// the list is scrolled

			listItem = getChildAt(count); // getChildAt is implemented in
											// ListView sub classes, so using a
											// 'raw'/'flat' position is fine

			if (listItem != null){

				checkableView = (Checkable) listItem.findViewById(android.R.id.checkbox);

				// Returns a packed position from the 'raw'/'flat' position we
				// got above
				packedPosition = getExpandableListPosition(count);

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


			count++;
		}

		isRefreshingView(false);
	}


	private void isRefreshingView(final boolean isRefreshing){

		mAdapterWrapper.isUserCheck = !isRefreshing;
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
	public int getGroupPosition(final long groupId){

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
	public int getChildPosition(final int groupPosition, final long childId){

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
	public int getChildPosition(final long groupId, final long childId){

		final int groupPosition = getGroupPosition(groupId);

		if (!(groupPosition < 0))
			return getChildPosition(groupPosition,
									childId);

		return -1;
	}


	/*********************************************************************
	 * Group and Child check state getters/setters
	 **********************************************************************/
	@Override
	public MultiCheckable
		setGroupCheckedState(final int groupPosition,
								final boolean isChecked){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");
			return this;
		}

		if (isOneItemChoice){

			clearAll();

			if (checkChildrenWithGroup)
				Log.e(	TAG,
						"One Item Choice Mode is on, but checkChildrenWithGroup is true. ");
		}

		switch(groupChoiceMode){

			case CHECK_MODE_NONE:
				return this;

			case CHECK_MODE_ONE:
				clearGroups();
				break;
		}


		mCheckStore.setGroupState(	groupPosition,
									isChecked,
									checkChildrenWithGroup);

		refreshVisibleCheckableViews();

		return this;
	}


	@Override
	public MultiCheckable
		setChildCheckedState(final int groupPosition,
								final int childPosition,
								final boolean isChecked){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");
			return this;
		}


		if (isOneItemChoice){

			clearAll();

			if (checkChildrenWithGroup)
				Log.e(	TAG,
						"One Item Choice Mode is on, but checkChildrenWithGroup is true. ");
		}

		switch(childChoiceMode){

			case CHECK_MODE_NONE:
				return this;

			case CHECK_MODE_ONE:
				clearChildren();
				break;

			case CHILD_CHECK_MODE_ONE_PER_GROUP:
				clearChildren(groupPosition);
				break;
		}

		mCheckStore.setChildState(	groupPosition,
									childPosition,
									isChecked);

		refreshVisibleCheckableViews();
		return this;
	}


	/*********************************************************************
	 * Checked State
	 **********************************************************************/
	@Override
	public boolean isGroupChecked(final int groupPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return false;
		}

		return mCheckStore.isGroupChecked(groupPosition);
	}


	@Override
	public boolean isChildChecked(final int groupPosition,
									final int childPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return false;
		}

		return mCheckStore.isChildChecked(	groupPosition,
											childPosition);
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

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return 0;
		}

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

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return 0;
		}

		return mCheckStore.getCheckedGroupCount();
	}


	/**
	 * Gives a count of ALL checked children in the list regardless of group
	 * parent.
	 * 
	 * @return total checked children
	 */
	@Override
	public int getCheckedChildCount(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return 0;
		}

		return mCheckStore.getCheckedChildCount();
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
	public int getCheckedChildCount(final int groupPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return 0;
		}

		return mCheckStore.getCheckedChildCount(groupPosition);
	}


	/*********************************************************************
	 * Checked Item id/position getters
	 **********************************************************************/

	@Override
	public long[] getCheckedGroupIds(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new long[0];
		}
		return mCheckStore.getCheckedGroupIds();
	}


	@Override
	public List<Long> getCheckedChildIds(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new ArrayList<Long>();
		}
		return mCheckStore.getCheckedChildIds();
	}


	@Override
	public List<Long> getCheckedChildIds(final int groupPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new ArrayList<Long>();
		}
		return mCheckStore.getCheckedChildIds(groupPosition);
	}


	@Override
	public int[] getCheckedGroupPositions(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new int[0];
		}
		return mCheckStore.getCheckedGroupPositions();
	}


	@Override
	public SparseArray<int[]> getCheckedChildPositions(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new SparseArray<int[]>();
		}
		return mCheckStore.getCheckedChildPositions();
	}


	@Override
	public int[] getCheckedChildPositions(final int groupPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return new int[0];
		}
		return mCheckStore.getCheckedChildPositions(groupPosition);
	}


	/*********************************************************************
	 * Clearing
	 **********************************************************************/

	/**
	 * Clears all checked items in the list and resets the all checked counts.
	 */
	@Override
	public MultiCheckable clearAll(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}
		mCheckStore.clearAll();

		refreshVisibleCheckableViews();

		return this;
	}


	@Override
	public MultiCheckable clearGroups(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}
		mCheckStore.clearGroups(checkChildrenWithGroup);

		refreshVisibleCheckableViews();

		return this;
	}


	@Override
	public MultiCheckable clearChildren(){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}
		mCheckStore.clearChildren();

		refreshVisibleCheckableViews();

		return this;
	}


	/**
	 * Clears all checked children for the specified parent group at
	 * groupPosition.
	 * 
	 * @param groupPosition
	 *            - the group position for the children you want to clear
	 */
	@Override
	public MultiCheckable
		clearChildren(final int groupPosition){

		if (!isChoiceOn){

			Log.e(	TAG,
					"Choice is not enabled. Try using enableChoice(groupChoiceMode, childChoiceMode) or enableOnlyOneItemChoice()");

			return this;
		}
		mCheckStore.clearCheckedGroupChildren(groupPosition);

		refreshVisibleCheckableViews();

		return this;
	}


	/***********************************************************
	 * Get/Set/Remove MultiCheckListener
	 ************************************************************/
	@Override
	public MultiCheckable
		setExpandableCheckListener(
									final MultiCheckListener listener){

		mClientCheckListener = listener;
		return this;
	}


	@Override
	public MultiCheckable
		removeExpandableCheckListener(){

		mClientCheckListener = null;

		return this;
	}


	@Override
	public MultiCheckListener getExpandableListCheckListener(){

		return mClientCheckListener;
	}


	/*********************************************************************
	 * Adapter delegates - internal use
	 **********************************************************************/
	protected boolean hasStableIds(){

		return mAdapterWrapper.hasStableIds();
	}


	protected long getChildId(final int groupPosition, final int childPosition){

		return mAdapterWrapper.getChildId(	groupPosition,
											childPosition);
	}


	protected long getGroupId(final int groupPosition){

		return mAdapterWrapper.getGroupId(groupPosition);
	}


	protected int getGroupCount(){

		return mAdapterWrapper.getGroupCount();
	}


	protected int getChildrenCount(final int groupPosition){

		return mAdapterWrapper.getChildrenCount(groupPosition);
	}


	protected long[] getGroupChildrenIds(final int groupPosition){

		long[] ids;
		final int childCount = getChildrenCount(groupPosition);

		if (hasStableIds()){
			ids = new long[childCount];
			for (int i = 0; i < childCount; i++)
				ids[i] = getChildId(groupPosition,
									i);
		}else
			ids = new long[0];

		return ids;
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


}
