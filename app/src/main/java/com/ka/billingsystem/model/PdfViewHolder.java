package com.ka.billingsystem.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;


public class PdfViewHolder extends RecyclerView.ViewHolder {

    public CardView container;
    public TextView IVbillno;
    public TextView IVDate;
    public TextView IVuser;

    public TextView IVCusName;
    public TextView IVcuspno;
    public  TextView IVtime;
    ImageView Printerimg;

    public PdfViewHolder(@NonNull View itemView) {
        super(itemView);
        container=itemView.findViewById(R.id.container1);
        IVbillno=itemView.findViewById(R.id.IVBillNo);
        IVDate=itemView.findViewById(R.id.IVDate);
        IVtime=itemView.findViewById(R.id.IVTime);
        IVuser=itemView.findViewById(R.id.IVUser);
        IVCusName=itemView.findViewById(R.id.IVCusName);
        IVcuspno=itemView.findViewById(R.id.IVcuspno);
        Printerimg=itemView.findViewById(R.id.printerimg);
    }
}
