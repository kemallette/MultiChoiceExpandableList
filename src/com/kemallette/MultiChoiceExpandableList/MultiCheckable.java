package com.kemallette.MultiChoiceExpandableList;


import java.util.List;

import android.util.SparseArray;


/**
 * Interface definition for two-level (group/child) expandable checkable list.
 * Methods which would normally return void return MultiCheckable as a
 * convenience for method chaining.
 * 
 * 
 * @author kemallette
 * 
 */
public interface MultiCheckable	extends
								MultiCheckListener{

	/**
	 * Depending on if applied to groups or children, does not allow checks. If
	 * selected for both group and child check modes, nothing will be checked.
	 */
	public static final int	CHECK_MODE_NONE					= 10;

	/**
	 * Only one item at a time throughout the whole list (including groups and
	 * children) can be checked.
	 */
	public static final int	CHECK_MODE_ONLY_ONE				= 15;

	/**
	 * Depending on if applied to groups or children, any number of items can be
	 * checked. If applied to both groups and children, any number of items
	 * throughout the list can be checked.
	 */
	public static final int	CHECK_MODE_MULTI				= 12;

	/**
	 * Only one group in the list can be checked at a time.
	 */
	public static final int	GROUP_CHECK_MODE_ONE			= 11;

	/**
	 * Only one child out of the entire list can be checked at one time. You
	 * cannot use this and set checkChildrenOnGroupCheck true
	 */
	public static final int	CHILD_CHECK_MODE_ONE			= 13;

	/**
	 * Only one child item per group can be checked at a time. You cannot use
	 * this and set checkChidrenOnGroupCheck true.
	 */
	public static final int	CHILD_CHECK_MODE_ONE_PER_GROUP	= 14;


	/*********************************************************************
	 * Checked ID Getter
	 **********************************************************************/
	/**
	 * 
	 * 
	 * @return an array of all checked group ids
	 */
	public long[] getCheckedGroupIds();


	/**
	 * Use this if you want ids for all checked children in the entire list,
	 * regardless of which group they're in.
	 * 
	 * @return
	 */
	public List<Long> getCheckedChildIds();


	/**
	 * Use this if you want all the checked children for a specific group at
	 * groupPosition.
	 * 
	 * @param groupPosition
	 *            - group position where the checked children fall under
	 * @return -
	 */
	public List<Long> getCheckedChildIds(int groupPosition);


	/*********************************************************************
	 * Checked Position Getters
	 **********************************************************************/
	/**
	 * 
	 * if !stableIds, cannot ensure these aren't stale
	 * 
	 * @return
	 */
	public int[] getCheckedGroupPositions();


	/**
	 * 
	 * if !stableIds, cannot ensure these aren't stale
	 * 
	 * @return
	 */
	public SparseArray<int[]> getCheckedChildPositions();


	/**
	 * 
	 * if !stableIds, cannot ensure these aren't stale
	 * 
	 * @return
	 */
	public int[] getCheckedChildPositions(int groupPosition);


	/*********************************************************************
	 * Check State Setters
	 **********************************************************************/
	public MultiCheckable
		setGroupCheckedState(int groupPosition,
								boolean isChecked);


	public MultiCheckable
		setChildCheckedState(int groupPosition,
								int childPosition,
								boolean isChecked);


	/*********************************************************************
	 * Check State
	 **********************************************************************/
	public boolean isGroupChecked(int groupPosition);


	public boolean isChildChecked(int groupPosition, int childPosition);


	/*********************************************************************
	 * Choice Mode
	 **********************************************************************/

	public int getGroupChoiceMode();


	public int getChildChoiceMode();


	public MultiCheckable setGroupChoiceMode(int choiceMode);


	public MultiCheckable setChildChoiceMode(int choiceMode);


	/**
	 * If set to true, on a group check change, that group's children will match
	 * the group's check state. In other words, if you check a group, all its
	 * children will also be checked and vice versa. If a group is unchecked,
	 * all its children will be unchecked.
	 * 
	 * @param checkChildrenOnGroupCheck
	 *            - true to enable, false to disable
	 * @return
	 */
	public MultiCheckable
		checkChildrenWithGroup(boolean checkChildrenWithGroup);


	public boolean checkChildrenWithGroup();


	/***********************************************************
	 * Checked Item Counts
	 ************************************************************/

	/**
	 * Gives a count of all the checked groups in the list
	 * 
	 * @return the total number of checked groups
	 */
	public int getCheckedGroupCount();


	/**
	 * Gives a count of all children in the list regardless of parent group.
	 * 
	 * @return total number of checked children in the list
	 */
	public int getCheckedChildCount();


	/**
	 * Gives a count of children checked for the group at groupPosition
	 * 
	 * @param groupPosition
	 * @return the number of checked children for group at groupPosition
	 */
	public int getCheckedChildCount(int groupPosition);


	/*********************************************************************
	 * Clearing
	 **********************************************************************/
	public MultiCheckable clearAllChoices();


	public MultiCheckable clearCheckedGroups();


	public MultiCheckable clearCheckedChildren();


	public MultiCheckable
		clearCheckedGroupChildren(int groupPosition);


	/*********************************************************************
	 * MultiCheck Listener
	 **********************************************************************/

	public MultiCheckable
		setExpandableCheckListener(MultiCheckListener mListener);


	public MultiCheckListener getExpandableListCheckListener();


	public MultiCheckable
		removeExpandableCheckListener();

}
