package com.example.victor.my_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageFragment extends Fragment
{
    static final String ARGUMENT_PAGE_PATH = "arg_page_path";
    final Fragment fragment = this;
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
        PhotoView photoPage = (PhotoView) view.findViewById(R.id.photoPage);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoPage);
        attacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent)
            {
                GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener();
                gestureListener.onSingleTapConfirmed(motionEvent);
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent)
            {
                try {
                    float scale = attacher.getScale();
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();

                    if (scale != attacher.getMediumScale()) {
                        attacher.setScale(attacher.getMediumScale(), x, y, true);
                    } else {
                        attacher.setScale(attacher.getMinimumScale(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Can sometimes happen when getX() and getY() is called
                }

                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent)
            {
                return false;
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        photoPage.setImageBitmap(bitmap);
        return view;
    }
}
