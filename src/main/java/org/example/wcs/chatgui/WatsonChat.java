/*
 * Copyright 2016-2017 Tom Misawa, riversun.org@gmail.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
 * Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package org.example.wcs.chatgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

import org.example.wcs.chatgui.swing.EDTHandler;
import org.example.wcs.chatgui.swing.JLinearLayout;
import org.example.wcs.chatgui.swing.JLinearLayout.Orientation;
import org.riversun.wcs.WcsClient;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * Watson Conversation Chat Bot Example<br>
 * Chat with Watson from Pure Java GUI<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
@SuppressWarnings("serial")
public class WatsonChat extends JFrame {

    // EDIT HERE[BEGIN]---------------------------------------------
    private static final String WATSON_CONVERSATION_USERNAME = "YOUR_WATON_CONVERSATION_USER_NAME_HERE";
    private static final String WATSON_CONVERSATION_PASSWORD = "YOUR_WATON_CONVERSATION_PASSWORD_HERE";
    private static final String WATSON_CONVERSATION_WORKSPACE_ID = "YOUR_WATON_CONVERSATION_WORKSPACE_ID_HERE";
    // EDIT HERE[END]---------------------------------------------

    private static final String WCS_CLIENT_ID = "dummy_user_id";

    private static final int WIDTH_PX = 640;
    private static final int HEIGHT_PX = 480;

    private final WcsClient mWatson = new WcsClient(
            WATSON_CONVERSATION_USERNAME,
            WATSON_CONVERSATION_PASSWORD,
            WATSON_CONVERSATION_WORKSPACE_ID);

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final StringBuilder mSb = new StringBuilder();

    private final JTextArea mTextArea = new JTextArea();
    private final JTextField mTextBox = new JTextField("");

    public static void main(String args[]) {

        // Run on event dispatcher thread
        EDTHandler.post(new Runnable() {
            public void run() {

                System.setProperty("jsse.enableSNIExtension", "false");
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                }

                final WatsonChat frame = new WatsonChat();
                frame.setVisible(true);
            }
        });
    }

    public WatsonChat() {
        EDTHandler.post(new Runnable() {
            @Override
            public void run() {
                buildGUI();
                sendMessageOnFirst();
            }
        });
    }

    /**
     * Call when user input text
     * 
     * @param userInputText
     * @return
     */
    boolean onUserInputText(String userInputText) {

        addTextIntoHistory("YOU:" + userInputText + "\n");

        sendMessageToWatson(WCS_CLIENT_ID, userInputText);

        return true;
    }

    /**
     * Call when "clear" button pressed
     */
    void onClearButtonPressed() {
        mSb.setLength(0);
        EDTHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextArea.setText(mSb.toString());
            }
        });
        mWatson.clearConversation(WCS_CLIENT_ID);
        sendMessageOnFirst();
    }

    /**
     * For the welcome node, send the first message
     */
    void sendMessageOnFirst() {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                MessageResponse welcomeRes = mWatson.startConversation(WCS_CLIENT_ID);
                addTextIntoHistory("WATSON:" + welcomeRes.getTextConcatenated("") + "\n");
                unlockTextBox();
            }
        });
    }

    /**
     * Send user input text to watson
     * 
     * @param wcsClientId
     * @param userInputText
     */
    void sendMessageToWatson(String wcsClientId, String userInputText) {

        // In order to prevent the screen from locking
        // during communication with Watson.Call from another thread
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {

                final String watsonOutputText = mWatson.sendMessageForText(wcsClientId, userInputText);
                addTextIntoHistory("WATSON:" + watsonOutputText);

                unlockTextBox();
            }
        });
    }

    /**
     * Add text into chat history
     * 
     * @param text
     */
    void addTextIntoHistory(String text) {

        mSb.append(text);

        // Run on event dispatcher thread
        EDTHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextArea.setText(mSb.toString());
            }
        });

    }

    void unlockTextBox() {

        // Run on event dispatcher thread
        EDTHandler.post(new Runnable() {
            @Override
            public void run() {

                mTextBox.setEditable(true);
                mTextBox.requestFocus();
                mTextBox.getCaret().setVisible(true);
                mTextBox.setCaretPosition(0);

            }
        });
    }

    void buildGUI() {

        setTitle("Chat with Watson");
        setSize(WIDTH_PX, HEIGHT_PX);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JLinearLayout layoutParent = new JLinearLayout().setChildOrientation(Orientation.VERTICAL).setPadding(5);
        final JLinearLayout layoutHeader = new JLinearLayout().setChildOrientation(Orientation.HORIZONTAL).setPadding(5, 0, 5, 0);
        final JLinearLayout layoutCenter = new JLinearLayout().setChildOrientation(Orientation.VERTICAL).setPadding(5, 0, 5, 0);
        final JLinearLayout layoutFooter = new JLinearLayout().setChildOrientation(Orientation.VERTICAL).setPadding(5);

        // Label on Top
        JLabel lbChatHistory = new JLabel("Chat History:");

        JButton btClear = new JButton("Clear");
        btClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                onClearButtonPressed();
            }
        });

        // Text Area for displaying history
        mTextArea.setLineWrap(true);
        mTextArea.setMargin(new Insets(4, 4, 4, 4));
        mTextArea.setEditable(false);

        // Scroll bar for Text Area
        JScrollPane textAreaScroll = new JScrollPane(mTextArea);
        textAreaScroll.setBorder(new LineBorder(Color.black, 1, true));

        JLabel lbInputText = new JLabel("Input Text:  (press ENTER-KEY to send)");

        mTextBox.setBorder(new LineBorder(Color.black, 1, true));
        mTextBox.setEditable(false);
        Font font = mTextBox.getFont().deriveFont(Font.PLAIN, 20f);
        mTextBox.setFont(font);
        mTextBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final String userInputText = mTextBox.getText();

                if ("".equals(userInputText)) {
                    return;
                }

                mTextBox.setEditable(false);

                final boolean consumed = onUserInputText(userInputText);

                if (consumed) {
                    mTextBox.setText("");
                }

            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                // Request focus on text box when window opened
                mTextBox.requestFocus();
            }
        });

        // Add components into each layout group
        layoutHeader.addView(lbChatHistory, 1.0d);
        layoutHeader.addView(btClear, 0d);
        layoutCenter.addView(textAreaScroll);
        layoutFooter.addView(lbInputText);
        layoutFooter.addView(mTextBox);

        // Add layout group into parent layout group with weight score
        layoutParent.addView(layoutHeader, 0.0d);
        layoutParent.addView(layoutCenter, 1.0d);
        layoutParent.addView(layoutFooter, 0.0d);

        JPanel mainPanel = layoutParent.getAsPanel();

        Container contentPane = getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);

    }
}