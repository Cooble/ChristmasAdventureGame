package mc.cooble.saving;
/*
 * @(#)Ogg.java
 *
 * This file is part of webCDwriter - Network CD Writing.
 *
 * Copyright (C) 2003 Jörg P. M. Haeger
 *
 * webCDwriter is free software. See CDcreator.java for details.
 */

import mc.cooble.core.Game;

import java.io.File;
import java.io.InputStream;

/**
 * Class to get the length of ogg files.
 *
 * @version 20030220
 * @author Jörg P. M. Haeger
 */
public class Ogg {
    int audio_channels;
    int audio_sample_rate;
    long dataLength;
    long headerStart;
    long sampleNum;
    int vorbis_version;

    public Ogg(File file) throws Exception {
        this(file.getPath());
    }

    public Ogg(String file) throws Exception {
        dataLength = new File(file).length();
        InputStream inStream = Game.saver.getIO().getResourceAsStream(file);
        byte[] array = new byte[4];
        inStream.read(array);//junk
        System.out.println("version "+getInt(array));

        array = new byte[1];
        inStream.read(array);
        System.out.println("byte channels "+array[0]+" : "+(array[0]&0b11111111));
        array = new byte[4];
        inStream.read(array);
        int sampleRate = getInt(array);//samplerate

        array = new byte[4];
        inStream.read(array);//maximum bitrate

        array = new byte[4];
        inStream.read(array);
        int bitrate = getInt(array);//nominal bitrate

        System.out.println("Bitrate of "+file +" is: "+bitrate);
        System.out.println("Samplerate of "+file +" is: "+sampleRate);
    }
    public static int getInt(byte[] bytes){
        int out = bytes[0];
        out<<=8;
        out|=bytes[1];
        out<<=8;
        out|=bytes[2];
        out<<=8;
        out|=bytes[3];
        return out;
    }
    public static long getUnsignedInt(int x) {
        return x & 0x00000000ffffffffL;
    }

    long getSeconds() {
        if (audio_sample_rate > 0)
            return sampleNum / audio_sample_rate;
        else
            return 0;
    }
    public int read32Bits(InputStream inStream) throws Exception {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int b = inStream.read();
            if (b == -1)
                throw new Exception("Unexpected end of input stream");
            n |= b << (8 * i);
        }
        return n;
    }

    public void showInfo() {
        System.out.println("audio_channels = " + audio_channels);
        System.out.println("audio_sample_rate = " + audio_sample_rate);
        System.out.println("dataLength = " + dataLength);
        System.out.println("seconds = " + getSeconds());
        System.out.println("headerStart = " + headerStart);
        System.out.println("vorbis_version = " + vorbis_version);
    }
}