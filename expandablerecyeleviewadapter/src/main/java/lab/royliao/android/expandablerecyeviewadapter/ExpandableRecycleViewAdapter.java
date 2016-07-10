package lab.royliao.android.expandablerecyeviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Provides predefined data and view holder types for group and child.
 *
 * @param <GI>  Group item data type
 * @param <CI>  Child item data type
 * @param <GVH> Group view holder.
 * @param <GI>  Child view holder
 */
public abstract class ExpandableRecycleViewAdapter<GI, CI, GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends
        ExpandableRecycleViewBaseAdapter {

    @Override
    public void onBindGroupViewHolder(int position, Object dataItem, RecyclerView.ViewHolder holder) {
        onBindDataToGroupViewHolder(position, (GI) dataItem, (GVH) holder);
    }

    public static abstract class ActionListener<GI, CI, GVH, CVH> implements ExpandableRecycleViewBaseAdapter.ActionListener {

        public abstract boolean onClickGroupItem(int position, GI dataItem, GVH holder);

        public abstract void onClickChildItem(int position, CI dataItem, CVH holder);

        @Override
        public boolean onClickGroup(int group, Object dataItem, RecyclerView.ViewHolder holder) {
            return onClickGroupItem(group, (GI) dataItem, (GVH) holder);
        }

        @Override
        public void onClickChild(int position, Object dataItem, RecyclerView.ViewHolder holder) {
            onClickChildItem(position, (CI) dataItem, (CVH) holder);
        }
    }

    public void setActionListener(ActionListener<GI, CI, GVH, CVH> listener) {
        super.setActionListener(listener);
    }

    public abstract GI getGroup(int group);

    public abstract CI getChild(int groupPosition, int childPosition);

    @Override
    public void onBindChildViewHolder(int groupPosition, int childPosition, Object dataItem, RecyclerView.ViewHolder holder) {
        onBindDataToChildViewHolder(groupPosition, childPosition, (CI) dataItem, (CVH) holder);
    }

    public abstract GVH onCreateGroupViewHolder(int viewType, ViewGroup parent);

    public abstract CVH onCreateChildViewHolder(int viewType, ViewGroup parent);

    public abstract void onBindDataToGroupViewHolder(int position, GI dataItem, GVH holder);

    public abstract void onBindDataToChildViewHolder(int groupPosition, int childPosition, CI dataItem, CVH holder);

}
