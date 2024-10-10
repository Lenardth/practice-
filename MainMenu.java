import java.awt.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {
    private double walletBalance;
    private JLabel clockLabel;
    private ArrayList<Taxi> taxis;
    private DefaultListModel<String> notificationsModel;

    public MainMenu(double initialBalance) {
        this.walletBalance = initialBalance;
        taxis = new ArrayList<>();
        initializeTaxis();

        setTitle("Taxi Service Dashboard");
        setSize(1080, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new BorderLayout());
        
        // Sidebar panel for navigation buttons with styling
        JPanel sidebarPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebarPanel.setBackground(new Color(52, 73, 94));
        sidebarPanel.setPreferredSize(new Dimension(150, getHeight()));
        sidebarPanel.setOpaque(false);

        Color buttonColor = new Color(70, 130, 180);

        RoundedButton bookTaxiButton = createActionButton("Book a Taxi", buttonColor);
        RoundedButton nearbyTaxiButton = createActionButton("See Nearby Taxis", buttonColor);
        RoundedButton taxiRoutesButton = createActionButton("View Taxi Routes", buttonColor);
        RoundedButton taxiScheduleButton = createActionButton("Taxi Schedule", buttonColor);
        RoundedButton themeSwitcher = createActionButton("Switch Theme", new Color(155, 89, 182));

        sidebarPanel.add(bookTaxiButton);
        sidebarPanel.add(nearbyTaxiButton);
        sidebarPanel.add(taxiRoutesButton);
        sidebarPanel.add(taxiScheduleButton);
        sidebarPanel.add(themeSwitcher);

        backgroundPanel.add(sidebarPanel, BorderLayout.WEST);

        // Top-right panel for clock and notifications
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 20));
        clockLabel.setForeground(Color.WHITE);
        initClock();

        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clockPanel.setOpaque(false);
        clockPanel.add(clockLabel);
        topRightPanel.add(clockPanel, BorderLayout.NORTH);

        notificationsModel = new DefaultListModel<>();
        addSampleNewsNotifications();
        JPanel notificationPanel = createModernNotificationPanel();
        topRightPanel.add(notificationPanel, BorderLayout.CENTER);

        backgroundPanel.add(topRightPanel, BorderLayout.EAST);

        // Center panel for wallet and destination input
        JPanel walletPanel = new JPanel(new GridBagLayout());
        walletPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        walletPanel.setOpaque(false);

        JLabel walletLabel = new JLabel("Wallet Balance: R" + walletBalance, SwingConstants.CENTER);
        walletLabel.setFont(new Font("Arial", Font.BOLD, 36));
        walletLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        inputPanel.setOpaque(false);

        JLabel initialPointLabel = new JLabel("Initial Point:");
        initialPointLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        initialPointLabel.setForeground(Color.WHITE);
        JTextField initialPointField = new JTextField();
        initialPointField.setBackground(new Color(200, 200, 200));
        initialPointField.setForeground(Color.BLACK);

        JLabel finalDestinationLabel = new JLabel("Final Destination:");
        finalDestinationLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        finalDestinationLabel.setForeground(Color.WHITE);
        JTextField finalDestinationField = new JTextField();
        finalDestinationField.setBackground(new Color(200, 200, 200));
        finalDestinationField.setForeground(Color.BLACK);

        inputPanel.add(initialPointLabel);
        inputPanel.add(initialPointField);
        inputPanel.add(finalDestinationLabel);
        inputPanel.add(finalDestinationField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        walletPanel.add(walletLabel, gbc);

        gbc.gridy = 1;
        walletPanel.add(inputPanel, gbc);

        JPanel walletButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        walletButtonPanel.setOpaque(false);
        RoundedButton addMoneyButton = createActionButton("Add Money", buttonColor);
        RoundedButton withdrawMoneyButton = createActionButton("Withdraw Money", buttonColor);

        walletButtonPanel.add(addMoneyButton);
        walletButtonPanel.add(withdrawMoneyButton);

        gbc.gridy = 2;
        walletPanel.add(walletButtonPanel, gbc);

        backgroundPanel.add(walletPanel, BorderLayout.CENTER);

        bookTaxiButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Book a Taxi..."));
        nearbyTaxiButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "See Nearby Taxis..."));
        taxiRoutesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Taxi Routes..."));
        taxiScheduleButton.addActionListener(e -> showTaxiSchedule());
        themeSwitcher.addActionListener(e -> switchTheme());

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private RoundedButton createActionButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text, color, color.brighter());
        button.setPreferredSize(new Dimension(120, 50)); // Adjusted button size to match login form
        return button;
    }

    private void showTaxiSchedule() {
        if (taxis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No taxi schedule available.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"Taxi Number", "Available Start Time", "Available End Time"};
        String[][] tableData = new String[taxis.size()][3];
        for (int i = 0; i < taxis.size(); i++) {
            Taxi taxi = taxis.get(i);
            tableData[i][0] = taxi.getTaxiNumber();
            tableData[i][1] = taxi.getStartTime();
            tableData[i][2] = taxi.getEndTime();
        }

        JTable table = new JTable(tableData, columnNames);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Taxi Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createModernNotificationPanel() {
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Notifications"));
        notificationPanel.setPreferredSize(new Dimension(150, 100));
        notificationPanel.setOpaque(false);

        JList<String> notificationsList = new JList<>(notificationsModel);
        notificationsList.setFont(new Font("Arial", Font.PLAIN, 12));
        notificationsList.setVisibleRowCount(2);
        notificationsList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane notificationsScrollPane = new JScrollPane(notificationsList);
        notificationsScrollPane.setPreferredSize(new Dimension(150, 80));
        notificationsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 200), 1));

        notificationPanel.add(notificationsScrollPane, BorderLayout.CENTER);
        return notificationPanel;
    }

    private void initClock() {
        Timer clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            clockLabel.setText("Current Time: " + sdf.format(new Date()));
        });
        clockTimer.start();
    }

    private void switchTheme() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initializeTaxis() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("taxis.ser"))) {
            taxis = (ArrayList<Taxi>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            taxis = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load taxi data. Starting with an empty list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleNewsNotifications() {
        notificationsModel.addElement("Welcome to the Taxi Service App!");
        notificationsModel.addElement("Maintenance on Sunday.");
        notificationsModel.addElement("New routes to Pretoria.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu(100.0));
    }
}