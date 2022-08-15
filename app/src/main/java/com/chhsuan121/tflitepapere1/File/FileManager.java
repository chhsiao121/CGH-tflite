package com.chhsuan121.tflitepapere1.File;

import com.chhsuan121.tflitepapere1.Config.Setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

public class FileManager {

    public String[] listDir(String path) {
        File file = new File(Setup.DEF_STORAGE_PATH + path);
        String[] listDirs = file.list();
        if (listDirs != null)
            return listDirs;
        else
            return null;
    }

    public String[] listSpecFile(String path, String type) {
        GenericExtFilter filter = new GenericExtFilter(type);

        File file = new File(Setup.DEF_STORAGE_PATH + path);
        String[] listFiles = file.list(filter);

        if (listFiles != null)
            return listFiles;
        else
            return null;
    }

    public String[] searchFileWithName(String path, String name, int type) {
        FilenameFilter filter;
        if (type == 2)
            filter = new SearchFileNameFilter2(name);
        else
            filter = new SearchFileNameFilter1(name);


        File file = new File(Setup.DEF_STORAGE_PATH + path);
        String[] listFiles = file.list(filter);

        if (listFiles != null)
            return listFiles;
        else
            return null;
    }

    public Set<Object> searchLabelFileWithName(String path, String name) {
        SearchFileNameFilter2 filter = new SearchFileNameFilter2(name);

        File file = new File(Setup.DEF_STORAGE_PATH + path);
        String[] listFiles = file.list(filter);

        if (listFiles != null) {
            Set<Object> labelSet = new HashSet<Object>();
            for (String listFile : listFiles) {
                String labels[] = listFile.split("_");
                Object labelObj = Integer.valueOf(labels[0]);
                labelSet.add(labelObj);
            }
            return labelSet;
        } else
            return null;
    }

    public void copyFile(String fromPath, String toPath) throws IOException {
        File fromfile = new File(Setup.DEF_STORAGE_PATH + fromPath);
        File tofile = new File(Setup.DEF_STORAGE_PATH + toPath);
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(fromfile).getChannel();
            destChannel = new FileOutputStream(tofile).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    // inner class, generic extension filter
    public class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }

    // inner class, generic file name search filter
    public class SearchFileNameFilter1 implements FilenameFilter {
        private String string;

        public SearchFileNameFilter1(String string) {
            this.string = string;
        }

        public boolean accept(File dir, String name) {
            //if(name.replace("wordcard","").split("_")[0].equals(string.split("_")[0]) && name.split("_")[1].equals(string.split("_")[1]))
            return (name.contains(string));
        }
    }

    // inner class, generic file name search filter
    public class SearchFileNameFilter2 implements FilenameFilter {
        private String string;

        public SearchFileNameFilter2(String string) {
            this.string = string;
        }

        public boolean accept(File dir, String name) {
            if (name.split("_")[1].equals(string.split("_")[0]) && name.split("_")[2].equals(string.split("_")[1]))
                return true;
            else
                return false;
        }
    }

}
