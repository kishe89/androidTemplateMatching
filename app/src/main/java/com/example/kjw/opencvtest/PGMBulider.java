package com.example.kjw.opencvtest;

import android.os.Environment;
import android.util.Log;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kjw on 2017. 2. 1..
 */

public class PGMBulider {
    private PGMInFo pgmInFo;
    private final String TAG = "PGMBulider";
    private final String magicNumber_CLASSIC = "P5";
    private final String magicNumber_PLANE = "P2";
    private final String whiteSpace = " ";
    private final String LFCR = "\n";
    private final String SingleWhieteSpace = "";
    private static final Integer PLANE_LINELIMIT = 70;
    private static final Integer CLASSIC_LINELIMIT = -1;
    private String header;
    private String pgm;
    private Integer width;
    private Integer height;
    private Integer lineLimit;
    private Integer gray_value;
    private String path;
    private File dir;
    private File output;

    public PGMBulider(Integer width, Integer height,PGMInFo pgmInFo) {
        this.width = width;
        this.height = height;
        this.pgmInFo = pgmInFo;
        this.header = makeHeader(this.pgmInFo);
        this.lineLimit = makeLineLimit(this.pgmInFo);
    }

    private String makeHeader(PGMInFo pgmInFo) {
        String header = "";
        if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_CLASSIC){
            header += magicNumber_CLASSIC + whiteSpace + this.width.toString() +
                    whiteSpace + this.height.toString() + whiteSpace +
                    pgmInFo.getMaxValue().toString()+LFCR;
        }else if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_PLANE){
            header += magicNumber_PLANE + whiteSpace + this.width.toString() +
                    whiteSpace + this.height.toString() + whiteSpace +
                    pgmInFo.getMaxValue().toString()+LFCR;
        }
        return header;
    }
    private Integer makeLineLimit(PGMInFo pgmInFo) {
        Integer lineLimit = 0;
        if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_CLASSIC){
            this.lineLimit = CLASSIC_LINELIMIT;
        }else if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_PLANE){
            this.lineLimit = PLANE_LINELIMIT;
        }
        return lineLimit;
    }

    public void setPgm(Mat pgm) {

        int size = (int)pgm.total();
        double [] arr = new double[1];
        ArrayList<Double>pixel = new ArrayList<Double>();
        Log.e(TAG,"IMAGE SIZE : "+size);
        this.pgm = header;
        if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_CLASSIC){
            for (int y = 0; y < pgm.rows(); y++){
                for(int x = 0; x < pgm.cols(); x++)
                {
                    double[] temp = pgm.get(y, x);
                    Log.e(TAG,"PIXEL VALUE : "+temp[0]);
                    this.pgm += (int)temp[0] + whiteSpace;
                    this.pgm += SingleWhieteSpace;
                }
                this.pgm+= LFCR;
            }
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            dir = new File(path+"/imageprocess");
            output = new File(dir+"/output"+ Calendar.getInstance().getTime()+".pgm");
            try {
                output.createNewFile();
                writeFile(output,this.pgm.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(pgmInFo.getType() == PGMInFo.PGM_FORMAT_PLANE){
            for (int y = 0; y < pgm.rows(); y++){
                for(int x = 0; x < pgm.cols(); x++)
                {
                    double[] temp = pgm.get(y, x);
                    Log.e(TAG,"PIXEL VALUE : "+temp[0]);
                    this.pgm += (int)temp[0] + whiteSpace;
                    this.pgm += SingleWhieteSpace;
                }
                this.pgm+= LFCR;
            }
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            dir = new File(path+"/imageprocess");
            output = new File(dir+"/output"+ Calendar.getInstance().getTime()+".pgm");
            try {
                output.createNewFile();
                writeFile(output,this.pgm.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    /**
     * 파일에 내용 쓰기
     * @param file
     * @param file_content
     * @return
     */
    private boolean writeFile(File file , byte[] file_content){
        boolean result;
        FileOutputStream fos;
        if(file!=null&&file.exists()&&file_content!=null){
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
