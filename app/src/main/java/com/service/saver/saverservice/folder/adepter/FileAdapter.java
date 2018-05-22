package com.service.saver.saverservice.folder.adepter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.folder.holder.FileHolder;
import com.service.saver.saverservice.folder.model.FileModel;
import com.service.saver.saverservice.player.PlayerActivity;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileHolder> {


    private List<FileModel> dataSet;

    public FileAdapter(List<FileModel> dataSet) {
        this.dataSet = dataSet;
    }

    public int getCount() {
        return dataSet.size();
    }

    public FileModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        FileModel item = getItem(position);
        holder.filename.setText(item.getName());
        Uri uri = Uri.fromFile(new File((item.getFilepath())));
        holder.draweeView.setImageURI(uri);
        holder.vm.setOnClickListener((v) -> {
            Intent intent = new Intent(v.getContext(), PlayerActivity.class);
            intent.putExtra("position", position);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}