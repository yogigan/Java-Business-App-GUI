package com.myapp.form;

import com.myapp.helper.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class ManageBrandForm extends JInternalFrame {
    private JPanel panel;
    private DBConnection dbConnection = new DBConnection();
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable();
    private JLabel labelTitle, labelId, labelName;
    private JTextField txtId, txtName;
    private JButton btnInsert, btnUpdate, btnDelete, btnSubmit, btnCancel;
    private String action = "";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ManageBrandForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ManageBrandForm() {
        initComponents();
    }

    private void initComponents() {
        showProduct();
        JScrollPane jScrollPane = new JScrollPane(table);
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panelBottom = new JPanel();
        panelBottom.setPreferredSize(new Dimension(800, 200));
        panelBottom.setMaximumSize(new Dimension(800, 200));
        panelBottom.setLayout(new GridLayout(5, 3));
        labelTitle = new JLabel("Brand List");
        labelTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitle.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        labelId = new JLabel("Brand ID : ");
        txtId = new JTextField();
        labelName = new JLabel("Brand Name : ");
        txtName = new JTextField();
        btnInsert = new JButton("Insert");
        btnInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnInsertActionPerformed(e);
            }
        });
        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUpdateActionPerformed(e);
            }
        });
        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnDeleteActionPerformed(e);
            }
        });
        btnSubmit = new JButton("Submit");
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSubmitActionPerformed(e);
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });
        panelBottom.add(labelId);
        panelBottom.add(txtId);
        panelBottom.add(btnInsert);
        panelBottom.add(labelName);
        panelBottom.add(txtName);
        panelBottom.add(btnUpdate);
        panelBottom.add(new JPanel());
        panelBottom.add(new JPanel());
        panelBottom.add(btnDelete);
        panelBottom.add(new JPanel());
        panelBottom.add(new JPanel());
        panelBottom.add(btnSubmit);
        panelBottom.add(new JPanel());
        panelBottom.add(new JPanel());
        panelBottom.add(btnCancel);

        panel.add(labelTitle);
        panel.add(jScrollPane);
        panel.add(panelBottom);
        add(panel, BorderLayout.NORTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setResizable(true);
        setVisible(true);

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        initial();
    }

    private void initial() {
        action = "";
        txtName.setText("");
        txtName.setEnabled(true);
        txtId.setText("");
        txtId.setEnabled(true);
        btnInsert.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnSubmit.setEnabled(false);
        btnCancel.setEnabled(false);
    }

    private void modify() {
        txtId.setEnabled(false);
        btnInsert.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSubmit.setEnabled(true);
        btnCancel.setEnabled(true);
    }


    private void btnSubmitActionPerformed(ActionEvent e) {
        switch (action) {
            case "INSERT":
                insert();
                break;
            case "UPDATE":
                update();
                break;
            case "DELETE":
                delete();
                break;
        }
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        txtName.setEnabled(false);
        modify();
        action = "DELETE";
    }

    private void btnUpdateActionPerformed(ActionEvent e) {
        modify();
        action = "UPDATE";
    }

    private void showProduct() {
        model.setRowCount(0);
        model.setColumnIdentifiers(new Object[]{
                "BrandID", "BrandName"
        });
        try {
            String query = "SELECT id, name FROM brand ";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2)};
                model.addRow(data);
            }
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }

    }

    private void tblProductMouseClicked(MouseEvent evt) {
        if (!action.equals("")){
            int i = table.getSelectedRow();
            txtId.setText(table.getValueAt(i, 0).toString());
            txtName.setText(table.getValueAt(i, 1).toString());
            txtId.setEnabled(false);
        }
    }

    private void btnInsertActionPerformed(ActionEvent e) {
        txtId.setText(generateId());
        action = "INSERT";
        modify();
    }

    public String generateId() {
        Random random = new Random();
        int randomNumber = random.nextInt(900) + 100;
        return "BD" + randomNumber;
    }

    public void update() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must select the data on the table");
        } else if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brand name must be fill");
        }else {
            try {
                String query = "UPDATE brand SET name = '" + txtName.getText() + "' WHERE id = '" + txtId.getText() + "'";
                dbConnection.connect().createStatement().executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Update success");
                initial();
                showProduct();
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(null, "Error : " + exception.getMessage());
            }
        }
    }

    public void insert() {
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brand name must be fill");
        }else {
            try {
                PreparedStatement pst = dbConnection.connect().prepareStatement(
                        "Insert into brand (id, name) "
                                + "values (?,?)"
                );
                pst.setString(1, txtId.getText());
                pst.setString(2, txtName.getText());
                pst.execute();
                JOptionPane.showMessageDialog(null, "Insert success");
                initial();
                showProduct();
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }
    }

    public void delete() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must select the data on the table");
        } else {
            try {
                String query = "delete from brand where id = ?";
                PreparedStatement preparedStmt = dbConnection.connect().prepareStatement(query);
                preparedStmt.setString(1, txtId.getText());
                preparedStmt.execute();
                dbConnection.connect().close();
                JOptionPane.showMessageDialog(null, "Delete success");
                initial();
                showProduct();
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }
    }
}
