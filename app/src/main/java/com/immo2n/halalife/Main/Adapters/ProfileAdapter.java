package com.immo2n.halalife.Main.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.immo2n.halalife.Main.DataObjects.ProfileGrids;
import com.immo2n.halalife.R;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.GridViewHolder> {

    private final List<ProfileGrids> itemList;
    private final Context context;
    ViewGroup parent;

    public ProfileAdapter(Context context, List<ProfileGrids> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_card_holder, parent, false);
        this.parent = parent;
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.mainCardBody.removeAllViews(); //NEVER EVER FORGET THIS LINE, IT CAUSES POSITION ERRORS AND GHOSTLY BEHAVIOUR
        ProfileGrids item = itemList.get(position);
        View view;
        if(item.isInfoGreed()){
            //Show the profile info
            view = LayoutInflater.from(context).inflate(R.layout.profile_main_grid, parent, false);
            //Do process info


            //Free loading
        }
        else {
            //Its a post
            view = LayoutInflater.from(context).inflate(R.layout.posts, parent, false);
            //Do process


            //Free loading
        }
        holder.mainCardBody.addView(view);
        freeLoading(holder);
    }

    private void freeLoading(GridViewHolder holder){
        //Free loading
        holder.loading.setVisibility(View.GONE);
        holder.mainCardBody.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainCardBody, loading;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            mainCardBody = itemView.findViewById(R.id.mainCardBody);
            loading = itemView.findViewById(R.id.loading);
        }
    }
}