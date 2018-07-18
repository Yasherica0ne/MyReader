package com.example.victor.my_reader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadingWindow extends AppCompatActivity
{
    public ReadingWindow()
    {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_window);
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
        String savedPath = stringBuilder.toString();
        file = new File(savedPath);
        ExtensionFileFilter filter = new ExtensionFileFilter(
                new String[]{".jpg", ".jpeg", ".png", ".gif"});
        folders = file.list(filter);
        pager =  findViewById(R.id.viewPager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        preferences = getPreferences(MODE_PRIVATE);
        int pageNumber = preferences.getInt(PAGE_NUMBER, 0);
        pager.setCurrentItem(pageNumber);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PAGE_NUMBER, position);
            editor.commit();
            return ImageFragment.newInstance(file.getAbsolutePath() + "/" + folders[position]);
        }

        @Override
        public int getCount() {
            return folders.length;
        }

    }
}
