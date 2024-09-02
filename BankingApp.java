import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.sql.SQLException;
import static java.lang.Class.forName;

public class BankingApp {
    private static final String url ="jdbc:mysql://localhost:3306/bank_management";
    private static final String username = "root";
    private static final String password = "123456";
    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            Accounts accounts = new Accounts(conn, scanner);
            AccountManager accountmanager = new AccountManager(conn, scanner);
            User user = new User(conn, scanner);

            String email;
            long account_number;

            while (true) {
                System.out.println("*** Welcome To Banking System ***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println("User Logged In!");
                            if (!accounts.account_exist(email)) {
                                System.out.println("1.Open new bank account");
                                System.out.println("2.Exit");
                                if (scanner.nextInt() == 1) {
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account created successfully.");
                                    System.out.println("Your Account Number is: " + account_number);
                                } else {
                                    break;
                                }
                            }
                            account_number = accounts.getAccountNumber(email);
                            int choice1 = 0;
                            while (choice1 != 5) {
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5.Log Out");
                                System.out.println("Enter your choice: ");
                                choice1 = scanner.nextInt();
                                switch (choice1) {
                                    case 1:
                                        accountmanager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountmanager.creditMoney(account_number);
                                        break;
                                    case 3:
                                        accountmanager.transfer_money(account_number);
                                        break;
                                    case 4:
                                        accountmanager.getBalance(account_number);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter valid choice!");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Enter valid email or password");
                        }
                    case 3:
                        System.out.println("Thankyou for using Banking System!!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
