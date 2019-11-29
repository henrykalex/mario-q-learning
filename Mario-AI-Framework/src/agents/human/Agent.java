package agents.human;

import java.util.Arrays;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

public class Agent extends KeyAdapter implements MarioAgent {
    private boolean[] actions = null;

    int count = 0;
    void printStateArray(int[][] matrix) {
      for (int i = 0; i < matrix.length; i++) {
        this.log("" + i + "-  " + Arrays.toString(matrix[i]));
      }
    }

    

    void log(String message) {
      System.out.println(message);
    }
  
    void printState(MarioForwardModel model) {
      this.log("" + this.count + "****************************************************************");
      // // this.log("RemainingTime: " + model.getRemainingTime());
      // // this.log("MarioMode: " + model.getMarioMode());
      this.log("CompletionPercentage: " + model.getCompletionPercentage());
      // this.log("EnemiesFloatPos: " + Arrays.toString(model.getEnemiesFloatPos()));
      // this.log("LevelFloatDimensions: " + Arrays.toString(model.getLevelFloatDimensions()));
      this.log("MarioCompleteObservation:");
      printStateArray(model.getMarioCompleteObservation());

      int[] marioScreenTile = model.getMarioScreenTilePos();
      int startPos = 8 - 5;// marioScreenTile[0] - 3;
      int endPos = 8 + 6;// marioScreenTile[0] + 3;
      int[][] completeObs = model.getMarioCompleteObservation();
      startPos = (startPos < 0 ? 0 : startPos);
      endPos = (endPos > completeObs.length - 1 ? completeObs.length - 1 : endPos);
      this.log("startPos " + startPos);
      this.log("endPos " + endPos);
      int[][] completeObsSlice =  Arrays.copyOfRange(completeObs, startPos, endPos);
      int length = completeObsSlice.length;
      for(int i = 0; i < length; i++) {
        int subStartPos = 8 - 5;// marioScreenTile[0] - 3;
        int subEndPos = 8 + 6;// marioScreenTile[0] + 3;
        completeObsSlice[i] =  Arrays.copyOfRange(completeObsSlice[i], subStartPos, subEndPos);
      }
      printStateArray(completeObsSlice);

      // this.log("MarioEnemiesObservation: ");
      // printStateArray(model.getMarioEnemiesObservation());
      // this.log("MarioFloatPos: " + Arrays.toString(model.getMarioFloatPos()));
      // this.log("MarioFloatVelocity: " + Arrays.toString(model.getMarioFloatVelocity()));
      // this.log("MarioSceneObservation: ");
      // printStateArray(model.getMarioSceneObservation());
      // this.log("MarioScreenTilePos: " + Arrays.toString(model.getMarioScreenTilePos()));
      // this.log("ScreenCompleteObservation: ");
      // printStateArray(model.getScreenCompleteObservation());
      // this.log("ScreenEnemiesObservation: ");
      // printStateArray(model.getScreenEnemiesObservation());
      // this.log("ScreenSceneObservation: ");
      // printStateArray(model.getScreenSceneObservation());
      this.log("MarioCanJumpHigher: " + model.getMarioCanJumpHigher());
      this.log("isMarioOnGround: " + model.isMarioOnGround());
      this.log("mayMarioJump: " + model.mayMarioJump());
      // // this.log("NumLives: " + model.getNumLives()); // Always 0
      // // this.log("hashCode: " + model.hashCode());  // Hash number
      this.log("*****************************************************" + model.getGameStatus());
    }
    
    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
      this.log("initialize");
	actions = new boolean[MarioActions.numberOfActions()];
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
      if (this.count % 10 == 0 || model.getRemainingTime() < 500) {
        this.printState(model);
      }
	return actions;
    }

    @Override
    public String getAgentName() {
	return "HumanAgent";
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
      // this.log("keyPressed");
	toggleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
	toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed) {
	if(this.actions == null) {
	    return;
	}
	switch (keyCode) {
	case KeyEvent.VK_LEFT:
	    this.actions[MarioActions.LEFT.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_RIGHT:
	    this.actions[MarioActions.RIGHT.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_DOWN:
	    this.actions[MarioActions.DOWN.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_S:
	    this.actions[MarioActions.JUMP.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_A:
	    this.actions[MarioActions.SPEED.getValue()] = isPressed;
	    break;
	}
    }

}
