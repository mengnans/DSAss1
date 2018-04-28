package activitystreamer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import activitystreamer.util.JsonHelper;
import com.google.gson.*;

@SuppressWarnings("serial")
public class FormWindow extends JFrame implements ActionListener {
    private JTextArea inputText;
    private JTextArea outputText;
    private JButton sendButton;
    private JButton disconnectButton;

    public FormWindow() {
        setTitle("ActivityStreamer Text I/O");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        JPanel inputPanel = new JPanel();
        JPanel outputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        outputPanel.setLayout(new BorderLayout());
        Border lineBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray), "JSON input, to send to server");
        inputPanel.setBorder(lineBorder);
        lineBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray), "JSON output, received from server");
        outputPanel.setBorder(lineBorder);
        outputPanel.setName("Text output");

        inputText = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(inputText);
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonGroup = new JPanel();
        sendButton = new JButton("Send");
        disconnectButton = new JButton("Disconnect");
        buttonGroup.add(sendButton);
        buttonGroup.add(disconnectButton);
        inputPanel.add(buttonGroup, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
        disconnectButton.addActionListener(this);

        outputText = new JTextArea();
        scrollPane = new JScrollPane(outputText);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel);
        mainPanel.add(outputPanel);
        add(mainPanel);

        setLocationRelativeTo(null);
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setOutputText(final JsonObject obj) {
        String prettyJsonString = JsonHelper.ObjectToString(obj);
        outputText.setText(prettyJsonString);
        outputText.revalidate();
        outputText.repaint();
    }

    public void setOutputText(final String argMessage) {
        outputText.setText(argMessage);
        outputText.revalidate();
        outputText.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String msg = inputText.getText().trim().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
            ClientItem.getInstance().ProcessUserMessage(JsonHelper.StringToObject(msg));
        } else if (e.getSource() == disconnectButton) {
            ClientItem.getInstance().disconnect();
        }
    }
}