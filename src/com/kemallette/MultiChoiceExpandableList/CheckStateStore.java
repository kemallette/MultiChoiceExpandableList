package com.kemallette.MultiChoiceExpandableList;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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

	final boolean										hasStableIds;

	/**
	 * Keys are groupIds. Values are last known groupPosition. If a key
	 * (groupId) is present, that group is checked.
	 * 
	 * Note: I decided not to trust last known positions for most things, since
	 * a call to mList.get..Position(id) gets delegated to the client's adapter
	 * which should have the most up to date positions.
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
	 * 
	 * Note: I decided not to trust last known positions for most things, since
	 * a call to mList.get..Position(id) gets delegated to the client's adapter
	 * which should have the most up to date positions.
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

	private final MultiChoiceExpandableListView			mList;


	CheckStateStore(final MultiChoiceExpandableListView mList){

		this.mList = mList;
		hasStableIds = mList.hasStableIds();

		if (hasStableIds){
			mCheckedGroups = new LongSparseArray<Integer>();
			mCheckedChildren = new LongSparseArray<LongSparseArray<Integer>>();
		}else{
			mUnstableCheckedGroups = new BitSet(mList.getGroupCount());
			mUnstableCheckedChildren = new SparseArray<BitSet>();
		}
	}


	/**
	 * Stores a group's checked state (checked/unchecked) regardless of if the
	 * list's adapter has stable ids or not. Since the params don't include a
	 * groupId, the unstable id storage is completed here. If the list's adapter
	 * does have stable ids, storage is delegated to its sibling method with
	 * groupId in its params.
	 * 
	 * @param groupPosition
	 * @param isChecked
	 * @param checkChildrenOnGroupCheck
	 */
	void setGroupState(final int groupPosition,
						final boolean isChecked,
						final boolean checkChildrenOnGroupCheck){

		if (hasStableIds)
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


	/**
	 * Stores a group's checked state (checked/unchecked) regardless of if the
	 * list's adapter has stable ids or not. Since the params include a groupId,
	 * if the list's adapter has stable ids the storage is completed here. If
	 * the list's adapter does have stable ids, storage is delegated to its
	 * sibling method without groupId in its params.
	 * 
	 * @param groupId
	 * @param groupPosition
	 * @param isChecked
	 * @param checkChildrenOnGroupCheck
	 */
	void setGroupState(final long groupId,
						final int groupPosition,
						final boolean isChecked,
						final boolean checkChildrenOnGroupCheck){

		if (hasStableIds){
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


	private void
		setUnstableGroupState(final int groupPosition,
								final boolean isChecked,
								final boolean checkChildrenOnGroupCheck){

		mUnstableCheckedGroups.set(	groupPosition,
									isChecked);
		if (checkChildrenOnGroupCheck){
			setGroupsChildrenState(	groupPosition,
									isChecked);
		}
	}


	private void setGroupsChildrenState(final long groupId,
										final int groupPosition,
										final boolean isGroupChecked){

		if (hasStableIds){

			final long[] mChildIds = mList.getGroupChildrenIds(groupPosition);
			int childPos;
			for (final long childId : mChildIds){
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


	private void setGroupsChildrenState(final int groupPosition,
										final boolean isGroupChecked){

		if (hasStableIds){
			setGroupsChildrenState(	mList.getGroupId(groupPosition),
									groupPosition,
									isGroupChecked);
		}else{
			final int childCount = mList.getChildrenCount(groupPosition);
			for (int i = 0; i < childCount; i++){
				setChildState(	groupPosition,
								i,
								isGroupChecked);
			}
		}
	}


	void setChildState(final int groupPosition,
						final int childPosition,
						final boolean isChecked){

		if (hasStableIds)
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


	void setChildState(final int groupPosition,
						final long groupId,
						final int childPosition,
						final long childId,
						final boolean isChecked){

		if (hasStableIds)
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


	boolean isGroupChecked(final long groupId){

		if (hasStableIds)
			return mCheckedGroups.indexOfKey(groupId) >= 0;
		else
			return isGroupChecked(mList.getGroupPosition(groupId));
	}


	boolean isGroupChecked(final int groupPosition){

		if (hasStableIds)
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
	boolean isChildChecked(final long groupId, final long childId){

		if (hasStableIds){

			if (mCheckedChildren.get(groupId) == null)
				return false;
			else
				return mCheckedChildren.get(groupId)
										.indexOfKey(childId) >= 0;
		}else{

			final int groupPosition = mList.getGroupPosition(groupId);
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
	boolean isChildChecked(final int groupPosition, final int childPosition){

		if (hasStableIds)
			return isChildChecked(	mList.getGroupId(groupPosition),
									mList.getChildId(	groupPosition,
														childPosition));

		if (mUnstableCheckedChildren.get(groupPosition) == null)
			return false;

		return mUnstableCheckedChildren.get(groupPosition)
										.get(childPosition);
	}


	/*********************************************************************
	 * Data store helpers (internal use)
	 **********************************************************************/

	private void putCheckedChild(final long groupId,
									final int childPosition,
									final long childId){

		if (mCheckedChildren.get(groupId) != null)
			mCheckedChildren.get(groupId)
							.put(	childId,
									childPosition);
		else{
			final LongSparseArray<Integer> mChildStates = new LongSparseArray<Integer>();
			mChildStates.put(	childId,
								childPosition);
			mCheckedChildren.put(	groupId,
									mChildStates);
		}

	}


	private void
		putUnstableIdCheckedChild(final int groupPosition,
									final int childPosition){


		if (mUnstableCheckedChildren.get(groupPosition) != null)

			mUnstableCheckedChildren.get(groupPosition)
									.set(	childPosition,
											true);
		else{
			final BitSet mBitSet = new BitSet(mList.getChildrenCount(groupPosition));
			mBitSet.set(childPosition,
						true);
			mUnstableCheckedChildren.put(	groupPosition,
											mBitSet);
		}

	}


	private void removeCheckedChild(final long groupId, final long childId){

		if (mCheckedChildren.get(groupId) == null
			|| mCheckedChildren.get(groupId)
								.get(childId) == null)
			return;

		mCheckedChildren.get(groupId)
						.remove(childId);

	}


	private void removeUnstableIdCheckedChild(final int groupPosition,
												final int childPosition){

		if (mUnstableCheckedChildren.get(groupPosition) == null
			|| mUnstableCheckedChildren.get(groupPosition)
										.get(childPosition) == false)
			return;

		mUnstableCheckedChildren.get(groupPosition)
								.clear(childPosition);
	}


	/*********************************************************************
	 * Checked Item id/position getters for MULTIPLE choice mode options
	 **********************************************************************/

	long[] getCheckedGroupIds(){

		if (!hasStableIds
			|| mCheckedGroups == null){
			Log.w(	TAG,
					"The adapter backing this list does not have stable ids. Please ensure your adapter implements stable ids and returns true for hasStableIds. If that's not possible, you will need to use getCheckedGroupPositions instead.");
			return new long[0];
		}

		return Util.getKeys(mCheckedGroups);
	}


	List<Long> getCheckedChildIds(){

		if (!hasStableIds){
			Log.w(	TAG,
					"The adapter backing this list does not have stable ids. Please ensure your adapter implements stable ids and returns true for hasStableIds. If that's not possible, you will need to use getCheckedGroupPositions instead.");
			return new ArrayList<Long>();
		}

		final LongSparseArray<LongSparseArray<Integer>> checkedChildren = mCheckedChildren;
		final int count = checkedChildren.size();
		final ArrayList<LongSparseArray<Integer>> groupChildren = new ArrayList<LongSparseArray<Integer>>(count);

		for (int i = 0; i < count; i++){
			groupChildren.add(checkedChildren.valueAt(i));
		}


		final ArrayList<Long> ids = new ArrayList<Long>();

		for (final LongSparseArray<Integer> groupCheckedChildren : groupChildren){
			for (final Long id : Util.getKeys(groupCheckedChildren)){
				ids.add(id);
			}
		}

		return ids;
	}


	List<Long> getCheckedChildIds(final long groupId){

		final LongSparseArray<LongSparseArray<Integer>> checkedChildren = mCheckedChildren;
		final int count = checkedChildren.size();
		long[] childIds = null;

		for (int i = 0; i < count; i++){
			if (checkedChildren.keyAt(i) == groupId){
				childIds = Util.getKeys(checkedChildren.valueAt(i));
				break;
			}
		}


		final ArrayList<Long> ids = new ArrayList<Long>();

		if (childIds != null)
			for (final long id : childIds){
				ids.add(id);
			}
		else
			Log.w(	TAG,
					"childIds was null! Check to see if groupId actually matched a key in checkedChildren");

		return ids;
	}


	/**
	 * Convenience for {@link getCheckedChildIds(long groupId)}
	 * 
	 * 
	 * @param groupPosition
	 * @return
	 */
	List<Long> getCheckedChildIds(final int groupPosition){

		return getCheckedChildIds(mList.getGroupId(groupPosition));
	}


	int[] getCheckedGroupPositions(){

		final int[] positions;
		if (hasStableIds){

			final long[] checkedGroupIds = getCheckedGroupIds();
			positions = new int[checkedGroupIds.length];

			for (int i = 0; i < checkedGroupIds.length; i++){
				positions[i] = mList.getGroupPosition(checkedGroupIds[i]);
			}

		}else{

			final BitSet checkedGroups = mUnstableCheckedGroups;

			positions = new int[checkedGroups.length()];
			// TODO for unstable ids
		}

		return positions;
	}


	/**
	 * 
	 * 
	 * @return a SparseArray<int[]> where keys are groupPositions and each value
	 *         (int[]) are positions of that group's checked children.
	 */
	SparseArray<int[]> getCheckedChildPositions(){

		final SparseArray<int[]> positions = new SparseArray<int[]>();
		
		if (hasStableIds){
			final SparseArray<LongSparseArray<Integer>> checkedChildren = new SparseArray<LongSparseArray<Integer>>();	
			final LongSparseArray<LongSparseArray<Integer>> children = mCheckedChildren;
			
			long groupId;
			int groupPosition;
			for(int i = 0; i < children.size(); i++) {
				groupId = children.keyAt(i);
				groupPosition = mList.getGroupPosition(groupId);
				checkedChildren.put(groupPosition, children.get(groupId));
			}
			
			int parentPos;
			int[] childPositions;
			long[] childIds;
			for (int i = 0; i < checkedChildren.size(); i++){
				
				parentPos = checkedChildren.keyAt(i); 
				childIds = Util.getKeys(checkedChildren.valueAt(i));
				childPositions = new int[childIds.length];
				
				for (int j = 0; j < childIds.length; j++){
					childPositions[j] = mList.getChildPosition(parentPos, childIds[j]);
				}			
				positions.put(parentPos,childPositions);
			}
		}else{
			// TODO for unstable ids
		}
		return positions;
	}
	

	int[] getCheckedChildPositions(final int groupPosition){

		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * TODO mo' docs fool
	 * 
	 * @param groupId
	 * @return a SparseArray<int[]> where the key is the groupPosition and the
	 *         value (int[]) stores the postitions for the group's checked
	 *         children.
	 */
	SparseArray<int[]> getCheckedChildPositions(final long groupId){

		final SparseArray<int[]> positions = new SparseArray<int[]>();

		if (hasStableIds){

			final LongSparseArray<LongSparseArray<Integer>> screamingChildren = mCheckedChildren;
			final LongSparseArray<Integer> tiredChildren = screamingChildren.get(groupId);

			final long[] silencedChildren = Util.getKeys(tiredChildren);
			final int[] brats = new int[silencedChildren.length];

			final int frustratedParent = mList.getGroupPosition(groupId);

			for (int i = 0; i < silencedChildren.length; i++){
				brats[i] = mList.getChildPosition(	groupId,
													silencedChildren[i]);
			}

			positions.put(	frustratedParent,
							brats);
		}else
			Log.w(	TAG,
					"The list's adapter does not have stable ids. Use getCheckedChildPositions(int groupPosition) instead.");
		return positions;
	}


	/*********************************************************************
	 * Checked item id/position getters for SINGLE choice mode options
	 **********************************************************************/
	long getCheckedGroupId(){

		return -1l;
	}


	long getCheckedChildId(){

		return -1l;
	}


	long getCheckedChildId(final long groupId){

		return -1l;
	}


	int getCheckedGroupPosition(){

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
	int[] getCheckedChildPosition(){

		// Log.e( TAG,
		// "Can't get checked child item position because group child mode is not CHILD_CHECK_MODE_ONE");
		return null;
	}


	/**
	 * @param groupPos
	 * @return checked child position in group at groupPosition
	 */
	int getCheckedChildPosition(final int groupPosition){

		// Log.e( TAG,
		// "Can't get checked child item position because group child mode is not CHILD_CHECK_MODE_ONE_PER_GROUP");
		return -1;
	}


	/*********************************************************************
	 * Checked Counts
	 **********************************************************************/
	int getCheckedGroupCount(){
		if(hasStableIds) {
			return mCheckedGroups.size();
		}else {
			return mUnstableCheckedGroups.length();
		}
	}


	int getCheckedChildCount(){

		// TODO yo dog, do dis
		final int count = 0;
		return count;
	}


	int getCheckedChildCount(final int groupPosition){

		// TODO yo dog, do dis
		final int count = 0;
		return count;
	}


	int getCheckedChildCount(final long groupId){

		// TODO yo dog, do dis
		final int count = 0;
		return count;
	}


	/*********************************************************************
	 * Clearing
	 **********************************************************************/
	void clearGroups(final boolean isCheckChildreonOnGroupCheckEnabled){

		if (hasStableIds){
			mCheckedGroups.clear();
		}else{
			mUnstableCheckedGroups.clear();
		}
	}


	void clearChildren(){

		if (hasStableIds){
			mCheckedChildren.clear();
		}else{
			mUnstableCheckedChildren.clear();
		}
	}


	void clearAll(){

		if (hasStableIds){
			mCheckedChildren.clear();
			mCheckedGroups.clear();
		}else{
			mUnstableCheckedChildren.clear();
			mUnstableCheckedGroups.clear();
		}
	}
}
