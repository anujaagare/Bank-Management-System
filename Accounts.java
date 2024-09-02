import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
public class Accounts {
    private Connection conn;
    private Scanner scanner;
    public Accounts(Connection conn, Scanner scanner){
        this.conn = conn;
        this.scanner = scanner;
    }

    public long open_account(String email){
        if(!account_exist(email)){
            String open_account_query = "Insert into Accounts(account_number, full_name, email, balance, security_pin) Values(?,?,?,?,?)";
            scanner.nextLine();
            System.out.print("Enter full name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter initial Amount: ");
            double balance = scanner.nextDouble();
            System.out.print("Enter Security pin: ");
            String security_pin = scanner.nextLine();
            try{
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = conn.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5,security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected > 0){
                    return account_number;
                }
                else{
                    throw new RuntimeException("Account Creation failed");
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exist");
    }

    public static String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] salt = getSalt();
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b: hashedPassword){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
    private static byte[] getSalt() throws NoSuchAlgorithmException{
        SecureRandom sr = SecureRandom.getInstance("SHA1PRnG");
        byte salt[] = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
    public long getAccountNumber(String email){
        String query = "SELECT account_number from Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }
        catch(SQLException e){
                e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }

    private long generateAccountNumber(){
        try{
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number from Accounts ORDER BY account_number DESC LIMIT 1");
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1;
            }
            else{
                return 10000100;
            }
       } catch(SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean account_exist(String email){
        String query = "SELECT account_number from Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
