package lab.royliao.android.expandablerecycleviewadapterexample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lab.royliao.android.expandablerecyeviewadapter.ExpandableRecycleViewAdapter;

/**
 * This example shows you one type of group view and two types of children views, along with one type of group data and two types of children mData.
 * If you only have one type of child view and data things are even easier.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SimpleExpandableRecycleViewAdapter mAdpt;

    private RecyclerView mRv;

    private ListPopupWindow mLstPopWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group g = new Group("New text group");
                g.children.add(new TextChild("Child 1"));
                g.children.add(new ImageChild(R.drawable.homer));
                g.children.add(new TextChild("Child 3"));

                int groupPosition;
                if (mAdpt.getGroupCount() != 0)
                    groupPosition = new Random().nextInt(mAdpt.getGroupCount());
                else
                    groupPosition = 0;

                mAdpt.getGroupList().add(groupPosition, g);
                int position = mAdpt.notifyGroupInserted(groupPosition, true, g.children.size());

                mRv.smoothScrollToPosition(position);
            }
        });

        mLstPopWin = new ListPopupWindow(this);
        mLstPopWin.setModal(true);

        mRv = (RecyclerView) findViewById(R.id.rv);

        List<Group> groupLst = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                Group g = new Group("Text group " + (i + 1));
                g.children.add(new TextChild("Child 1"));
                g.children.add(new TextChild("Child 2"));
                g.children.add(new TextChild("Child 3"));
                groupLst.add(g);
            } else {
                Group g = new Group("Image group " + (i + 1));
                g.children.add(new ImageChild(R.drawable.homer));
                g.children.add(new ImageChild(R.drawable.homer));
                g.children.add(new ImageChild(R.drawable.homer));
                groupLst.add(g);
            }
        }

        mAdpt = new SimpleExpandableRecycleViewAdapter(groupLst);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // If you are using ExpandableRecycleViewAdapter you might want to call its setActionListener instead of ExpandableRecycleViewBaseAdapter's.
        // It can correctly show you the defined data and view holder types in the subclass that extends ExpandableRecycleViewAdapter.
        mAdpt.setActionListener(new ExpandableRecycleViewAdapter.ActionListener<Group, Child, SimpleExpandableRecycleViewAdapter.GroupViewHolder,
                SimpleExpandableRecycleViewAdapter.ChildViewHolder>() {
            @Override
            public boolean onClickGroupItem(int position, Group dataItem, SimpleExpandableRecycleViewAdapter.GroupViewHolder holder) {
                return false;
            }

            @Override
            public void onClickChildItem(int position, Child dataItem, SimpleExpandableRecycleViewAdapter.ChildViewHolder holder) {
                if (dataItem instanceof ImageChild)
                    Toast.makeText(MainActivity.this, "Donut !", Toast.LENGTH_SHORT).show();
                if (dataItem instanceof TextChild)
                    Toast.makeText(MainActivity.this, ((TextChild) dataItem).text + " Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggledGroup(int group, boolean toggle) {

            }
        });

        mRv.setAdapter(mAdpt);
    }

    private class Group {
        String text;
        private List<Child> children = new ArrayList<>();

        public Group(String text) {
            this.text = text;
        }
    }

    private abstract class Child {

    }

    private class TextChild extends Child {
        String text;

        public TextChild(String text) {
            this.text = text;
        }
    }

    private class ImageChild extends Child {
        public ImageChild(int imgRestId) {
            this.imgRestId = imgRestId;
        }

        int imgRestId;
    }

    public class SimpleExpandableRecycleViewAdapter extends ExpandableRecycleViewAdapter<Group, Child, SimpleExpandableRecycleViewAdapter
            .GroupViewHolder, SimpleExpandableRecycleViewAdapter.ChildViewHolder> {

        public static final int CHILD_VIEW_TYPE_TEXT = 1;
        public static final int CHILD_VIEW_TYPE_IMAGE = 2;

        private List<Group> groupList;

        public List<Group> getGroupList() {
            return groupList;
        }

        public SimpleExpandableRecycleViewAdapter(List<Group> groupList) {
            this.groupList = groupList;
        }

        @Override
        public Group getGroup(int group) {
            return groupList.get(group);
        }

        @Override
        public Child getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).children.get(childPosition);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public boolean initialExpanded(int group) {
            return true;
        }

        @Override
        public int getChildrenCount(int group) {
            Log.d(TAG, "getChildrenCount: " + group);
            return groupList.get(group).children.size();
        }

        @Override
        public int getGroupViewType(int group) {
            return 0;
        }

        @Override
        public int getChildViewType(int groupPosition, int childPosition) {
            Log.d(TAG, "getChildViewType: " + String.valueOf(groupPosition) + ", " + String.valueOf(childPosition));
            Child child = getChild(groupPosition, childPosition);
            if (child instanceof ImageChild)
                return CHILD_VIEW_TYPE_IMAGE;
            else
                return CHILD_VIEW_TYPE_TEXT;
        }

        @Override
        public GroupViewHolder onCreateGroupViewHolder(int viewType, ViewGroup parent) {
            final GroupViewHolder holder = new GroupViewHolder(parent);
            holder.addImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupIndex groupIndex = getGroupIndex(holder.getAdapterPosition());

                    // Always check if non-null.
                    if (groupIndex == null)
                        return;

                    int childPosition;
                    if (groupIndex.getChildrenCount() != 0)
                        childPosition = new Random().nextInt(groupIndex.getChildrenCount());
                    else
                        childPosition = 0;

                    // Insert range children. Randomly between 1~3.
                    int itemCount = new Random().nextInt(3) + 1;
                    for (int i = 0; i < itemCount; i++) {
                        // Get random number between 0 and 1. 0 to generate text child; 1 to generate image child.
                        if (new Random().nextInt(2) == 0)
                            groupList.get(groupIndex.getPosition()).children.add(childPosition + i, new TextChild("New Child"));
                        else
                            groupList.get(groupIndex.getPosition()).children.add(childPosition + i, new ImageChild(R.drawable.homer));
                    }

                    notifyChildRangedInserted(groupIndex.getPosition(), childPosition, itemCount);
                }
            });
            holder.dumpImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final GroupIndex groupIdx = getGroupIndex(holder.getAdapterPosition());
                    if (groupIdx == null)
                        return;

                    // Show a popup list with deletable group item count based on clicked group.
                    // Ex. Total group is 5, clicked on 3rd group, we show 1, 2, 3 options for deletion.
                    final int rangeDeletableGroupCount = groupList.size() - groupIdx.getPosition();
                    mLstPopWin.setAnchorView(v);
                    mLstPopWin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mLstPopWin.dismiss();
                            // Delete data start to remove from the end to avoid out of index exception.
                            int deleteItemCount = position + 1;
                            for (int i = groupIdx.getPosition() + deleteItemCount - 1; i >= groupIdx.getPosition(); i--) {
                                groupList.remove(i);
                            }
                            // Notify adapter.
                            notifyGroupRangedRemoved(groupIdx, deleteItemCount);
                        }
                    });
                    mLstPopWin.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return rangeDeletableGroupCount;
                        }

                        @Override
                        public Integer getItem(int position) {
                            return position + 1;
                        }

                        @Override
                        public long getItemId(int position) {
                            return 0;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView tv;
                            if (convertView == null)
                                tv = (TextView) getLayoutInflater().inflate(R.layout.popup_list_item_text, parent, false);
                            else
                                tv = (TextView) convertView;
                            tv.setText(String.valueOf(position + 1));
                            return tv;
                        }
                    });
                    mLstPopWin.show();
                }
            });
            return holder;
        }

        @Override
        public ChildViewHolder onCreateChildViewHolder(int viewType, ViewGroup parent) {
            final ChildViewHolder holder;
            switch (viewType) {
                case CHILD_VIEW_TYPE_IMAGE:
                    holder = new ChildImageViewHolder(parent);
                    break;
                case CHILD_VIEW_TYPE_TEXT:
                default:
                    holder = new ChildTextViewHolder(parent);
                    break;
            }
            holder.dumpImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ChildIndex childIdx = getChildIndex(holder.getAdapterPosition());
                    if (childIdx == null)
                        return;
                    final Group group = groupList.get(childIdx.getGroup().getPosition());

                    // Show a popup list with deletable children item count based on clicked child.
                    // Ex. Total children is 5, clicked on 3rd child, we show 1, 2, 3 options for deletion.
                    final int rangeDeletableChildrenCount = group.children.size() - childIdx.getPosition();
                    mLstPopWin.setAnchorView(v);
                    mLstPopWin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mLstPopWin.dismiss();
                            // Start to remove from the end to avoid out of index exception.
                            int deleteItemCount = position + 1;
                            for (int i = childIdx.getPosition() + deleteItemCount - 1; i >= childIdx.getPosition(); i--) {
                                group.children.remove(i);
                            }
                            notifyChildRangeRemoved(childIdx, deleteItemCount);
                        }
                    });
                    mLstPopWin.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return rangeDeletableChildrenCount;
                        }

                        @Override
                        public Integer getItem(int position) {
                            return position + 1;
                        }

                        @Override
                        public long getItemId(int position) {
                            return 0;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView tv;
                            if (convertView == null)
                                tv = (TextView) getLayoutInflater().inflate(R.layout.popup_list_item_text, parent, false);
                            else
                                tv = (TextView) convertView;
                            tv.setText(String.valueOf(position + 1));
                            return tv;
                        }
                    });
                    mLstPopWin.show();
                }
            });
            holder.dumpImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ChildIndex childIdx = getChildIndex(holder.getAdapterPosition());
                    if (childIdx != null) {
                        groupList.get(childIdx.getGroup().getPosition()).children.remove(childIdx.getPosition());
                        notifyChildRemoved(childIdx);
                    }
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindDataToGroupViewHolder(int position, Group dataItem, final GroupViewHolder holder) {
            holder.textView.setText(dataItem.text);
        }

        @Override
        public void onBindDataToChildViewHolder(final int groupPosition, final int childPosition, Child dataItem, final ChildViewHolder holder) {
            if (holder instanceof ChildTextViewHolder && dataItem instanceof TextChild) {
                ((ChildTextViewHolder) holder).textView.setText(((TextChild) dataItem).text);
            } else if (holder instanceof ChildImageViewHolder && dataItem instanceof ImageChild) {
                ((ChildImageViewHolder) holder).imageView.setImageResource(((ImageChild) dataItem).imgRestId);
            }
        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView addImageView;
            ImageView dumpImageView;

            public GroupViewHolder(ViewGroup parent) {
                super(getLayoutInflater().inflate(R.layout.list_item_text, parent, false));
                textView = (TextView) itemView.findViewById(R.id.tv1);
                addImageView = (ImageView) itemView.findViewById(R.id.iv_add);
                dumpImageView = (ImageView) itemView.findViewById(R.id.iv_dump);
                itemView.setBackgroundColor(Color.LTGRAY);
            }
        }

        abstract class ChildViewHolder extends RecyclerView.ViewHolder {
            ImageView dumpImageView;

            public ChildViewHolder(View itemView) {
                super(itemView);
            }
        }

        class ChildTextViewHolder extends ChildViewHolder {
            TextView textView;

            public ChildTextViewHolder(ViewGroup parent) {
                super(getLayoutInflater().inflate(R.layout.list_item_text, parent, false));
                textView = (TextView) itemView.findViewById(R.id.tv1);
                itemView.findViewById(R.id.iv_add).setVisibility(View.GONE);
                dumpImageView = (ImageView) itemView.findViewById(R.id.iv_dump);
            }
        }

        class ChildImageViewHolder extends ChildViewHolder {
            ImageView imageView;

            public ChildImageViewHolder(ViewGroup parent) {
                super(getLayoutInflater().inflate(R.layout.list_item_img, parent, false));
                imageView = (ImageView) itemView.findViewById(R.id.iv1);
                dumpImageView = (ImageView) itemView.findViewById(R.id.iv_dump);
            }
        }
    }
}
