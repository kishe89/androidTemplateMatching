package com.example.kjw.opencvtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.AlignExposures;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{

    private Mat img_input;
    private Mat img_result;
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private String path;
    private File output;
    private File dir;

    public native int convertNativeLib(long matAddrInput, long matAddrResult);
    public native int MatchingMethod(long matAddrInput, long matAddrResult);
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {"android.permission.CAMERA"};

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean camreaAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!camreaAccepted  )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }else
                        {
                            //이미 사용자에게 퍼미션 허가를 받음.
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  MainActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir = new File(path+"/imageprocess");
        if( !dir.exists() ) {
            Log.e(TAG,"CREATE DIRECTORY : "+dir.getAbsolutePath());
            dir.mkdirs();
        }
        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        img_input = inputFrame.rgba();
        img_result = new Mat();
        Mat copyImg = new Mat();
        Mat templateCopy = new Mat();
        img_input.copyTo(copyImg);
        Imgproc.cvtColor(copyImg, copyImg, Imgproc.COLOR_BGR2GRAY);
        copyImg.convertTo(copyImg,CvType.CV_32FC1);

        Mat imageUri = null;
        try {
            imageUri = Utils.loadResource(this, R.drawable.number0, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            imageUri.copyTo(templateCopy);
            Imgproc.cvtColor(templateCopy, templateCopy, Imgproc.COLOR_BGR2GRAY);
            templateCopy.convertTo(templateCopy,CvType.CV_32FC1);

            output = new File(dir+"/output"+ Calendar.getInstance().getTime()+".jpeg");
            run(copyImg,templateCopy,output.getAbsolutePath(),Imgproc.TM_CCORR_NORMED);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        convertNativeLib(img_input.getNativeObjAddr(), img_result.getNativeObjAddr());
        return img_input;
    }
    public void run(Mat inFile, Mat templateFile, String outFile, int match_method) {
        Log.e(TAG,"\nRunning Template Matching");

        Mat img = inFile;
        Mat templ = templateFile;

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        Log.e(TAG,"result_cols : "+result_cols+"\nimg_cols : "+img.cols()+"\ntempl_cols : "+templ.cols());
        int result_rows = img.rows() - templ.rows() + 1;
        Log.e(TAG,"result_rows : "+result_cols+"\nimg_rows : "+img.rows()+"\ntempl_rows : "+templ.rows());
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        Log.e(TAG,"final output mat rows : "+result.rows()+"\nfinal output mat cols : "+result.cols());
        // / Do the Matching and Normalize

        Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0));

        // Save the visualized detection.
        Log.e(TAG,"Writing "+ outFile);
        if(!output.exists())
        Imgcodecs.imwrite(outFile, img);

    }
}
