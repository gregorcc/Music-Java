package Musical;

/**
 *
 * @author Greg Mich
 * A class for creating and modifying songs
 * 11/4/17
 * last updated 11/6/17
 */
import java.util.*;

public class Song {

    // measures is a list of Measures containing MusicNotes
    private LinkedListWithNode measures;
    // length is the number of Measures in this song
    private int length;

    public Song() {
        measures = new LinkedListWithNode();
        length = 0;
    }

    //adds measure to the list
    public void addMeasure(int position, Measure m) {
        if (position > length + 1) {
            position = length + 1;
            System.out.println(length + 1);
        }
        measures.add(m, position);
        length++;
    }
//adds measure to the end of the song

    public void addAfter(Measure m) {
        measures.add(m);
        length++;
    }

    //removes measure from the list
    public void deleteMeasure(int position) {
        if (measures.remove(position)) //only do this if this condition is true, if the index does contain anything then this will not execute
        {
            length--;
        } else {
            System.out.println("No measure exists there");
        }
    }
    //deletes the last measure in the list

    public void deleteEnd() {
        measures.remove(length);
        length--;
    }

    //changes the pitch of all the music notes of every measure of the song 
    public void changeKey(int factor) {
        LinkedListWithNode newMeasures = new LinkedListWithNode(); //create new list of Measures that original list will point to
        Measure cursor = new Measure(); // Will point to the current measure to be modified
        Measure newMes = new Measure();
        Object[] data; //array to point to the array of musicNotes contained in each measure
        MusicNote current = new Musical();
        int newMidi;
        double dur;
        for (int i = 1; i <= length; i++) { //do this for all the measures
            cursor = (Measure) measures.get(i); //cursor points to the measure object of the current "index"
            data = cursor.toArray();
            for (int j = 0; j < data.length; j++) { //loop for all the music notes in the measure
                try { //attempt to cast current MusicNote as a Musical object
                    current = (Musical) data[j];
                    newMidi = current.getMidi() + factor;
                    dur = current.getDuration();
                    newMes.addNote(new Musical(newMidi, dur));
                } catch (RuntimeException e) { //if the cast has failed, a Rest has likely been encountered instead, so we will add a rest to the new measure instead
                    current = (Rest) data[j];
                    dur = current.getDuration();
                    newMes.addNote(new Rest(dur));
                }
            }

            newMeasures.add(newMes); //new linked list of measures
            newMes = new Measure(); //make instance linkedlist point to the newly modified list
        }
        this.measures = newMeasures; //set this objects measures to point to the newMeasures, all of the objects floating in memory should be
        //garbage collected
    }

    //Returns a sample song to be played or used as a template
    public static Song sampleSong() {
        Song sample = new Song();
        Measure Current = new Measure();

        Current.addNote(new Musical(64, .5));
        Current.addNote(new Musical(74, .5));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(71, .25));
        Current.addNote(new Musical(71, .25));
        Current.addNote(new Musical(69, .25));
        Current.addNote(new Musical(67, .25));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(67, .25));
        Current.addNote(new Musical(72, .25));
        Current.addNote(new Musical(71, .25));
        Current.addNote(new Musical(71, .25));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(69, .5));
        Current.addNote(new Musical(67, .5));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(74, .25));
        Current.addNote(new Musical(71, .5));
        Current.addNote(new Musical(69, .25));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(69, .25));
        Current.addNote(new Musical(67, .5));
        Current.addNote(new Musical(64, .25));
        sample.addAfter(Current);
        Current = new Measure();
        Current.addNote(new Musical(64, .25));
        Current.addNote(new Musical(62, .75));
        sample.addAfter(Current);
        return sample;
    }

    public void playSong() {
        for (int mct = 1; mct <= length; mct++) {
            Measure m = (Measure) measures.get(mct);
            // Print out notes played (in case there's no sound)
            System.out.println("Playing  " + m);
            m.play();
        }
    }

    //returns number of measures
    public int getLength() {
        return length;
    }

    @Override
    //prints the song in progress
    public String toString() {
        String display = "";
        for (int i = 1; i <= length; i++) {
            display = display + i + ":" + "[" + measures.get(i) + "] ";
        }
        return display;
    }

    //I got tired of writing try catch blocks so this handles when a user inputs a string that doesn't contain an integer for menu selection stuff
    private static boolean validateIntegerInput(String str) {
        boolean failed = false;
        try {
            int select = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            failed = true;
            System.out.println("\nInvalid input, try again\n");
        }
        return !failed;
    }
    //method for checking if double values are equal

    private static boolean checkDoubleEquivalency(double x, double y) {
        double result;
        result = Math.abs(x - y);
        if ((result > Math.pow(10, -9)) && result != 0) { //if more precision is needed just change the exponent
            return false;
        }
        return true;
    }
    //Used for checking that a rest has one of the lengths that are allowed for notes, the Rest class does not limit the time that can be provided to it
    //so this will handle that from becomeing a problem if a user adds a weird rest time
    private static boolean checkRestTimeFormat(double d) {
        boolean failed = true;
        if ((Song.checkDoubleEquivalency(d, 1.0) == true) || (Song.checkDoubleEquivalency(d, .5) == true) || (Song.checkDoubleEquivalency(d, .25) == true)
                || (Song.checkDoubleEquivalency(d, .125) == true) || (Song.checkDoubleEquivalency(d, .0625) == true)) {
            failed = false;
        }
        return !failed;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the song creator.");
        Song track = new Song();
        Scanner kb = new Scanner(System.in);
        String input = "";
        int selection = 0;
        double selectionDouble = 0;
        boolean mainQuit = false, measureQuit = false, noteQuit = false;
        while (mainQuit == false) {
            measureQuit = false; //reset measureQuit
            System.out.println("Main Menu:");
            System.out.println("Current song:" + track + "\n1: Play Song\n2: Add Measure\n3: Delete Measure\n4: Change Pitch\n5: Play Sample song\n6: Start with the sample song\n0: Quit");
            input = kb.nextLine();
            if (Song.validateIntegerInput(input)) { //this recurring line just checks to make sure the user has given us a number using a private static method I have written
                selection = Integer.parseInt(input);
                if (selection == 1) {
                    track.playSong();
                } //end playSong
                else if (selection == 2) {
                    Measure current = new Measure(); //new measure that we will be working on
                    while (measureQuit == false) {
                        noteQuit = false; //reset noteQuit
                        System.out.println("Measure Creation Menu:\nCurrent measure:" + current);
                        System.out.println("1: Add Note\n2: Add Rest\n3: Save measure to song\n4: Save measure at specific index\n5: Cancel");
                        input = kb.nextLine();
                        if (Song.validateIntegerInput(input)) {
                            selection = Integer.parseInt(input);
                            if (selection == 1) { //add note
                                if (!current.measureFull()) { //check to see if measure is full first
                                    Musical newNote = new Musical(); //new Musical object that we will be working on, possibly adding it to the list (a measure)
                                    while (noteQuit == false) {
                                        System.out.println("NoteMaker Menu:\nCurrent Note: " + newNote);
                                        System.out.println("1: Set note MIDI\n2: Set note pitch\n3: Set note Octave\n4: Set note duration\n"
                                                + "5: Change pitch\n6: Set Note to flat\n7: Set note to sharp\n8: Set note to normal\n9: Save note to measure\n10: Cancel");
                                        input = kb.nextLine();
                                        if (Song.validateIntegerInput(input)) {
                                            selection = Integer.parseInt(input);
                                            if (selection == 1) { //change midi
                                                System.out.println("Enter a Midi from 21 to 108");
                                                input = kb.nextLine();
                                                try { //We need to prevent my implemntation of MusicNote from crashing when a user enters in a MIDI that is out of range
                                                    selection = Integer.parseInt(input);
                                                    newNote.setMidi(selection);
                                                } catch (RuntimeException e) {
                                                    System.out.println("\nEnter a number from 21 to 108\n");
                                                }
                                            } else if (selection == 2) { //Set pitch
                                                System.out.println("Enter Pitch Symbol");
                                                input = kb.nextLine();
                                                newNote.setPitch(input.charAt(0));
                                            } else if (selection == 3) { //change octave
                                                System.out.println("Enter an Octave from 1-8");
                                                input = kb.nextLine();
                                                try {// we need to prevent crashes due to my implementation
                                                    selection = Integer.parseInt(input);
                                                    newNote.setOctave(selection);
                                                } catch (RuntimeException e) {
                                                    System.out.println("\nEnter a number from 1 to 8\n");
                                                }
                                            } else if (selection == 4) { //change duration
                                                System.out.println("Enter a duration in decimal form: 1.0, .5, .25, .125, or .0625");
                                                input = kb.nextLine();
                                                try {
                                                    selectionDouble = Double.parseDouble(input);
                                                    newNote.setDuration(selectionDouble);
                                                } catch (RuntimeException e) {
                                                    System.out.println("\nThat is not a valid duration\n");
                                                }
                                            } else if (selection == 5) { //Change pitch
                                                System.out.println("Enter a factor to change the pitch by");
                                                input = kb.nextLine();
                                                if (Song.validateIntegerInput(input)) {
                                                    selection = Integer.parseInt(input);
                                                    newNote.changePitch(selection);
                                                }
                                            } else if (selection == 6) { //set flat
                                                newNote.setFlat();
                                            } else if (selection == 7) { //set sharp
                                                newNote.setSharp();
                                            } else if (selection == 8) { //set normal
                                                newNote.setNormal();
                                            } else if (selection == 9) { //save note to measure
                                                current.addNote(newNote);
                                                noteQuit = true;
                                            } else if (selection == 10) { //cancel
                                                noteQuit = true;
                                            }
                                        }

                                    }//end of while quit3
                                } else {
                                    System.out.println("\nYour measure is full!\n");
                                }
                            }//end of notemaker
                            else if (selection == 2) { //Add rest
                                Rest newRest = new Rest();
                                System.out.println("Enter a duration in decimal form: 1.0, .5, .25, .125, or .0625 or 0 to cancel");
                                input = kb.nextLine();
                                try {
                                    selectionDouble = Double.parseDouble(input);
                                    if (!Song.checkDoubleEquivalency(selectionDouble, 0) && Song.checkRestTimeFormat(selectionDouble)) { //we need to check that the user
                                        //followed directions and entered in an acceptable duration
                                        newRest.setDuration(selectionDouble);
                                        current.addNote(newRest);
                                    } else {
                                        System.out.println("Please select an appropriate rest duration");
                                    }
                                } catch (RuntimeException e) { //catch if a user enters in something random that will cause crashes
                                    System.out.println("\nThat is not a valid duration\n");
                                }
                            } else if (selection == 3) { //Add measure to song
                                if (current.getExcessCapacity() > .001) {
                                    System.out.println("\nYour measure was not full, so a rest has been added to fill the excess capacity.\n");
                                    Rest gap = new Rest();
                                    gap.setDuration(current.getExcessCapacity());
                                    current.addNote(gap);
                                }
                                track.addAfter(current);
                                measureQuit = true;
                            } //end save measure
                            //add at index
                            else if (selection == 4) { //Add measure to song
                                if (current.getExcessCapacity() > .001) {
                                    System.out.println("\nYour measure was not full, so a rest has been added to fill the excess capacity.\n");
                                    Rest gap = new Rest();
                                    gap.setDuration(current.getExcessCapacity());
                                    current.addNote(gap);
                                }
                                System.out.println("What position would you like to insert the measure at?");
                                input = kb.nextLine();
                                if (Song.validateIntegerInput(input)) {
                                    selection = Integer.parseInt(input);
                                    track.addMeasure(selection, current);
                                    measureQuit = true;
                                }
                            } //end save measure //end add at index
                            else if (selection == 5) {
                                measureQuit = true;
                            } //end cancel
                        }
                    }//end quit2

                } else if (selection == 3) {
                    System.out.println("Which position would you like to delete?");
                    input = kb.nextLine();
                    if (Song.validateIntegerInput(input)) {
                        selection = Integer.parseInt(input);
                        track.deleteMeasure(selection);
                    }
                } else if (selection == 4) { //change pitch
                    System.out.println("Enter the factor to change the song pitch by: ");
                    input = kb.nextLine();
                    if (Song.validateIntegerInput(input)) {
                        selection = Integer.parseInt(input);
                        try {
                            track.changeKey(selection);
                        } catch (RuntimeException e) {
                            System.out.println("Changing the pitch by this factor will put one of your notes out of the range of MIDI values,\ntry reducing the factor "
                                    + "if increasing pitch or try increasing the factor if lowering pitch.");
                        }
                    }
                } else if (selection == 5) { //sample song
                    System.out.println("All Star");
                    Song sample = new Song();
                    sample = Song.sampleSong();
                    sample.playSong();

                } else if (selection == 6) {
                    track = Song.sampleSong();
                } else if (selection == 0) { //quit
                    mainQuit = true;
                    System.out.println("Goodbye!");
                    AudioResource.close();
                }
            }
        }
    }
}