package sound.synthesizeMethods;

import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;

import java.util.ArrayList;

public class HighestNewbornOnString implements SynthesizingMethod {

    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
        NewBornOnly newBornOnly = new NewBornOnly();
        HighestAlive highestAlive = new HighestAlive();
        AllAlive allAlive = new AllAlive();


        ArrayList<Pair> pairs = new ArrayList<>();
        ArrayList<Pair> pairsNewborn = newBornOnly.cellsToPlay(automata);
        ArrayList<Pair> pairsHighestAlive = highestAlive.cellsToPlay(automata);
        ArrayList<Pair> pairsAllalive = allAlive.cellsToPlay(automata);
        for(Pair p : pairsAllalive) {
            if(pairsNewborn.contains(p) && pairsHighestAlive.contains(p)) {
                pairs.add(p);
            }
        }
        return pairs;
    }
}
