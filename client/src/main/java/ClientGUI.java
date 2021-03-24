import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class ClientGUI extends JFrame {

    private JTextArea txtClientsList;
    private JTextArea txtMessageList;
    private JTextField txtMessageSend;
    private static final int COUNT_HISTORY_LINES = 100;
    private static String loginUser;
    private static final Path path = Paths.get("client/history/");

    private ClientNetwork clientNetwork;

    JPanel loginPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JTextField login = new JTextField();

    public ClientGUI() {

        setTitle("Мой чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300, 300, 300, 466);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                clientNetwork.sendMessage("/end");
                super.windowClosing(event);
            }
        });

        loginJPanel();
        //mainPanel();

        setVisible(true);

        //Выводим сообщение callBack в текстовое поле
        this.clientNetwork = new ClientNetwork();
        setCallBacks();
        this.clientNetwork.connect();



    }

    private void onSendMessage() {
        if (txtMessageSend.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Введите текст сообщения!");
        } else {
            this.clientNetwork.sendMessage(txtMessageSend.getText());
        }
        txtMessageSend.setText("");
    }


    private void mainPanel() {

        JButton btnSend = new JButton("Отправить сообщение");
        JLabel lbMessageList = new JLabel("Переписка");
        JLabel lbMessageListSend = new JLabel("Отправить сообщение: ");


        txtClientsList = new JTextArea();
        txtMessageList = new JTextArea();
        txtMessageSend = new JTextField();

        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        mainPanel.setBackground(new Color(17, 30, 36));
        mainPanel.setBorder(new LineBorder(Color.WHITE, 1));

        txtClientsList.setBackground(new Color(9, 16, 21));
        txtClientsList.setBorder(new LineBorder(new Color(112, 132, 153), 0));
        txtClientsList.setPreferredSize(new Dimension(280, 32));
        txtClientsList.setForeground(new Color(200, 200, 200));
        txtClientsList.setLineWrap(true);
        txtClientsList.setEditable(false);

        txtMessageList.setBackground(new Color(9, 16, 21));
        txtMessageList.setBorder(new LineBorder(new Color(112, 132, 153), 0));
        txtMessageList.setPreferredSize(new Dimension(280, 252));
        txtMessageList.setForeground(new Color(200, 200, 200));
        txtMessageList.setLineWrap(true);
        txtMessageList.setEditable(false);

        txtMessageSend.setBackground(new Color(9, 16, 21));
        txtMessageSend.setBorder(new LineBorder(new Color(112, 132, 153), 0));
        txtMessageSend.setPreferredSize(new Dimension(280, 27));
        txtMessageSend.setForeground(new Color(200, 200, 200));
        txtMessageSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSendMessage();
            }
        });

        lbMessageList.setForeground(new Color(200, 200, 200));
        lbMessageListSend.setForeground(new Color(200, 200, 200));

        btnSend.setBackground(new Color(41, 221, 126));
        btnSend.setForeground(new Color(9, 16, 21));
        btnSend.setFocusPainted(false);
        btnSend.setMargin(new Insets(10, 10, 10, 10));

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSendMessage();
            }
        });

        add(mainPanel);
        mainPanel.add(lbMessageList);
        mainPanel.add(txtClientsList);
        mainPanel.add(txtMessageList);
        mainPanel.add(lbMessageListSend);
        mainPanel.add(txtMessageSend);
        mainPanel.add(btnSend);
    }

    private void loginJPanel() {
        loginPanel.setBackground(Color.white);
        loginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        loginPanel.setPreferredSize(new Dimension(300, 150));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Authorization"));

        loginPanel.setBackground(new Color(17, 30, 36));
        loginPanel.setBorder(new LineBorder(Color.WHITE, 1));

        JTextField login = new JTextField();
        login.setBackground(new Color(9, 16, 21));
        login.setBorder(new LineBorder(new Color(112, 132, 153), 0));
        login.setForeground(new Color(200, 200, 200));

        JLabel loginLabel = new JLabel("Введите ваш логин: ");
        loginLabel.setForeground(new Color(200, 200, 200));

        JLabel passwordLabel = new JLabel("Введите ваш пароль: ");
        passwordLabel.setForeground(new Color(200, 200, 200));

        JPasswordField password = new JPasswordField();
        password.setBackground(new Color(9, 16, 21));
        password.setBorder(new LineBorder(new Color(112, 132, 153), 0));
        password.setForeground(new Color(200, 200, 200));

        login.setPreferredSize(new Dimension(100, 25));
        password.setPreferredSize(new Dimension(100, 25));
        loginPanel.add(loginLabel);
        loginPanel.add(login);
        loginPanel.add(passwordLabel);
        loginPanel.add(password);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(279, 55));
        buttonPanel.setBackground(new Color(17, 30, 36));
        JButton button = new JButton("Авторизироваться");

        button.setBackground(new Color(41, 221, 126));
        button.setForeground(new Color(9, 16, 21));
        button.setFocusPainted(false);
        button.setMargin(new Insets(10, 10, 10, 10));

        buttonPanel.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNetwork.sendMessage("/auth " + login.getText()
                        + " " + String.valueOf(password.getPassword()));
                loginUser = login.getText();
                login.setText("");
                password.setText("");
            }
        });
        loginPanel.add(buttonPanel);
        add(loginPanel);
    }

    //Метод сохранения истории
    private void saveHistory(String nickname, String msg) {

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (!msg.startsWith("/clients ")) {
                Files.write(Paths.get(path + "/" + nickname), (msg + System.lineSeparator()).getBytes(),
                        CREATE, APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHistory(String login) {
        List<String> lines = null;
        if (!Files.notExists(Path.of(path + "/" + login))) {
            try {
                lines = new BufferedReader(new FileReader(path + "/" + login)).lines().
                        collect(Collectors.toList());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            int startPos = 0;
            assert lines != null;
            if (lines.size() > COUNT_HISTORY_LINES) {
                startPos = lines.size() - COUNT_HISTORY_LINES;
            }
            for (int i = startPos; i < lines.size(); i++) {
                txtMessageList.append(lines.get(i) + System.lineSeparator());
            }
        }
    }

    private void  setCallBacks(){
        this.clientNetwork.setCallOnChangeClientList(clientsList -> txtClientsList.setText(clientsList));
        this.clientNetwork.setCallOnMsgRecieved(message -> {
            txtMessageList.append(message + "\n");
            saveHistory(loginUser, message);
        });
        this.clientNetwork.setCallOnAuth(s -> {
            login.getText();
            loginPanel.setVisible(false);
            mainPanel();
            loadHistory(loginUser);
        });
        this.clientNetwork.setCallOnError(message ->
                JOptionPane.showMessageDialog(null, message, "Внимание!", JOptionPane.ERROR_MESSAGE));
    }
}