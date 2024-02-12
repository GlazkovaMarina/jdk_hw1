package server;
import client.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    private static final int WINDOW_HEIGHT = 400;
    private static final int WINDOW_WIDHT = 400;
    private static final int WINDOW_POSX = 410;
    private static final int WINDOW_POSY = 300;
    private boolean isOn;
    JButton btnOn, btnOff;
    JTextArea log;
    JPanel panBottom;
    JScrollPane scrollPane;
    List<ClientGUI> clientGUIList;
    public static final String LOG_PATH = "src/main/java/server/log.txt";
    public JButton getBtnOff(){
        return btnOff;
    }
    public ServerWindow() {
        clientGUIList = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_WIDHT, WINDOW_HEIGHT);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Server");

        isOn = false;
        createMap();
        setVisible(true);
    }

    public boolean isOn(){
        return isOn;
    }
    public String getFileName(){ return LOG_PATH;}
    public boolean connectUser(ClientGUI clientGUI){
        if (!isOn){
            return false;
        }
        appendLog(clientGUI.getUserName() + " подключился к серверу");
        clientGUIList.add(clientGUI);
        return true;
    }

    public String getLog() {
        return readLog();
    }
    public void disconnectUser(ClientGUI clientGUI){
        clientGUIList.remove(clientGUI);
        if (clientGUI.isConnected()){
            clientGUI.disconnectFromServer();
             }
        else {
            appendLog(clientGUI.getUserName() + " отключен от сервера!");
        }
    }

    public void message(String msg){
        if (!isOn){
            return;
        }
        msg += "";
        appendLog(msg);
        answerAll(msg);
        saveInLog(msg);
    }

    public void answerAll(String text){
        for (ClientGUI clientGUI: clientGUIList){
                clientGUI.answer(text);
        }
    }

    private void saveInLog(String text){
        try (FileWriter writer = new FileWriter(LOG_PATH, true)){
            writer.write(text);
            writer.write("\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createMap(){
        log = new JTextArea(21, 30);
        log.setEditable(false);
        add(log);
        add(createButtons(),BorderLayout.SOUTH);
    }
    private JPanel createButtons(){
        panBottom = new JPanel(new GridLayout(1,3));
        btnOn = new JButton("on");
        btnOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isOn) {
                    appendLog("Сервер уже был запущен");
                } else {
                    appendLog("Сервер запущен!");
                    isOn = true;
                }
            }
        });
        btnOff = new JButton("off");
        btnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isOn) {
                    isOn = false;
                    while (!clientGUIList.isEmpty()){
                        disconnectUser(clientGUIList.get(clientGUIList.size()-1));
                    }
                    appendLog("Сервер остановлен!");
                } else{
                    appendLog("Сервер уже был остановлен");
                }
            }
        });
        panBottom.add(btnOn);
        panBottom.add(btnOff);
        return panBottom;
    }
    public static String readLog() {
        try (FileReader fr = new FileReader(LOG_PATH)){
            String lines = "";
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                lines += line + '\n';
            }
            br.close();
            return lines;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public void appendLog(String text){
        log.append(text + '\n');
    }
}
