package sunjc.materialdesigntest.smsList.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;

/**
 * Created by SunJc on Feb/19/16.
 */
public class SMSComposeView implements ViewMVC {

    View mRootView;

    FloatingActionButton mSendButtton;
    FloatingActionButton mCancelButton;

    public SMSComposeView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.sms_compose,container,false);

        mSendButtton = (FloatingActionButton)mRootView.findViewById(R.id.sendFAB);
        mCancelButton = (FloatingActionButton)mRootView.findViewById(R.id.cancelFAB);

        mSendButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the event bus
                EventBus eventBus = EventBus.getDefault();
                // Post a new event to the bus
                eventBus.post(new SMSsendEvent());
            }
        });
        
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the event bus
                EventBus eventBus = EventBus.getDefault();
                // Post a new event to the bus
                eventBus.post(new SMScancelSendEvent());
            }
        });
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }

    public static class SMSsendEvent{
    }

    public static class SMScancelSendEvent{}
}
