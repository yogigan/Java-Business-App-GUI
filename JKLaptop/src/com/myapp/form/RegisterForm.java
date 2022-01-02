package com.myapp.form;

import com.myapp.helper.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class RegisterForm extends JFrame {
    private DBConnection dbConnection = new DBConnection();
    private JLabel labelTitle, labelUsername, labelEmail, labelGender, labelAddress, labelPassword;
    private JRadioButton rbMale, rbFemale;
    private ButtonGroup btnGroupGender;
    private JTextField txtUsername, txtEmail;
    private JTextArea txtAddress;
    private JPasswordField txtPassword;
    private JButton btnReset, btnLogin, btnSubmit;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new RegisterForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public RegisterForm() {
        initComponents();
    }

    private void initComponents() {
        JPanel frame1 = new JPanel();
        frame1.setLayout(new FlowLayout());
        JPanel frame2 = new JPanel();
        frame2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame2.setLayout(new GridLayout(6, 2));
        JPanel frame3 = new JPanel();
        frame3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        frame3.setLayout(new FlowLayout());

        labelTitle = new JLabel("JKLAPTOP");
        labelTitle.setFont(new java.awt.Font("Tahoma", 1, 20));

        labelUsername = new JLabel("Username");
        txtUsername = new JTextField();

        labelEmail = new JLabel("Email");
        txtEmail = new JTextField();

        labelPassword = new JLabel("Password");
        txtPassword = new JPasswordField();

        labelGender = new JLabel("Gender");
        btnGroupGender = new ButtonGroup();
        rbMale = new JRadioButton();
        rbMale.setText("Male");
        rbMale.setActionCommand("Male");
        rbFemale = new JRadioButton();
        rbFemale.setText("Female");
        rbFemale.setActionCommand("Female");
        btnGroupGender.add(rbMale);
        btnGroupGender.add(rbFemale);
        JPanel rButtons = new JPanel();
        rButtons.add(rbMale);
        rButtons.add(rbFemale);


        labelAddress = new JLabel("Address");
        txtAddress = new JTextArea();
        txtAddress.setRows(3);

        btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnResetActionPerformed(e);
            }
        });

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLoginActionPerformed(e);
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
        frame2.add(labelEmail);
        frame2.add(txtEmail);
        frame2.add(labelPassword);
        frame2.add(txtPassword);
        frame2.add(labelGender);
        frame2.add(rButtons);
        frame2.add(labelAddress);
        frame2.add(txtAddress);
        frame3.add(btnReset);
        frame3.add(btnLogin);
        frame3.add(btnSubmit);

        add(frame1, BorderLayout.NORTH);
        add(frame2, BorderLayout.CENTER);
        add(frame3, BorderLayout.SOUTH);

        setSize(380, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void btnSubmitActionPerformed(ActionEvent evt) {
        if (isVerifiedForm()) {
            try {
                String query = " insert into user (id, username, email, password, gender, address, role)"
                        + " values (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement prepareStatement = dbConnection.connect().prepareStatement(query);
                prepareStatement.setString(1, generateId());
                prepareStatement.setString(2, txtUsername.getText());
                prepareStatement.setString(3, txtEmail.getText());
                prepareStatement.setString(4, txtPassword.getText());
                prepareStatement.setString(5, btnGroupGender.getSelection().getActionCommand());
                prepareStatement.setString(6, txtAddress.getText());
                prepareStatement.setBoolean(7, false);
                prepareStatement.execute();
                JOptionPane.showMessageDialog(null, "Register Success!");
                setVisible(false);
                JFrame frame = new LoginForm();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Failed to insert data \n Error @ :" + e.getMessage());
            }
        }
    }

    private void btnLoginActionPerformed(ActionEvent evt) {
        setVisible(false);
        JFrame frame = new LoginForm();
        frame.setTitle("JKLaptop");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void btnResetActionPerformed(ActionEvent evt) {
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        btnGroupGender.clearSelection();
        txtAddress.setText("");
    }

    public String generateId() {
        Random random = new Random();
        int randomNumber = random.nextInt(900) + 100;
        return "US" + randomNumber;
    }

    public boolean isVerifiedForm() {
        if (!isVerifiedUsername()) {
            return false;
        } else if (!isVerifiedEmail()) {
            return false;
        } else if (!isVerifiedPassword()) {
            return false;
        } else if (!rbMale.isSelected() && !rbFemale.isSelected()) {
            JOptionPane.showMessageDialog(null, "One gender must be selected");
            return false;
        } else if (!isVerifiedAddress()) {
            return false;
        }
        return true;
    }

    public boolean isVerifiedUsername() {
        if (txtUsername.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username must be filled");
            return false;
        }
        if (txtUsername.getText().length() < 5 || txtUsername.getText().length() > 20) {
            JOptionPane.showMessageDialog(null, "Username length must between 5 and 20 characters");
            return false;
        }
        return true;
    }

    public boolean isVerifiedEmail() {
        if (txtEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email must be filled");
            return false;
        }

        char[] arrEmail = txtEmail.getText().toCharArray();
        int dotCount = 0, atCount = 0;
        int length = arrEmail.length;

        for (char email : arrEmail) {
            if (String.valueOf(email).equals("@")) {
                atCount += 1;
            }

            if (String.valueOf(email).equals(".")) {
                dotCount += 1;
            }

            if (dotCount > 0) {
                if (String.valueOf(email).equals("@")) {
                    JOptionPane.showMessageDialog(null, "Email character '@' must not be next to '.'");
                    return false;
                }
            }

        }

        if (String.valueOf(arrEmail[0]).equals("@") || String.valueOf(arrEmail[0]).equals(".")) {
            JOptionPane.showMessageDialog(null, "Email input must not stars with '@' or '.'");
            return false;
        } else if (dotCount < 1 || atCount < 1) {
            JOptionPane.showMessageDialog(null, "Email must not contains more than 1 '@' or '.'");
            return false;
        } else if (length < 4) {
            JOptionPane.showMessageDialog(null, "Email input must end with '.com'");
            return false;
        }

        String endChar = String.valueOf(arrEmail[length - 4]) +
                arrEmail[length - 3] +
                arrEmail[length - 2] +
                arrEmail[length - 1];
        if (!endChar.equals(".com")) {
            JOptionPane.showMessageDialog(null, "Email input must end with '.com'");
            return false;
        }
        return true;
    }

    public boolean isVerifiedPassword() {
        if (txtPassword.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password must be filled");
            return false;
        }

        char[] arrPassword = txtPassword.getText().toCharArray();
        int charCount = 0, numCount = 0;

        for (char password : arrPassword) {
            if (Character.isDigit(password)) {
                numCount += 1;
            } else {
                charCount += 1;
            }
        }

        if (charCount == 0 || numCount == 0) {
            JOptionPane.showMessageDialog(null, "Password must alphanumeric");
            return false;
        }
        return true;
    }

    public boolean isVerifiedAddress() {
        if (txtAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address must be filled");
            return false;
        }

        char[] arrAddress = txtAddress.getText().toCharArray();
        int length = arrAddress.length;

        if (length < 4) {
            JOptionPane.showMessageDialog(null, "Address must end with 'Street'");
            return false;
        }
        String endChar = String.valueOf(arrAddress[length - 6]) +
                arrAddress[length - 5] +
                arrAddress[length - 4] +
                arrAddress[length - 3] +
                arrAddress[length - 2] +
                arrAddress[length - 1];
        if (!endChar.equalsIgnoreCase("Street")) {
            JOptionPane.showMessageDialog(null, "Address must end with 'Street'");
            return false;
        }
        return true;
    }
}

