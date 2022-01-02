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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManageProductForm extends JInternalFrame {
    private JPanel panel;
    private DBConnection dbConnection = new DBConnection();
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable();
    private JLabel labelTitle, labelId, labelName, labelPrice, labelRating, labelStok, labelBrand;
    private JTextField txtId, txtName;
    private JSpinner spinnerPrice, spinnerRating, spinnerStok;
    private JComboBox comboBrand;
    private JButton btnInsert, btnUpdate, btnDelete, btnSubmit, btnCancel;
    private String action = "";
    private List<String> brandNameList = new ArrayList<>();
    private List<String> brandIDList = new ArrayList<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ManageProductForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ManageProductForm() {
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
        panelBottom.setLayout(new GridLayout(6, 3));
        labelTitle = new JLabel("Product List");
        labelTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelTitle.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        labelId = new JLabel("Product ID : ");
        txtId = new JTextField();
        labelName = new JLabel("Product Name : ");
        txtName = new JTextField();
        labelPrice = new JLabel("Product Price : ");
        spinnerPrice = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
        labelRating = new JLabel("Product Rating : ");
        spinnerRating = new JSpinner(new SpinnerNumberModel(0, 0, 10, 0.1));
        labelStok = new JLabel("Product Stok : ");
        spinnerStok = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
        labelBrand = new JLabel("Product Brand : ");
        comboBrand = new JComboBox(getBrandNameList());
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
        panelBottom.add(labelPrice);
        panelBottom.add(spinnerPrice);
        panelBottom.add(btnDelete);
        panelBottom.add(labelRating);
        panelBottom.add(spinnerRating);
        panelBottom.add(btnSubmit);
        panelBottom.add(labelStok);
        panelBottom.add(spinnerStok);
        panelBottom.add(btnCancel);
        panelBottom.add(labelBrand);
        panelBottom.add(comboBrand);

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
        spinnerPrice.setValue(0);
        spinnerPrice.setEnabled(true);
        spinnerStok.setValue(0);
        spinnerStok.setEnabled(true);
        spinnerRating.setValue(0);
        spinnerRating.setEnabled(true);
        comboBrand.setSelectedIndex(0);
        comboBrand.setEnabled(true);
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
        spinnerPrice.setEnabled(false);
        spinnerStok.setEnabled(false);
        spinnerRating.setEnabled(false);
        comboBrand.setEnabled(false);
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
                "ProductID", "BrandName", "ProductName", "ProductPrice", "ProductStok", "ProductRating"
        });
        try {
            String query = "SELECT p.id, b.name, p.name, p.price, p.stok, p.rating " +
                    "FROM Product p INNER JOIN brand b on p.brand = b.id ";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                String[] data = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)};
                model.addRow(data);
            }
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }

    }

    private String[] getBrandNameList() {
        brandIDList.add("");
        brandNameList.add("");
        try {
            String query = "SELECT b.id, b.name " +
                    "FROM brand b ";
            ResultSet rs = dbConnection.connect().createStatement().executeQuery(query);
            while (rs.next()) {
                brandIDList.add(rs.getString(1));
                brandNameList.add(rs.getString(2));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
            return new String[0];
        }
        return brandNameList.toArray(new String[0]);
    }

    private void tblProductMouseClicked(MouseEvent evt) {
        if (!action.equals("") && !action.equals("INSERT")) {
            int i = table.getSelectedRow();
            txtId.setEnabled(false);
            txtId.setText(table.getValueAt(i, 0).toString());
            txtName.setText(table.getValueAt(i, 2).toString());
            spinnerPrice.setValue(Integer.parseInt(table.getValueAt(i, 3).toString()));
            spinnerStok.setValue(Integer.parseInt(table.getValueAt(i, 4).toString()));
            spinnerRating.setValue(Double.parseDouble(table.getValueAt(i, 5).toString()));
            comboBrand.setSelectedIndex(brandNameList.indexOf(table.getValueAt(i, 1).toString()));
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
            JOptionPane.showMessageDialog(null, "Product name must be fill");
        } else {
            try {
                String query = "UPDATE Product SET name = '" + txtName.getText() + "', " +
                        "price = " + Integer.parseInt(spinnerPrice.getValue().toString()) + ", " +
                        "rating = " + Double.parseDouble(spinnerRating.getValue().toString()) + ", " +
                        "stok = " + Integer.parseInt(spinnerStok.getValue().toString()) + ", " +
                        "brand = '" + brandIDList.get(brandNameList.indexOf(comboBrand.getSelectedItem().toString())) + "' " +
                        "WHERE id = '" + txtId.getText() + "'";
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
            JOptionPane.showMessageDialog(null, "Product name must be fill");
        } else if (Integer.parseInt(spinnerPrice.getValue().toString()) < 1 || spinnerPrice.getValue().toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Price must be fill");
        } else if (Double.parseDouble(spinnerRating.getValue().toString()) < 1 || Double.parseDouble(spinnerRating.getValue().toString()) > 10) {
            JOptionPane.showMessageDialog(null, "Rating must be 1 to 10");
        } else if (Integer.parseInt(spinnerStok.getValue().toString()) < 1 || spinnerStok.getValue().toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Stok must be fill");
        } else if (comboBrand.getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brand must be fill");
        } else {
            try {
                PreparedStatement pst = dbConnection.connect().prepareStatement(
                        "Insert into product (id, name, price, stok, rating, brand) "
                                + "values (?,?,?,?,?,?)"
                );
                pst.setString(1, txtId.getText());
                pst.setString(2, txtName.getText());
                pst.setInt(3, Integer.parseInt(spinnerPrice.getValue().toString()));
                pst.setInt(4, Integer.parseInt(spinnerStok.getValue().toString()));
                pst.setDouble(5, Double.parseDouble(spinnerRating.getValue().toString()));
                pst.setString(6, brandIDList.get(brandNameList.indexOf(comboBrand.getSelectedItem().toString())));
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
                String query = "delete from product where id = ?";
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