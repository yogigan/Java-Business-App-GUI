package com.myapp.form;

import com.myapp.helper.DBConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class LoginForm extends JFrame {
    private JLabel labelTitle, labelUsername, labelPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnSubmit;
    private DBConnection dbConnection = new DBConnection();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        JPanel frame1 = new JPanel();
        frame1.setLayout(new FlowLayout());
        JPanel frame2 = new JPanel();
        frame2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame2.setLayout(new GridLayout(3, 2));
        JPanel frame3 = new JPanel();
        frame3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        frame3.setLayout(new FlowLayout());

        labelTitle = new JLabel("Login");
        labelTitle.setFont(new java.awt.Font("Tahoma", 1, 20));
        labelUsername = new JLabel("Username");
        txtUsername = new JTextField();
        txtUsername.add(Box.createVerticalStrut(25));
        labelPassword = new JLabel("Password");
        txtPassword = new JPasswordField();
        txtPassword.add(Box.createVerticalStrut(25));
        btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnRegisterActionPerformed(e);
            }
        });
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSubmitActionPerformed(e);
            }
        });

        frame1.add(labelTitle);
        frame2.add(labelUsername);
        frame2.add(txtUsername);
        frame2.add(labelPassword);
        frame2.add(txtPassword);
        frame3.add(btnRegister);
        frame3.add(btnSubmit);

        add(frame1, BorderLayout.NORTH);
        add(frame2, BorderLayout.CENTER);
        add(frame3, BorderLayout.SOUTH);

        setSize(380, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void btnSubmitActionPerformed(ActionEvent evt) {
        if (txtUsername.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username Field must be filled");
        } else if (txtPassword.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password Field must be filled");
        } else {
            try {
                String userName = txtUsername.getText();
                String password = txtPassword.getText();
                PreparedStatement st = dbConnection.connect()
                        .prepareStatement("Select id, username, role from user where username=? and password=?");

                st.setString(1, userName);
                st.setString(2, password);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    try {
                        PrintWriter writer = new PrintWriter("userData.ser");
                        writer.println(rs.getString(1));
                        writer.println(rs.getString(2));
                        writer.println(rs.getString(3));
                        writer.close();
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                    dispose();


                    setVisible(false);
                    JFrame frame = new MainForm();
                    frame.setTitle("Main Form");
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    JOptionPane.showMessageDialog(null, "Login Success");
                } else {
                    JOptionPane.showMessageDialog(null, "Inputted username and password is invalid");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Failed to login \n Error @ :" + e.getMessage());
            }
        }
    }

    private void btnRegisterActionPerformed(ActionEvent evt) {
        setVisible(false);
        JFrame frame = new RegisterForm();
        frame.setTitle("JKLaptop");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
