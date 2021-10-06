import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.*;
import java.lang.Math;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Track;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;

public class ValueUserPlugin implements ValueUserPluginInterface {

  private int[] snareParams = {100, 4};
  private int[] bassParams = {110, 4};
  private int[] tom1Params = {100, 4};
  private int[] tom2Params = {100, 4};
  private int[] floorTomParams = {100, 4};
  private int[] hiHatOpenParams = {100, 4};
  private int[] hiHatClosedParams = {90, 4};
  private int[] hiHatPedalParams = {90, 4};
  private int[] rideParams = {100, 4};
  private int[] crashParams = {120, 8};

  Sequencer sequencer;
  Sequence seq;
  Track track;
  Integer bpm = 90;

  Map<String, Sequence> tracks = new HashMap<String, Sequence>();
  Map<String, HashMap> trackSounds = new HashMap<String, HashMap>();

  @Override
  public String name() {
      return "Drum loop machine user plugin.";
  }

  @Override
  public Value user(Value... args) throws ARTException {
    System.out.println("ValueUserPlugin called with " + args.length + " argument" + (args.length == 1 ? "" : "s"));    
    System.out.println(args[0].toString());

    if (args[0].toString().equals("print")) {
      System.out.println(args[1].value());
      return new __done(args[0].iTerms());
    }

    if (args[0].toString().equals("bpm")) {
      return this.setBpm(args);
    }

    if(args[0].toString().equals("drumVolume")) {
      return this.drumVolume(args);
    }

    if(args[0].toString().equals("drumDuration")) {
      return this.drumDuration(args);
    }

    if (args[0].toString().equals("track")) {
      return this.track(args);
    }

    if (args[0].toString().equals("castInt")) {
      Double asDouble = (double)args[1].value();
      return new __int32((int)(Math.round(asDouble)), args[0].iTerms());
    }

    if (args[0].toString().equals("castReal")) {
      return new __real64(Double.parseDouble(args[1].value().toString()), args[0].iTerms());
    }

    if (args[0].toString().equals("track")) {
      return new __done(args[0].iTerms());
    }

    if (args[0].toString().equals("setSound")) {
      return this.setSound(args);
    }

    if(args[0].toString().equals("runTrack")) {
      return this.runTrack(args);
    }

    return new __string("No valid instruction", args[0].iTerms());
  }

//Adapted from geeksforgeeks https://www.geeksforgeeks.org/java-midi/
  public MidiEvent midiEvent(int command, int channel, int note, int velocity, int tick) {
    MidiEvent event = null;

    try { 

      ShortMessage note_store = new ShortMessage();
      note_store.setMessage(command, channel, note, velocity);
      event = new MidiEvent(note_store, tick);
    
  } catch (Exception ex)      {
      ex.printStackTrace();
    }

    return event;
  }

  public Value track(Value... args) throws ARTException {
    try {
      tracks.put(args[1].toString(), new Sequence(Sequence.PPQ, 4));
    } catch(Exception e){
      System.out.println(e.getMessage());
      return new __done(args[0].iTerms());
      }    
    trackSounds.put(args[2].toString(), new HashMap<Integer, String>());
    return new __done(args[0].iTerms());
  }

  public Value setBpm(Value... args) throws ARTException {
    this.bpm = (int)args[1].value();
    return new __done(args[0].iTerms());
  }

  public Value drumVolume(Value... args) throws ARTException {
    switch(args[1].toString()){
          case "snare": this.snareParams[0] = (int)args[1].value();
            break;

          case "bass": this.bassParams[0] = (int)args[1].value();
            break;

          case "tom1": this.tom1Params[0] = (int)args[1].value();
            break;

          case "tom2": this.tom2Params[0] = (int)args[1].value();
            break;

          case "floorTom": this.floorTomParams[0] = (int)args[1].value();
            break;

          case "crash": this.crashParams[0] = (int)args[1].value();
            break;

          case "ride": this.rideParams[0] = (int)args[1].value();
            break;

          case "hiHatOpen": this.hiHatOpenParams[0] = (int)args[1].value();
            break;

          case "hiHatClosed": this.hiHatClosedParams[0] = (int)args[1].value();
            break;

          case "hiHatPedal": this.hiHatPedalParams[0] = (int)args[1].value();
            break;
    }
    return new __done(args[0].iTerms());
  }

  public Value drumDuration(Value... args) throws ARTException {
    switch(args[1].toString()){
          case "snare": this.snareParams[1] = (int)args[1].value();
            break;

          case "bass": this.bassParams[1] = (int)args[1].value();
            break;

          case "tom1": this.tom1Params[1] = (int)args[1].value();
            break;

          case "tom2": this.tom2Params[1] = (int)args[1].value();
            break;

          case "floorTom": this.floorTomParams[1] = (int)args[1].value();
            break;

          case "crash": this.crashParams[1] = (int)args[1].value();
            break;

          case "ride": this.rideParams[1] = (int)args[1].value();
            break;

          case "hiHatOpen": this.hiHatOpenParams[1] = (int)args[1].value();
            break;

          case "hiHatClosed": this.hiHatClosedParams[1] = (int)args[1].value();
            break;

          case "hiHatPedal": this.hiHatPedalParams[1] = (int)args[1].value();
            break;
    }
    return new __done(args[0].iTerms());
  }

  public Value setSound(Value... args) throws ARTException {
    Map noteMap;
    switch (args[3].toString()) {
      case "snare": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "snare");
                    System.out.println(noteMap);
        break;

      case "bass": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "bass");
                    System.out.println(noteMap);
        break;

      case "tom1": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "tom1");
                    System.out.println(noteMap);
        break;

      case "tom2": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "tom2");
                    System.out.println(noteMap);
        break;

      case "floor_tom": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "floorTom");
                    System.out.println(noteMap);
        break;

      case "crash": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "crash");
                    System.out.println(noteMap);
        break;

      case "ride": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "ride");
                    System.out.println(noteMap);
        break;

      case "hiHatOpen": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "hiHatOpen");
                    System.out.println(noteMap);
        break;

      case "hiHatClosed": noteMap = this.trackSounds.get(args[1].toString());
                    noteMap.put(((int)args[2].value()), "hiHatClosed");
                    System.out.println(noteMap);
        break;
    }
    return new __done(args[0].iTerms());
  }

  public Value runTrack(Value... args) throws ARTException {
    HashMap<Integer, String> soundmapping = trackSounds.get(args[1].toString());
    Set<Integer> tickPositions = soundmapping.keySet();

    try {
      sequencer = MidiSystem.getSequencer();
      sequencer.open();
      seq = tracks.get(args[1].value());
      track = seq.createTrack();

      for (Integer tick : tickPositions) {
        switch (soundmapping.get(tick)) {
          case "snare": track.add(midiEvent(144, 9, 40, snareParams[0], tick));
            track.add(midiEvent(128, 9, 40, snareParams[0], tick + snareParams[1]));
            break;

          case "bass": track.add(midiEvent(144, 9, 39, bassParams[0], tick));
            track.add(midiEvent(128, 9, 39, bassParams[0], tick + bassParams[1]));
            break;

          case "tom1": track.add(midiEvent(144, 9, 48, snareParams[0], tick));
            track.add(midiEvent(128, 9, 48, tom1Params[0], tick + tom1Params[1]));
            break;

          case "tom2": track.add(midiEvent(144, 9, 47, snareParams[0], tick));
            track.add(midiEvent(128, 9, 47, tom2Params[0], tick + tom2Params[1]));
            break;

          case "floorTom": track.add(midiEvent(144, 9, 41, snareParams[0], tick));
            track.add(midiEvent(128, 9, 41, floorTomParams[0], tick + floorTomParams[1]));
            break;

          case "crash": track.add(midiEvent(144, 9, 49, snareParams[0], tick));
            track.add(midiEvent(128, 9, 49, crashParams[0], tick + crashParams[1]));
            break;

          case "ride": track.add(midiEvent(144, 9, 51, snareParams[0], tick));
            track.add(midiEvent(128, 9, 51, rideParams[0], tick + rideParams[1]));
            break;

          case "hiHatOpen": track.add(midiEvent(144, 9, 46, snareParams[0], tick));
            track.add(midiEvent(128, 9, 46, hiHatOpenParams[0], tick + hiHatOpenParams[1]));
            break;

          case "hiHatClosed": track.add(midiEvent(144, 9, 42, snareParams[0], tick));
            track.add(midiEvent(128, 9, 42, hiHatClosedParams[0], tick + hiHatClosedParams[1]));
            break;

          case "hiHatPedal": track.add(midiEvent(144, 9, 44, snareParams[0], tick));
            track.add(midiEvent(128, 9, 44, hiHatPedalParams[0], tick + hiHatPedalParams[1]));
            break;
        }
      }

      sequencer.setSequence(seq);
      sequencer.setTempoInBPM(bpm);
      sequencer.start();

      System.out.println("playing track..");
      while (sequencer.isRunning()) {
        //wait for track to finish
      }
      sequencer.close();
  
    } catch(Exception e) {
      e.printStackTrace();
    }
    return new __done(args[0].iTerms());
  }

}

