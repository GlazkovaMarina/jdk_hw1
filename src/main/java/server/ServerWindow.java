package server;
import client.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    private static final int WINDOW_HEIGHT = 400;
    private static final int WINDOW_WIDHT = 400;
    private static final int WINDOW_POSX = 410;
    private static final int WINDOW_POSY = 300;
    private boolean isOn;
    JButton btnOn;
    JButton btnOff;
    JTextArea textArea;
    JPanel panBottom;
    JScrollPane scrollPane;
    List<ClientGUI> clientGUIList;
    private String fileName;
    public JButton getBtnOff(){
        return btnOff;
    }
    public ServerWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setSize(WINDOW_WIDHT, WINDOW_HEIGHT);
        setTitle("Server");
        createMap();
        setVisible(true);

        isOn = false;
        fileName = "src/main/java/server/log.txt";

        clientGUIList = new ArrayList<>();
    }

    private void createMap(){
        panBottom = new JPanel(new GridLayout(1,3));
        btnOn = new JButton("on");
        btnOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isOn) {
                    textArea.append("Сервер запущен!\n");
                    isOn = true;
                }
            }
        });
        btnOff = new JButton("off");
        btnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isOn) {
                    textArea.append("Сервер остановлен!\n");
                    isOn = false;
                }
            }
        });
        panBottom.add(btnOn);
        panBottom.add(btnOff);
        textArea = new JTextArea(21, 30);
        scrollPane = new JScrollPane(textArea);
        add(panBottom,BorderLayout.SOUTH);
        add(scrollPane,BorderLayout.NORTH);
        textArea.setEditable(false);
    }

    public boolean connectUser(ClientGUI clientGUI){
        if (!isOn){
            return false;
        }
        clientGUIList.add(clientGUI);
        return true;
    }

    public static String readUsingBufferedReader(String fileName) {
        String lines = "";
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                lines += line + '\n';
            }
            br.close();
            fr.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return lines;
    }

    public boolean isOn(){
        return isOn;
    }
    public String getFileName(){ return fileName;}
    public void addText(String text){
        textArea.append(text);
    }
    public void answer(String text){
        textArea.append(text);
        for (ClientGUI clientGUI: clientGUIList){
            if (clientGUI.isConnected())
                clientGUI.answer(text);
        }
    }



}
