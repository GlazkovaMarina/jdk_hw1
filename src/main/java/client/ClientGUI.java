package client;
import server.ServerWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ClientGUI extends JFrame{
    private ServerWindow serverWindow;
    private static final int WINDOW_HEIGHT = 400;
    private static final int WINDOW_WIDHT = 400;
    private static final int WINDOW_POSX = 5;
    private static final int WINDOW_POSY = 300;
    private boolean isConnected;
    private JTextArea log;
    private JTextField  ipField, portField, nameField, sendFiled;
    private JPasswordField passwordField;
    private JButton btnLogin, btnSend;
    private JPanel  addressUserPan;

    public ClientGUI(ServerWindow serverWindow){
        this.serverWindow = serverWindow;
        isConnected = false;
        setLocation(WINDOW_POSX + (new Random()).nextInt(400,500), WINDOW_POSY);
        setSize(WINDOW_WIDHT, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Chat client");
        createMap();
        setVisible(true);
    }

    public String getUserName(){
        return nameField.getText();
    }

    private void createMap(){
        add(setAddressUserPanel(), BorderLayout.NORTH);
        add(setTextPanel(),BorderLayout.CENTER);
        add(setSendPanel(), BorderLayout.SOUTH);
    }

    private JScrollPane setTextPanel(){
        log = new JTextArea(15, 30);
        log.setEditable(false);
        return new JScrollPane(log);
    }
    private JPanel setAddressUserPanel(){
        addressUserPan = new JPanel(new GridLayout(4, 3));

        btnLogin = new JButton("login");
        JLabel ipLabel = new JLabel("Ip address");
        ipField = new JTextField("127.0.0.1");
        JLabel portLabel = new JLabel("Port");
        portField = new JTextField("8189");

        JLabel nameLabel = new JLabel("User name");
        nameField = new JTextField("Ivan Ivanovich");
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField("ivanov", 10);

        addressUserPan.add(ipLabel);
        addressUserPan.add(portLabel);
        addressUserPan.add(new JPanel());
        addressUserPan.add(ipField);
        addressUserPan.add(portField);
        addressUserPan.add(new JPanel());
        addressUserPan.add(nameLabel);
        addressUserPan.add(passwordLabel);
        addressUserPan.add(new JPanel());
        addressUserPan.add(nameField);
        addressUserPan.add(passwordField);
        addressUserPan.add(btnLogin);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectServer();
            }
        });
        return addressUserPan;
    }
    private void connectServer(){
        if (!isActivePanel()){
            appendLog("Необходимо заполнить все поля ввода!");
        } else if (serverWindow.connectUser(this)){
            appendLog("Вы успешно подключились!\n");
            addressUserPan.setVisible(false);
            isConnected = true;
            String log = serverWindow.getLog();
            if (log != null){
                appendLog(log);
            }
        } else {
            appendLog("Подключение не удалось");
        }
    }
    private boolean isActivePanel(){
        if (!nameField.getText().isEmpty() && !passwordField.getText().isEmpty() && !portField.getText().isEmpty() && !ipField.getText().isEmpty())
            return true;
        return false;
    }
    private JPanel setSendPanel(){
        btnSend = new JButton("send");
        JPanel containerSend = new JPanel();
        containerSend.setLayout(new GridBagLayout());
        GridBagConstraints cSend = new GridBagConstraints();
        sendFiled = new JTextField(25);
        cSend.fill = GridBagConstraints.HORIZONTAL;
        cSend.gridx = 0;
        cSend.gridwidth = 2;
        cSend.gridy = 0;
        containerSend.add(sendFiled,cSend);
        cSend.weightx = 1.0f;
        cSend.fill = GridBagConstraints.HORIZONTAL;
        cSend.gridx = 2;
        cSend.gridy = 0;
        containerSend.add(btnSend,cSend);
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendFiled.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        return containerSend;
    }

    public void sendMessage(){
        if (isConnected){
            String text = sendFiled.getText();
            if (!text.equals("")){
                serverWindow.message(nameField.getText() + ": " + text);
                sendFiled.setText("");
            }
            else{
                appendLog("Введите сообщение!");
            }
        } else {
            appendLog("Нет подключения к серверу");
        }

    }


    public void answer(String msg){
        appendLog(msg);
    }
    public void disconnectFromServer() {
        if (isConnected) {
            addressUserPan.setVisible(true);
            isConnected = false;
            serverWindow.disconnectUser(this);
            appendLog("Вы отключены от сервера!");
        }
    }
    public boolean isConnected(){
        return isConnected;
    }
    private void appendLog(String text){
        log.append(text + "\n");
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING){
            disconnectFromServer();
        }
        super.processWindowEvent(e);
    }
}


