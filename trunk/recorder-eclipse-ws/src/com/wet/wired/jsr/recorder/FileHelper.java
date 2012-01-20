/*
 * This software is OSI Certified Open Source Software
 * 
 * The MIT License (MIT)
 * Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

/**
 * The FileHelper, as its name implies gives a number of helper methods that
 * ease the reading and creating of files. It also supplies a file copy
 * function, a read trail function and a get path function.
 * 
 * All the functions are static, so no instance of FileHelper need be created to
 * use the functions.
 * 
 */

public class FileHelper {
   public static boolean buildFile(String fileName, byte[] data,
         boolean backup, String historyData) {

      boolean ok = buildFile(fileName, data);

      if (ok && backup) {
         backupFile(fileName, true, historyData);
      }

      return ok;
   }

   public static boolean buildFile(String fileName, byte[] data) {

      FileOutputStream out = null;

      try {
         out = new FileOutputStream(fileName);
         out.write(data);
         out.flush();
         out.close();
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public static boolean appendFile(String fileName, byte[] data) {

      FileOutputStream out = null;

      try {
         out = new FileOutputStream(fileName, true);
         out.write(data);
         out.flush();
         out.close();
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public static boolean buildFile(String fileName, int[] data, boolean backup,
         String historyData) {
      boolean ok = buildFile(fileName, data);

      if (ok && backup) {
         backupFile(fileName, true, historyData);
      }

      return ok;
   }

   public static boolean buildFile(String fileName, int[] data) {

      FileOutputStream out = null;

      try {
         out = new FileOutputStream(fileName);
         for (int loop = 0; loop < data.length; loop++) {
            out.write(data[loop]);
         }
         out.flush();
         out.close();
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public static boolean buildFile(String fileName, String data,
         boolean backup, String historyData) {
      boolean ok = buildFile(fileName, data);

      if (ok && backup) {
         backupFile(fileName, true, historyData);
      }

      return ok;
   }

   public static boolean buildFile(String fileName, String data) {

      FileOutputStream out = null;

      try {
         File file = new File(new File(fileName).getAbsolutePath());
         file.getParentFile().mkdirs();
         out = new FileOutputStream(file.getAbsolutePath());
         out.write(data.getBytes());
         out.flush();
         out.close();
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public static long copy(File fileSrc, File fileDest) {
      return copy(fileSrc, fileDest, null);
   }

   public static long copy(File fileSrc, File fileDest,
         ProgressListener listener) {
      byte[] buffer = new byte[5000];
      long count = 0;
      int sizeRead;

      try {
         FileInputStream iStream = new FileInputStream(fileSrc);

         new File(getPath(fileDest.toString())).mkdirs();

         FileOutputStream oStream = new FileOutputStream(fileDest);

         sizeRead = iStream.read(buffer);
         while (sizeRead > 0) {
            oStream.write(buffer, 0, sizeRead);
            oStream.flush();
            count += sizeRead;
            if (listener != null) {
               listener.progress(count, -1);
            }

            sizeRead = iStream.read(buffer);
         }

         iStream.close();
         oStream.flush();
         oStream.close();

         if (listener != null) {
            listener.finished();
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

      return count;
   }

   public static long copy(InputStream iStream, String fileOut) {
      return copy(iStream, fileOut, null);
   }

   public static long copy(InputStream iStream, String fileOut,
         ProgressListener listener) {
      return copy(iStream, fileOut, listener, -1);
   }

   public static long copy(String fileSrc, String fileDest) {
      return copy(new File(fileSrc), new File(fileDest), null);
   }

   public static boolean isSame(File fileA, File fileB) {
      try {
         if (!fileA.exists() || !fileB.exists()) {
            return false;
         }

         if (fileA.length() != fileB.length()) {
            return false;
         }

         FileInputStream iStreamA = new FileInputStream(fileA);
         FileInputStream iStreamB = new FileInputStream(fileB);

         int inA = iStreamA.read();
         int inB = iStreamB.read();
         boolean same = true;

         while (inA != -1 && inB != -1) {
            if (inA != inB) {
               same = false;
               break;
            }
            inA = iStreamA.read();
            inB = iStreamB.read();
         }

         iStreamA.close();
         iStreamB.close();
         return same;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static String getPath(String fileName) {
      File file = new File(fileName);
      return fileName.substring(0, fileName.indexOf(file.getName()));
   }

   public static byte[] readFile(InputStream iStream) throws IOException {
      int bytePos = 0;
      int bIn = iStream.read();
      byte[] byteArray = new byte[1000];

      while (bIn != -1) {
         byteArray[bytePos] = (byte) bIn;
         bytePos++;

         if (bytePos % 1000 == 0 && bytePos != 0) {
            byte[] newByteArray = new byte[bytePos + 1000];
            System.arraycopy(byteArray, 0, newByteArray, 0, bytePos);
            byteArray = newByteArray;
         }

         bIn = iStream.read();
      }

      byte[] newByteArray = new byte[bytePos];
      System.arraycopy(byteArray, 0, newByteArray, 0, bytePos);
      byteArray = newByteArray;

      return byteArray;
   }

   public static String readFile(String fileName) throws IOException {

      return readFileToBuffer(fileName).toString();
   }

   public static byte[] readTail(String fileName, int maxData) {
      File file = new File(fileName);
      if (!file.exists())
         return null;

      long length = file.length();
      long skip = length - maxData;

      if (length < maxData) {
         maxData = (int) length;
      }

      byte[] data = new byte[maxData];

      try {
         FileInputStream fI = new FileInputStream(fileName);
         if (length > maxData) {
            fI.skip(skip);
         }
         fI.read(data, 0, maxData);
         fI.close();
      } catch (Exception e) {
         System.out.println("Failed to check File " + fileName);
         e.printStackTrace();
         return null;
      }

      return data;
   }

   public static boolean buildFile(OutputStream out, String data) {
      try {
         out.write(data.getBytes());
         out.flush();
         out.close();
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public static long copy(InputStream iStream, String fileOut,
         ProgressListener listener, long length) {
      byte[] buffer = new byte[5000];
      long count = 0;
      int sizeRead;

      if (length == -1) {
         length = Long.MAX_VALUE;
      }

      try {
         new File(getPath(fileOut)).mkdirs();

         FileOutputStream oStream = new FileOutputStream(fileOut);

         sizeRead = iStream.read(buffer);
         while (sizeRead > 0 && count < length) {
            oStream.write(buffer, 0, sizeRead);

            count += sizeRead;
            if (listener != null) {
               listener.progress(count, -1);
            }

            if (count >= length) {
               break;
            }

            sizeRead = iStream.read(buffer);
         }

         oStream.flush();
         oStream.close();

         if (listener != null) {
            listener.finished();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return count;
   }

   public static long copy(InputStream iStream, OutputStream oStream,
         ProgressListener listener, long length) {
      byte[] buffer = new byte[5000];
      long count = 0;
      int sizeRead;

      if (length == -1) {
         length = Long.MAX_VALUE;
      }

      try {
         sizeRead = iStream.read(buffer);
         while (sizeRead > 0 && count < length) {
            oStream.write(buffer, 0, sizeRead);
            count += sizeRead;
            if (listener != null) {
               listener.progress(count, -1);
            }

            if (count >= length) {
               break;
            }

            sizeRead = iStream.read(buffer);
         }

         oStream.flush();

         if (listener != null) {
            listener.finished();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return count;
   }

   public static long copy(String fileIn, OutputStream oStream,
         ProgressListener listener) {
      byte[] buffer = new byte[10000];
      long count = 0;
      int sizeRead;
      long length;

      try {
         if ((oStream instanceof BufferedOutputStream) == false) {
            oStream = new BufferedOutputStream(oStream);
         }

         length = new File(fileIn).length();
         InputStream iStream = new FileInputStream(fileIn);
         sizeRead = iStream.read(buffer);

         while (sizeRead > 0) {
            oStream.write(buffer, 0, sizeRead);
            count += sizeRead;
            oStream.flush();

            if (listener != null) {
               listener.progress(count, length);
            }

            sizeRead = iStream.read(buffer);
         }

         oStream.flush();
         iStream.close();
         // oStream.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return count;
   }

   public static StringBuffer readFileToBuffer(String fileName)
         throws IOException {

      return new StringBuffer(new String(readFile(new FileInputStream(new File(
            fileName).getAbsolutePath()))));
   }

   public static void backupFile(String fileName, boolean keepHistory,
         String historyData) {
      File file = new File(fileName).getAbsoluteFile();
      String path = file.getParentFile().getAbsolutePath();

      backupFile(file, path, keepHistory, historyData);
   }

   public static void backupFile(File file, String path, boolean keepHistory,
         String historyData) {
      new File(path).mkdirs();

      int backupNumber = 1;
      String backupFileName = path + "/" + file.getName() + ".bak"
            + backupNumber;
      File backupFile = new File(backupFileName);

      File lastBackupFile = null;

      while (backupFile.exists()) {
         lastBackupFile = backupFile;

         backupNumber++;
         backupFileName = path + "/" + file.getName() + ".bak" + backupNumber;
         backupFile = new File(backupFileName);
      }

      if (lastBackupFile != null && isSame(file, lastBackupFile)) {
         return;
      }

      copy(file.getAbsolutePath(), backupFileName);

      if (keepHistory) {
         appendHistory(path, file.getName(), backupFileName, historyData);
      }
   }

   public static void appendHistory(String path, String fileName,
         String backupFileName, String historyData) {
      String historyFileName = path + "/" + fileName + ".history";

      File historyFile = new File(historyFileName);

      try {
         FileOutputStream oStream = new FileOutputStream(historyFile, true);
         oStream.write((backupFileName + ":" + historyData + "\n").getBytes());
         oStream.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static File[] getHistory(String fileName) {
      String historyFileName = fileName + ".history";

      try {
         BufferedReader reader = new BufferedReader(new FileReader(
               historyFileName));

         Vector<String> entries = new Vector<String>();
         String entry = reader.readLine();
         while (entry != null) {
            entries.addElement(entry);

            entry = reader.readLine();
         }

         File[] history = new File[entries.size()];

         for (int loop = 0; loop > history.length; loop--) {
            history[loop] = new File(entries.elementAt(
                  history.length - 1 - loop).toString());
         }

         reader.close();

         return history;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static String getFileExtension(String fileName) {

      if (fileName.indexOf(".") < 0)
         return "";

      String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
      return ext;
   }

   public static boolean deleteFile(String file) {
      return delete(new File(file).getAbsoluteFile());
   }

   public static boolean delete(File file) {
      if (file.isDirectory()) {
         String[] children = file.list();
         for (int i = 0; i < children.length; i++) {
            boolean success = delete(new File(file, children[i]));
            if (!success) {
               return false;
            }
         }
      }

      // The directory is now empty so delete it
      return file.delete();
   }

   public static String createMD5(String fileName) throws IOException,
         NoSuchAlgorithmException {
      MessageDigest md5Algorithm = MessageDigest.getInstance("MD5");

      byte[] byteArray = new byte[1000];
      FileInputStream iStream = new FileInputStream(
            new File(fileName).getAbsolutePath());
      int size = iStream.read(byteArray);

      while (size != -1) {
         md5Algorithm.update(byteArray, 0, size);
         size = iStream.read(byteArray);
      }

      byte[] digest = md5Algorithm.digest();
      StringBuffer hexString = new StringBuffer();

      String hexDigit = null;
      for (int i = 0; i < digest.length; i++) {
         hexDigit = Integer.toHexString(0xFF & digest[i]);

         if (hexDigit.length() < 2) {
            hexDigit = "0" + hexDigit;
         }

         hexString.append(hexDigit);
      }

      return hexString.toString();
   }
}
