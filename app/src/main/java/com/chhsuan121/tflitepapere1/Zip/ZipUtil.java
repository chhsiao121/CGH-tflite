package com.chhsuan121.tflitepapere1.Zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static final String SEPERATOR = "/";
    public static File zip(File source, File targetFile) throws IOException {
        ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
        zOut.setLevel(9);//設定壓縮的程度0~9
        try {
            if (source.isFile()) {
                ZipUtil.writeFile(zOut, source, source.getName());
            } else {
                ZipUtil.writeFolder(zOut, source, "");
            }
        } finally {
            if (zOut != null) {
                zOut.close();
            }
        }
        return targetFile;
    }

    public static File unZip(File zipFile, File targetFolder) throws IOException {
        String fileName = zipFile.getName();
        String upZipRootFolderName = fileName.split("[.]")[0];
        ZipInputStream zIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        ZipEntry zipEntry = null;
        FileOutputStream fOut = null;
        try {
            File upZipRootFolder = new File(targetFolder + SEPERATOR + upZipRootFolderName);
            if (!upZipRootFolder.exists()) {
                upZipRootFolder.mkdirs();
            }
            while ((zipEntry = zIn.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    File targerFile = new File(
                            targetFolder.getPath() + SEPERATOR + upZipRootFolderName + SEPERATOR + zipEntry.getName());
                    File parent = targerFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fOut = new FileOutputStream(targerFile);
                    int byteNo1;
                    byte[] b1 = new byte[64];
                    while ((byteNo1 = zIn.read(b1)) > 0) {
                        fOut.write(b1, 0, byteNo1);
                    }
                    fOut.close();
                }
            }
        } finally {
            if (fOut != null) {
                fOut.close();
            }
            if (zIn != null) {
                zIn.close();
            }
        }
        return new File(targetFolder.getPath() + SEPERATOR + upZipRootFolderName);
    }

    private static void writeFolder(ZipOutputStream zOut, File sourceFolder, String entryPath) throws IOException {
        File[] files = sourceFolder.listFiles();
        String entryName = "";
        for (File f : files) {
            if (entryPath.equals("")) {
                entryName = f.getName();
            } else {
                entryName = entryPath + SEPERATOR + f.getName();
            }
            if (f.isFile()) {
                ZipUtil.writeFile(zOut, f, entryName);
            } else {
                ZipUtil.writeFolder(zOut, f, entryName);
            }
        }
    }

    private static void writeFile(ZipOutputStream zOut, File sourceFile, String entryName) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceFile);
            zOut.putNextEntry(new ZipEntry(entryName));
            zOut.setLevel(5);
            int byteNo;
            byte[] b = new byte[64];
            while ((byteNo = fis.read(b)) > 0) {
                zOut.write(b, 0, byteNo);
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}