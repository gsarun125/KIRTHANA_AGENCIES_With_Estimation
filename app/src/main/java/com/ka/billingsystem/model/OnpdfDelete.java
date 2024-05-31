package com.ka.billingsystem.model;

import java.io.File;

public interface OnpdfDelete {
    void onpdfSelected(File file, String mPbillno, String filename);
    void Undo(String mPbillno);
    void image(String image);
}
