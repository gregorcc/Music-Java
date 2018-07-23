package Musical;

public class Measure implements Cloneable {

    private LinkedListWithNode noteList;
    private double measureQuantity;

    public Measure() {
        noteList = new LinkedListWithNode();
        measureQuantity = 0;
    }

    public Measure clone() {
        Measure copy = null;
        try {
            copy = (Measure) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Well this shouldn't happen!");
        }
        //copy.noteList = noteList.clone(); THIS IS NOT WORKING FOR SOME REASON
        return copy;
    }

    public boolean measureFull() {
        return getExcessCapacity() <= .003;
        // seems to be the threshold beyond which double is no
        // longer reliable - determined by trial & error
    }

    public double getExcessCapacity() {
        return 1.0 - measureQuantity;
    }

    public boolean addNote(MusicNote n) {
        if (getExcessCapacity() >= n.getDuration()) {
            noteList.add(n);
            measureQuantity += n.getDuration();
            return true;
        }
        return false;
    }

    public MusicNote[] toArray() {
        int numNotes = noteList.size();
        MusicNote[] measureNotes = new MusicNote[numNotes];
        for (int i = 1; i <= numNotes; i++) {
            measureNotes[i - 1] = (MusicNote) noteList.get(i);
        }
        return measureNotes;
    }

    public String toString() {
        String string = "";
        MusicNote[] notes = this.toArray();
        for (int x = 0; x < notes.length; x++) {
            string = string + notes[x] + " ";
        }
        return string;
    }

    public int size() {
        return noteList.size();
    }

    public void play() {
        MusicNote[] notes = this.toArray();
        for (MusicNote n : notes) {
            n.play();
        }
    }
}
