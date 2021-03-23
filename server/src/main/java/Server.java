import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ArrayList<ClientServer> clients;

    private static final int PORT = 8888;
    private AtomicInteger number = new AtomicInteger(1);
    private AuthService authService = new DBAuthService();

    public AuthService getAuthService() {
        return authService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public Server() {
        this.clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен, порт: " + PORT);
            /*Цикл для подключения не ограниченного количества клиентов. Только этот класс может рассылать
            сообщения всем клиентам*/
            while (true) {
                Socket socket = serverSocket.accept();//ждём и подключаем клиента
                System.out.println("Клиент подключился");
                new ClientServer(number.getAndIncrement(), this, socket);/*создаём клиента и ниже через метод
                "subscribe" добавляем его в коллекцию*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Метод рассылки сообщений всем клиентам
    public void broadcastMessage(String msg) {
        for (ClientServer client : clients) {
            client.sendMessage(msg);
        }
    }

    //Метод рассылки сообщений одному клиенту
    public void broadcastMessagePrivate(String msg, String user, String nickName) {
        for (ClientServer client : clients) {
            if (client.getNickname().equals(user) || client.getNickname().equals(nickName))
                client.sendMessage(msg);
        }
    }

    //Метод рассылки списка клиентов
    public void broadcastClientsList() {
        StringBuilder stringBuilder = new StringBuilder(15 * clients.size());
        stringBuilder.append("/clients ");
        for (ClientServer client : clients) {
            stringBuilder.append(client.getNickname()).append(" ");
        }
        String clientsList = stringBuilder.toString();
        broadcastMessage(clientsList);
    }

    //Метод, для того, чтобы клиента сам мог добавится при авторизации в классе ClientServer
    public void subscribe(ClientServer client) {
        clients.add(client);
        broadcastClientsList();//список клиентов
    }

    //удаляем клиента из коллекции
    public void unsubscribe(ClientServer client) {
        clients.remove(client);
        broadcastClientsList();//список клиентов
    }
}

