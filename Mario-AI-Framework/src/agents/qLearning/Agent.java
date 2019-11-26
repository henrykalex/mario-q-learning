package agents.qLearning;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
  int count = 0;
  int printOffset = 5;

  private float epsilon;
  private float gamma;
  private float alpha;
  HashMap<String, Float> qValues;
  private double epsilonDecay;

  @Override
  public void initialize(MarioForwardModel model, MarioTimer timer) {
    this.epsilon =  0.5f;
    this.gamma = 0.92f; 
    this.alpha = 0.1f;
    this.qValues = new HashMap<>();
    this.epsilonDecay = 0.0001;
    

    this.log("Actions: " + MarioActions.numberOfActions());
    this.log("obsGridHeight: " + model.obsGridHeight);
    this.log("obsGridWidth: " + model.obsGridWidth);
    this.log("NumLives: " + model.getNumLives());
    this.printState(model);
    // this.log("getMarioFloatVelocity: " + model.);
    // this.log("getMarioFloatVelocity: " + model.);
    // this.log("getMarioFloatVelocity: " + model.);
    MarioRandom.init();
  }

  void printStateArray(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      this.log("" + i + "-  " + Arrays.toString(matrix[i]));
    }
  }

  void printState(MarioForwardModel model) {
    this.log("" + this.count + "****************************************************************");
    this.log("RemainingTime: " + model.getRemainingTime());
    this.log("MarioMode: " + model.getMarioMode());
    this.log("CompletionPercentage: " + model.getCompletionPercentage());
    this.log("EnemiesFloatPos: " + Arrays.toString(model.getEnemiesFloatPos()));
    this.log("LevelFloatDimensions: " + Arrays.toString(model.getLevelFloatDimensions()));
    this.log("MarioCanJumpHigher: " + model.getMarioCanJumpHigher());
    this.log("MarioCompleteObservation:");
    printStateArray(model.getMarioCompleteObservation());
    this.log("MarioEnemiesObservation: ");
    printStateArray(model.getMarioEnemiesObservation());
    this.log("MarioFloatPos: " + Arrays.toString(model.getMarioFloatPos()));
    this.log("MarioFloatVelocity: " + Arrays.toString(model.getMarioFloatVelocity()));
    this.log("MarioSceneObservation: ");
    printStateArray(model.getMarioSceneObservation());
    this.log("MarioScreenTilePos: " + Arrays.toString(model.getMarioScreenTilePos()));
    this.log("ScreenCompleteObservation: " + Arrays.toString(model.getScreenCompleteObservation()));
    this.log("ScreenEnemiesObservation: " + Arrays.toString(model.getScreenEnemiesObservation()));
    this.log("ScreenSceneObservation: " + Arrays.toString(model.getScreenSceneObservation()));
    this.log("isMarioOnGround: " + model.isMarioOnGround());
    this.log("mayMarioJump: " + model.mayMarioJump());
    this.log("NumLives: " + model.getNumLives());
    this.log("hashCode: " + model.hashCode());
    this.log("****************************************************************");
  }

  String getActionsHash(boolean[] action){
    return  action[0] + "_" + action[1] + "_" + action[2] + "_" + action[3] + "_" + action[4];
  }

  String getStateHash(MarioForwardModel model) {
    return "" + model.getMarioFloatPos()[0] + "_" + model.getMarioFloatPos()[1];
  }

  boolean[] getMaxQValueMove(String state, ArrayList<boolean[]> posibleActions) {
    float maxValue = 0f;
    int maxIndex = 0;
    String hash = "";
    int index = 0;
    // ArrayList<boolean[]> posibleActions = MarioRandom.posibleChoices;
    //log(""+posibleActions.toString());
    for(boolean[] action: posibleActions){
      hash += state + "_" +getActionsHash(action);           
      // log("hash " + hash);
      if(qValues.containsKey(hash)) {
        float value = qValues.get(hash);
        if(value > maxValue) {
          maxValue = value;
          maxIndex = index;     
        }
      }
      hash = ""; //reset
      index ++;
    }

    if(maxValue == 0.0) {
      return MarioRandom.getRandomActions();
     }
     boolean[] bestMove = posibleActions.get(maxIndex);
     return bestMove;
  }

  private float getMaxQValue(String state, ArrayList<boolean[]> posibleActions) {
    float maxValue = -1;
    String hash = "";
    // ArrayList<boolean[]> posibleActions = MarioRandom.posibleChoices;
    for(boolean[] action: posibleActions) {
      hash += state + "_" +getActionsHash(action);        
      if(qValues.containsKey(hash)) {
        float value = qValues.get(hash);   
        if(value > maxValue) {
          maxValue = value;
        }
      }
      hash = ""; //reset
    }
    if(maxValue == 0.0) {
      return 0f;
    }
    return maxValue;
  }

  private void updateQValues(String prevState, float reward, String nextState) {
    float oldValue = this.qValues.containsKey(prevState) ? this.qValues.get(prevState): 0f;
    
    float newValue = oldValue + this.alpha*(reward + this.gamma * getMaxQValue(nextState, MarioRandom.posibleChoices) - oldValue);
    this.qValues.put(prevState,newValue);
  }

  @Override
  public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
    if (this.count % printOffset == 0 || model.getRemainingTime() < 500) {
      this.printState(model);
    }
    this.count++;

    /* Q learning */
    boolean[] selectedMove;
    // TODO: get posibleActions[] validating actual state
    if(MarioRandom.rnd.nextFloat() < this.epsilon) {
      // Explore
      // TODO: getRandomAction based in PosibleActions
      selectedMove = MarioRandom.getRandomActions();
    } else {
      // Exploit (largest q value)
      String state = getStateHash(model);
      ArrayList<boolean[]> posibleActions = MarioRandom.posibleChoices;
      selectedMove = getMaxQValueMove(state, posibleActions);
    }

    String prevStateHash = getStateHash(model);
    model.advance(selectedMove);
    String nextStateHash = getStateHash(model);
    if (this.count % printOffset == 0 || model.getRemainingTime() < 500) {
      this.printState(model);
    }

    float reward = 0f;
    
    updateQValues(prevStateHash, reward, nextStateHash);

    if(model.getGameStatus() != GameStatus.RUNNING) {
      // TODO: save qtable in static file
    }

    return selectedMove;
    // return new boolean[MarioActions.numberOfActions()];
  }

  @Override
  public String getAgentName() {
    return "QLearningAgent";
  }

  void log(String message) {
    System.out.println(message);
  }
}
