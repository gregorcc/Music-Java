package Musical;

/**
 *
 * @author Greg
 * simulates a rest in the form of MusicNote object
 */
public class Rest implements MusicNote{
    
private double duration;

public Rest(){
    this.duration = 1.0;
}
public Rest(double d){
    duration = d;
}
    @Override
    public void setPitch(char p) {}

    @Override
    public void setOctave(int o) {}

    @Override
    public void setMidi(int midi) {}

    @Override
    public void setDuration(double d) {
        if ((Rest.checkDoubleEquivalency(d, 1.0) == true) || (Rest.checkDoubleEquivalency(d, .5) == true) || (Rest.checkDoubleEquivalency(d, .25) == true)
                || (Rest.checkDoubleEquivalency(d, .125) == true) || (Rest.checkDoubleEquivalency(d, .0625) == true)) {
            duration = d;
        } else {
            throw new IllegalArgumentException(); //throws exception if out of bounds, this will cause the program to crash
            //this prevents undesired results but it should be changed if the option for program recovery is desired
        }
    }

    @Override
    public void changePitch(int factor) {}

    @Override
    public void setSharp() {}

    @Override
    public void setFlat() {}

    @Override
    public void setNormal() {}

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public char getPitch() {
        return 'R';
    }

    @Override
    public int getOctave() {
        return -1;
    }

    @Override
    public String getPitchSymbol() {
        return "R";
    }

    @Override
    public int getMidi() {
        return 0;
    }

    @Override
    public double getPitchFrequency() {
        return 0;
    }

    @Override
    public boolean isFlat() {
        return false;
    }

    @Override
    public boolean isSharp() {
        return false;
    }

    @Override
    public boolean isNormal() {
        return false;
    }

    @Override
    public void play() {
        AudioResource.playArray(AudioResource.makeNote(this.getPitchFrequency(), this.getDuration(), 1));
    }
    private static boolean checkDoubleEquivalency(double x, double y) { //method for checking if double values are equal
        double result;
        result = Math.abs(x - y);
        if ((result > Math.pow(10, -9)) && result != 0) { //if more precision is needed just change the exponent
            return false;
        }
        return true;
    }
    
}
