package com.mybank.gui;

import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class SWINGDemo {
    
    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox clients;
    
    public SWINGDemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(400, 500));
        
        show = new JButton("Show");
        report = new JButton("Report");
        clients = new JComboBox();
        loadCustomers("data\\test.dat");
        
        for (int i=0; i<Bank.getNumberOfCustomers(); i++)
        {
            clients.addItem(Bank.getCustomer(i).getLastName()+", "+Bank.getCustomer(i).getFirstName());
        }
    }
    
    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 2));
        
        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);
        
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer current = Bank.getCustomer(clients.getSelectedIndex());
                String accType = current.getAccount(0)instanceof CheckingAccount?"Checking":"Savings";       
                String custInfo="<br>&nbsp;<b><span style=\"font-size:2em;\">"+current.getLastName()+", "+
                        current.getFirstName()+"</span><br><hr>"+
                        "&nbsp;<b>Acc Type: </b>"+accType+
                        "<br>&nbsp;<b>Balance: <span style=\"color:red;\">$"+current.getAccount(0).getBalance()+"</span></b>";
                if(current.getNumberOfAccounts() == 2){
                    accType = current.getAccount(1)instanceof CheckingAccount?"Checking":"Savings";
                    custInfo += "&nbsp;<b><br><br> Acc Type: </b>"+accType+
                        "<br>&nbsp;<b>Balance: <span style=\"color:red;\">$"+current.getAccount(1).getBalance()+"</span></b>";
                }
                
                log.setText(custInfo);                
            }
        });
        
        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String title = "<br>&nbsp;<b><span style=\"font-size:1.6em;\">CUSTOMERS REPORT" + "</span><br><hr>";
                String customersList = "";
                for (int cust_idx = 0; cust_idx < Bank.getNumberOfCustomers(); cust_idx++ ) {
                    Customer customer = Bank.getCustomer(cust_idx);
                    String CustomerInfo;

                    String customerFullName = "<br><b><span style=\"font-size:1.1em;\">Customer: </b>" + customer.getLastName() + ", " + customer.getFirstName();

                    for ( int acct_idx = 0; acct_idx < customer.getNumberOfAccounts(); acct_idx++ ) {
                        Account account = customer.getAccount(acct_idx);
                        String account_type = "";

                        if ( account instanceof SavingsAccount ) {
                          account_type = "Savings Account";
                        } else if ( account instanceof CheckingAccount ) {
                          account_type = "Checking Account";
                        } else {
                          account_type = "Unknown Account Type";
                        }

                        String typeAndBalance = "<br><b>" + account_type + "</b>: current balance is <i>" + account.getBalance() + "</i>";

                        CustomerInfo = customerFullName + typeAndBalance;
                        customersList += CustomerInfo + "<br>";
                      }
                    }
                String reportText = title + customersList;
                log.setText(reportText);
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setResizable(false);
        frame.setVisible(true);        
    }
    
    private void loadCustomers(String fileName){
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))){
            int numberOfCustomers = Integer.parseInt(reader.readLine());
            for(int i = 0; i < numberOfCustomers; i++){
                reader.readLine();
                String[] customersInfo = reader.readLine().split("\t");
                Bank.addCustomer(customersInfo[0], customersInfo[1]);
                
                int numberOfCustomersAccounts = Integer.parseInt(customersInfo[2]);
                
                Customer customer = Bank.getCustomer(i);
                
                for(int j = 0; j < numberOfCustomersAccounts; j++){
                
                String[] accountInfo = reader.readLine().split("\t");
                    String accountType = accountInfo[0];
                    double balance = Double.parseDouble(accountInfo[1]);
                    switch (accountType) {
                        case "S":
                            double interestRate = Double.parseDouble(accountInfo[2]);
                            customer.addAccount(new SavingsAccount(balance, interestRate));
                            break;
                        case "C":
                            double overdraftAmount = Double.parseDouble(accountInfo[2]);
                            customer.addAccount(new CheckingAccount(balance, overdraftAmount));
                            break;
                    }
                    }
            }
                
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SWINGDemo demo = new SWINGDemo();        
        demo.launchFrame();
    }
}
