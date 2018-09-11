package com.example.victor.my_reader;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenReading extends AppCompatActivity {
    private static File file;

    String[] folders;
    ListView lvMain;
    ArrayAdapter<String> adapter;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final String folderPath = "folderPath";
    private static Object locker = new Object();
    private static String internalStoragePath = "";
    AppCompatActivity thisActivity = this;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            synchronized (locker) {
                if (requestCode != PERMISSION_REQUEST_CODE && grantResults.length == 2) {
                    throw new Exception("Permission not granted");
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                locker.notify();
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_reading);

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                WaitPermissions mt = new WaitPermissions();
                mt.execute();
                return;
            }
            SharedPreferences sp = getPreferences(MODE_PRIVATE);
            String path = sp.getString(folderPath, "");
            if (!path.equals("")) {
                file = new File(path);
                openReadingWindow();
                setFile(file.getParentFile(), getInternalStoragePath());
            } else {
                File file = getExternalStorageFile();
                setFile(file, file.getAbsolutePath());
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }

    public void setSortedFoldersArray() {
        try {
            FilenameFilter filter1 = new ExtensionFileFilter(
                    new String[]{".jpg", ".jpeg", ".png", ".gif", ".zip", ".rar"});
            folders = file.list(filter1);
            Arrays.sort(folders, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            Arrays.sort(folders, new Comparator() {
                public int compare(Object o1, Object o2) {
                    File file1 = new File(file.getAbsolutePath(), o1.toString());
                    File file2 = new File(file.getAbsolutePath(), o2.toString());
                    if (file1.isDirectory() && file2.isFile()) return -1;
                    else if (file1.isDirectory() && file2.isDirectory()) return 0;
                    else if (file1.isFile() && file2.isFile()) return 0;
                    else return 1;
                }
            });
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
    }

    private String getInternalStoragePath() {
        return getExternalStorageFile().getAbsolutePath();
    }

    @Override
    public void onBackPressed() {
        if (file.getAbsolutePath().equals(internalStoragePath)) return;
        file = file.getParentFile();
        setSortedFoldersArray();
        fillFolderList();
    }

    private void openReadingWindow() {
        Intent intent = new Intent(thisActivity, ReadingWindow.class);
        intent.putExtra(folderPath, file.getAbsolutePath());
        startActivity(intent);
    }

    private void openReadingWindow(int fileNumber) {
        Intent intent = new Intent(thisActivity, ReadingWindow.class);
        intent.putExtra(folderPath, file.getAbsolutePath());
        intent.putExtra("fileNumber", fileNumber);
        startActivity(intent);
    }

    private void OpenReader(int i) {
        String path;
        if (file.isFile()) {
            path = file.getParentFile().getAbsolutePath();
        } else {
            file = new File(file, folders[i]);
            path = file.getAbsolutePath();
        }
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(folderPath, path);
        editor.commit();
        if (file.isFile())
        {
            file = file.getParentFile();
            openReadingWindow(i);
        }
        else {
            openReadingWindow();
            file = file.getParentFile();
        }
    }

    public void fillFolderList() {
        // находим список
        lvMain = (ListView) findViewById(R.id.FolderList);
        // создаем адаптер
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, folders);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                file = new File(file, folders[position]);
                if (file.isDirectory()) {
                    setSortedFoldersArray();
                    fillFolderList();
                } else {
                    if (file.getName().endsWith(".zip") || file.getName().endsWith(".rar")) {
                        Button button = (Button) findViewById(R.id.applyButton);
                        button.setVisibility(View.VISIBLE);
                    } else {
                        OpenReader(position);
                    }
                }
            }
        });
        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (file.isDirectory()) {
                    OpenReader(i);
                }
                return false;
            }
        });
    }

    @Nullable
    private File getExternalStorageFile() {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            final File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null) {
                return externalStorage;
            }
        }
        return null;
    }

    private void setFile(File newFile, String path) {
        file = newFile;
        folders = file.list();
        if (folders != null) {
            setSortedFoldersArray();
            fillFolderList();
        }
        internalStoragePath = path;
    }

    class WaitPermissions extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                synchronized (locker) {
                    locker.wait();
                }
            } catch (Exception e) {
                String message = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            File file = getExternalStorageFile();
            setFile(file, file.getAbsolutePath());
        }
    }
}
