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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.*;

public class DBHandler {

	private static final String PROP_FILE_PATH = "/home/bharat/mytestworkspace/MlkStuckStatus/src/mlk/dbconnection.properties";
	
	Properties prop = null;

	public JdbcTemplate getJdbcTemplate() {
		if (prop == null) {
			this.loadProperties();
		}
		
		String url = prop.getProperty("jdbc.url");
		String user = prop.getProperty("jdbc.username");
		String password = prop.getProperty("jdbc.password");
		String driverclass = prop.getProperty("jdbc.driverClassName");

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		try {
			dataSource.setDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		
		return jdbcTemplate;

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
			
					}

		catch (IOException ex) {
		
		}

		finally {
			try {
				if (in != null) {
					in.close();
				}

			} catch (IOException ex) {
				

			}
		}
	}

}
