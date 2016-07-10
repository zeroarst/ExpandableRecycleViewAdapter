package lab.royliao.android.expandablerecyeviewadapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements group and child structure displayed in {@link RecyclerView}.
 * This provides similar interfaces with {@link android.widget.ExpandableListAdapter}.
 */
public abstract class ExpandableRecycleViewBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ExpandableRcVwBaseAdpt";

    private static abstract class Index {

        // The position in data, not adapter position.
        protected int position;

        public Index(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class GroupIndex extends Index {

        public boolean expanded;
        private List<ChildIndex> children = new ArrayList<>();

        public GroupIndex(int position, boolean expanded) {
            super(position);
            this.expanded = expanded;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public int getChildrenCount() {
            return children.size();
        }
    }

    public static class ChildIndex extends Index {

        private GroupIndex group;

        public ChildIndex(int position, GroupIndex group) {
            super(position);
            this.group = group;
        }

        public GroupIndex getGroup() {
            return group;
        }
    }

    /**
     * Items used by the adapter.
     */
    private List<Index> mIndices;

    protected List<Index> getIndices() {
        return mIndices;
    }

    protected static final int VIEW_TYPE_GROUP = 1;
    protected static final int VIEW_TYPE_CHILD = 2;

    public interface ActionListener {

        /**
         * Triggered when click a child.
         *
         * @param position Note this is the position under group.
         * @param dataItem
         * @param holder
         */
        void onClickChild(int position, Object dataItem, RecyclerView.ViewHolder holder);

        /**
         * Triggered when click a group. This default will toggle group and cause {@link #onToggledGroup(int, boolean)} to be called.
         * Returns false to consume the action.
         *
         * @param group
         * @param dataItem
         * @param holder
         * @return false if throttling {@link #onToggledGroup(int, boolean)}.
         */
        boolean onClickGroup(int group, Object dataItem, RecyclerView.ViewHolder holder);

        void onToggledGroup(int group, boolean toggle);
    }

    private ActionListener mListener;

    public void setActionListener(ActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mIndices.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mIndices == null) {
            initIndices();
        }
    }

    /**
     * Generates index items based on current group and children sizes.
     */
    private void initIndices() {
        mIndices = new ArrayList<>();
        int pos = -1;
        for (int i = 0; i < getGroupCount(); i++) {
            pos++;

            GroupIndex groupIdx = new GroupIndex(i, initialExpanded(i));
            mIndices.add(pos, groupIdx);

            if (initialExpanded(i)) {
                for (int j = 0; j < getChildrenCount(i); j++) {
                    pos++;
                    ChildIndex childIndex = new ChildIndex(j, groupIdx);
                    groupIdx.children.add(childIndex);
                    mIndices.add(pos, childIndex);
                }
            }
        }
    }

    /**
     * Gets the group index data with given adapter position.
     * <p/>
     * Called when need to know the group's status such as position, expanded and children size.
     * <p/>
     * You should always check if the return is null.
     * <br/>
     * <br/>
     * {@link GroupIndex#getPosition()}
     * <br/>
     * {@link GroupIndex#isExpanded()}
     * <br/>
     * {@link GroupIndex#getChildrenCount()}.
     *
     * @param adapterPosition The value from {@link RecyclerView.ViewHolder#getAdapterPosition()}.
     * @return Null if is not a {@link GroupIndex}.
     */
    @Nullable
    public GroupIndex getGroupIndex(int adapterPosition) {
        if (adapterPosition < mIndices.size()) {
            Index idx = getIndices().get(adapterPosition);
            if (idx != null && idx instanceof GroupIndex) {
                return (GroupIndex) idx;
            }
        }
        return null;
    }

    /**
     * Gets the child index data with given adapter position.
     * <p/>
     * Called when need to know the child's position and group status.
     * You should always check if the return is null.
     * <br/>
     * <br/>
     * {@link ChildIndex#getPosition()}
     * <br/>
     * {@link ChildIndex#getGroup()}
     *
     * @param adapterPosition The value from {@link RecyclerView.ViewHolder#getAdapterPosition()}.
     * @return Null if is not a {@link ChildIndex}.
     */
    @Nullable
    public ChildIndex getChildIndex(int adapterPosition) {
        if (adapterPosition < mIndices.size()) {
            Index idx = getIndices().get(adapterPosition);
            if (idx != null && idx instanceof ChildIndex)
                return (ChildIndex) idx;
        }
        return null;
    }

    public int notifyGroupInserted(int groupPosition, boolean expanded, int childrenCount) {
        return notifyGroupRangedInserted(groupPosition, new boolean[]{expanded}, new int[]{childrenCount});
    }

    public int notifyGroupRangedInserted(int groupPositionStart, boolean[] expanded, int[] childrenCount) {
        if (expanded == null || childrenCount == null)
            throw new IllegalArgumentException("expanded array and children count array cannot be null");
        if (expanded.length != childrenCount.length)
            throw new IllegalArgumentException("expanded array size must be the same with children count array size");

        int currentGroupPosition = -1;
        int positionInIndices = -1;

        // Address the case inserting empty list.
        if (groupPositionStart == 0 && mIndices.size() == 0) {
            currentGroupPosition = positionInIndices = 0;
        } else {
            for (Index idx : mIndices) {
                positionInIndices++;
                if (idx instanceof GroupIndex) {
                    GroupIndex groupIdx = (GroupIndex) idx;
                    if (groupIdx.position == groupPositionStart) {
                        currentGroupPosition = groupIdx.position;
                        break;
                    }
                }
            }
        }
        if (currentGroupPosition == -1)
            return -1;

        final int notifiedPositionStart = positionInIndices;

        for (int i = 0; i < expanded.length; i++) {
            GroupIndex groupIdx = new GroupIndex(currentGroupPosition + i, expanded[i]);
            mIndices.add(positionInIndices + i, groupIdx);

            // Insert children to the group.
            for (int j = 0; j < childrenCount[i]; j++) {
                ChildIndex childIndex = new ChildIndex(j, groupIdx);
                groupIdx.children.add(childIndex);
                // Only add to indices if expanded.
                if (expanded[i]) {
                    positionInIndices++;
                    mIndices.add(positionInIndices, childIndex);
                }
            }
        }

        // Adding offset to groups' positions after the last inserted group.
        for (int j = positionInIndices + 1; j < mIndices.size(); j++) {
            Index idx = mIndices.get(j);
            if (idx instanceof GroupIndex) {
                GroupIndex gpIdx = (GroupIndex) idx;
                gpIdx.position += expanded.length;
            }
        }

        notifyItemRangeInserted(notifiedPositionStart, positionInIndices - notifiedPositionStart + 1);

        return notifiedPositionStart;
    }

    public void notifyGroupRemoved(GroupIndex groupIndexStart) {
        notifyGroupRangedRemoved(groupIndexStart, 1);
    }

    public void notifyGroupRangedRemoved(GroupIndex groupIndexStart, int itemCount) {
        int adapterPosition = mIndices.indexOf(groupIndexStart);
        if (groupIndexStart == null || itemCount < 0 || adapterPosition == -1)
            return;

        int deletedItemCount = 0;
        for (int i = 0; i < itemCount; i++) {
            GroupIndex groupIdx = (GroupIndex) mIndices.get(adapterPosition);
            mIndices.removeAll(groupIdx.children);
            mIndices.remove(groupIdx);
            // Add deleted item count: children size + group self.
            deletedItemCount += groupIdx.children.size() + 1;
        }

        // Update group positions after the removed group. Simply minus 1 to each's position.
        for (int i = adapterPosition; i < mIndices.size(); i++) {
            Index idx = mIndices.get(i);
            if (idx instanceof GroupIndex) {
                idx.position = idx.position - itemCount;
            }
        }
        notifyItemRangeRemoved(adapterPosition, deletedItemCount);
    }

    public int notifyChildInserted(int groupPosition, int childPosition) {
        return notifyChildRangedInserted(groupPosition, childPosition, 1);
    }

    public int notifyChildRangedInserted(int groupPosition, int childPositionStart, int itemCount) {
        if (groupPosition >= 0 && childPositionStart >= 0 && itemCount > 0) {
            // Find out group by matching group position.
            for (int i = 0; i < mIndices.size(); i++) {
                Index idx = mIndices.get(i);
                if (idx instanceof GroupIndex) {
                    GroupIndex groupIdx = (GroupIndex) idx;
                    if (groupIdx.position == groupPosition) {
                        // Add children to group.
                        for (int j = 0; j < itemCount; j++) {
                            ChildIndex childIndex = new ChildIndex(childPositionStart + j, groupIdx);
                            groupIdx.children.add(childPositionStart + j, childIndex);
                            mIndices.add(i + 1 + childPositionStart + j, childIndex);
                        }
                        // Add offset to children's positions after the last inserted child position.
                        for (int j = childPositionStart + itemCount; j < groupIdx.children.size(); j++) {
                            groupIdx.children.get(j).position += itemCount;
                        }
                        notifyItemRangeInserted(i + 1 + childPositionStart, itemCount);
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public void notifyChildRemoved(ChildIndex childIndex) {
        notifyChildRangeRemoved(childIndex, 1);
    }

    public void notifyChildRangeRemoved(ChildIndex childIndexStart, int itemCount) {

        int adapterPosition = mIndices.indexOf(childIndexStart);
        // Unable to find adapter position. The child index does not exist in the indices.
        if (adapterPosition == -1)
            return;
        GroupIndex groupIndex = childIndexStart.getGroup();
        // Remove from the end to avoid exception.
        for (int i = adapterPosition + itemCount - 1; i >= adapterPosition; i--) {
            if (mIndices.get(i) instanceof ChildIndex) {
                ChildIndex deletedChildIdx = (ChildIndex) mIndices.remove(i);
                groupIndex.children.remove(deletedChildIdx);
            } else
                break;
        }
        for (int i = childIndexStart.getPosition(); i < groupIndex.children.size(); i++) {
            groupIndex.children.get(i).position = i;
        }
        notifyItemRangeRemoved(adapterPosition, itemCount);
    }

    @Override
    public int getItemViewType(int position) {
        Index index = mIndices.get(position);
        if (index instanceof GroupIndex) {
            return (VIEW_TYPE_GROUP << 16) + (getGroupViewType(((GroupIndex) index).position) & 0xffff);
        } else if (index instanceof ChildIndex) {
            ChildIndex cidxr = (ChildIndex) index;
            return (VIEW_TYPE_CHILD << 16) + (getChildViewType(cidxr.group.position, cidxr.position) & 0xffff);
        } else
            return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int type = viewType >> 16;
        if (type != 0)
            switch (type) {
                case VIEW_TYPE_GROUP:
                    return onCreateGroupViewHolder(viewType & 0xffff, parent);
                case VIEW_TYPE_CHILD:
                    return onCreateChildViewHolder(viewType & 0xffff, parent);
            }
        throw new IllegalArgumentException("Unable to identify group or item view type");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Index idx = mIndices.get(position);
        if (idx instanceof GroupIndex) {
            final GroupIndex groupIdx = (GroupIndex) idx;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = holder.getAdapterPosition();

                    if (mListener != null)
                        if (mListener.onClickGroup(groupIdx.position, getGroup(groupIdx.position), holder))
                            return;

                    if (groupIdx.expanded) {
                        mIndices.removeAll(groupIdx.children);
                        notifyItemRangeRemoved(pos + 1, groupIdx.children.size());
                    } else {
                        mIndices.addAll(pos + 1, groupIdx.children);
                        notifyItemRangeInserted(pos + 1, groupIdx.children.size());
                    }
                    groupIdx.expanded = !groupIdx.expanded;

                    if (mListener != null)
                        mListener.onToggledGroup(groupIdx.position, groupIdx.expanded);

                }
            });
            onBindGroupViewHolder(position, getGroup(groupIdx.position), holder);

        } else if (idx instanceof ChildIndex) {
            final ChildIndex childIdx = (ChildIndex) idx;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (mListener != null)
                        mListener.onClickChild(pos, getChild(childIdx.group.position, childIdx.position), holder);

                }
            });
            onBindChildViewHolder(childIdx.group.position, childIdx.position, getChild(childIdx.group.position, childIdx.position), holder);
        }
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an group item.
     *
     * @param viewType
     * @param parent
     * @return
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    public abstract RecyclerView.ViewHolder onCreateGroupViewHolder(int viewType, ViewGroup parent);

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an child item.
     *
     * @param viewType
     * @param parent
     * @return
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    public abstract RecyclerView.ViewHolder onCreateChildViewHolder(int viewType, ViewGroup parent);

    /**
     * Called to display the data at the specified group position.
     *
     * @param position
     * @param dataItem
     * @param holder
     */
    public abstract void onBindGroupViewHolder(int position, Object dataItem, RecyclerView.ViewHolder holder);

    /**
     * Called to display the data at the specified child position.
     *
     * @param groupPosition
     * @param childPosition
     * @param dataItem
     * @param holder
     */
    public abstract void onBindChildViewHolder(int groupPosition, int childPosition, Object dataItem, RecyclerView.ViewHolder holder);

    /**
     * Gets the group data at given position.
     *
     * @param groupPosition
     * @return
     */
    public abstract Object getGroup(int groupPosition);

    /**
     * Gets the child data at given group and child positions.
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public abstract Object getChild(int groupPosition, int childPosition);

    /**
     * Gets the group size.
     *
     * @return
     */
    public abstract int getGroupCount();

    /**
     * Only called once when indices is built with {@link #initIndices()} in {@link #onAttachedToRecyclerView(RecyclerView)}.
     *
     * @param groupPosition
     * @return
     */
    public abstract boolean initialExpanded(int groupPosition);

    /**
     * Gets the children size.
     *
     * @param groupPosition
     * @return
     */
    public abstract int getChildrenCount(int groupPosition);

    /**
     * Gets a view type for the group at given position. The view type then will be passed to
     * {@link #onCreateGroupViewHolder(int, ViewGroup)}.
     *
     * @param groupPosition
     * @return
     */
    public abstract int getGroupViewType(int groupPosition);

    /**
     * Gets a view type for the child at given group and child positions.
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public abstract int getChildViewType(int groupPosition, int childPosition);

}
