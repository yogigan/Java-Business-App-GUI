package com.myapp.form;

import com.myapp.helper.DBConnection;
import com.myapp.model.Cart;
import com.myapp.model.Product;
import com.myapp.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CartForm extends JInternalFrame {
    private DBConnection dbConnection = new DBConnection();
    private JLabel labelTitle, labelUserId, labelUserName, labelDate, labelTotalPrice;
    private JLabel userId, userName, date, totalPrice;
    private JTable jTable;
    private DefaultTableModel model;
    private JButton btnCheckOut;
    private User user = new User();
    private List<Cart> cartList = new ArrayList<>();
    private int total = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new CartForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public CartForm() {
        initComponents();
    }

    private void initComponents() {
        setUserData();
        showProduct();
        JScrollPane jScrollPane = new JScrollPane(jTable);
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panelTop = new JPanel();
        panelTop.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        panelTop.setPreferredSize(new Dimension(800, 100));
        panelTop.setMaximumSize(new Dimension(800, 100));
        panelTop.setLayout(new GridLayout(2, 4, 5, 5));
        labelTitle = new JLabel("Cart");
        labelTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitle.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        labelUserId = new JLabel("User ID : ");
        userId = new JLabel(user.getId());
        userId.setHorizontalAlignment(SwingConstants.LEFT);
        labelUserName = new JLabel("UserName : ");
        userName = new JLabel(user.getUsername());
        userName.setHorizontalAlignment(SwingConstants.LEFT);
        labelDate = new JLabel("Date : ");
        date = new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        date.setHorizontalAlignment(SwingConstants.LEFT);
        labelTotalPrice = new JLabel("Total Price : ");
        totalPrice = new JLabel(total + "");
        totalPrice.setHorizontalAlignment(SwingConstants.LEFT);
        btnCheckOut = new JButton("Check Out");
        if (cartList.size() < 1) {
            btnCheckOut.setEnabled(false);
        }
        btnCheckOut.setSize(400, 50);
        btnCheckOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCheckOutActionPerformed(e);
            }
        });

        panelTop.add(labelUserId);
        panelTop.add(userId);
        panelTop.add(labelUserName);
        panelTop.add(userName);
        panelTop.add(labelDate);
        panelTop.add(date);
        panelTop.add(labelTotalPrice);
        panelTop.add(totalPrice);
        panel.add(labelTitle);
        panel.add(panelTop);
        panel.add(jScrollPane);
        panel.add(btnCheckOut);
        add(panel, BorderLayout.NORTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setResizable(true);
        setVisible(true);
    }

    private void btnCheckOutActionPerformed(ActionEvent e) {
        try {
            PreparedStatement pst = dbConnection.connect().prepareStatement(
                    "Insert into header_transaction (id, user, date) "
                            + "values (?,?,?)"
            );
            String generatedId = generateId();
            pst.setString(1, generatedId);
            pst.setString(2, user.getId());
            pst.setDate(3, new java.sql.Date(new Date().getTime()));
            pst.execute();

            for (Cart cart : cartList) {
                pst = dbConnection.connect().prepareStatement(
                        "Insert into detail_transaction (transaction, qty, product) "
                                + "values (?,?,?)"
                );
                pst.setString(1, generatedId);
                pst.setInt(2, cart.getQty());
                pst.setString(3, cart.getProductId());
                pst.execute();
            }

            pst = dbConnection.connect().prepareStatement(
                    "DELETE FROM cart WHERE user = ?"
            );
            pst.setString(1, user.getId());
            pst.execute();

            JOptionPane.showMessageDialog(null, "Check out success");
            MainForm.refresh(null, null);

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, "Error : " + exception.getMessage());
        }
    }

    private void showProduct() {
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "ProductID", "ProductName", "ProductPrice", "ProductQuantity"
        });

        try {
            String query = "SELECT p.id, p.name, p.price, c.qty FROM cart c INNER JOIN product p " +
                    "ON c.product = p.id WHERE user = '" + user.getId() + "'";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                int price = Integer.parseInt(rs.getString(3));
                int qty = Integer.parseInt(rs.getString(4));
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};
                model.addRow(data);
                total = total + (price * qty);
                setCart(data);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }

        jTable = new JTable(model);
    }

    public void setCart(String[] data) {
        Cart cart = new Cart();
        cart.setProductId(data[0]);
        cart.setUserId(data[2]);
        cart.setQty(Integer.parseInt(data[3]));
        cartList.add(cart);
    }

    private void setUserData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("userData.ser"));
            user.setId(reader.readLine());
            user.setUsername(reader.readLine());
            user.setRole(reader.readLine().equals("1"));
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }
    }

    public String generateId() {
        Random random = new Random();
        int randomNumber = random.nextInt(900) + 100;
        return "TR" + randomNumber;
    }
}
