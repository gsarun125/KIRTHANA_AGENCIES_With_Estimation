package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ka.billingsystem.R;
import java.util.List;

public class UserseclectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private SelectionListener listener;
    private List<String> mUsername;
    private List<String> mUserid;
    private List<String> mGen_Date;

    public UserseclectionAdapter(Context context, SelectionListener listener, List<String> mUsername, List<String> mUserid, List<String> mGen_Date) {
        this.context = context;
        this.listener = listener;
        this.mUsername = mUsername;
        this.mUserid = mUserid;
        this.mGen_Date = mGen_Date;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserseclectionHolder(LayoutInflater.from(context).inflate(R.layout.user_selection_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mUsername != null && mUsername.size() > 0) {

            UserseclectionHolder userseclectionHolder = (UserseclectionHolder) holder;
            userseclectionHolder.userid.setText(mUserid.get(position).toString());
            if (!mGen_Date.get(position).toString().equals("0")) {
                userseclectionHolder.generated_date.setText(mGen_Date.get(position).toString());
            } else {
                userseclectionHolder.generated_date.setText("");
            }
            holder.itemView.setOnClickListener(view -> {
                listener.onpdfSelected(mUserid.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mUsername != null && mUsername.size() > 0) {
            return mUsername.size();
        } else {
            return 1; // Return 1 to show the empty state view
        }
    }
}
