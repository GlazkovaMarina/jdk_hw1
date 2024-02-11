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
    private static final int WINDOW_HEIGHT = 400;
    private static final int WINDOW_WIDHT = 400;
    private static final int WINDOW_POSX = 5;
    private static final int WINDOW_POSY = 300;
    private boolean isConnected;
    JButton btnLogin, btnSend;
    JTextField nameField, sendFiled, ipField, portField;
    JPasswordField passwordField;
    JPanel  addressUserpan;
    JScrollPane scrollPane;
    JTextArea textArea;



    public ClientGUI(ServerWindow serverWindow){
        isConnected = false;
        setLocation(WINDOW_POSX + (new Random()).nextInt(0,500), WINDOW_POSY);
        setSize(WINDOW_WIDHT, WINDOW_HEIGHT);
        setTitle("Chat client");
        createMap(serverWindow);
        setVisible(true);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                serverWindow.addText(nameField.getText() + " отключился!\n");
            }
            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        serverWindow.getBtnOff().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    isConnected = false;
                    textArea.append("Вы были отключены от сервера!\n");
                    addressUserpan.setVisible(true);
                }
            }
        });


    }

    private void createMap(ServerWindow serverWindow){
        add(setAddressUserPanel(serverWindow), BorderLayout.NORTH);
        add(setTextPanel(),BorderLayout.CENTER);
        add(setSendPanel(serverWindow), BorderLayout.SOUTH);
    }

    private JScrollPane setTextPanel(){
        textArea = new JTextArea(15, 30);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        return scrollPane;
    }
    private JPanel setAddressUserPanel(ServerWindow serverWindow){
        addressUserpan = new JPanel(new GridLayout(4, 3));

        btnLogin = new JButton("login");
        JLabel ipLabel = new JLabel("Ip address");
        ipField = new JTextField("127.0.0.1");
        JLabel portLabel = new JLabel("Port");
        portField = new JTextField("8189");

        JLabel nameLabel = new JLabel("User name");
        nameField = new JTextField("Ivan Ivanovich");
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField("ivanov", 10);

        addressUserpan.add(ipLabel);
        addressUserpan.add(portLabel);
        addressUserpan.add(new JPanel());
        addressUserpan.add(ipField);
        addressUserpan.add(portField);
        addressUserpan.add(new JPanel());
        addressUserpan.add(nameLabel);
        addressUserpan.add(passwordLabel);
        addressUserpan.add(new JPanel());
        addressUserpan.add(nameField);
        addressUserpan.add(passwordField);
        addressUserpan.add(btnLogin);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectServer(serverWindow);
            }
        });
        return addressUserpan;
    }
    private void connectServer(ServerWindow serverWindow){
        if (!serverWindow.isOn()) {
            isConnected = false;
            textArea.append("Подключение не удалось\n");
        }
        else if (isConnected == false && serverWindow.connectUser(this) && isActivePanel()){
                isConnected = true;
                textArea.append("Вы успешно подключились!\n\n");
                serverWindow.addText(nameField.getText() + " подключился к беседе!\n");
                textArea.append(serverWindow.readUsingBufferedReader(serverWindow.getFileName()));
                addressUserpan.setVisible(false);
        } else if (!isActivePanel()){
                textArea.append("Необходимо заполнить все поля ввода!\n");
        } else {
            textArea.append("Вы уже подключены!\n");
        }
    }
    private boolean isActivePanel(){
        if (!nameField.getText().equals("") && !passwordField.getText().equals("") && !portField.getText().equals("") && !ipField.getText().equals(""))
            return true;
        return false;
    }
    private JPanel setSendPanel(ServerWindow serverWindow){
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
                sendMessage(serverWindow);
            }
        });

        sendFiled.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    sendMessage(serverWindow);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        return containerSend;
    }

    private void sendMessage(ServerWindow serverWindow){
        if (!serverWindow.isOn() || isConnected == false) {
            isConnected = false;
            textArea.append("Вы отключены от сервера!\n");
        } else if (isConnected == true && !sendFiled.getText().equals("")){
            String msg = nameField.getText() + ": " + sendFiled.getText() + '\n';

            try (BufferedWriter writter = new BufferedWriter(new FileWriter(serverWindow.getFileName(), true))) {
                writter.write(msg);
                serverWindow.answer(msg);
            }catch (IOException err)
            {
                System.out.println(err.getMessage());
            }
            sendFiled.setText("");
        } else{
            textArea.append("Введите сообщение!\n");
        }
    }
    public void answer(String msg){
        textArea.append(msg);
    }
    public boolean isConnected(){
        return isConnected;
    }
}


