package sunjc.materialdesigntest.smsList.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;

/**
 * Created by SunJc on Feb/19/16.
 */
public class SMSRecyclerView implements ViewMVC {

    View mRootView;

    FloatingActionButton mComposeFab;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public SMSRecyclerView(LayoutInflater inflater, ViewGroup container,Activity a) {
        mRootView = inflater.inflate(R.layout.sms_list,container,false);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.smsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(a);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mComposeFab = (FloatingActionButton) mRootView.findViewById(R.id.floatingActionButtonCompose);
        mComposeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the event bus
                EventBus eventBus = EventBus.getDefault();
                // Post a new event to the bus
                eventBus.post(new composeFabEvent());
            }}
        );
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter<?> a){
        Log.i("sunjcdebug","set adapter");
        mRecyclerView.setAdapter(a);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }

    public static class composeFabEvent{}
}
