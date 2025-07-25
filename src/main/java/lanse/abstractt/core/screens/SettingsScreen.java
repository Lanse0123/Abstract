package lanse.abstractt.core.screens;

import lanse.abstractt.core.bubblesortlogic.BubbleSorter;
import lanse.abstractt.core.displaylogic.DisplayModeSelector;
import lanse.abstractt.storage.ExcludedBubbleList;
import lanse.abstractt.storage.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class SettingsScreen extends JPanel {

    private final JFrame frame;
    private final JPanel previousScreen;
    private final JTextArea excludedExtensionsArea;

    //    private final JSlider volumeSlider;
//    private final JComboBox<String> backgroundSelector;
//    private final JCheckBox showDeathMarkersBox;
//    private final JCheckBox playIllegalMoveSoundBox;
    private final JComboBox<DisplayModeSelector.DisplayMode> displayModeSelector;
    private final JComboBox<BubbleSorter.Sorter> bubbleSorterModeSelector;
    private final JComboBox<BubbleSorter.FunctionSorter> functionBubbleSorterModeSelector;

    public SettingsScreen(JFrame frame, JPanel previousScreen, Color bgColor) {
        this.frame = frame;
        this.previousScreen = previousScreen;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        excludedExtensionsArea = new JTextArea(5, 30);
        excludedExtensionsArea.setLineWrap(true);
        excludedExtensionsArea.setWrapStyleWord(true);

        StringBuilder initial = new StringBuilder();
        for (String ext : ExcludedBubbleList.getExcludedEndings()) {
            if (!ext.equals("AbstractionVisualizerStorage"))
                initial.append(ext).append("\n");
        }
        excludedExtensionsArea.setText(initial.toString().trim());

        addLabeled("Excluded Extensions (one per line):", new JScrollPane(excludedExtensionsArea));


        // Display Mode
        displayModeSelector = new JComboBox<>(DisplayModeSelector.DisplayMode.values());
        displayModeSelector.setSelectedItem(DisplayModeSelector.displayMode);
        addLabeled("Display Mode:", displayModeSelector);

        // Bubble Sorter Mode
        bubbleSorterModeSelector = new JComboBox<>(BubbleSorter.Sorter.values());
        bubbleSorterModeSelector.setSelectedItem(BubbleSorter.sorter);
        addLabeled("Bubble Sorter Mode:", bubbleSorterModeSelector);

        // Function Bubble Sorter Mode
        functionBubbleSorterModeSelector = new JComboBox<>(BubbleSorter.FunctionSorter.values());
        functionBubbleSorterModeSelector.setSelectedItem(BubbleSorter.functionSorter);
        addLabeled("Function Bubble Sorter Mode:", functionBubbleSorterModeSelector);

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

    private void saveSettings(ActionEvent e) {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        Settings.setVolume(volumeSlider.getValue());
//        Settings.setBackground((String) backgroundSelector.getSelectedItem());
//        Settings.setShowDeathMarkers(showDeathMarkersBox.isSelected());
//        Settings.setPlayIllegalMoveSound(playIllegalMoveSoundBox.isSelected());

        DisplayModeSelector.displayMode = (DisplayModeSelector.DisplayMode) displayModeSelector.getSelectedItem();

        BubbleSorter.sorter = (BubbleSorter.Sorter) bubbleSorterModeSelector.getSelectedItem();
        BubbleSorter.functionSorter = (BubbleSorter.FunctionSorter) functionBubbleSorterModeSelector.getSelectedItem();

        Set<String> userExtensions = new HashSet<>();
        for (String line : excludedExtensionsArea.getText().split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty())
                userExtensions.add(trimmed);
        }
        ExcludedBubbleList.setExcludedEndings(userExtensions);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Settings.save()) // Attempt save to file
            JOptionPane.showMessageDialog(this, "Settings saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "Settings unable to be saved.", "Error", JOptionPane.INFORMATION_MESSAGE);

        returnToParent();
    }

    private void returnToDefaultSettings(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return to default settings?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) return;

        Settings.returnToDefaultSettings();

        JOptionPane.showMessageDialog(this, "Returned to Default Settings!", "Success", JOptionPane.INFORMATION_MESSAGE);
        returnToParent();
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

    //TODO - this will be used for boolean values. example at line 44 or something
//    private void styleCheckBox(JCheckBox box) {
//        box.setForeground(Color.WHITE);
//        box.setBackground(Color.DARK_GRAY);
//        box.setFocusPainted(false);
//        box.setAlignmentX(Component.LEFT_ALIGNMENT);
//    }

    private void returnToParent() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(previousScreen);
        frame.revalidate();
        frame.repaint();
    }
}