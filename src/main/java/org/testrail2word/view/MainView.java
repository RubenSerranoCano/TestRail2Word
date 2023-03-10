package org.testrail2word.view;

import org.testrail2word.controller.MainViewController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {

    private static MainView instance;

    private javax.swing.JPanel jPanel;
    private JButton printButton;
    private JTextField testCaseUrlTextField;
    private JComboBox versionComboBox;
    private JButton chooseFileButton;
    private JTextField outputPathTextField;
    private JLabel testRailVersionLabel;
    private JLabel destinationFolderLabel;
    private JLabel testCasesUrlsLabel;
    private JList<String> testCasesUrlJList;

    private final DefaultListModel<String> testCasesUrls = new DefaultListModel<>();

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    private MainView(String appTitle) {
        super(appTitle);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(jPanel);
        this.setVisible(true);
        this.pack();
        this.setBounds(0, 0, 800, 800);
        this.setLocationRelativeTo(null);
        this.testRailVersionLabel.setFont(new Font("Default", Font.PLAIN, 22));
        this.destinationFolderLabel.setFont(new Font("Default", Font.PLAIN, 22));
        this.testCasesUrlsLabel.setFont(new Font("Default", Font.PLAIN, 22));

        this.versionComboBox.setFont(new Font("Default", Font.PLAIN, 16));
        this.chooseFileButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.addButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.printButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.editButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.deleteButton.setFont(new Font("Default", Font.PLAIN, 16));

        this.outputPathTextField.setFont(new Font("Default", Font.PLAIN, 16));
        this.testCaseUrlTextField.setFont(new Font("Default", Font.PLAIN, 16));
        this.testCasesUrlJList.setFont(new Font("Default", Font.PLAIN, 16));

        this.editButton.setEnabled(false);
        this.deleteButton.setEnabled(false);
        this.printButton.setEnabled(false);

        MainViewController mainViewController = new MainViewController();

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.print(testCasesUrlJList.getSelectedValuesList(), String.valueOf(versionComboBox.getSelectedItem()));
            }
        });

        outputPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                mainViewController.setOutputPath(outputPathTextField.getText());
            }
        });

        testCasesUrlJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (testCaseUrlTextField.getText().isEmpty()) {
                    editButton.setEnabled(true);
                }
                deleteButton.setEnabled(true);
                printButton.setEnabled(true)
                ;
                if (testCasesUrlJList.getSelectedValuesList().size() > 1) {
                    editButton.setEnabled(false);
                }

                if (testCasesUrlJList.getSelectedValuesList().size() == 0) {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    printButton.setEnabled(false);
                }
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showOpenDialog(instance);
                if (fileChooser.getSelectedFile() != null) {
                    outputPathTextField.setText(String.valueOf(fileChooser.getSelectedFile()));
                    mainViewController.setOutputPath(String.valueOf(fileChooser.getSelectedFile()));
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testCasesUrls.addElement(testCaseUrlTextField.getText());
                testCasesUrlJList.setModel(testCasesUrls);
                testCaseUrlTextField.setText("");
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectionIndex = testCasesUrlJList.getSelectedIndex();
                String selectedUrl = testCasesUrlJList.getSelectedValue();

                testCasesUrls.remove(selectionIndex);
                testCasesUrlJList.setModel(testCasesUrls);
                testCaseUrlTextField.setText(selectedUrl);
            }
        });

        testCaseUrlTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            public void change() {
                if (!testCaseUrlTextField.getText().isEmpty()) {
                    editButton.setEnabled(false);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testCasesUrls.remove(testCasesUrlJList.getSelectedIndex());
            }
        });
    }

    public static void runApplication() {
        new MainView("TestRail2Word");
    }

}
