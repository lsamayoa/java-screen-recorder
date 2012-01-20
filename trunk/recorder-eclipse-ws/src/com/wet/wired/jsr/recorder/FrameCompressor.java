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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class FrameCompressor {

   private FramePacket frame;

   public class FramePacket {

      private OutputStream oStream;
      private long frameTime;

      private int[] previousData;
      private int[] newData;

      private FramePacket(OutputStream oStream, int frameSize) {
         this.oStream = oStream;
         previousData = new int[frameSize];
      }

      private void nextFrame(int[] frameData, long frameTime, boolean reset) {
         this.frameTime = frameTime;
         previousData = newData;
         newData = null;
         if (previousData == null) {
            previousData = new int[frameData.length];
         }
         if (reset) {
            this.newData = new int[frameData.length];
         } else {
            this.newData = frameData;// new int[frameData.length];
         }
      }
   }

   public FrameCompressor(OutputStream oStream, int frameSize) {
      frame = new FramePacket(oStream, frameSize);
   }

   public void pack(int[] newData, long frameTimeStamp, boolean reset)
         throws IOException {
      frame.nextFrame(newData, frameTimeStamp, reset);

      byte[] packed = new byte[newData.length * 4];

      int inCursor = 0;
      int outCursor = 0;
      int blocks = 0;

      boolean inBlock = true;
      int blockSize = 0;
      byte blockRed = 0;
      byte blockGreen = 0;
      byte blockBlue = 0;

      int blankBlocks = 0;

      // Sentinel value
      int uncompressedCursor = -1;

      byte red;
      byte green;
      byte blue;

      boolean hasChanges = false;
      boolean lastEntry = false;

      while (inCursor < newData.length) {
         if (inCursor == newData.length - 1) {
            lastEntry = true;
         }

         if (newData[inCursor] == frame.previousData[inCursor]) {
            red = 0;
            green = 0;
            blue = 0;
         } else {
            red = (byte) ((newData[inCursor] & 0x00FF0000) >>> 16);
            green = (byte) ((newData[inCursor] & 0x0000FF00) >>> 8);
            blue = (byte) ((newData[inCursor] & 0x000000FF));

            if (red == 0 && green == 0 && blue == 0) {
               blue = 1;
            }
         }

         if (blockRed == red && blockGreen == green && blockBlue == blue) {
            if (inBlock == false) {
               if (uncompressedCursor > -1) {
                  blocks++;
                  hasChanges = true;
                  packed[uncompressedCursor] = (byte) (blockSize + 0x80);
               }
               inBlock = true;
               blockSize = 0;
               blankBlocks = 0;
            } else if (blockSize == 126 || lastEntry == true) {
               if (blockRed == 0 && blockGreen == 0 && blockBlue == 0) {
                  if (blankBlocks > 0) {
                     blankBlocks++;
                     packed[outCursor - 1] = (byte) blankBlocks;
                  } else {
                     blocks++;
                     blankBlocks++;
                     packed[outCursor] = (byte) 0xFF;
                     outCursor++;
                     packed[outCursor] = (byte) blankBlocks;
                     outCursor++;
                  }
                  if (blankBlocks == 255) {
                     blankBlocks = 0;
                  }
               } else {
                  blocks++;
                  hasChanges = true;
                  packed[outCursor] = (byte) blockSize;
                  outCursor++;
                  packed[outCursor] = blockRed;
                  outCursor++;
                  packed[outCursor] = blockGreen;
                  outCursor++;
                  packed[outCursor] = blockBlue;
                  outCursor++;

                  blankBlocks = 0;
               }
               inBlock = true;
               blockSize = 0;
            }
         } else {
            if (inBlock == true) {
               if (blockSize > 0) {
                  blocks++;
                  hasChanges = true;
                  packed[outCursor] = (byte) blockSize;
                  outCursor++;
                  packed[outCursor] = blockRed;
                  outCursor++;
                  packed[outCursor] = blockGreen;
                  outCursor++;
                  packed[outCursor] = blockBlue;
                  outCursor++;
               }

               uncompressedCursor = -1;
               inBlock = false;
               blockSize = 0;

               blankBlocks = 0;
            } else if (blockSize == 126 || lastEntry == true) {
               if (uncompressedCursor > -1) {
                  blocks++;
                  hasChanges = true;
                  packed[uncompressedCursor] = (byte) (blockSize + 0x80);
               }

               uncompressedCursor = -1;
               inBlock = false;
               blockSize = 0;

               blankBlocks = 0;
            }

            if (uncompressedCursor == -1) {
               uncompressedCursor = outCursor;
               outCursor++;
            }

            packed[outCursor] = red;
            outCursor++;
            packed[outCursor] = green;
            outCursor++;
            packed[outCursor] = blue;
            outCursor++;

            blockRed = red;
            blockGreen = green;
            blockBlue = blue;
         }
         inCursor++;
         blockSize++;
      }

      frame.oStream.write(((int) frame.frameTime & 0xFF000000) >>> 24);
      frame.oStream.write(((int) frame.frameTime & 0x00FF0000) >>> 16);
      frame.oStream.write(((int) frame.frameTime & 0x0000FF00) >>> 8);
      frame.oStream.write(((int) frame.frameTime & 0x000000FF));

      if (hasChanges == false) {
         frame.oStream.write(0);
         frame.oStream.flush();
         frame.newData = frame.previousData;

         return;
      } else {
         frame.oStream.write(1);
         frame.oStream.flush();
      }

      ByteArrayOutputStream bO = new ByteArrayOutputStream();

      byte[] bA = new byte[0];

      GZIPOutputStream zO = new GZIPOutputStream(bO);

      zO.write(packed, 0, outCursor);
      zO.close();
      bO.close();

      bA = bO.toByteArray();

      frame.oStream.write((bA.length & 0xFF000000) >>> 24);
      frame.oStream.write((bA.length & 0x00FF0000) >>> 16);
      frame.oStream.write((bA.length & 0x0000FF00) >>> 8);
      frame.oStream.write((bA.length & 0x000000FF));

      frame.oStream.write(bA);
      frame.oStream.flush();
   }
}
