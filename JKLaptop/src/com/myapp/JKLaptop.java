package com.myapp;

import com.myapp.form.LoginForm;
import com.myapp.helper.DBConnection;

public class JKLaptop {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        DBConnection dbConnection = new DBConnection();
        if (dbConnection.connect() != null){
            LoginForm frame = new LoginForm();
            frame.setTitle("JKLaptop");
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

}
