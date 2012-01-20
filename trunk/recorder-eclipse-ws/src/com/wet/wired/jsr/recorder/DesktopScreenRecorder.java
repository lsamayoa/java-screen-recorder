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

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class DesktopScreenRecorder extends ScreenRecorder {

   public static boolean useWhiteCursor;
   private Robot robot;
   private BufferedImage mouseCursor;

   public DesktopScreenRecorder(OutputStream oStream,
         ScreenRecorderListener listener) {
      super(oStream, listener);

      try {

         String mouseCursorFile;

         if (useWhiteCursor)
            mouseCursorFile = "white_cursor.png";
         else
            mouseCursorFile = "black_cursor.png";

         URL cursorURL = getClass().getClassLoader().getResource(
               "mouse_cursors/" + mouseCursorFile);

         mouseCursor = ImageIO.read(cursorURL);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Rectangle initialiseScreenCapture() {
      try {
         robot = new Robot();
      } catch (AWTException awe) {
         awe.printStackTrace();
         return null;
      }
      return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
   }

   public Robot getRobot() {
      return robot;
   }

   public BufferedImage captureScreen(Rectangle recordArea) {
      Point mousePosition = MouseInfo.getPointerInfo().getLocation();
      BufferedImage image = robot.createScreenCapture(recordArea);

      Graphics2D grfx = image.createGraphics();

      grfx.drawImage(mouseCursor, mousePosition.x - 8, mousePosition.y - 5,
            null);

      grfx.dispose();

      return image;
   }
}
