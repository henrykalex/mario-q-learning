package agents.qLearning;

import java.util.ArrayList;
import java.util.Random;

public class MarioRandom {
  public static Random rnd = new Random();
  public static ArrayList<boolean[]> posibleChoices = new ArrayList<boolean[]>();
  public static ArrayList<boolean[]> choices = new ArrayList<>();

  public static void init() {
    posibleChoices.add(new boolean[] { false, true, false, true, false }); // right run
    posibleChoices.add(new boolean[] { false, true, false, true, true }); // right jump and run
    posibleChoices.add(new boolean[] { false, true, false, false, false }); // right
    posibleChoices.add(new boolean[] { false, true, false, false, true }); // right jump
    posibleChoices.add(new boolean[] { true, false, false, false, false }); // left
    posibleChoices.add(new boolean[] { true, false, false, true, false }); // left run
    posibleChoices.add(new boolean[] { true, false, false, false, true }); // left jump
    posibleChoices.add(new boolean[] { true, false, false, true, true }); // left jump and run
    // right run
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    choices.add(posibleChoices.get(0));
    // right jump and run
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    choices.add(posibleChoices.get(1));
    // right
    choices.add(posibleChoices.get(2));
    choices.add(posibleChoices.get(2));
    choices.add(posibleChoices.get(2));
    choices.add(posibleChoices.get(2));
    // right jump
    choices.add(posibleChoices.get(3));
    choices.add(posibleChoices.get(3));
    choices.add(posibleChoices.get(3));
    choices.add(posibleChoices.get(3));
    // left
    choices.add(posibleChoices.get(4));
    // left run
    choices.add(posibleChoices.get(5));
    // left jump
    choices.add(posibleChoices.get(6));
    // left jump and run
    choices.add(posibleChoices.get(7));
  }

  public static boolean[] getRandomActions() {
    return getRandomActions(MarioRandom.choices);
  }

  public static boolean[] getRandomActions(ArrayList<boolean[]> _choices) {
    return _choices.get(rnd.nextInt(_choices.size()));
  }
}