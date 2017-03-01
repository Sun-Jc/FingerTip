package sunjc.materialdesigntest.heartRate;


import android.app.Fragment;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.smsList.controller.SMSComposeFragment;
import sunjc.materialdesigntest.smsList.view.SMSComposeView;
import sunjc.materialdesigntest.util.BackableFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    private static final int DURTIME = 2;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private TextView mTextView;
    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.before_measurement, container, false);
        mTextView = (TextView) rootView.findViewById(R.id.notice);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.timerProgress);

        Timer timer = new Timer();
        timer.start();

        return rootView;
    }

    private Handler mTimerProgressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressBar.setProgress(msg.arg1);
        }
    };

    BackableFragment motherFragment;
    public void setBackableFragment(BackableFragment b){
        motherFragment = b;
    }

    private class Timer extends Thread{

        @Override
        public void run() {
            super.run();
            int timer = 0;
            double PerProgress = 1.0 / DURTIME;
            int FINAL = DURTIME * 100;
            while( timer < FINAL ) {
                try {
                    Thread.sleep(10);
                    timer ++;
                    Message msg = new Message();
                    msg.arg1 = (int) (timer * PerProgress);
                    mTimerProgressHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timer = 0;
            while( timer < 10 ) {
                try {
                    Thread.sleep(10);
                    timer ++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            motherFragment.goBack();
        }
    }

}
