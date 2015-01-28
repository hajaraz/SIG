package tp_sig;

import database.Utils;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by guolei on 28/01/15.
 */
public class SigMain {
    // connecter la base de donnee
    private static Connection connection = Utils.getConnection();

    private static String key = null;
    static Scanner readKeyBoard = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        /*Menu*/
        System.out.println("-------Menu---------");
        System.out.println("q8 : Question 8");
        System.out.println("q9 : Question 9");
        System.out.println("q10 : Question 10a");
        System.out.println("help : display commands");
        System.out.println("--------------------");

        while(true){
            System.out.println("tape your choice...");
            key = readKeyBoard.nextLine();

            switch (key) {
                case "q8":
                    System.out.println("tape the tags you want to search...");
                    String tags = null;
                    tags = readKeyBoard.nextLine();
                    System.out.println("tape the value you want to search...");
                    String value = null;
                    value = readKeyBoard.nextLine();
                    Request.question8(connection, tags, value);
                    break;
                case "q9":
                    System.out.println("tape the name you want to search...");
                    String name = null;
                    name = readKeyBoard.nextLine();
                    Request.question9(connection, name);
                    break;
                case "q10":
                    Request.question10(connection);
                    break;
                case "help":
                    System.out.println("q8 : Question 8");
                    System.out.println("q9 : Question 9");
                    System.out.println("q10 : Question 10a");
                    break;
                case "exit":
                    System.out.println("exit the program");
                    System.exit(0);
                    break;
                default:
            }
        }
    }
}
