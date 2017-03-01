package sunjc.materialdesigntest.smsList.controller;


import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.MainActivity;
import sunjc.materialdesigntest.drawerMain.controller.MainFrame;
import sunjc.materialdesigntest.smsList.model.SMSinfoModel;
import sunjc.materialdesigntest.smsList.view.SMSRecyclerView;
import sunjc.materialdesigntest.util.BackableFragment;
import sunjc.materialdesigntest.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SMSRecyclerFragment extends Fragment {

    SMSRecyclerView mView;
    SMSRVAdapter mSmsRVAdapter;

    LinkedList<SMSinfoModel> smsInfo = null;


    // Region: get main frame and main activity
    MainFrame rootMainFrame;
    MainActivity rootActivity;

    Object syncLock = new Object();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        rootMainFrame = ((MainActivity) activity).getMainFrame();
        // End of Region: get main frame and main activity

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (syncLock) {
                    getSMSDataAndAdapter();
                    syncLock.notify();
                    Log.i("sunjcdebug", "lock released");
                }
            }
        }).start();
    }

    public SMSRecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        Log.i("sunjcdebug", "fragment created");

        if (null == mView) {
            mView = new SMSRecyclerView(inflater, container, getActivity());

            synchronized (syncLock) {
                try {
                    Log.i("sunjcdebug", "inside");
                    if (mSmsRVAdapter == null) {
                        Log.i("sunjcdebug", "waiting");
                        syncLock.wait();
                    }
                    Log.i("sunjcdebug", "get the lock x:" + smsInfo.size());
                    mView.setRecyclerViewAdapter(mSmsRVAdapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i("sunjcdebug", "fr");
        }
        //else{
        //    Log.i("sunjcdebug","ok with view");
        //}
        return mView.getRootView();
    }

    // Region: Event Bus setting
    EventBus mEventBus = EventBus.getDefault();

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister this fragment from the EventBus
        mEventBus.unregister(this);
    }
    // End of Region: Event Bus setting

    void getSMSDataAndAdapter() {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        smsInfo = new LinkedList<SMSinfoModel>();
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            int count = 0;
            do {
                smsInfo.add(
                        new SMSinfoModel(
                                cursor.getString(cursor.getColumnIndex("address")),
                                Utils.convertToHumanReadableDate(cursor.getString(cursor.getColumnIndex("date")))));
                count++;
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }

        SMSRVAdapter.SetLayoutInflater(getActivity());
        mSmsRVAdapter = new SMSRVAdapter(smsInfo);
        Log.i("sunjcdebug", "sms got");
    }

    private void openComposeFragment() {
        SMSComposeFragment composeFragment = new SMSComposeFragment();
        composeFragment.setBackableFragment(new BackableFragment() {
            @Override

            public void goBack() {
                rootMainFrame.switchHomeFromContent();
            }
        });
        rootMainFrame.switchContentFragment(composeFragment);
    }

    public void onEvent(SMSRecyclerView.composeFabEvent e) {
        openComposeFragment();
    }
}
