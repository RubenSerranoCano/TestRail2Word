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

    private JPanel jPanel;
    private JButton printButton;
    private JTextField testCaseUrlTextField;
    private JComboBox<String> versionComboBox;
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
        jPanel.setBackground(new Color(230,242,255));

        this.testRailVersionLabel.setFont(new Font("Calibri", Font.PLAIN, 22));
        this.destinationFolderLabel.setFont(new Font("Calibri", Font.PLAIN, 22));
        this.testCasesUrlsLabel.setFont(new Font("Calibri", Font.PLAIN, 22));

        this.versionComboBox.setFont(new Font("Default", Font.PLAIN, 16));
        this.chooseFileButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.addButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.printButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.editButton.setFont(new Font("Default", Font.PLAIN, 16));
        this.deleteButton.setFont(new Font("Default", Font.PLAIN, 16));

        this.outputPathTextField.setFont(new Font("Default", Font.PLAIN, 16));
        this.testCaseUrlTextField.setFont(new Font("Default", Font.PLAIN, 16));
        this.testCasesUrlJList.setFont(new Font("Default", Font.PLAIN, 16));

        this.versionComboBox.setBackground(Color.white);
        this.chooseFileButton.setBackground(Color.white);
        this.addButton.setBackground(Color.white);
        this.printButton.setBackground(Color.white);
        this.editButton.setBackground(Color.white);
        this.deleteButton.setBackground(Color.white);

        this.addButton.setEnabled(false);
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
                mainViewController.setButtonStateOnListSelection(testCaseUrlTextField, testCasesUrlJList, editButton, deleteButton, printButton);
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.chooseFile(jPanel, outputPathTextField);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.add(addButton, testCasesUrls, testCaseUrlTextField, testCasesUrlJList);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.edit(testCasesUrlJList, testCasesUrls, testCaseUrlTextField, addButton);
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
                mainViewController.setButtonStateOnUrlInput(testCaseUrlTextField, editButton, addButton);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.deleteSelection(testCasesUrlJList, testCasesUrls);
            }
        });
    }

    public static void runApplication() {
        new MainView("TestRail2Word");
    }

}
