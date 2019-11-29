package agents.qLearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
  public static int instanceCount = 0;
  public static float maxReward= 0f;
  int count = 0;
  int printOffset = 5;
  float totalReward = 0;

  QTable qTable;
  private float epsilonDecay = 0.001f;
  
  boolean play = false;

  public Agent(){
    super();
  }
  public Agent(boolean play){
    super();
    this.play = play;
  }

  @Override
  public void initialize(MarioForwardModel model, MarioTimer timer) {
    qTable = new QTable(0.5f - (this.epsilonDecay * Agent.instanceCount));
    Agent.instanceCount++;
    this.totalReward = 0f;
    this.log("Game init " + Agent.instanceCount);
    this.log("Game qValues size " + qTable.qValues.size());
    // this.log("Actions: " + MarioActions.numberOfActions());
    // this.log("obsGridHeight: " + model.obsGridHeight);
    // this.log("obsGridWidth: " + model.obsGridWidth);
    // this.log("NumLives: " + model.getNumLives());
    // this.printState(model);
    MarioRandom.init();
    // this.qTable.clearQTable();
    this.qTable.readQTable();
  }

  void printStateArray(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      this.log("" + i + "-  " + Arrays.toString(matrix[i]));
    }
  }

  void printState(MarioForwardModel model) {
    this.log("" + this.count + "****************************************************************");
    // // this.log("RemainingTime: " + model.getRemainingTime());
    // // this.log("MarioMode: " + model.getMarioMode());
    // this.log("CompletionPercentage: " + model.getCompletionPercentage());
    // this.log("EnemiesFloatPos: " + Arrays.toString(model.getEnemiesFloatPos()));
    // this.log("LevelFloatDimensions: " +
    // Arrays.toString(model.getLevelFloatDimensions()));
    // this.log("MarioCanJumpHigher: " + model.getMarioCanJumpHigher());
    // this.log("MarioCompleteObservation:");
    // printStateArray(model.getMarioCompleteObservation());
    // this.log("MarioEnemiesObservation: ");
    // printStateArray(model.getMarioEnemiesObservation());
    // this.log("MarioFloatPos: " + Arrays.toString(model.getMarioFloatPos()));
    // this.log("MarioFloatVelocity: " +
    // Arrays.toString(model.getMarioFloatVelocity()));
    // this.log("MarioSceneObservation: ");
    // printStateArray(model.getMarioSceneObservation());
    // this.log("MarioScreenTilePos: " +
    // Arrays.toString(model.getMarioScreenTilePos()));
    // this.log("ScreenCompleteObservation: ");
    // printStateArray(model.getScreenCompleteObservation());
    // this.log("ScreenEnemiesObservation: ");
    // printStateArray(model.getScreenEnemiesObservation());
    // this.log("ScreenSceneObservation: ");
    // printStateArray(model.getScreenSceneObservation());
    // this.log("isMarioOnGround: " + model.isMarioOnGround());
    // this.log("mayMarioJump: " + model.mayMarioJump());
    // // this.log("NumLives: " + model.getNumLives()); // Always 0
    // // this.log("hashCode: " + model.hashCode()); // Hash number
    this.log("*****************************************************" + model.getGameStatus());
  }

  String getStateHash(MarioForwardModel model) {
    // String marioFloatPos = "p_" + (int)model.getMarioFloatPos()[0] + "_" +
    // (int)model.getMarioFloatPos()[1];
    String marioStatus = "jh_" + (model.getMarioCanJumpHigher() ? "1" : "0");
    // marioStatus += "_mg_" + (model.isMarioOnGround() ? "1" : "0") 
    // marioStatus += "_mj_" + (model.mayMarioJump() ? "1" : "0");
    String marioCompleteObs = "o";
    int startPos = 8 - 6;
    int endPos = 8 + 7;
    int[][] completeObs = model.getMarioCompleteObservation();
    startPos = (startPos < 0 ? 0 : startPos);
    endPos = (endPos > completeObs.length - 1 ? completeObs.length - 1 : endPos);
    int[][] completeObsSlice = Arrays.copyOfRange(completeObs, startPos, endPos);
    int length = completeObsSlice.length;
    for(int i = 0; i < length; i++) {
      int subStartPos = 8 - 5;// marioScreenTile[0] - 3;
      int subEndPos = 8 + 6;// marioScreenTile[0] + 3;
      completeObsSlice[i] =  Arrays.copyOfRange(completeObsSlice[i], subStartPos, subEndPos);
    }

    // Flaten
    for (int[] val1 : completeObsSlice) {
      for (int val2 : val1) {
        marioCompleteObs += val2;
      }
    }

    return marioStatus + "_" + marioCompleteObs;
    // return marioCompleteObs;
  }

  int zeroCount = 0;
  private float calculateReward(MarioForwardModel model) {
    float f = 0f;
    float v = model.getMarioFloatVelocity()[0] * 2;

    if((int)v <= 1){
      if(zeroCount > 2) {
        v = -15f;
      }
      zeroCount++;
    }else {
      zeroCount = 0;
    }
    if (model.getGameStatus() == GameStatus.LOSE) {
      f = -15f;
    }
    if (model.getGameStatus() == GameStatus.TIME_OUT) {
      f = -5f;
    }
    return 0f + v + f;
  }

  ArrayList<boolean[]> getPosibleChoices(MarioForwardModel model) {
    ArrayList<boolean[]> result = new ArrayList<boolean[]>();
    int length = MarioRandom.posibleChoices.size();
    if(zeroCount < 2){
      for (int i = 0; i < length; i++) {
        if (i == 1 || i == 3) {
          result.add(MarioRandom.posibleChoices.get(i));
        }
      } 
    }
    return result;
  }

  @Override
  public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
    // if (this.count % printOffset == 0 || model.getRemainingTime() < 500) {
    //   this.printState(model);
    // }
    this.count++;

    /* Q learning */
    boolean[] selectedMove;
    String state = getStateHash(model);
    // this.log("state: " + state);
    // TODO: get posibleActions[] validating actual state
    ArrayList<boolean[]> posibleActions = this.getPosibleChoices(model);
    selectedMove = qTable.getAction(state, this.play);
    if (this.play) {
      return selectedMove;
    }
    model.advance(selectedMove);
    // if (this.count % printOffset == 0 || model.getRemainingTime() < 500) {
    //   this.printState(model);
    // }
    float reward = calculateReward(model);
    this.totalReward += reward;
    String nextStateHash = getStateHash(model);
    //this.log("reward" + reward);
    qTable.updateQValues(state, selectedMove, reward, nextStateHash);
    
    if (this.totalReward > Agent.maxReward) {
      Agent.maxReward = this.totalReward;
    }

    if (model.getGameStatus() != GameStatus.RUNNING) {
      this.log("GameStatus " + model.getGameStatus());
      this.log("epsilon " + this.qTable.epsilon);
      this.log("totalReward" + this.totalReward);
      this.log("maxReward" + Agent.maxReward);
      // this.qTable.printQTable();
      this.qTable.saveQTable();
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
