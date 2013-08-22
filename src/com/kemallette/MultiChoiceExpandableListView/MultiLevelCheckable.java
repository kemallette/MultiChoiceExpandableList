package com.kemallette.MultiChoiceExpandableListView;

public interface MultiLevelCheckable extends ExpandableListCheckListener {
	
	/**
	 * Both groups and children cannot be checked.
	 */
	public static final int CHECK_MODE_NONE = 10;
	
	/**
	 * Any number of groups and children can be checked.
	 */
	public static final int CHECK_MODE_MULTI = 12;

	public static final int GROUP_CHECK_MODE_ONE = 11;
	/**
	 * Only one child out of the entire list can be checked at one time. You
	 * cannot use this and set checkChildrenOnGroupCheck true
	 */
	public static final int CHILD_CHECK_MODE_ONE = 13;
	/**
	 * Only one child item per group can be checked at a time. You cannot use
	 * this and set checkChidrenOnGroupCheck true.
	 */
	public static final int CHILD_CHECK_MODE_ONE_PER_GROUP = 14;
	
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
}
