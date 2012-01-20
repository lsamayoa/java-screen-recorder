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

import java.io.IOException;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

class PlayerDataSource extends PullBufferDataSource {
   PlayerSourceStream streams[];

   private PlayerSourceStream playerSourceStream;

   PlayerDataSource(String screenRecordingFileName) throws IOException {
      streams = new PlayerSourceStream[1];
      playerSourceStream = new PlayerSourceStream(screenRecordingFileName);
      streams[0] = playerSourceStream;
   }

   public void setLocator(MediaLocator source) {
   }

   public MediaLocator getLocator() {
      return null;
   }

   public String getContentType() {
      return ContentDescriptor.RAW;
   }

   public void connect() {
   }

   public void disconnect() {
   }

   public void start() {
   }

   public void stop() {
   }

   public PullBufferStream[] getStreams() {
      return streams;
   }

   public Time getDuration() {
      return DURATION_UNKNOWN;
   }

   public Object[] getControls() {
      return new Object[0];
   }

   public Object getControl(String type) {
      return null;
   }
}
