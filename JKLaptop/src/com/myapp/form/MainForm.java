package com.myapp.form;

import com.myapp.helper.DBConnection;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainForm extends JFrame {
    private DBConnection dbConnection = new DBConnection();
    private User user = new User();
    public static JDesktopPane desktop;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainForm() {
        initComponents();
    }

    public static void refresh(JInternalFrame frame, String title) {
        desktop.removeAll();
        if (frame != null) {
            frame.setTitle(title);
            desktop.add(frame);
            desktop.setVisible(true);
        } else {
            desktop.setVisible(false);
        }
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuTransaction, menuManage;
        JMenuItem itemBrand, itemProduct, itemBuy, itemView, menuLogout;
        setUserData();

        if (user.isRole()) {
            menuManage = new JMenu("Manage");
            itemBrand = new JMenuItem("Brand");
            itemBrand.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    itemBrandActionPerformed(e);
                }
            });
            itemProduct = new JMenuItem("Product");
            itemProduct.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    itemProductActionPerformed(e);
                }
            });
            menuManage.add(itemBrand);
            menuManage.add(itemProduct);
            menuBar.add(menuManage);
        }
        menuTransaction = new JMenu("Transaction");

        if (!user.isRole()) {
            itemBuy = new JMenuItem("Buy Product");
            itemBuy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    itemBuyActionPerformed(e);
                }
            });
            menuTransaction.add(itemBuy);
        }

        itemView = new JMenuItem("View Transaction");
        itemView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemViewActionPerformed(e);
            }
        });
        menuTransaction.add(itemView);
        menuBar.add(menuTransaction);

        menuLogout = new JMenuItem("Logout");
        menuLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuLogoutActionPerformed(e);
            }
        });
        menuBar.add(menuLogout);

        setJMenuBar(menuBar);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        desktop = new JDesktopPane();
        desktop.setLayout(new BorderLayout());
        getContentPane().add(desktop);

    }

    public void itemBrandActionPerformed(ActionEvent e) {
        JInternalFrame frame = new ManageBrandForm();
        frame.setTitle("Manage Brand");
        desktop.removeAll();
        desktop.add(frame);
        desktop.setVisible(true);
    }

    public void itemProductActionPerformed(ActionEvent e) {
        JInternalFrame frame = new ManageProductForm();
        frame.setTitle("Manage Product");
        desktop.removeAll();
        desktop.add(frame);
        desktop.setVisible(true);

    }

    public void itemViewActionPerformed(ActionEvent e) {
        JInternalFrame frame = new ViewTransactionForm();
        frame.setTitle("View Transaction");
        desktop.removeAll();
        desktop.add(frame);
        desktop.setVisible(true);
    }

    public void itemBuyActionPerformed(ActionEvent e) {
        JInternalFrame frame = new BuyProductForm();
        frame.setTitle("Buy Product");
        desktop.removeAll();
        desktop.add(frame);
        desktop.setVisible(true);
    }

    public void menuLogoutActionPerformed(ActionEvent e) {
        File file = new File("userData.ser");
        if (file.delete()) {
            System.out.println("Deleted the file: " + file.getName());
        }
        setVisible(false);
        JFrame frame = new LoginForm();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setUserData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("userData.ser"));
            user.setId(reader.readLine());
            user.setUsername(reader.readLine());
            user.setRole(reader.readLine().equals("1"));
            reader.close();
        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "Error : " + e.getMessage());

            JFrame frame = new LoginForm();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            setVisible(false);
            dispose();
        }
    }
}
