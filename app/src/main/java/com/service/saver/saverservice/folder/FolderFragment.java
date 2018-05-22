package com.service.saver.saverservice.folder;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.folder.adepter.FileAdapter;
import com.service.saver.saverservice.folder.model.FileModel;
import com.service.saver.saverservice.util.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FolderFragment extends Fragment {
    private FileAdapter fileAdapter;
    public static List<FileModel> FILE_MODEL_LIST = new ArrayList<>();


    public FolderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        RecyclerView listView = view.findViewById(R.id.folder_grid);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getBaseContext(), getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5);
        listView.setLayoutManager(layoutManager);
        fileAdapter = new FileAdapter(FILE_MODEL_LIST);
        listView.setAdapter(fileAdapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        loadFiles();
        return super.getView();
    }


    private void loadFiles() {
        List<File> fileList = Files.getfiles(Files.getRunningDirByFile());
        fileList.sort(Comparator.comparingLong(File::lastModified).reversed());
        if (fileList.size() > FILE_MODEL_LIST.size()) {
            for (File f : fileList) {
                FILE_MODEL_LIST.add(new FileModel(FILE_MODEL_LIST.size() + 0L, f.getName(), f.getAbsolutePath()));
            }
        }
        fileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload_files_folder: {
                loadFiles();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
