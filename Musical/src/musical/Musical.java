package Musical;

/**
 *
 * @author Greg Mich
 * A class for creating and modifying notes
 * 9/15/17
 * last updated 11/6/17
 * NOTE TO SELF, FIX SOME OF THE BOOLEAN GETTERS TO MAKE THEM LESS REDUNDANT
 */
import java.util.Scanner;

public class Musical implements MusicNote {

    private char pitchChar;
    private int MIDI, octave, octalDiff, midiDiff;
    private double duration, pitchFrequency;
    private String pitchSymbol, octaveStr, accidental;

    private double midiOctFreq[][] = new double[3][88]; //row1 is midi value, row 2 is frequency, row 3 is octave
    private String pitchMatrix[][] = new String[2][88]; //row1 is midi value, row 2 contains pitch symbols

    private int[] WhiteKeys = {21, 23, 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43, 45, //collection of white key MIDI values
        47, 48, 50, 52, 53, 55, 57, 59, 60, 62, 64, 65, 67, 69, 71, 72, 74, 76, 77,
        79, 81, 83, 84, 86, 88, 89, 91, 93, 95, 96, 98, 100, 101, 103, 105, 107, 108};

    private int[] BlackKeys = {22, 25, 27, 30, 32, 34, 37, 39, 42, 44, 46, 49, 51, 54, 56, 58, //collection of black key MIDI values
        61, 63, 66, 68, 70, 73, 75, 78, 80, 82, 85, 87, 90, 92, 94, 97, 99, 102, 104, 106};

    Musical() { //default constructor, sets note to middle C (C4) with a duration of 1/4 note
        accidental = "Normal";
        pitchChar = 'C';
        MIDI = 60;
        octave = 4;
        octaveStr = "4";
        pitchFrequency = 261.63;
        duration = .25;
        this.populateArray();

    }

    Musical(int midi, double dur) { // constructor that takes midi value and duration as an argument, midis seem to be easy to work with especially
        //  with computers so I have chosen them as a paramter, by default if a midi is selected corresponding to a sharp or a flat, it will default to a sharp due to ambiguity 
        // 11/06/17 added second param to constructor dur, allows user to create a music note with an initial duration as well
        if (midi < 21 || midi > 108) {
            throw new IllegalArgumentException("Invalid value, Midi must be between 21 and 108");
        }
        this.populateArray();
        try {
            if (Musical.checkIntArray(midi, BlackKeys) == 0) { //assign accidental to sharp if found
                accidental = "Sharp";
            } else {  //assign accidental to normal if not found
                accidental = "Normal";
            }
            MIDI = midi;
            pitchFrequency = midiOctFreq[1][MIDI - 21];
            octave = (int) midiOctFreq[2][MIDI - 21];
            octaveStr = Integer.toString(octave);
            pitchChar = pitchMatrix[1][MIDI - 21].charAt(0);
            duration = dur;
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Override
    public void setPitch(char p) {
        int oct = this.getOctave();
        int start = 0, range = 0; //used for range values to pass to array search method

        // finding range of octave so we search the correct area of the array
        if (oct == 0) {
            start = 0;
            range = 2;
        }
        if (oct == 1) {
            start = 3;
            range = 14;
        }
        if (oct == 2) {
            start = 15;
            range = 26;
        }
        if (oct == 3) {
            start = 27;
            range = 38;
        }
        if (oct == 4) {
            start = 39;
            range = 50;
        }
        if (oct == 5) {
            start = 51;
            range = 62;
        }
        if (oct == 6) {
            start = 63;
            range = 74;
        }
        if (oct == 7) {
            start = 75;
            range = 86;
        }
        if (oct == 8) {
            start = 87;
            range = 87;
        }

        String element = Character.toString(p); //casting char parameter as String
        int index = Musical.searchStringArray2d(element, pitchMatrix, start, range); //searching array for the character in the specified octave range

        if (index != -1) { //will only run if method does find the pitch symbol, if not nothing changes
            this.setMidi(index + 21); //update midi at index location
            pitchChar = p;
        }
    }

    @Override
    public void setOctave(int oct) {
        if (oct < 0 || oct > 8) {
            throw new IllegalArgumentException("Octave must be between 0 and 8");
        }
        try {
            octalDiff = oct - this.getOctave();//difference in octaves, used to update the note with setMidi
            this.setMidi(this.getMidi() + (octalDiff * 12)); //setting the new MIDI value
        } catch (IllegalArgumentException f) {
            f.getMessage();
        }
    }

    @Override
    public void setMidi(int midi) {
        midiDiff = this.getMidi(); //used for sharp/flat determination
        if (midi < 21) {
            midi = 21;
        }
        if (midi > 108) {
            midi = 108;
        }
        MIDI = midi;
        midiDiff = midiDiff - MIDI; //used for calculating sharps or flats based on whether this value is positive or negative

        if ((midiDiff > 0) && (Musical.checkIntArray(MIDI, BlackKeys) == 0)) { //if Midi change is lower and it is a black key then assign flat to accidental
            accidental = "Flat";
        }
        if ((midiDiff < 0) && (Musical.checkIntArray(MIDI, BlackKeys) == 0)) { //if Midi change is higher and it is a black key then assign sharp to accidental
            accidental = "Sharp";
        }
        if (Musical.checkIntArray(midi, WhiteKeys) == 0) {
            accidental = "Normal";
        }

        pitchFrequency = midiOctFreq[1][MIDI - 21]; //acquiring values from respective arrays, index corresponds to MIDI value - 21
        octave = (int) midiOctFreq[2][MIDI - 21];
        octaveStr = Integer.toString(octave);
        pitchChar = pitchMatrix[1][MIDI - 21].charAt(0);
    }

    @Override
    public void setDuration(double d) {
        if ((Musical.checkDoubleEquivalency(d, 1.0) == true) || (Musical.checkDoubleEquivalency(d, .5) == true) || (Musical.checkDoubleEquivalency(d, .25) == true)
                || (Musical.checkDoubleEquivalency(d, .125) == true) || (Musical.checkDoubleEquivalency(d, .0625) == true)) {
            duration = d;
        } else {
            throw new IllegalArgumentException(); //throws exception if out of bounds, this will cause the program to crash
            //this prevents undesired results but it should be changed if the option for program recovery is desired
        }
    }

    @Override
    public void changePitch(int factor) {
        int midiChange;
        midiChange = this.getMidi() + factor;

        if (midiChange < 21) { //if midi is lower than range set it to lowest value
            midiChange = 21;
        }
        if (midiChange > 108) { //if midi is higher than range set it to highest value
            midiChange = 108;
        }
        this.setMidi(midiChange); //update note using new midi
    }

    @Override
    public void setSharp() {
        if ((this.isNormal() == true) && (Musical.checkIllegalSharps(this.getPitch() + "") == false)) { //checks for illegal sharp pitches
            this.setMidi(MIDI + 1); //increase by half step in MIDI
            accidental = "Sharp";
        }
    }

    @Override
    public void setFlat() {
        if ((this.isNormal() == true) && (Musical.checkIllegalFlats(this.getPitch() + "") == false)) { //checks for illegal flat pitches
            this.setMidi(MIDI - 1);//decrease by half step in MIDI
            accidental = "Flat";
        }
    }

    @Override
    public void setNormal() {
        if ((this.isNormal() != true) && (this.isSharp() == true)) {
            this.setMidi(MIDI - 1); //reduce the half step by MIDI
            accidental = "Normal";
        }
        if ((this.isNormal() != true) && (this.isFlat() == true)) {
            this.setMidi(MIDI + 1); //increase the half step by MIDI
            accidental = "Normal";
        }
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public char getPitch() {
        return pitchChar;
    }

    @Override
    public int getOctave() {
        return octave;
    }

    @Override
    public String getPitchSymbol() {
        if (this.isSharp() == true) { //builds sharp string
            pitchSymbol = this.getPitch() + "#" + octaveStr;
        }
        if (this.isFlat() == true) { //builds flat string
            pitchSymbol = this.getPitch() + "b" + octaveStr;
        }
        if (this.isNormal() == true) { //builds normal string
            pitchSymbol = this.getPitch() + octaveStr;
        }
        return pitchSymbol;
    }

    @Override
    public int getMidi() {
        return MIDI;
    }

    @Override
    public double getPitchFrequency() {
        return pitchFrequency;
    }

    @Override
    public boolean isFlat() {
        if (accidental.equalsIgnoreCase("Flat") == true) { //checks accidental, situationally dependant on changes in pitch
            return true;
        }
        return false;
    }

    @Override
    public boolean isSharp() { //checks accidental, situationally dependant on changes in pitch
        if (accidental.equalsIgnoreCase("Sharp") == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNormal() {
        if (Musical.checkIntArray(MIDI, WhiteKeys) == 0) { //checks array for presence of current MIDI
            return true;
        }
        if (Musical.checkIntArray(MIDI, WhiteKeys) == -1) {
            return false;
        }

        return false;
    }

    @Override
    public void play() { //plays notes using the makeNote and playArray methods from Audio Resource with standard volume of 1
        AudioResource.playArray(AudioResource.makeNote(this.getPitchFrequency(), this.getDuration(), 1));

    }

    private void populateArray() { //this method is called in the constructors to access the arrays that will make finding and assigning the desired values much easier
        for (int i = 0; i < 88; i++) { //populate midi row
            midiOctFreq[0][i] = 21 + i;
        }

        //populates frequency row
        for (int i = 0; i < 88; i++) {
            midiOctFreq[1][i] = 27.5 * (Math.pow(2, (i / 12.0))); //this will assign the correct frequency value to the elements required, the forumla is: base frequency (27.5)
            // * 2 ^ (i/12), where i starts as 0 and increments by one each time and i = (21 - MIDI)
        }

        //populates octave row
        for (int i = 0; i < 3; i++) {
            midiOctFreq[2][i] = 0;
        }
        for (int i = 3; i < 15; i++) {
            midiOctFreq[2][i] = 1;
        }
        for (int i = 15; i < 27; i++) {
            midiOctFreq[2][i] = 2;
        }
        for (int i = 27; i < 39; i++) {
            midiOctFreq[2][i] = 3;
        }
        for (int i = 39; i < 51; i++) {
            midiOctFreq[2][i] = 4;
        }
        for (int i = 51; i < 63; i++) {
            midiOctFreq[2][i] = 5;
        }
        for (int i = 63; i < 75; i++) {
            midiOctFreq[2][i] = 6;
        }
        for (int i = 75; i < 87; i++) {
            midiOctFreq[2][i] = 7;
        }

        midiOctFreq[2][87] = 8;

        //now time to populate the string array notice that the array element is accessed through subtracting 21(the literal midi starting point)
        //from i to get to the desired value, this is different from the previous loops.
        for (int i = 21; i < 106; i = i + 12) {
            pitchMatrix[1][i - 21] = "A";
        }
        for (int i = 23; i < 108; i = i + 12) {
            pitchMatrix[1][i - 21] = "B";
        }
        for (int i = 24; i < 109; i = i + 12) {
            pitchMatrix[1][i - 21] = "C";
        }
        for (int i = 26; i < 99; i = i + 12) {
            pitchMatrix[1][i - 21] = "D";
        }
        for (int i = 28; i < 101; i = i + 12) {
            pitchMatrix[1][i - 21] = "E";
        }
        for (int i = 29; i < 102; i = i + 12) {
            pitchMatrix[1][i - 21] = "F";
        }
        for (int i = 31; i < 104; i = i + 12) {
            pitchMatrix[1][i - 21] = "G";
        }

        // sharps are contained here
        for (int i = 22; i < 107; i = i + 12) {
            pitchMatrix[1][i - 21] = "A#";
        }
        for (int i = 25; i < 98; i = i + 12) {
            pitchMatrix[1][i - 21] = "C#";
        }
        for (int i = 27; i < 100; i = i + 12) {
            pitchMatrix[1][i - 21] = "D#";
        }
        for (int i = 30; i < 103; i = i + 12) {
            pitchMatrix[1][i - 21] = "F#";
        }
        for (int i = 32; i < 105; i = i + 12) {
            pitchMatrix[1][i - 21] = "G#";

        }

    }

    @Override
    public String toString() {
        if (this.isSharp() == true) {
            return duration + " " + pitchChar + " " + octave + " Sharp";
        }
        if (this.isFlat() == true) {
            return duration + " " + pitchChar + " " + octave + " Flat";
        }

        return duration + " " + pitchChar + " " + octave;
    }

    public static void runAudioTest() {
        Musical testClass = new Musical();
        testClass.setDuration(.25);
        System.out.println("Testing MIDI# 21 - 108");
        for (int i = 21; i < 109; i++) {
            testClass.setMidi(i);
            System.out.println(testClass.getMidi() + " : " + testClass);
            testClass.play();
        }
        testClass.setMidi(21);
        testClass.setDuration(1.0);
        System.out.println("Testing octaves of A, starting at 0");
        for (int i = 0; i < 9; i++) {
            testClass.setOctave(i);
            System.out.println("Octave: " + testClass.getOctave() + "  " + testClass);
            testClass.play();
        }
        testClass.setMidi(60);
        System.out.println("Testing durations starting at C4");
        testClass.setDuration(.0625);
        testClass.play();
        testClass.setMidi(63);
        testClass.setDuration(.125);
        testClass.play();
        testClass.setMidi(66);
        testClass.setDuration(.25);
        testClass.play();
        testClass.setMidi(69);
        testClass.setDuration(.5);
        testClass.play();
        testClass.setMidi(72);
        testClass.setDuration(1.0);
        testClass.play();

        testClass.setOctave(5);
        System.out.println("Testing pitches of octave 5: ");

        testClass.setPitch('C');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('D');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('E');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('F');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('G');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('A');
        System.out.println(testClass);
        testClass.play();

        testClass.setPitch('B');
        System.out.println(testClass);
        testClass.play();

        testClass.setDuration(1.0);
        testClass.setMidi(21);
        for (int i = 0; i < 9; i++) {
            System.out.println("Increasing by 10: " + testClass.getMidi());
            testClass.changePitch(10);
            testClass.play();
        }
        testClass.setMidi(60);
        System.out.println(testClass);
        testClass.play();

        System.out.println("Making sharp: ");
        testClass.setSharp();
        System.out.println(testClass);
        testClass.play();

        System.out.println("Making flat: ");
        testClass.setNormal();
        testClass.setFlat();
        System.out.println(testClass);
        testClass.play();
    }

    //private static methods used to help class are defined below
    private static boolean checkDoubleEquivalency(double x, double y) { //method for checking if double values are equal
        double result;
        result = Math.abs(x - y);
        if ((result > Math.pow(10, -9)) && result != 0) { //if more precision is needed just change the exponent
            return false;
        }
        return true;
    }

    //returns true if illegal flat is found
    private static boolean checkIllegalFlats(String comp) {
        String flat1, flat2;
        flat1 = "C"; //cannot have flat C or F
        flat2 = "F";
        if ((comp.equals(flat1) || comp.equals(flat2))) {
            return true;
        }
        return false;
    }

    //returns true if illegal sharp is found
    private static boolean checkIllegalSharps(String comp) {
        String sharp1, sharp2;
        sharp1 = "E"; //cannot have sharp E or B
        sharp2 = "B";
        if ((comp.equals(sharp1) || comp.equals(sharp2))) {
            return true;
        }
        return false;
    }

    private static int searchStringArray2d(String element, String[][] array, int start, int extent) { //used to search String arrays by brute force, the arrays in this class
        //are small so this shouldn't be too time consuming or painful for the CPU
        for (int i = start; i <= extent; i++) {
            if (array[1][i].equals(element)) {
                return i; //return index of element
            }

        }
        return -1; //return -1 if not found
    }

    private static int checkIntArray(int element, int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return 0; //returns 0 if found
            }
        }
        return -1; //returns -1 if not found
    }

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        Musical song = new Musical();
        String userInput;
        int userSelect = 0;
        double userSelectD = 0.0;
        boolean quitKey = false;
        System.out.println("Welcome to Greg's note creation class\nChoose from the following options\n");
        while (quitKey == false) {

            System.out.println("0: Quit\n1: Change note MIDI\n2: Change note Pitch\n3: Change note Octave\n4: Change note duration\n"
                    + "5: Set Pitch\n6: Set Note to flat\n7: Set note to sharp\n8: Set note to normal\n9: Play your note\n10: Run automated tests"
                    + "\nYour note is: " + song.getPitchSymbol() + " , " + song);

            userInput = kb.nextLine();
            try {
                userSelect = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid option");
                userSelect = 11;
            }
            if (userSelect == 0) {
                System.out.println("Goodbye");
                quitKey = true;
            } //end user select 0
            if (userSelect == 1) {
                System.out.println("Enter a MIDI value between 21 and 108");
                userInput = kb.nextLine();
                try {
                    userSelect = Integer.parseInt(userInput);
                    song.setMidi(userSelect);
                } catch (NumberFormatException e) {
                    System.out.println("That is not a valid option");
                }
            } //end user select 1
            else if (userSelect == 2) {
                System.out.println("Enter a factor to change pitch by");
                userInput = kb.nextLine();
                try {
                    userSelect = Integer.parseInt(userInput);
                    song.changePitch(userSelect);
                } catch (NumberFormatException e) {
                    System.out.println("That is not a valid option");
                }
            } //end 2
            else if (userSelect == 3) {
                System.out.println("Enter an octave between 0 and 8");
                userInput = kb.nextLine();
                try {
                    userSelect = Integer.parseInt(userInput);
                    song.setOctave(userSelect);
                } catch (NumberFormatException e) {
                    System.out.println("That is not a valid option");
                }
            } //end 3
            else if (userSelect == 4) {
                System.out.println("Enter a duration in decimal form: 1.0, .5, .25, .125, or .0625");
                userInput = kb.nextLine();
                try {
                    userSelectD = Double.parseDouble(userInput);
                    song.setDuration(userSelectD);
                } catch (NumberFormatException e) {
                    System.out.println("That is not a valid option");
                }
            } //end 4
            else if (userSelect == 5) {
                char in;
                System.out.println("Enter a pitch character: ");
                userInput = kb.nextLine();
                in = userInput.charAt(0);
                song.setPitch(in);
            } //end 5
            else if (userSelect == 6) {
                song.setFlat();
            } //end 6
            else if (userSelect == 7) {
                song.setSharp();
            } //end 7
            else if (userSelect == 8) {
                song.setNormal();
            } //end 8
            else if (userSelect == 9) {
                song.play();
            } //end 9
            else if (userSelect == 10) {
                song.runAudioTest();
            } //end 10
        } // end while

        AudioResource.close();
    } //end main

} //end Musical