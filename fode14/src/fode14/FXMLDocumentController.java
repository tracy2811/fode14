/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fode14;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 *
 * @author trang
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField x0, y0, x, n, n1, n2, step;
    @FXML
    private CheckBox exact, euler, improvedEuler, rungeKutta;
    @FXML
    private LineChart solution, error, grid;
    @FXML
    private Label mess, gErrorLabel, eulerError, improvedEulerError, rungeKuttaError;
    
    @FXML
    private void handleResetButtonAction(ActionEvent event) throws Exception {
        resetMessage();
        x0.setText("1.0");
        y0.setText("2.0");
        x.setText("6.0");
        n.setText("10");
        n1.setText("5");
        n2.setText("30");
        step.setText("2");
        selectAllCheckboxes();
        clearGlobalError();
        clearGraphs();
        resetGraphs();
    }
    
    @FXML
    private void handleDrawButtonAction(ActionEvent event) {
        resetMessage();
        clearGlobalError();
        clearGraphs();

        double inputX0, inputY0, inputX;
        int inputN, inputN1, inputN2, inputStep;
        try {
            // get user input
            inputX0 = Double.valueOf(x0.getText());
            inputY0 = Double.valueOf(y0.getText());
            inputX = Double.valueOf(x.getText());
            inputN = Integer.valueOf(n.getText());
            inputN1 = Integer.valueOf(n1.getText());
            inputN2 = Integer.valueOf(n2.getText());
            inputStep = Integer.valueOf(step.getText());

            updateGraphsAndLabels(new Fode14(inputX0, inputY0, inputX), inputN, inputN1, inputN2, inputStep);
            
        } catch (Exception ex) {
            // show error message
            mess.setTextFill(Color.RED);
            mess.setText(ex.getMessage());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {
            resetGraphs();
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void resetMessage() {
        mess.setTextFill(Color.BLACK);
        mess.setText("Exact solution: y = x (1+y0/x0)^(x/x0)");
    }
    
    private void selectAllCheckboxes() {
        exact.setSelected(true);
        euler.setSelected(true);
        improvedEuler.setSelected(true);
        rungeKutta.setSelected(true);
    }
    
    private void updateGraphsAndLabels(Fode14 fode, int inputN, int inputN1, int inputN2, int inputStep) throws Exception {
        if (!exact.isSelected() && !euler.isSelected() && !improvedEuler.isSelected() && !rungeKutta.isSelected()) {
            selectAllCheckboxes();
        }

        gErrorLabel.setText("Global Error (N = " + String.valueOf(inputN) + "):");
        
        // Draw solution and error
        if (euler.isSelected()) {
            solution.getData().add(fode.getEulerSeries(inputN));
            error.getData().add(fode.getEulerErrorSeries(inputN));
            eulerError.setText(String.valueOf(fode.getEulerGlobalError(inputN)));
        }

        if (improvedEuler.isSelected()) {
            solution.getData().add(fode.getImprovedEulerSeries(inputN));
            error.getData().add(fode.getImprovedEulerErrorSeries(inputN));
            improvedEulerError.setText(String.valueOf(fode.getImprovedEulerGlobalError(inputN)));
            
        }

        if (rungeKutta.isSelected()) {
            solution.getData().add(fode.getRungeKuttaSeries(inputN));
            error.getData().add(fode.getRungeKuttaErrorSeries(inputN));
            rungeKuttaError.setText(String.valueOf(fode.getRungeKuttaGlobalError(inputN)));
        }

        if (exact.isSelected()) {
            solution.getData().add(fode.getExactSolutionSeries(inputN));
        }

        // Draw grid
        if (euler.isSelected()) {
            grid.getData().add(fode.getEulerGlobalErrorSeries(inputN1, inputN2, inputStep));
        }

        if (improvedEuler.isSelected()) {
            grid.getData().add(fode.getImprovedEulerGlobalErrorSeries(inputN1, inputN2, inputStep));
        }

        if (rungeKutta.isSelected()) {
            grid.getData().add(fode.getRungeKuttaGlobalErrorSeries(inputN1, inputN2, inputStep));
        }
    }

    private void resetGraphs() throws Exception {
        updateGraphsAndLabels(new Fode14(), 10, 5, 30, 2);
    }
    
    private void clearGraphs() {
        solution.getData().clear();
        error.getData().clear();
        grid.getData().clear();
    }
    
    private void clearGlobalError() {
        gErrorLabel.setText("");
        eulerError.setText("");
        improvedEulerError.setText("");
        rungeKuttaError.setText("");
    }
}
