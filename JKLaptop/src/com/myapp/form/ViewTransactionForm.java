package com.myapp.form;

import com.myapp.helper.DBConnection;
import com.myapp.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.awt.Label.CENTER;

public class ViewTransactionForm extends JInternalFrame {
    private DBConnection dbConnection = new DBConnection();
    private JPanel panel = new JPanel();
    private DefaultTableModel modelTransaction = new DefaultTableModel();
    private DefaultTableModel modelDetailTransaction = new DefaultTableModel();
    private JTable tblTransaction = new JTable();
    private JTable tblDetailTransaction = new JTable();
    private JLabel labelTitleTransaction, labelTitleDetailTransaction;
    private User user = new User();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ViewTransactionForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ViewTransactionForm() {
        initComponents();
    }

    private void initComponents() {
        setUserData();
        showTransaction();
        panel.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        labelTitleTransaction = new JLabel("Transaction List");
        labelTitleTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTitleTransaction.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitleTransaction.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        tblTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransactionMouseClicked(evt);
            }
        });
        labelTitleDetailTransaction = new JLabel("Transaction Detail");
        labelTitleDetailTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTitleDetailTransaction.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitleDetailTransaction.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        panel.add(labelTitleTransaction);
        panel.add(new JScrollPane(tblTransaction));
        panel.add(labelTitleDetailTransaction);
        panel.add(new JScrollPane(tblDetailTransaction));
        add(panel, BorderLayout.NORTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setClosable(true);
        setResizable(true);
        setVisible(true);
    }

    private void tblTransactionMouseClicked(MouseEvent evt) {
        showDetailTransaction();
    }

    private void showDetailTransaction() {
        modelDetailTransaction.setRowCount(0);
        modelDetailTransaction.setColumnIdentifiers(new Object[]{
                "TransactionID", "ProductID", "Qty"
        });
        try {
            String query = "SELECT transaction, product, qty FROM detail_transaction WHERE transaction = '" + modelTransaction.getValueAt(tblTransaction.getSelectedRow(), 0).toString() + "'";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3)};
                modelDetailTransaction.addRow(data);
            }
            tblDetailTransaction.setModel(modelDetailTransaction);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }

    }

    private void showTransaction() {
        modelTransaction = new DefaultTableModel();
        modelTransaction.setColumnIdentifiers(new Object[]{
                "TransactionID", "UserID", "TransactionDate"
        });
        try {
            String query = user.isRole() ?
                    "SELECT id, user, date FROM header_transaction" :
                    "SELECT id, user, date FROM header_transaction WHERE  user = '" + user.getId() + "'";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3)};
                modelTransaction.addRow(data);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }
        tblTransaction = new JTable(modelTransaction);
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
