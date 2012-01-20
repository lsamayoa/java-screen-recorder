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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class JRecorder extends JFrame implements ScreenRecorderListener,
      ActionListener {

   private ScreenRecorder recorder;
   private File temp;

   private JButton control;
   private JLabel text;

   private boolean shuttingDown = false;
   private int frameCount = 0;

   public JRecorder() {

      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            shutdown();
         }
      });

      control = new JButton("Start Recording");
      control.setActionCommand("start");
      control.addActionListener(this);
      this.getContentPane().add(control, BorderLayout.WEST);

      text = new JLabel("Ready to record");
      this.getContentPane().add(text, BorderLayout.SOUTH);

      this.pack();
      this.setVisible(true);
   }

   public void startRecording(String fileName) {

      setState(Frame.ICONIFIED);
      try {
         Thread.sleep(500);
      } catch (InterruptedException e1) {
      }

      if (recorder != null) {
         return;
      }

      try {
         FileOutputStream oStream = new FileOutputStream(fileName);
         temp = new File(fileName);
         recorder = new DesktopScreenRecorder(oStream, this);
         recorder.startRecording();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void actionPerformed(ActionEvent ev) {
      if (ev.getActionCommand().equals("start") && recorder == null) {
         try {
            temp = File.createTempFile("temp", "rec");

            startRecording(temp.getAbsolutePath());
            control.setActionCommand("stop");
            control.setText("Stop Recording");
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else if (ev.getActionCommand().equals("stop") && recorder != null) {
         text.setText("Stopping");
         recorder.stopRecording();
      }
   }

   public void frameRecorded(boolean fullFrame) {
      frameCount++;
      if (text != null) {
         text.setText("Frame: " + frameCount);
      }
   }

   public void recordingStopped() {

      if (!shuttingDown) {

         UIManager.put("FileChooser.readOnly", true);
         JFileChooser fileChooser = new JFileChooser();
         FileExtensionFilter filter = new FileExtensionFilter();

         filter = new FileExtensionFilter();
         filter.addExtension("cap");
         filter.setDescription("Screen Capture File");

         fileChooser.setFileFilter(filter);
         fileChooser.showSaveDialog(this);

         File target = fileChooser.getSelectedFile();

         if (target != null) {

            if (!target.getName().endsWith(".cap"))
               target = new File(target + ".cap");

            FileHelper.copy(temp, target);
         }

         FileHelper.delete(temp);
         recorder = null;
         frameCount = 0;

         control.setActionCommand("start");
         control.setText("Start Recording");

         text.setText("Ready to record");
      } else
         FileHelper.delete(temp);
   }

   public static void main(String[] args) {

      if (args.length >= 1)
         if (args[0].equals("-white_cursor"))
            DesktopScreenRecorder.useWhiteCursor = true;
         else {
            System.out
                  .println("Usage: java -jar screen_recorder.jar [OPTION]...");
            System.out.println("Start the screen recorder.");
            System.out.println("Options:   ");
            System.out.println("   -white_cursor   record with white cursor");
            System.exit(0);
         }
      @SuppressWarnings("unused")
      JRecorder jRecorder = new JRecorder();
   }

   public void shutdown() {

      shuttingDown = true;

      if (recorder != null)
         recorder.stopRecording();

      dispose();
   }
}
