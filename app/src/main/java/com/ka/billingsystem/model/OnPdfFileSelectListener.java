package com.ka.billingsystem.model;

import java.io.File;

public interface OnPdfFileSelectListener {
    void onpdfSelected(File file, String mPbillno,String filename);
    void image(String image);


}
