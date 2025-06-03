package ui;

import controller.PatientController;
import javax.swing.*;
import java.awt.*;

public class PatientPanel extends JPanel {
    private final PatientController patientController;

    public PatientPanel(PatientController patientController) {
        this.patientController = patientController;
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // TODO: Добавить компоненты панели
    }
} 