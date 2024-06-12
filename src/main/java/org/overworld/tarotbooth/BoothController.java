/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.sound.SoundLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

@Component
public class BoothController implements Initializable {
    
	@Autowired
	SoundLibrary sounds;
	
	@Autowired
	EzzieMachine machine;
	
    @FXML
    private AnchorPane beaches;
   
    @FXML
    private AnchorPane idle;

    @FXML
    private AnchorPane benny;

    @FXML
    private AnchorPane drawing;

    @FXML
    private AnchorPane ezzie;

    @FXML
    private AnchorPane reading;

    @FXML
    private AnchorPane estralada;
    
    @FXML
    private AnchorPane quinn;

    private Pane current;
    
    private Pane[] allPanes;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	allPanes = new Pane[] {beaches, idle, benny, drawing, ezzie, reading, estralada, quinn};
    }
    
    public void estraladaMode() {}
    public void beachesMode() {}
    public void readingMode() {}
    public void drawingMode() {}
    public void quinnMode() {}
    
    public void blackMode() {
    	
		sounds.stopAll();    	
    	fadeTo(idle);
    }

	public void ezzieMode() {
		
		sounds.stopAll();
		fadeTo(ezzie);
		
		MediaPlayer mp = sounds.get("R01");
		mp.setOnEndOfMedia(() -> {
			System.out.println("firing presence sensor");
			machine.fire(Trigger.PRESENCE_SENSOR);
		});		
		mp.play();
	}

	public void bennyMode() {

		sounds.stopAll();
		fadeTo(benny);
	}
	
    private void fadeTo(Pane pane) {
    	
		FadeTransition fadeIn = new FadeTransition(Duration.millis(900), pane);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		
    	if (current == null) {
    		fadeIn.play();
			current = pane;
    	} else {
    		FadeTransition fadeOut = new FadeTransition(Duration.millis(900), current);
    		fadeOut.setFromValue(1);
    		fadeOut.setToValue(0);
    		fadeOut.setOnFinished(e -> {
    			fadeIn.play();
    			current = pane;
    		});
    		fadeOut.play();
    	}
    	
    	for (Pane p : allPanes) {
    		
    		if (p != current && p != pane)
    			p.setOpacity(0);
    	}
    }
}
