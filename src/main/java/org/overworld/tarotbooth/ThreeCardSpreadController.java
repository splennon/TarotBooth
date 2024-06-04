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
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;

public class ThreeCardSpreadController implements Initializable {

    @FXML
    private MenuButton settingsMenu;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        settingsMenu.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> settingsMenu.setOpacity(1.0));
        settingsMenu.addEventHandler(MouseEvent.MOUSE_EXITED, e -> settingsMenu.setOpacity(0.5));
    }    
}
