/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway;
import java.sql.Connection;
import railway.db.DBConnection;
import railway.gui.LoginFrame;
/**
 *
 * @author tharu
 */
public class Main {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("✅ Connection successful!");
            new LoginFrame();
        } else {
            System.out.println("❌ Connection failed. Exiting...");
        }
    }
}
