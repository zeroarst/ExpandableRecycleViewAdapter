# ExpandableRecycleViewAdapter
Allow you to use same features like the ExpandableListAdapter. You define view types for groups and children.

##Demo
See <a href="https://youtu.be/FTSeDt0QICU">here</a>.

##How to use
Write your adapter and extend either `ExpandableRecycleViewAdapter` or `ExpandableRecycleViewBaseAdapter`. In general, you just need the first. 

Simply override those methods required, similar to `ExpandableListAdapter`.

```
public abstract RecyclerView.ViewHolder onCreateGroupViewHolder(int viewType, ViewGroup parent);

public abstract RecyclerView.ViewHolder onCreateChildViewHolder(int viewType, ViewGroup parent);

public abstract void onBindGroupViewHolder(int position, Object dataItem, RecyclerView.ViewHolder holder);

public abstract void onBindChildViewHolder(int groupPosition, int childPosition, Object dataItem, RecyclerView.ViewHolder holder);

public abstract Object getGroup(int groupPosition);

public abstract Object getChild(int groupPosition, int childPosition);

public abstract int getGroupCount();

public abstract boolean initialExpanded(int groupPosition);

public abstract int getChildrenCount(int groupPosition);

public abstract int getGroupViewType(int groupPosition);

public abstract int getChildViewType(int groupPosition, int childPosition);

```

You can call `setActionListener` to interact with items.
```
abstract boolean onClickGroupItem(int position, GI dataItem, GVH holder);

abstract void onClickChildItem(int position, CI dataItem, CVH holder);

void onToggledGroup(int group, boolean toggle);

```

If you want to get current group or child's status such as positions, call `getGroupIndex(int adapterPosition)` and `getChild(int adapterPosition)`. In generic case of adding/updating/deleting data and update views:

Update group data.
```
GroupIndex groupIdx = getGroupIndex(holder.getAdapterPosition());
if(groupIdx == null)
  return;
// Get group position by calling groupIdx.getPosition().
// Get group's children index data by calling groupIdx.getChildren().
// Get group's toggled status by calling groupIdx.isExpanded().
// Use those info to update your data. Then call:
notifyGroupInserted(int groupPosition, boolean expanded, int childrenCount);
notifyGroupRangedInserted(int groupPositionStart, boolean[] expanded, int[] childrenCount);
notifyGroupRemoved(GroupIndex groupIndexStart);
notifyGroupRangedRemoved(GroupIndex groupIndexStart, int itemCount);
```

Update child data.
```
ChildIndex childIdx = getChildIndex(holder.getAdapterPosition());
if(childIdx == null)
  return;
// Get child position by calling childIdx.getPosition().
// Get child's group index data by calling childIdx.getGroup().
// Use those info to update your data. Then call one of these:
notifyChildInserted(int groupPosition, int childPosition);
notifyChildRangedInserted(int groupPosition, int childPositionStart, int itemCount);
notifyChildRemoved(ChildIndex childIndex);
notifyChildRangeRemoved(ChildIndex childIndexStart, int itemCount);

```

##Unknowing issue
This is a bug from nagive SDK.
If you give transparent colors for all items when using `RecyclerView.ItemDecoration`. You will see they are not animating when items are animating with notifing. If anyone knows how to fix this please let me know.

##Contributing
Welcome any comments and pull requests.
