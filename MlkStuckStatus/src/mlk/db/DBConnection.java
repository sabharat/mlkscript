package mlk.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mysql.jdbc.*;

public class DBConnection {

	private static final String PROP_FILE_PATH = "/home/bharat/mytestworkspace/MlkStuckStatus/src/mlk/dbconnection.properties";
	Logger lgr = Logger.getLogger(DBConnection.class.getName()); // trigger java logger
	Connection con = null;
	Properties prop = null;

	public Connection getConnection() {
		if (prop == null) {
			this.loadProperties();
		}

		String url = prop.getProperty("jdbc.url");
		String user = prop.getProperty("jdbc.username");
		String password = prop.getProperty("jdbc.password");
		String driverclass = prop.getProperty("jdbc.driverClassName");

		try {

			Class.forName(driverclass).newInstance();
			con = DriverManager.getConnection(url, user, password);
			System.out.println("Database connection established");
		} catch (Exception ex) {
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return con;

	}

	public void closeConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void loadProperties() {
		prop = new Properties();
		FileInputStream in = null;

		try {
			// try to load properties file
			in = new FileInputStream(PROP_FILE_PATH);
			// load properties from variable "in" above :)
			prop.load(in);

		} catch (FileNotFoundException ex) {
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
					}

		catch (IOException ex) {
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}

		finally {
			try {
				if (in != null) {
					in.close();
				}

			} catch (IOException ex) {
				lgr.log(Level.SEVERE, ex.getMessage(), ex);

			}
		}
	}

}
