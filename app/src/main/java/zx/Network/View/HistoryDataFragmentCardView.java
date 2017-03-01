package zx.Network.View;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sunjc.materialdesigntest.R;

/**
 * Created by Lesley on 2016/3/7.
 */
public class HistoryDataFragmentCardView extends Fragment{
    View mRootView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> mDataset;
    private ArrayList<Item> items;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initData();
        mRootView=  inflater.inflate(R.layout.history_data,container,false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.history_data_recyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(new MyAdapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        Log.i("zxdebug", "fragment created");

        return mRootView;
    }

    protected void initData()
    {
        items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            items.add(new Item("Item" + i, "This is the Item number" + i));
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public MyAdapter() {
        }
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).from(parent.getContext()).inflate(R.layout.history_data_item_cardview, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Item item = items.get(position);
            viewHolder.maintitle.setText(item.getTitle());
            viewHolder.subtitle.setText(item.getSubtitle());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView maintitle;
            TextView subtitle;

            public ViewHolder(View itemView) {
                super(itemView);
                subtitle = (TextView) itemView.findViewById(R.id.subtitle);
                maintitle = (TextView) itemView.findViewById(R.id.maintitle);

            }
        }
    }
}
