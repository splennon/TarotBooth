/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class DebugController implements Initializable {
    
    @FXML
    private Button pastButton;

    @FXML
    private TextField presentText;

    @FXML
    private TextField futureText;

    @FXML
    private Button futureButton;

    @FXML
    private Button presentButton;

    @FXML
    private ToggleButton presenceToggle;

    @FXML
    private ToggleButton approachToggle;
	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
}
