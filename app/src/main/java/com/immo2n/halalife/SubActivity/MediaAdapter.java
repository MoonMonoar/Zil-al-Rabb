package com.immo2n.halalife.SubActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.immo2n.halalife.Custom.FileUtils;
import com.immo2n.halalife.R;

import java.io.File;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.FileViewHolder>{
    private final List<File> fileList;
    private final Context context;
    private TextView title;

    public MediaAdapter(List<File> fileList, Context context, TextView title) {
        this.fileList = fileList;
        this.context = context;
        this.title = title;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_list, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = fileList.get(position);
        //Reset needed!
        holder.videoLength.setVisibility(View.GONE);
        holder.selectedSymbol.setVisibility(View.GONE);
        holder.relativeLayout.setBackground(null);
        //Check if selected already
        if(Media.selectedFiles.contains(file)){
            holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.media_selected));
            holder.selectedSymbol.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(file.getAbsolutePath())
                .centerCrop()
                .placeholder(R.drawable.file_placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);
        if(FileUtils.isVideoFile(file)){
            holder.videoLength.setText(FileUtils.getVideoDuration(file.getAbsolutePath()));
            holder.videoLength.setVisibility(View.VISIBLE);
        }
        holder.relativeLayout.setOnClickListener(view -> {
            if(Media.selectedFiles.contains(file)){
                //Remove
                Media.selectedFiles.remove(file);
                holder.relativeLayout.setBackground(null);
                holder.selectedSymbol.setVisibility(View.GONE);
            }
            else {
                //Add
                Media.selectedFiles.add(file);
                holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.media_selected));
                holder.selectedSymbol.setVisibility(View.VISIBLE);
            }
            if(null != title){
                Media.updateCount(title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, selectedSymbol;
        RelativeLayout relativeLayout;
        TextView videoLength;
        public FileViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            relativeLayout = itemView.findViewById(R.id.lin);
            videoLength = itemView.findViewById(R.id.videoLength);
            selectedSymbol = itemView.findViewById(R.id.selectedSymbol);
        }
    }

}
