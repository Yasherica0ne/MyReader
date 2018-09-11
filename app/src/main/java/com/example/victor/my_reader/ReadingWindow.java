package com.example.victor.my_reader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

public class ReadingWindow extends AppCompatActivity
{
    public ReadingWindow()
    {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            hideSystemUI();
        }
    }

    private void hideSystemUI()
    {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private static String PAGE_NUMBER = "PageNumber";

    File file;
    String[] folders;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reading_window);
            Intent intent = getIntent();
            String savedPath = intent.getStringExtra("folderPath");
            file = new File(savedPath);
            ExtensionFileFilter filter = new ExtensionFileFilter(
                    new String[]{".jpg", ".jpeg", ".png", ".gif"});
            folders = file.list(filter);
            pager = (ViewPager) findViewById(R.id.viewPager);
            pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);
            int pageNumber = intent.getIntExtra("fileNumber", -1);
            if (pageNumber == -1) {
                preferences = getPreferences(MODE_PRIVATE);
                pageNumber = preferences.getInt(PAGE_NUMBER, 0);
            }
            pager.setCurrentItem(pageNumber);
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {

            }

            @Override
            public void onPageSelected(int i)
            {
                preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(PAGE_NUMBER, i);
                editor.commit();
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {

            }
        });
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public MyFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            return ImageFragment.newInstance(file.getAbsolutePath() + "/" + folders[position]);
        }

        @Override
        public int getCount()
        {
            return folders.length;
        }
    }
}
