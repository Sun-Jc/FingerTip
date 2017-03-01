package sunjc.materialdesigntest.heartRate;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.drawerMain.view.LeftView;
import zx.Utility.NetworkUtility.Upload;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRateFragment extends Fragment {

    private static final int WINDOW = 4;

    private int mCount;
    private int mHeartRateVal;
    private ProgressBar mMeasureProgress;
    private UploadFragment mUploadFragment;
    private Boolean isValid;

    private View rootView;

    private CameraCaptureFragment mCameraCaptureFragment;
    private TextView mTestText;

    private ProgressBar mProgessBar;

    private boolean onMeasuring;

    public HeartRateFragment() {
        // Required empty public constructor
    }

    FloatingActionButton mCancelBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.heart_rate_fragment,container,false);

        mTestText=(TextView)rootView.findViewById(R.id.testText);

        mProgessBar = (ProgressBar)rootView.findViewById(R.id.progressBar);

        mMeasureProgress = (ProgressBar) rootView.findViewById(R.id.measureProgess);

        mCancelBtn = (FloatingActionButton) rootView.findViewById(R.id.cancelButton);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus eventBus = EventBus.getDefault();
                // Post a new event to the bus
                eventBus.post(new LeftView.ListItemClickEvent(0, 0));
            }
        });

        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();

        startM();
    }

    private void startM(){
        View mask = (View) rootView.findViewById(R.id.mask);
        mask.setVisibility(View.VISIBLE);
        mTestText.setVisibility(View.GONE);
        mMeasureProgress.setVisibility(View.GONE);

        createCaptureFragment();

        mCount = 0;
        isValid = true;
        UpdateProgress updateProgress = new UpdateProgress();
        updateProgress.start();

        onMeasuring = false;
    }


    private void displayText(){
        mProgessBar.setVisibility(View.GONE);
        //mTestText.setVisibility(View.VISIBLE);
        mMeasureProgress.setVisibility(View.VISIBLE);
    }


    private void createCaptureFragment() {

        Log.i("sunjcdebug", "create fragment in hrf");

        FragmentManager fManager = getChildFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();


        if (mCameraCaptureFragment != null) {
            fTransaction.remove(mCameraCaptureFragment);
            fTransaction.commit();
            mCameraCaptureFragment = null;
        }

        //here create an instance of CCF to measure heart rate
        mCameraCaptureFragment = new CameraCaptureFragment();
        //here
        mCameraCaptureFragment.setmOnMeasureListner(
                new CameraCaptureFragment.OnMeasurementListener() {
                    @Override
                    public void onMeasurementCallback(int heartRate) {


                        if (!onMeasuring){
                            onMeasuring = true;
                            displayText();
                        }
                        //Log.i("sunjcdebug","on m");

                        if (mCount < WINDOW){
                            mCount++;
                            //Log.i("SUNJCDEBUG",mCount+"tick");
                            mHeartRateVal += heartRate;
                        }else{
                            mesaureFinish();
                        }

                        //mTestText.setText(heartRate + " BPM");
                        //mHeartRateVal = heartRate;
                    }
                    @Override
                    public void onNotOnFinger(){
                        if (!onMeasuring){
                            onMeasuring = true;
                            displayText();
                        }
                        mCount++;
                        isValid = false;
                        mTestText.setText("please put finger on camera");
                    }
                }
        );

        // Adding the new fragment
        fTransaction.replace(R.id.mainHRContainer, mCameraCaptureFragment);
        fTransaction.commit();
    }

    void setMeasurementProgress(){
        mMeasureProgress.setProgress((int)(100.0 / WINDOW * mCount));
    }

    private class UpdateProgress extends Thread{
        @Override
        public void run() {
            super.run();
            while(WINDOW > mCount ){
                if(mMeasureProgress.getVisibility() == View.VISIBLE )
                    setMeasurementProgress();
            }
        }
    }

    void mesaureFinish() {
        if (isValid) {
            mHeartRateVal = mHeartRateVal / WINDOW;
        } else{
            mHeartRateVal = -1;
        }

        View mask = (View) rootView.findViewById(R.id.mask);
        mask.setVisibility(View.GONE);
        mMeasureProgress.setVisibility(View.GONE);


        FragmentManager fManager = getChildFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        mUploadFragment = new UploadFragment();
        mUploadFragment.setAvgHeartRate(mHeartRateVal);
        fTransaction.replace(R.id.mainHRContainer, mUploadFragment);
        fTransaction.commit();
    }


    private void destroy(){
        FragmentManager fManager = getChildFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();

        if (mCameraCaptureFragment != null) {
            fTransaction.remove(mCameraCaptureFragment);
            fTransaction.commit();
            mCameraCaptureFragment = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("sunjcdebug", "pause father frag");
        destroy();
    }


}
