package sunjc.materialdesigntest.heartRate;

import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import sunjc.materialdesigntest.R;
import zx.Utility.DatabaseUtility.DbHelper;
import zx.Utility.DatabaseUtility.User;

/**
 * this fragment measures heart rate
 * Author: SunJc
 * in SG and NUS
 * Date: Aug. 2015
 * Reference and Thanks to: Justin Wetherell <phishman3579@gmail.com>
 * Libraries and Thanks to: Apache Math3
 * key algorithm of DSP in onPreviewFrame
 * you may not change other parts
 */

public class CameraCaptureFragment extends Fragment {

    //consts
    static final String DEBUG = "SunJcdebug";

    //consts about processing: parameters
    static final int sigWindowSize = 256;
    static final int hrWindowSize = 40;
    static final int minHR = 45;
    static final int maxHR = 170;
    static final double instantGap = 0.1;
    static final int offset = 8;
    static final int clWindowSize = 50;
    static final double currentRatio = 0.2;
    static final int reasonableMinAvg = 80;/////////////////////////////////////////////////////////////////////////////////////////
    static final int reasonableMaxAvg = 254;
    static final int reasonableMaxAvgGreen = 80;
    static final int changingTimes = 50;

    //measurement vars
    TextView textDisp_result;
    TextView textDisp_parameter;
    FigureDisp figureAbove;
    FigureDisp figureBelow;
    String disp;
    Queue<Integer> windowSignal;
    Queue<Long> windowTime;
    Queue<Integer> windowHeartRate;
    int hrCount;

    //CallBack
    OnMeasurementListener mOnMeasureListener;
    //camera and the surfaceview
    private SurfaceView mCameraSurfacePreview;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private long startTime;

    /*************************************
     * measurement and signal processing
     ************************************/
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            //get camera size
            if (data == null)
                throw new NullPointerException();
            Camera.Size size = camera.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();
            int width = size.width;
            int height = size.height;

            //get time and real-time camera data
            long nowTime = System.currentTimeMillis();
            int[] avgArr = ImageProcessing.decodeYUV420SPtoAvg(data.clone(), width, height);
            int imgAvg = avgArr[0];
            int imgAvgGreen = avgArr[1];

            /**********processing********/
            windowSignal.offer(imgAvg);
            windowTime.offer(nowTime);

            if (windowSignal.size() > sigWindowSize) {

                //remain window of a fixed size
                windowSignal.poll();
                startTime = windowTime.poll();

                /**
                 * here to filter
                 */

                //get basic aux vars
                //during time of this window
                double delay = (nowTime - startTime) / 1000.0;
                //sampleRate of this window
                double sampleRate = sigWindowSize / delay;
                //max frequency of corresponding frequency domain
                double freqDomRange = sampleRate / 2;
                //number of *intervals* in the corresponding frequency domain
                int freqDomLen = sigWindowSize / 2;
                //how many Hz(s) does one freqDom interval means
                double freqResolution = freqDomRange / freqDomLen;

                //sig: this window
                double[] sig = new double[windowSignal.size()];
                int count = 0;
                for (Integer it : windowSignal) {
                    sig[count] = it;
                    count++;
                }

                //FFT
                // here through test, we know that STANDARD or NORMAL, FORWARD or INVERSE don't matter!
                //ampInFreq: frequency domain, size: freqDomLen
                FastFourierTransformer ffter = new FastFourierTransformer(DftNormalization.STANDARD);
                final Complex[] freqDom = ffter.transform(sig, TransformType.FORWARD);
                double[] ampInFreq = new double[freqDomLen];
                for (int i = 0; i < freqDomLen; i++) {
                    ampInFreq[i] = freqDom[i + 1].abs();
                }

                //here to select one peak as heart rate

                //average heart rate in the small window
                int sumHR = 0;
                for (int i : windowHeartRate)
                    sumHR += i;
                int avgWindow = 0;
                if (windowHeartRate.size() < 3)
                    avgWindow = 70;
                else
                    avgWindow = (int) Math.ceil(sumHR / windowHeartRate.size());

                //simply find peak to help
                int freqIndex = findPeakIndex(ampInFreq);
                int aux_Simple = (int) Math.ceil(freqIndex * freqResolution * 60);

                if (aux_Simple < maxHR && aux_Simple > minHR) {
                    //   avgWindow = (int)(avgWindow * 0.7 + 0.3*aux_Simple);
                }

                //find all possible peaks
                if (ampInFreq.length < 3)
                    return;
                int pqSize = (int) Math.floor((maxHR - minHR) / (freqResolution * 60)) + 2;
                PriorityQueue<peakPoint> peaks = new PriorityQueue<peakPoint>(pqSize,
                        new Comparator<peakPoint>() {
                            @Override
                            public int compare(peakPoint lhs, peakPoint rhs) {
                                return (int) (rhs.ampVal - lhs.ampVal);
                            }
                        });
                for (int i = 1; i < ampInFreq.length - 1; i++) {
                    if (ampInFreq[i - 1] < ampInFreq[i] && ampInFreq[i + 1] < ampInFreq[i]) {
                        int thisHR = (int) Math.floor(i * freqResolution * 60);
                        if (thisHR > minHR && thisHR < maxHR) {
                            peaks.offer(new peakPoint(thisHR, ampInFreq[i]));
                        } else if (thisHR > maxHR) {
                            break;
                        }
                    }
                }

                //$ debug
                int test_actual = -1;
                //$

                int heartRate = avgWindow;
                while (!peaks.isEmpty()) {
                    peakPoint p = peaks.poll();
                    if (Math.abs(p.hrVal - avgWindow) < avgWindow * instantGap) {
                        heartRate = p.hrVal;

                        //$ debug
                        test_actual = heartRate;
                        //$

                        break;
                    }
                }


                // keep and clear history effect when needed
                if (hrCount > clWindowSize) {
                    windowHeartRate.clear();
                    hrCount = 0;
                }
                hrCount++;

                windowHeartRate.offer(heartRate);
                if (windowHeartRate.size() > hrWindowSize)
                    windowHeartRate.remove();


                //get the final result and pass it back to upper layer
                heartRate = (int) Math.ceil((currentRatio) * heartRate + (1 - currentRatio) * avgWindow);
                heartRate += offset;
                if (imgAvg < reasonableMinAvg || imgAvg > reasonableMaxAvg || imgAvgGreen > reasonableMaxAvgGreen)
                    mOnMeasureListener.onNotOnFinger();
                else if (hrCount % changingTimes == 0)
                    mOnMeasureListener.onMeasurementCallback(heartRate);
                /*
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String tableName = User.getUsername().split("@")[0] + User.getUsername().split("@")[1].split("\\.")[0];

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());

                DbHelper.updateTestData(tableName, date + "#" + String.valueOf(heartRate));

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                */
                //output result for debug
                //Log.d("sunjc-debug","freq"+heartRate);
                String r = "";
                r += "cur_sig_val: " + imgAvg;
                r += "\ncur_sig_val_green: " + imgAvgGreen;
                String nn = Double.toString(sampleRate);
                r += "\nsample_rate: " + nn.substring(0, Math.min(5, nn.length() - 1));
                r += "\npeak_find: " + test_actual;
                r += "\nsimple_peak_find: " + aux_Simple;
                r += "\naverage_HR: " + avgWindow;
                textDisp_result.setText(r);
                //draw real-time figure
                figureAbove.set(ampInFreq, Color.rgb(0, 131, 143));
                figureAbove.invalidate();
                figureBelow.set(sig, Color.rgb(134, 94, 155));
                figureBelow.invalidate();
            }
        }
    };


    /*******************************************************
     * no modifying at following part
     *******************************************************/
    private SurfaceHolder.Callback mCameraSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("sunjcdebug", "SurfaceHolder.Callback：Surface Created");
            try {
                //Log.i(DEBUG, "SurfaceHolder.Callback：surface Created");
                mCamera.setPreviewDisplay(mSurfaceHolder);//set the surface to be used for live preview
                mCamera.setPreviewCallback(mPreviewCallback);
            } catch (Exception ex) {
                if (null != mCamera) {
                    mCamera.release();
                    mCamera = null;
                }
                Log.i("sunjcDEBUG", "CATCHED EX WHEN SET PREVIEW WITH CAMERA" + ex.getMessage());
            }
            Log.d("sunjcdebug", "SurfaceHolder.Callback：Surface Created end");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i("SUNJCDEBUG", "SurfaceHolder.Callback：Surface Changed");
            initCamera(width, height);
            Log.i("SUNJCDEBUG", "SurfaceHolder.Callback：Surface Changed end");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(DEBUG, "SurfaceHolder.Callback：Surface Destroyed");
            Log.i(DEBUG, "SurfaceHolder.Callback：Surface Destroyed end");
        }
    };

    /*************
     * fragment and camera surface view stuff
     ********/
    public CameraCaptureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("sunjcdebug", "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_camera_capture_frame, container, false);

        initProcessView(rootView);

        initCameraSurfacePreview(rootView);

        Log.d("sunjcdebug", "onCreateView end");

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d("sunjcdebug", "onResume");

        super.onResume();

        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            mCamera = Camera.open();

            getParentFragment().getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } catch (Exception e) {
            //Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();

        Log.d("sunjcdebug", "onResume end");
    }


    @Override
    public void onPause() {
        Log.d("sunjcdebug", "onPause");
        super.onPause();
        //Log.i(DEBUG, "pause");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("sunjcdebug", "onPause end");
    }

    private void initCameraSurfacePreview(View v) {
        mCameraSurfacePreview = (SurfaceView) v.findViewById(R.id.cameraPreview);
        mSurfaceHolder = mCameraSurfacePreview.getHolder();
        mSurfaceHolder.addCallback(mCameraSurfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initProcessView(View v) {
        textDisp_result = (TextView) v.findViewById(R.id.textDisp);
        textDisp_parameter = (TextView) v.findViewById(R.id.textParameter);
        figureAbove = (FigureDisp) v.findViewById(R.id.figureAbove);
        figureBelow = (FigureDisp) v.findViewById(R.id.figureBelow);


        windowSignal = new LinkedList<Integer>();
        windowTime = new LinkedList<Long>();
        windowHeartRate = new LinkedList<Integer>();
        hrCount = 0;


        String p = "";
        p += "sigWindowSize=" + sigWindowSize;
        p += "\nhrWindowSize=" + hrWindowSize;
        p += "\nminHR=" + minHR;
        p += "\nmaxHR=" + maxHR;
        p += "\ninstantGap=" + instantGap;
        p += "\noffset=" + offset;
        p += "\nclWindowSize=" + clWindowSize;
        p += "\ncurrentRatio=" + currentRatio;
        textDisp_parameter.setText(p);
    }

    private void initCamera(int width, int height) {//call in surfaceChanged
        Log.i("SUNJCDEBUG", "going into initCamera");
        if (null != mCamera) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    //Log.d(DEBUG, "Using width=" + size.width + " height=" + size.height);
                }
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    int findPeakIndex(double[] s) {
        if (s.length >= 3) {
            double max = -20000;
            int index = -1;
            for (int i = 1; i < s.length - 1; i++) {
                if (s[i - 1] < s[i] && s[i + 1] < s[i]) {
                    if (s[i] > max) {
                        max = s[i];
                        index = i;
                    }
                }
            }
            return index;
        } else {
            return -1;
        }
    }

    void setmOnMeasureListner(OnMeasurementListener listener) {
        mOnMeasureListener = listener;
    }

    interface OnMeasurementListener {
        void onMeasurementCallback(int heartRate);

        void onNotOnFinger();
    }

    class peakPoint {
        int hrVal;
        double ampVal;

        public peakPoint(int hrVal, double ampVal) {
            this.ampVal = ampVal;
            this.hrVal = hrVal;
        }
    }
}
