package com.example.victor.myreader;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class ExtensionFileFilter implements FilenameFilter {
    String extensions[];

    public ExtensionFileFilter(String extensions[]) {
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File file, String s) {
        if (file.isDirectory() && !s.startsWith(".")) {
            return true;
        }
        else
            {
            String path = s.toLowerCase();
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = extensions[i];
                if (path.endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
}