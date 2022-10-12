package com.nh.dataspider.audio;

import java.io.File;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

public class WavToMp3 {

	public void WavToMp3Test() {
		
		File source = new File("source.wav");
		MultimediaObject msource = new MultimediaObject(source);
		
		File target = new File("target.mp3");
		
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(128000));
		audio.setChannels(new Integer(2));
		audio.setSamplingRate(new Integer(44100));
		
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setOutputFormat("mp3");
		attrs.setAudioAttributes(audio);
		
		Encoder encoder = new Encoder();
		try {
			encoder.encode(msource, target, attrs);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
