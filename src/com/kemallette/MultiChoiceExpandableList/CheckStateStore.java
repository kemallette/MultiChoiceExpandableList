package com.kemallette.MultiChoiceExpandableList;


import java.util.BitSet;

import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.util.SparseArray;


/**
 * This class is used to store {@link MultiChoiceExpandableListView} checked
 * states.
 * 
 * @author kemallette
 */
class CheckStateStore{

	private static final String							TAG	= "CheckStateStore";

	/**
	 * Keys are groupIds. Values are last known groupPosition. If a key
	 * (groupId) is present, that group is checked.
	 */
	private LongSparseArray<Integer>					mCheckedGroups;
	/**
	 * This maps a groupId to a {@link LongSparseArray}s which store the groups
	 * child states. The child states are also stored in a
	 * {@link LongSparseArray} where checked children ids are mapped to the
	 * children's last known position within the group.
	 * 
	 * Caution: the presence of a groupId does not mean that group is checked
	 * itself, just that there are checked children in that group.
	 */
	private LongSparseArray<LongSparseArray<Integer>>	mCheckedChildren;


	/**
	 * For lists backed by adapters with unstable ids only.
	 * 
	 * Index is groupPosition and bit values are true if checked false if not.
	 */
	private BitSet										mUnstableCheckedGroups;
	/**
	 * For lists backed by adapters with unstable ids only.
	 * 
	 * This maps a groupPosition to a {@link BitSet}. The {@link BitSet} indexes
	 * are childPositions and the bit values are true if checked, false if not.
	 */
	private SparseArray<BitSet>							mUnstableCheckedChildren;

	private MultiChoiceExpandableListView				mList;


	CheckStateStore(MultiChoiceExpandableListView mList){

		this.mList = mList;

		if (mList.hasStableIds()){
			mCheckedGroups = new LongSparseArray<Integer>();
			mCheckedChildren = new LongSparseArray<LongSparseArray<Integer>>();
		}else{
			mUnstableCheckedGroups = new BitSet(mList.getGroupCount());
			mUnstableCheckedChildren = new SparseArray<BitSet>();
		}
	}


	void setGroupState(int groupPosition,
						boolean isChecked,
						boolean checkChildrenOnGroupCheck){

		if (mList.hasStableIds())
			setGroupState(	mList.getGroupId(groupPosition),
							groupPosition,
							isChecked,
							checkChildrenOnGroupCheck);
		else{
			setUnstableGroupState(	groupPosition,
									isChecked,
									checkChildrenOnGroupCheck);
			if (checkChildrenOnGroupCheck){
				setGroupsChildrenState(	groupPosition,
										isChecked);
			}
		}
	}


	void setGroupState(long groupId,
						int groupPosition,
						boolean isChecked,
						boolean checkChildrenOnGroupCheck){

		if (mList.hasStableIds()){
			if (isChecked)
				mCheckedGroups.put(	groupId,
									groupPosition);
			else
				mCheckedGroups.remove(groupId);

			if (checkChildrenOnGroupCheck){
				setGroupsChildrenState(	groupId,
										groupPosition,
										isChecked);
			}
		}else
			setGroupState(	mList.getGroupPosition(groupId),
							isChecked,
							checkChildrenOnGroupCheck);
	}


	private void setUnstableGroupState(int groupPosition,
										boolean isChecked,
										boolean checkChildrenOnGroupCheck){

		mUnstableCheckedGroups.set(	groupPosition,
									isChecked);
		if (checkChildrenOnGroupCheck){
			setGroupsChildrenState(	groupPosition,
									isChecked);
		}
	}


	private void setGroupsChildrenState(long groupId,
										int groupPosition,
										boolean isGroupChecked){

		if (mList.hasStableIds()){

			long[] mChildIds = mList.getGroupChildrenIds(groupPosition);
			int childPos;
			for (long childId : mChildIds){
				childPos = mList.getChildPosition(	groupPosition,
													childId);
				setChildState(	groupPosition,
								groupId,
								childPos,
								childId,
								isGroupChecked);
			}

		}else{
			setGroupsChildrenState(	groupPosition,
									isGroupChecked);
		}
	}


	private void setGroupsChildrenState(int groupPosition,
										boolean isGroupChecked){

		if (mList.hasStableIds()){
			setGroupsChildrenState(	mList.getGroupId(groupPosition),
									groupPosition,
									isGroupChecked);
		}else{
			int childCount = mList.getChildrenCount(groupPosition);
			for(int i = 0; i < childCount; i++) {
				setChildState(groupPosition, i, isGroupChecked);
			}
		}
	}


	void setChildState(int groupPosition, int childPosition, boolean isChecked){

		if (mList.hasStableIds())
			setChildState(	groupPosition,
							mList.getGroupId(groupPosition),
							childPosition,
							mList.getChildId(	groupPosition,
												childPosition),
							isChecked);
		else if (isChecked)
			putUnstableIdCheckedChild(	groupPosition,
										childPosition);
		else
			removeUnstableIdCheckedChild(	groupPosition,
											childPosition);


	}


	void setChildState(int groupPosition,
						long groupId,
						int childPosition,
						long childId,
						boolean isChecked){

		if (mList.hasStableIds())
			if (isChecked)
				putCheckedChild(groupId,
								childPosition,
								childId);
			else
				removeCheckedChild(	groupId,
									childId);
		else if (isChecked)
			putUnstableIdCheckedChild(	groupPosition,
										childPosition);
		else
			removeUnstableIdCheckedChild(	groupPosition,
											childPosition);
	}


	boolean isGroupChecked(long groupId){

		if (mList.hasStableIds())
			return mCheckedGroups.indexOfKey(groupId) >= 0;
		else
			return isGroupChecked(mList.getGroupPosition(groupId));
	}


	boolean isGroupChecked(int groupPosition){

		if (mList.hasStableIds())
			return isGroupChecked(mList.getGroupId(groupPosition));
		else
			return mUnstableCheckedGroups.get(groupPosition);
	}


	/**
	 * Will check to see if the specified child is checked.
	 * 
	 * <b>Caution:</b> If adapter backing the list doesn't have stable ids, we
	 * have to find both the group and child positions. Depending on the size of
	 * the list, this could cause performance issues as finding these positions
	 * requires looping through the entire list.
	 * 
	 * @param groupId
	 * @param childId
	 * @return true if checked, false if not or if positions could not be found
	 *         for these items in the case of unstable ids.
	 */
	boolean isChildChecked(long groupId, long childId){

		if (mList.hasStableIds()){

			if (mCheckedChildren.get(groupId) == null)
				return false;
			else
				return mCheckedChildren.get(groupId)
										.indexOfKey(childId) >= 0;
		}else{

			int groupPosition = mList.getGroupPosition(groupId);
			int childPosition;

			if (groupPosition >= 0){
				childPosition = mList.getChildPosition(	groupPosition,
														childId);

				if (childPosition >= 0)
					return isChildChecked(	groupPosition,
											childPosition);
				else{
					Log.e(	TAG,
							"Couldn't find group position from groupId.");
					return false;
				}
			}else{
				Log.e(	TAG,
						"Couldn't find child position from groupId, childId");
				return false;
			}

		}
	}


	/**
	 * Will check to see if the specified child is checked.
	 * 
	 * @param groupPosition
	 * @param childPosition
	 * @return true if checked, false if not
	 */
	boolean isChildChecked(int groupPosition, int childPosition){

		if (mList.hasStableIds())
			return isChildChecked(	mList.getGroupId(groupPosition),
									mList.getChildId(	groupPosition,
														childPosition));

		if (mUnstableCheckedChildren.get(groupPosition) == null)
			return false;

		return mUnstableCheckedChildren.get(groupPosition)
										.get(childPosition);
	}

	/*********************************************************************
	 * Checked Counts
	 **********************************************************************/
	protected int getCheckedGroupCount() {
		// TODO yo dog, do dis
		int count = 0;
		return count;
	}
	protected int getCheckedChildCount() {
		// TODO yo dog, do dis
				int count = 0;
				return count;
	}
	protected int getCheckedChildCount(int groupPosition) {
		// TODO yo dog, do dis
				int count = 0;
				return count;
	}
	protected int getCheckedChildCount(long groupId){
		// TODO yo dog, do dis
				int count = 0;
				return count;
	}
	/*********************************************************************
	 * Data store helpers
	 **********************************************************************/

	private void putCheckedChild(long groupId, int childPosition, long childId){

		if (mCheckedChildren.get(groupId) != null)
			mCheckedChildren.get(groupId)
							.put(	childId,
									childPosition);
		else{
			LongSparseArray<Integer> mChildStates = new LongSparseArray<Integer>();
			mChildStates.put(	childId,
								childPosition);
			mCheckedChildren.put(	groupId,
									mChildStates);
		}

	}


	private void
		putUnstableIdCheckedChild(int groupPosition, int childPosition){


		if (mUnstableCheckedChildren.get(groupPosition) != null)

			mUnstableCheckedChildren.get(groupPosition)
									.set(	childPosition,
											true);
		else{
			BitSet mBitSet = new BitSet(mList.getChildrenCount(groupPosition));
			mBitSet.set(childPosition,
						true);
			mUnstableCheckedChildren.put(	groupPosition,
											mBitSet);
		}

	}


	private void removeCheckedChild(long groupId, long childId){

		if (mCheckedChildren.get(groupId) == null
			|| mCheckedChildren.get(groupId)
								.get(childId) == null)
			return;

		mCheckedChildren.get(groupId)
						.remove(childId);

	}


	private void removeUnstableIdCheckedChild(int groupPosition,
												int childPosition){

		if (mUnstableCheckedChildren.get(groupPosition) == null
			|| mUnstableCheckedChildren.get(groupPosition)
										.get(childPosition) == false)
			return;

		mUnstableCheckedChildren.get(groupPosition)
								.clear(childPosition);
	}


	/*********************************************************************
	 * Clearing
	 **********************************************************************/
	public void clearGroups(boolean isCheckChildreonOnGroupCheckEnabled){

		if (mList.hasStableIds()){
			mCheckedGroups.clear();
		}else{
			mUnstableCheckedGroups.clear();
		}
	}


	public void clearChildren(){

		if (mList.hasStableIds()){
			mCheckedChildren.clear();
		}else{
			mUnstableCheckedChildren.clear();
		}
	}


	public void clearAll(){

		if (mList.hasStableIds()){
			mCheckedChildren.clear();
			mCheckedGroups.clear();
		}else{
			mUnstableCheckedChildren.clear();
			mUnstableCheckedGroups.clear();
		}
	}
}
