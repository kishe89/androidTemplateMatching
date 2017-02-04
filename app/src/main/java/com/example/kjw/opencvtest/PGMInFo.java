package com.example.kjw.opencvtest;

/**
 * Created by kjw on 2017. 2. 1..
 */

public class PGMInFo {
    public static final int ONE_BYTE_PGM = 1;
    public static final int TWO_BYTE_PGM = 2;
    public static final int ONE_BYTE_TYPE_MAX_VALUE = 256;
    public static final int TWO_BYTE_TYPE_MAX_VALUE = 65536;
    public static final int PGM_FORMAT_CLASSIC = 1;
    public static final int PGM_FORMAT_PLANE = 2;
    private static final String NOT_ALLOWED_TYPE = "NOT ALLOWED TYPE EXCEPTION, You must given PGM_FORMAT_CLASSIC or PGM_FORMAT_PLANE";
    private static final String NOT_ALLOWED_SIZE_TYPE = "NOT ALLOWED TYPE EXCEPTION, You must given ONE_BYTE_PGM or TWO_BYTE_PGM";
    private Integer size_type;
    private Integer maxValue;
    private Integer type;
    public PGMInFo(Integer type,Integer size_type) throws PGMException {
        this.type = type;
        this.size_type = size_type;
        checkAvailability();
    }

    private void checkAvailability() throws PGMException {
        if(this.type == PGM_FORMAT_CLASSIC){
            if(this.size_type == ONE_BYTE_PGM){
                this.maxValue = ONE_BYTE_TYPE_MAX_VALUE;
            }else if(this.size_type == TWO_BYTE_PGM){
                this.maxValue = TWO_BYTE_TYPE_MAX_VALUE;
            }else{
                throw new PGMException(NOT_ALLOWED_SIZE_TYPE);
            }

        }else if(this.type == PGM_FORMAT_PLANE){
            if(this.size_type == ONE_BYTE_PGM){
                this.maxValue = ONE_BYTE_TYPE_MAX_VALUE;
            }else if(this.size_type == TWO_BYTE_PGM){
                this.maxValue = TWO_BYTE_TYPE_MAX_VALUE;
            }else{
                throw new PGMException(NOT_ALLOWED_SIZE_TYPE);
            }
        }else{
            throw new PGMException(NOT_ALLOWED_TYPE);
        }
    }

    public Integer getSize_type() {
        return size_type;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public Integer getType() {
        return type;
    }
}
