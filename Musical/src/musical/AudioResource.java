package Musical;

/**
 * AudioResource class based on StdAudio as described in 
 * <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a>
 * of <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 * by Robert Sedgewick and Kevin Wayne.
 */
import java.applet.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public final class AudioResource {
    public static final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2; 
    private static final int BITS_PER_SAMPLE = 16;
    private static final double MAX_16_BIT = Short.MAX_VALUE;
    private static final int SAMPLE_BUFFER_SIZE = 4096;
    private static SourceDataLine line;   // to play the sound
    private static byte[] buffer;         // our internal buffer
    private static int bufferSize = 0;    // number of samples in buffer
    
    // not-instantiable
    private AudioResource() { }
   
    // static initializer
    static { init(); }

    // open up an audio stream
    private static void init() {
        try {
            // 44.1K samples/sec, 16-bit audio, mono, signed PCM, little Endian
            AudioFormat format = new AudioFormat
						((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info
								(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
            buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // no sound gets made before this call
        line.start();
    }
     
     // Close standard audio.
     public static void close() {
        line.drain();
        line.stop();
    }
    
    // create a note (sine wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    public static double[] makeNote
						(double hz, double duration, double amplitude) {
        int N = (int)(AudioResource.SAMPLE_RATE * duration);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++)
            a[i] = amplitude * Math.sin(2 * Math.PI * i 
											* hz / AudioResource.SAMPLE_RATE);
        return a;
    }
    
    // Write one sample (between -1.0 and +1.0) to standard audio. If sample
    // is outside the range, it will be clipped.
    public static void playTone(double in) {
        // clip if outside [-1, +1]
        if (in < -1.0) in = -1.0;
        if (in > +1.0) in = +1.0;
        // convert to bytes
        short s = (short)(MAX_16_BIT * in);
        buffer[bufferSize++] = (byte)s;
        buffer[bufferSize++] = (byte)(s >> 8);   // little Endian
        // send to sound card if buffer is full        
        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }

     // Write an array of samples (between -1.0 and +1.0) to std audio. If a
	 // sample is outside the range, it will be clipped.
    public static void playArray(double[] input) {
        for (int i = 0; i < input.length; i++) {
            playTone(input[i]);
        }
    }
    
    // return data as a byte array
    private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {
            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                System.out.println("file open");
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }

            // try to read from URL
            else {
                URL url = AudioResource.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data = new byte[ais.available()];
                ais.read(data);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }
        return data;
    }


     // Read audio samples from a file (in .wav or .au format)
	 // and return them as a double array with values between -1.0 and +1.0.
    public static double[] read(String filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N/2];
        for (int i = 0; i < N/2; i++) {
            d[i] = ((short)(((data[2*i+1] & 0xFF) << 8) + 
                                    (data[2*i] & 0xFF))) / 
                                    ((double) MAX_16_BIT);
        }
        return d;
    }
    
     // Save a double array as a sound file (using .wav or .au format).
	public static void save(String filename, double[] input) {
        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] data = new byte[2 * input.length];
        for (int i = 0; i < input.length; i++) {
            int temp = (short) (input[i] * MAX_16_BIT);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }
        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = 
						new AudioInputStream(bais, format, input.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, 
													new File(filename));
            }
			
			
            else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU,
													new File(filename));
            }
            else {
                throw new RuntimeException("File format not supported: "
																+ filename);
            }
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public static void main (String [] args) {
        double freq = 440.0;
        // play one note
        for (int x=0; x<AudioResource.SAMPLE_RATE; x++) {
            AudioResource.playTone(0.5 * Math.sin(2*Math.PI * freq * x
											/ AudioResource.SAMPLE_RATE));
        }
        // play scale
        int[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
        for (int i = 0; i < steps.length; i++) {
            double hz = 440.0 * Math.pow(2, steps[i] / 12.0);
            AudioResource.playArray(makeNote(hz, 1.0, 0.5));
        }
        AudioResource.close();
        System.exit(0);
    }
}