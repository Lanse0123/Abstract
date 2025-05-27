package lanse.abstractt.core.screens;

import lanse.abstractt.core.DisplayModeSelector;
import lanse.abstractt.storage.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsScreen extends JPanel {

    private final JFrame frame;
//    private final JSlider volumeSlider;
//    private final JComboBox<String> backgroundSelector;
//    private final JCheckBox showDeathMarkersBox;
//    private final JCheckBox playIllegalMoveSoundBox;
    private final JComboBox<DisplayModeSelector.DisplayMode> displayModeSelector;

    public SettingsScreen(JFrame frame, Color bgColor) {
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Volume Slider
//        volumeSlider = new JSlider(0, 100, Settings.getVolume());
//        volumeSlider.setMajorTickSpacing(25);
//        volumeSlider.setPaintTicks(true);
//        volumeSlider.setPaintLabels(true);
//        addLabeled("Volume:", volumeSlider);
//
//        // Background Selector
//        backgroundSelector = new JComboBox<>(new String[]{"Warped Plane", "Void", "Space", "Solid"});
//        backgroundSelector.setSelectedItem(Settings.getBackground());
//        addLabeled("Background:", backgroundSelector);
//
//        // Death Markers Checkbox
//        showDeathMarkersBox = new JCheckBox("Show Death Markers", Settings.isShowDeathMarkers());
//        styleCheckBox(showDeathMarkersBox);
//        add(showDeathMarkersBox);

        // Illegal Move Sound Checkbox
//        playIllegalMoveSoundBox = new JCheckBox("Play Illegal Move Sound", Settings.isPlayIllegalMoveSound());
//        styleCheckBox(playIllegalMoveSoundBox);
//        add(playIllegalMoveSoundBox);

        // Display Mode
        displayModeSelector = new JComboBox<>(DisplayModeSelector.DisplayMode.values());
        displayModeSelector.setSelectedItem(DisplayModeSelector.displayMode);
        addLabeled("Display Mode:", displayModeSelector);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Save Button
        JButton saveButton = new JButton("Save Settings");
        saveButton.addActionListener(this::saveSettings);
        JButton returnToDefaultButton = new JButton("Return to Default Settings");
        returnToDefaultButton.addActionListener(this::returnToDefaultSettings);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(saveButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(returnToDefaultButton);
    }

    private void addLabeled(String label, JComponent comp) {
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(jLabel);
        add(comp);
        add(Box.createVerticalStrut(10));
    }

    private void styleCheckBox(JCheckBox box) {
        box.setForeground(Color.WHITE);
        box.setBackground(Color.DARK_GRAY);
        box.setFocusPainted(false);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void saveSettings(ActionEvent e) {
//        Settings.setVolume(volumeSlider.getValue());
//        Settings.setBackground((String) backgroundSelector.getSelectedItem());
//        Settings.setShowDeathMarkers(showDeathMarkersBox.isSelected());
//        Settings.setPlayIllegalMoveSound(playIllegalMoveSoundBox.isSelected());

        DisplayModeSelector.displayMode = (DisplayModeSelector.DisplayMode) displayModeSelector.getSelectedItem();

        Settings.save(); // Save to file
        JOptionPane.showMessageDialog(this, "Settings saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        returnToMainMenu();
    }

    private void returnToDefaultSettings(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return to default settings?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) return;

        Settings.returnToDefaultSettings();

        JOptionPane.showMessageDialog(this, "Returned to Default Settings!", "Success", JOptionPane.INFORMATION_MESSAGE);
        returnToMainMenu();
    }

    private void returnToMainMenu() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }
}
