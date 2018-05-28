package com.cuong.filemanage;

import java.text.DecimalFormat;

/**
 * Created by Cuong on 3/29/2018.
 */

public class Utils {
    static public String size(long size){
        String hrSize = "";
        DecimalFormat dec = new DecimalFormat("0.0");
        double m = size/1024.0;
        if (m<1)   {
            return dec.format(size).concat(" Byte");
        }
        if (m>1&&m<1024){
            return dec.format(m).concat(" Kb");
        }
        m = m/1024;
        if (m <1024) {
            return dec.format(m).concat(" Mb");
        }
        m = m/1024;
        return dec.format(m).concat(" Gb");
    }
}
