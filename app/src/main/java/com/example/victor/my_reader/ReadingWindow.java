package com.example.victor.my_reader;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadingWindow extends AppCompatActivity
{
    public ReadingWindow()
    {
    }

    File file;
    File[] folders;
    private int pageCounter = 0;

    private boolean nextPage()
    {
        if (pageCounter < folders.length)
        {
            pageCounter++;
            return true;
        } else return false;
    }

    private boolean previousPage()
    {
        if (pageCounter >= 0)
        {
            pageCounter--;
            return true;
        } else return false;
    }

    private GestureDetectorCompat lSwipeDetector;

    RelativeLayout main_layout;
    ImageView imageView;

    private static final int SWIPE_MIN_DISTANCE = 130;
    private static final int SWIPE_MAX_DISTANCE = 300;
    private static final int SWIPE_MIN_VELOCITY = 200;

    private void setNewImage()
    {
        try
        {
            imageView = (ImageView) findViewById(R.id.mainImageView);
            Bitmap bitmap = BitmapFactory.decodeFile(folders[pageCounter].getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_window);
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        BufferedReader br = null;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            br = new BufferedReader(new InputStreamReader(
                    openFileInput("folderPath.txt")));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null)
            {
                stringBuilder.append(str);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String savedPath = stringBuilder.toString(); //sharedPreferences.getString("folderPath", "");
        file = new File(savedPath);
        ExtensionFileFilter filter = new ExtensionFileFilter(
                new String[]{".jpg", ".jpeg", ".png", ".gif"});
        folders = file.listFiles(filter);
        lSwipeDetector = new GestureDetectorCompat(this, new MyGestureListener());
        main_layout = findViewById(R.id.mainLayout);
        setNewImage();
        main_layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return lSwipeDetector.onTouchEvent(event);
            }
        });
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_DISTANCE)
                return false;
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(
                    velocityX) > SWIPE_MIN_VELOCITY)
            {
                if (previousPage())
                {
                    setNewImage();
                }
            }
            else if (Math.abs(e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE && Math.abs(
                    velocityX) > SWIPE_MIN_VELOCITY)
            {
                if (nextPage())
                {
                    setNewImage();
                }
            }
            return false;
        }
    }
}
