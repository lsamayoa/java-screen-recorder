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

package com.wet.wired.jsr.converter;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RecordingStream {

   private Rectangle area;
   private Rectangle outputArea;
   private FrameDecompressor decompressor;
   private long frameTime;
   private boolean finished = false;

   public RecordingStream(InputStream iStream, int width, int height) {
      this(iStream);
      outputArea = new Rectangle(width, height);
   }

   public RecordingStream(InputStream iStream) {

      try {
         int width = iStream.read();
         width = width << 8;
         width += iStream.read();
         int height = iStream.read();
         height = height << 8;
         height += iStream.read();
         area = new Rectangle(width, height);
         outputArea = area;
         decompressor = new FrameDecompressor(iStream, width * height);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public BufferedImage readFrame() throws IOException {

      FrameDecompressor.FramePacket frame = decompressor.unpack();
      frameTime = frame.getTimeStamp();
      int result = frame.getResult();
      if (result == 0) {
         return null;
      } else if (result == -1) {
         finished = true;
         return null;
      }

      BufferedImage bufferedImage = new BufferedImage(area.width, area.height,
            BufferedImage.TYPE_INT_RGB);
      bufferedImage.setRGB(0, 0, area.width, area.height, frame.getData(), 0,
            area.width);

      return bufferedImage;
   }

   public Rectangle getArea() {
      return outputArea;
   }

   public long getFrameTime() {
      return frameTime;
   }

   public boolean isFinished() {
      return finished;
   }
}
