package tp_sig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Utils;


public class Requete {

	public static void main(String[] args) {
		Connection connection = Utils.getConnection();
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement("SELECT tags FROM relations WHERE tags->'name' = 'Ensimag';");
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
			    System.out.println("colonne 1 = " + res.getString(1) );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
