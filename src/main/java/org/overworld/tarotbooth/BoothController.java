/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

@Component
public class BoothController implements Initializable {

    @FXML
    private AnchorPane beaches;
   
    @FXML
    private AnchorPane curtains;

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
    	allPanes = new Pane[] {beaches, curtains, benny, drawing, ezzie, reading, estralada, quinn};
    }
    
    public void estraladaMode() {}
    public void beachesMode() {}
    public void readingMode() {}
    public void drawingMode() {}
    
    public void quinnMode() {
    	fadeTo(quinn);
    }
    
    public void curtainskMode() {
    	fadeTo(curtains);
    }

	public void ezzieMode() {
		fadeTo(ezzie);
	}

	public void bennyMode() {
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
    	
    	if (allPanes != null ) {
	    	for (Pane p : allPanes) {
	    		
	    		if (p != current && p != pane)
	    			p.setOpacity(0);
	    	}
    	}
    }
}
