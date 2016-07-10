package lab.royliao.android.expandablerecycleviewadapterexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lab.royliao.android.expandablerecyeviewadapter.ExpandableRecycleViewBaseAdapter;

public class MultiViewTypesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Group {
        String text;
    }

    private class Child {
        String text;
    }

    private class MultiViewTypeRecycleViewAdapter extends ExpandableRecycleViewBaseAdapter {

        public static final int GROUP_TYPE_TEXT = 1;
        public static final int GROUP_TYPE_IMAGE = 2;

        @Override
        public Group getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Child getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public int getGroupCount() {
            return 0;
        }

        @Override
        public boolean initialExpanded(int group) {
            return false;
        }

        @Override
        public int getChildrenCount(int group) {
            return 0;
        }

        @Override
        public int getGroupViewType(int group) {
            return group % 2 == 0 ? GROUP_TYPE_TEXT : GROUP_TYPE_IMAGE;
        }

        @Override
        public int getChildViewType(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateGroupViewHolder(int viewType, ViewGroup parent) {
            return new GroupViewHolder(parent);
        }

        @Override
        public RecyclerView.ViewHolder onCreateChildViewHolder(int viewType, ViewGroup parent) {
            return new ChildViewHolder(parent);
        }

        @Override
        public void onBindGroupViewHolder(int position, Object dataItem, RecyclerView.ViewHolder holder) {

        }

        @Override
        public void onBindChildViewHolder(int groupPosition, int childPosition, Object dataItem, RecyclerView.ViewHolder holder) {

        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public GroupViewHolder(ViewGroup parent) {
                super(getLayoutInflater().inflate(R.layout.list_item_text, parent, false));
                textView = (TextView) itemView.findViewById(R.id.tv1);
            }
        }

        class ChildViewHolder extends RecyclerView.ViewHolder {

            public ChildViewHolder(ViewGroup parent) {
                super(getLayoutInflater().inflate(R.layout.list_item_text, parent, false));
            }
        }
    }
}
