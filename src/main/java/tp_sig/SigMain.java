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

    public static void main(String[] args)  {
        /*Menu*/
        System.out.println("-------Menu---------");
        System.out.println("q8 : Question 8");
        System.out.println("q9 : Question 9");
        System.out.println("q10a : Question 10a");
        System.out.println("q10b : Question 10b");
        System.out.println("q10c : Question 10c");
        System.out.println("q11a : Question 11a");
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
                    try {
                        Request.question8(connection, tags, value);
                    } catch (SQLException e) {
                        System.out.println("Error SQL");
                    }
                    break;
                case "q9":
                    System.out.println("tape the name you want to search...");
                    String name = null;
                    name = readKeyBoard.nextLine();
                    try {
                        Request.question9(connection, name);
                    } catch (SQLException e) {
                        System.out.println("Error SQL");
                    }
                    break;
                case "q10a":
                    try {
                        Request.question10a(connection);
                    } catch (SQLException e) {
                        System.out.println("Error SQL");
                    }
                    break;
                case "q10b":
                    try {
                        Request.question10b(connection);
                    } catch (SQLException e) {
                        System.out.println("Error SQL");
                    }
                    break;
                case "q10c":
                    try {
                        Request.question10c(connection);
                    } catch (SQLException e) {
                        System.out.println("Error SQL");
                    }
                    break;
                case "q11a":
                        Request.question11a(connection);
                    break;
                case "help":
                    System.out.println("q8 : Question 8");
                    System.out.println("q9 : Question 9");
                    System.out.println("q10a : Question 10a");
                    System.out.println("q10b : Question 10b");
                    System.out.println("q10c : Question 10c");
                    System.out.println("q11a : Question 11a");
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
