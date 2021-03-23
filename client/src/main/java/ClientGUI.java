import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame {

    private JTextArea txtClientsList;
    private JTextArea txtMessageList;
    private JTextField txtMessageSend;

    private ClientNetwork clientNetwork;

    JPanel loginPanel = new JPanel();
    JPanel mainPanel = new JPanel();

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
                login.setText("");
                password.setText("");
            }
        });
        loginPanel.add(buttonPanel);
        add(loginPanel);
    }

    private void  setCallBacks(){
        this.clientNetwork.setCallOnChangeClientList(clientsList -> txtClientsList.setText(clientsList));
        this.clientNetwork.setCallOnMsgRecieved(message -> txtMessageList.append(message + "\n"));
        this.clientNetwork.setCallOnAuth(s -> {
            loginPanel.setVisible(false);
            mainPanel();
        });
        this.clientNetwork.setCallOnError(message ->
                JOptionPane.showMessageDialog(null, message, "Внимание!", JOptionPane.ERROR_MESSAGE));
    }
}