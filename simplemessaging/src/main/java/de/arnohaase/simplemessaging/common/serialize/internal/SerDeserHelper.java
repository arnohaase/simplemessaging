package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SerDeserHelper {
    public static final String ENCODING = "utf-8";
    
    public void writeBoolean (OutputStream out, boolean b) throws IOException {
        if (b)
            writeOneByte (out, 1);
        else
            writeOneByte (out, 0);
    }

    public void writeString (OutputStream out, String s) throws IOException {
        final byte[] bytes = s.getBytes (ENCODING);
        if (bytes.length > 65535)
            throw new IllegalArgumentException ("string is longer than 65535 bytes");
        writeTwoBytes (out, bytes.length);

        out.write (bytes);
    }

    public void writeOneByte (OutputStream out, int i) throws IOException {
        out.write(i);
    }

    public void writeTwoBytes (OutputStream out, int i) throws IOException {
        out.write (i);
        out.write (i>>8);
    }

    public void writeThreeBytes (OutputStream out, int i) throws IOException {
        out.write (i);
        out.write (i>>8);
        out.write (i>>16);
    }

    public void writeFourBytes (OutputStream out, int i) throws IOException {
        out.write (i);
        out.write (i>>8);
        out.write (i>>16);
        out.write (i>>24);
    }

    public void writeEightBytes (OutputStream out, long i) throws IOException {
        out.write ((int) (i & 255));
        out.write ((int) (i>> 8) & 255);
        out.write ((int) (i>>16) & 255);
        out.write ((int) (i>>24) & 255);
        out.write ((int) (i>>32) & 255);
        out.write ((int) (i>>40) & 255);
        out.write ((int) (i>>48) & 255);
        out.write ((int) (i>>56) & 255);
    }

    public void writeDouble (OutputStream out, double d) throws IOException {
        writeEightBytes (out, Double.doubleToRawLongBits (d));
    }

    public boolean readBoolean (InputStream in) throws IOException {
        final int i = readOneByte (in);
        switch (i) {
        case 0: return false;
        case 1: return true;
        default: throw new IllegalStateException ("kein gueltiger Boolean-Wert: " + i);
        }
    }

    public String readString (InputStream in) throws IOException {
        final int len = readTwoBytes (in);

        final byte[] bytes = new byte[len];
        // Einlesen in einer Schleife - die Methode read(byte[]) garantiert nicht, dass sie
        //  alle verfuegbaren Daten auf einmal einliest und wuerde deshalb den Code hier
        //  verkomplizieren
        for (int i=0; i<len; i++)
            bytes[i] = (byte) in.read();

        return new String (bytes, ENCODING);
    }

    private int readUnsignedByte (InputStream in) throws IOException {
        final int result = in.read() & 255;
        //        System.out.print (Integer.toHexString (result + 256).substring(1) + "_");
        return result;
    }

    public int readOneByte (InputStream in) throws IOException {
        return readUnsignedByte(in);
    }

    public int readTwoBytes (InputStream in) throws IOException {
        int result = readUnsignedByte (in);
        result += readUnsignedByte (in) << 8;
        return result;
    }

    public int readThreeBytes (InputStream in) throws IOException {
        int result = readUnsignedByte (in);
        result += readUnsignedByte (in) << 8;
        result += readUnsignedByte (in) << 16;
        return result;
    }

    public int readFourBytes (InputStream in) throws IOException {
        int result = readUnsignedByte (in);
        result += readUnsignedByte (in) << 8;
        result += readUnsignedByte (in) << 16;
        result += readUnsignedByte (in) << 24;
        return result;
    }

    public long readEightBytes (InputStream in) throws IOException {
        long result = readUnsignedByte (in);
        result += ((long) readUnsignedByte (in)) <<  8;
        result += ((long) readUnsignedByte (in)) << 16;
        result += ((long) readUnsignedByte (in)) << 24;
        result += ((long) readUnsignedByte (in)) << 32;
        result += ((long) readUnsignedByte (in)) << 40;
        result += ((long) readUnsignedByte (in)) << 48;
        result += ((long) readUnsignedByte (in)) << 56;
        return result;
    }

    public double readDouble (InputStream in) throws IOException {
        return Double.longBitsToDouble(readEightBytes (in));
    }
}
