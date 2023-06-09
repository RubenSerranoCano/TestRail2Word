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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainView extends JFrame {

    private JPanel jPanel;
    private JButton printButton;
    private JTextField testCaseUrlTextField;
    private JComboBox<String> browserVersionComboBox;
    private JButton chooseFileButton;
    private JTextField outputPathTextField;
    private JLabel browserlVersionLabel;
    private JLabel destinationFolderLabel;
    private JLabel testCasesUrlsLabel;
    private JList<String> testCasesUrlJList;
    private final DefaultListModel<String> testCasesUrls = new DefaultListModel<>();
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel attributionHyperlink;
    private JComboBox testRailVersionComboBox;
    private JLabel testRailVersionLabel;
    private JLabel browserVersionLabel;

    private MainView(String appTitle) {
        super(appTitle);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(jPanel);
        this.setVisible(true);
        this.pack();
        this.setBounds(0, 0, 800, 800);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon("src/main/java/org/testrail2word/assets/clipboard.png").getImage());
        jPanel.setBackground(new Color(230, 242, 255));

        attributionHyperlink.setForeground(Color.BLUE.darker());
        attributionHyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        testRailVersionLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
        browserVersionLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
        destinationFolderLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
        testCasesUrlsLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        testRailVersionComboBox.setFont(new Font("Default", Font.PLAIN, 14));
        browserVersionComboBox.setFont(new Font("Default", Font.PLAIN, 14));
        chooseFileButton.setFont(new Font("Default", Font.PLAIN, 14));
        addButton.setFont(new Font("Default", Font.PLAIN, 14));
        printButton.setFont(new Font("Default", Font.PLAIN, 14));
        editButton.setFont(new Font("Default", Font.PLAIN, 14));
        deleteButton.setFont(new Font("Default", Font.PLAIN, 14));

        outputPathTextField.setFont(new Font("Default", Font.PLAIN, 14));
        testCaseUrlTextField.setFont(new Font("Default", Font.PLAIN, 14));
        testCasesUrlJList.setFont(new Font("Default", Font.PLAIN, 14));

        testRailVersionComboBox.setBackground(Color.white);
        browserVersionComboBox.setBackground(Color.white);
        chooseFileButton.setBackground(Color.white);
        addButton.setBackground(Color.white);
        printButton.setBackground(Color.white);
        editButton.setBackground(Color.white);
        deleteButton.setBackground(Color.white);

        addButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        printButton.setEnabled(false);

        MainViewController mainViewController = new MainViewController();

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainViewController.print(testCasesUrlJList.getSelectedValuesList(), String.valueOf(browserVersionComboBox.getSelectedItem()), String.valueOf(testRailVersionComboBox.getSelectedItem()));
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

        attributionHyperlink.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.flaticon.com/free-icons/files-and-folders"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // the mouse has entered the label
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // the mouse has exited the label
            }
        });
    }

    public static void runApplication() {
        new MainView("TestRail2Word");
    }

}
