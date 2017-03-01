package sunjc.materialdesigntest.smsList.controller;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.smsList.view.SMSComposeView;
import sunjc.materialdesigntest.util.BackableFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SMSComposeFragment extends Fragment {

    // Region: Event Bus setting
    EventBus mEventBus = EventBus.getDefault();

    @Override
    public void onResume(){
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

    SMSComposeView mView;

    public SMSComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = new SMSComposeView(inflater,container);
        return mView.getRootView();
    }


    BackableFragment motherFragment;
    public void setBackableFragment(BackableFragment b){
        motherFragment = b;
    }

    public void onEvent(SMSComposeView.SMScancelSendEvent e){
        motherFragment.goBack();
    }

    public void onEvent(SMSComposeView.SMSsendEvent e){
        Context context = getActivity().getApplicationContext();
        CharSequence text = "Message Sending...";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
