/*Класс, который хранит информацию о клиентах*/

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientServer {
    private String nickname;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;

    public ClientServer(Integer number, Server server, Socket socket) { /*Server передаём, чтобы можно было использовать его методы для
        рассылки сообщений всем клиентам. т.к. только он имеет список клиентов*/
        this.server = server;
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.nickname = "Клиент №: " + number;
            new Thread(() -> {

                boolean continueChat = true;
                /*Цикл, для того, чтобы читать все сообщения одного клиента.
                Только этот класс может читать сообщения клиентов.*/
                String msg = null;
                boolean isAuthorised = false;
                try {
                    while (!isAuthorised && continueChat) {
                        msg = in.readUTF();//читаем сообщение клиента
                        if (msg.startsWith("/auth")) {
                            String[] tokens = msg.split("\\s");
                            nickname = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);
                            if (nickname != null) {
                                isAuthorised = true;
                                sendMessage("/authok");
                                server.subscribe(this);//Добавляем клиента в коллекцию
                            } else {
                               sendMessage("/error");
                            }
                        }
                        if (msg.equalsIgnoreCase("/end")) {
                            sendMessage("/end");
                            continueChat = false;
                        }
                    }
                    while (continueChat) {
                        msg = in.readUTF();//читаем сообщение клиента
                        if (msg.startsWith("/")) {
                            if (msg.equalsIgnoreCase("/end")) {
                                sendMessage("/end");
                                continueChat = false;
                            } else if (msg.startsWith("/w")) {
                                String[] equalsUser = msg.split("\\s");
                                server.broadcastMessagePrivate(nickname + " : " + msg.substring(3), equalsUser[1], nickname);
                            }
                        } else {
                            server.broadcastMessage(nickname + " : " + msg); //просим сервер отправить сообщения всем клиентам
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Метод необходимый для метода сервера. Который отправляет сообщения клиента в цикле по коллекции класса Server
    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
