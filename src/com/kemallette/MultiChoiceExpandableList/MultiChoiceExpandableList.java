package com.kemallette.MultiChoiceExpandableList;


import java.util.List;


/**
 * Interface definition for two-level (group/child) expandable checkable list.
 * Methods which would normally return void return MultiChoiceExpandableList as
 * a convenience for method chaining.
 * 
 * 
 * @author kemallette
 * 
 */
public interface MultiChoiceExpandableList	extends
											ExpandableListCheckListener{

	/**
	 * Depending on if applied to groups or children, does not allow checks. If
	 * selected for both group and child check modes, nothing will be checked.
	 */
	public static final int	CHECK_MODE_NONE					= 10;

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


	/**
	 * Only one item at a time throughout the whole list (including groups and
	 * children) can be checked.
	 */
	public static final int	CHECK_MODE_ONLY_ONE				= 15;


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


	/**
	 * Gives a count of children checked for the group with groupId
	 * 
	 * @param groupId
	 * @return the number of checked children for the group with groupId
	 */
	public int getCheckedChildCount(long groupId);


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
	 * @return an array of all checked children ids
	 */
	public long[] getCheckedChildIds();


	/**
	 * Use this if you want all the checked children for a specific group at
	 * groupPosition.
	 * 
	 * @param groupPosition
	 *            - group position where the checked children fall under
	 * @return - an array of checked children ids
	 */
	public long[] getCheckedChildIds(int groupPosition);


	/**
	 * Use this if you want all the checked children for a specific group with
	 * the specified id
	 * 
	 * @param groupId
	 * @return - an array of checked children ids
	 */
	public long[] getCheckedChildIds(long groupId);


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
	 * @return List of length 2 int arrays representing checked child positions.
	 *         int[0] is the child's group position and int[1] is the child's
	 *         position in the group
	 */
	public List<int[]> getCheckedChildPositions();


	/**
	 * 
	 * if !stableIds, cannot ensure these aren't stale
	 * 
	 * @return
	 */
	public int[] getCheckedChildPositions(int groupPosition);


	/**
	 * 
	 * if !stableIds, cannot ensure these aren't stale
	 * 
	 * @return
	 */
	public int[] getCheckedChildPositions(long groupId);


	
	public int getGroupChoiceMode();


	public int getChildChoiceMode();


	public MultiChoiceExpandableList setGroupChoiceMode(int choiceMode);


	public MultiChoiceExpandableList setChildChoiceMode(int choiceMode);
	
	

	/**
	 * If set to true, on a group check change, that group's children will match the
	 * group's check state. In other words, if you check a group, all its
	 * children will also be checked and vice versa. If a group is unchecked,
	 * all its children will be unchecked.
	 * 
	 * @param checkChildrenOnGroupCheck - true to enable, false to disable
	 * @return
	 */
	public MultiChoiceExpandableList checkChildrenOnGroupCheck(boolean checkChildrenOnGroupCheck);
	
	public boolean isCheckChildrenOnGroupCheckEnabled();
	
	public MultiChoiceExpandableList
		setGroupCheckedState(int groupPosition, boolean checkState);


	public MultiChoiceExpandableList
		setGroupCheckedState(long groupId, boolean checkState);


	public MultiChoiceExpandableList
		setChildCheckedState(long childId, boolean checkState);


	public MultiChoiceExpandableList
		setChildCheckedState(int groupPosition,
								int childPosition,
								boolean checkState);


	public MultiChoiceExpandableList clearAllChoices();


	public MultiChoiceExpandableList clearCheckedGroups();


	public MultiChoiceExpandableList clearCheckedChildren();


	public MultiChoiceExpandableList
		clearCheckedGroupChildren(int groupPosition);


	public MultiChoiceExpandableList clearCheckedGroupChildren(long groupId);


	public boolean isGroupChecked(int groupPosition);


	public boolean isGroupChecked(long groupId);


	public boolean isChildChecked(long groupId, long childId);


	public boolean isChildChecked(int groupPosition, int childPosition);


	public MultiChoiceExpandableList
		setExpandableCheckListener(ExpandableListCheckListener mListener);


	public ExpandableListCheckListener getExpandableListCheckListener();


	public MultiChoiceExpandableList
		removeExpandableCheckListener();

}
