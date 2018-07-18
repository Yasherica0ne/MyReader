package com.example.victor.my_reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;
import java.util.Random;

import uk.co.senab.photoview.PhotoView;

public class ImageFragment extends Fragment
{
    static final String ARGUMENT_PAGE_PATH = "arg_page_path";
    String filePath;

    static ImageFragment newInstance(String filePath) {
        ImageFragment pageFragment = new ImageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_PAGE_PATH, filePath);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getArguments().getString(ARGUMENT_PAGE_PATH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, null);

        PhotoView photoPage = view.findViewById(R.id.photoPage);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        photoPage.setImageBitmap(bitmap);
        return view;
    }
}
