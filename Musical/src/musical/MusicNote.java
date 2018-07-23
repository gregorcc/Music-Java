package Musical;
public interface MusicNote {
    /**
     * sets a note's pitch to a letter value in the range A-G
     * @param p: pitch value
     */
	public void setPitch(char p);
	
	/**
	 * sets a note's octave to a value in the range 0-8
	 * @param o: octave value
	 */
	public void setOctave(int o);
	
	/**
	 * sets a note MIDI value to a value corresponding to its
	 * intended pitch and octave values
	 * @param midi: pitch MIDI value
	 */
    public void setMidi(int midi);
    
    /**
     * sets a note's duration value to a value within the set
     * {1.0, 0.5, 0.25, 0.125, 0.0625} corresponding to whole,
     * half, quarter, eighth, and sixteenth note values, respectively
     * @param d: note's duration
     */
    public void setDuration(double d); 
    
    /**
     * raises or lowers the pitch of a note by the specified
     * number of steps (where a step is defined by MIDI value)
     * @param factor: number of steps to raise or lower pitch
     */
    public void changePitch(int factor);
    
    /**
     * sharpens a normal note; no effect on a sharp or flat
     */
	public void setSharp();
	
	/**
	 * flattens a normal note; no effect on a sharp or flat
	 */
	public void setFlat();
	
	/**
	 * removes accidental from sharp or flat; no effect on 
	 * already normal note
	 */
	public void setNormal();	
	
	/**
	 * returns a double value indicating note duration
	 */
    public double getDuration ();
    
    /**
     * returns pitch (letter in range A-G)
     */
    public char getPitch();
    
    /**
     * returns octave (number in range 0-8)
     */
	public int getOctave();
	
	/**
	 * returns a String containing the pitch and octave of the note
	 * if the note is sharp, includes the symbol '#' between pitch
	 * and octave; if flat, inclued the symbol 'b' between them
	 */
    public String getPitchSymbol();
    
    /**
     * returns MIDI value
     */
    public int getMidi();
    
    /**
     * returns frequency (in hertz) corresponding to MIDI value
     */
    public double getPitchFrequency();
    
    /**
     * returns true if note is a flat, false otherwise
     */
	public boolean isFlat();
	
	/**
	 * returns true if note is a sharp, false otherwise
	 */
	public boolean isSharp();
	
	/**
	 * returns true if note is neither sharp nor flat
	 */
	public boolean isNormal();
	
	/**
	 * returns a String containing note information in the form
	 * <duration> <pitch> <octave> <accidental>
	 * For example, if the note is C#4 quarter note, the returned
	 * String would be "0.25 C 4 sharp"
	 */
	public String toString();
	
	/** 
	 * uses AudioResource class to play the note according to
	 * its pitch and duration
	 */
	public void play();
}