package com.myapp.form;

import com.myapp.helper.DBConnection;
import com.myapp.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BuyProductForm extends JInternalFrame {
    private JPanel panel = new JPanel();
    private JPanel panelBottom = new JPanel();
    private DBConnection dbConnection = new DBConnection();
    private DefaultTableModel model = new DefaultTableModel();
    private JTable tblProduct = new JTable();
    private JLabel labelTitle, labelProductId, labelProductName, labelProductPrice, labelProductBrand, labelProductQuantity, labelProductRating;
    private JLabel productId, productName, productPrice, productBrand, productQuantity, productRating;
    private JSpinner spinnerQuantity;
    private JButton btnAddCart;
    private int valueQuantity = 0;
    private User user = new User();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new BuyProductForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public BuyProductForm() {
        initComponents();
    }

    private void initComponents() {
        setUserData();
        showProduct();
        panel.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panelBottom.setPreferredSize(new Dimension(400, 300));
        panelBottom.setMaximumSize(new Dimension(400, 300));
        panelBottom.setLayout(new GridLayout(6, 2));
        labelTitle = new JLabel("Our Product");
        labelTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitle.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        labelProductId = new JLabel("Product ID : ");
        productId = new JLabel("-");
        labelProductName = new JLabel("Product Name : ");
        productName = new JLabel("-");
        labelProductPrice = new JLabel("Product Price : ");
        productPrice = new JLabel("-");
        labelProductBrand = new JLabel("Product Brand : ");
        productBrand = new JLabel("-");
        labelProductQuantity = new JLabel("Quantity : ");
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
        labelProductRating = new JLabel("Rating : ");
        productRating = new JLabel("-");
        btnAddCart = new JButton("Add to Cart");
        btnAddCart.setEnabled(false);
        btnAddCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddCartActionPerformed(e);
            }
        });

        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });

        panelBottom.add(labelProductId);
        panelBottom.add(productId);
        panelBottom.add(labelProductName);
        panelBottom.add(productName);
        panelBottom.add(labelProductPrice);
        panelBottom.add(productPrice);
        panelBottom.add(labelProductBrand);
        panelBottom.add(productBrand);
        panelBottom.add(labelProductQuantity);
        panelBottom.add(spinnerQuantity);
        panelBottom.add(labelProductRating);
        panelBottom.add(productRating);

        panel.add(labelTitle);
        panel.add(new JScrollPane(tblProduct));
        panel.add(panelBottom);
        panel.add(btnAddCart);
        add(panel, BorderLayout.NORTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setClosable(true);
        setResizable(true);
        setVisible(true);

    }

    private void showProduct() {
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "ProductID", "BrandName", "ProductName", "ProductPrice", "ProductQuantity", "ProductRating"
        });

        try {
            String query = "SELECT p.id, b.name, p.name, p.price, p.stok, p.rating FROM product p JOIN brand b ON p.brand = b.id ";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)};
                model.addRow(data);
            }
            tblProduct.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }

        tblProduct = new JTable(model);
    }

    private void tblProductMouseClicked(MouseEvent evt) {
        int i = tblProduct.getSelectedRow();
        valueQuantity = Integer.parseInt(tblProduct.getValueAt(i, 4).toString());

        productId.setText(tblProduct.getValueAt(i, 0).toString());
        productName.setText(tblProduct.getValueAt(i, 2).toString());
        productPrice.setText(tblProduct.getValueAt(i, 3).toString());
        productBrand.setText(tblProduct.getValueAt(i, 1).toString());
        spinnerQuantity.setModel(new SpinnerNumberModel(0, 0, 9999999, 1));
        productRating.setText(tblProduct.getValueAt(i, 5).toString());
        btnAddCart.setEnabled(true);
    }

    private void btnAddCartActionPerformed(ActionEvent e) {
        int quantityInput = Integer.parseInt(spinnerQuantity.getValue().toString());
        if (quantityInput < 1) {
            JOptionPane.showMessageDialog(null, "Quantity minimum is 1");
        } else if (quantityInput > valueQuantity) {
            JOptionPane.showMessageDialog(null, "Quantity cannot be more than available stock");
        } else {
            try {
                String query = "UPDATE product SET stok = stok - " + quantityInput + " WHERE id = '" + productId.getText() + "'";
                int rs = dbConnection.connect().createStatement().executeUpdate(query);
                if (rs == 1) {
                    try {
                        String sql = " insert into cart (user, product, qty) values (?, ?, ?)";
                        PreparedStatement prepareStatement = dbConnection.connect().prepareStatement(sql);
                        prepareStatement.setString(1, user.getId());
                        prepareStatement.setString(2, productId.getText());
                        prepareStatement.setInt(3, quantityInput);
                        prepareStatement.execute();
                        JOptionPane.showMessageDialog(null, "Product added to cart");
                        MainForm.refresh(new CartForm(), "Cart");
                    } catch (SQLException exception) {
                        JOptionPane.showMessageDialog(null, "Failed to insert data \n Error @ :" + exception.getMessage());
                    }
                }
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(null, "Error : " + exception.getMessage());
            }
        }
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

}
