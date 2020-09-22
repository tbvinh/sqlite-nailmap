/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lexor.sqlite.nailmap;

/**
 *
 * @author VinhE7440
 */
import java.sql.*;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.FileWriter;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.poi.ss.usermodel.Workbook;

public class NailmapDao {

    public void createDatabase(String dbName) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE COMPANY "
                    + "(ID INTEGER PRIMARY KEY  AUTOINCREMENT,"
                    + " company        VARCHAR(255), "
                    + " address        VARCHAR(255), "
                    + " city           VARCHAR(255), "
                    + " state          VARCHAR(10), "
                    + " zip            VARCHAR(10),"
                    + " command        text"
                    + ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public File chooseFile(javax.swing.JFrame jframe) {

        File selectedFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));//System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(jframe);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
        return selectedFile;

    }

    public void doImport(String db, String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            try {
                InputStream is = new FileInputStream(file);

                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
                        .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
                        .open(is);            // InputStream or File for XLSX file (required)

                Sheet sheet = workbook.getSheetAt(0);

                String sql = "insert into COMPANY(company, address, city, state, zip) values(?, ?, ?, ?, ?)";
                Class.forName("org.sqlite.JDBC");
                Connection con = DriverManager.getConnection("jdbc:sqlite:" + db);
                PreparedStatement ps = con.prepareStatement(sql);
                String company, address, city, state, zip;

                for (Row row : sheet) {

                    try {

                        company = row.getCell(0).getStringCellValue();
                        address = row.getCell(1).getStringCellValue();
                        city = row.getCell(2).getStringCellValue();
                        state = row.getCell(3).getStringCellValue();
                        zip = row.getCell(4).getStringCellValue();

                        //System.out.println( String.format("insert into COMPANY(company, address, city, state) values(%s, %s, %s, %s)", company, address, city, state) );
                        ps.setString(1, company);
                        ps.setString(2, address);
                        ps.setString(3, city);
                        ps.setString(4, state);
                        ps.setString(5, zip);

                        ps.executeUpdate();

                        System.out.print(", row: " + row.getRowNum());
//                        new Thread(new Runnable() {
//                            public void run() {
//                                SwingUtilities.invokeLater(new Runnable() {
//                                    public void run() {
//                                        lbl.setText("row: " + row.getRowNum());
//                                    }
//                                });
//                            }
//                        });

                    } catch (Exception ex) {
                        System.out.println("row: " + row.getRowNum() + ", " + ex.getMessage());
                    }
                }
                System.out.println("\nDONE\nDONE\nDONE\nDONE\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void doExclude(String nailmap1db, String nailmapexcludedb) {
        Connection conNailMap = null, conNailMapExclude = null;
        Statement statement = null, statementExclude = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conNailMap = DriverManager.getConnection("jdbc:sqlite:" + nailmap1db);
            conNailMapExclude = DriverManager.getConnection("jdbc:sqlite:" + nailmapexcludedb);

            // Create the statement to be used to get the results.
            statement = conNailMap.createStatement();
            statementExclude = conNailMapExclude.createStatement();

            resultSet = statementExclude.executeQuery("SELECT * FROM COMPANY");
            String company, address, city, state;

            ps = conNailMap.prepareStatement("Update COMPANY set command='DELETE' WHERE address=? and city=?");
            while (resultSet.next()) {
                //company = resultSet.getString("company");
                address = resultSet.getString("address");
                city = resultSet.getString("city");

                ps.setString(1, address);
                ps.setString(2, city);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.print(String.format(" %s %s", address, city));
                } else {
                    //System.out.print(String.format(" %s %s", address, city));
                    System.out.print(" ,");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (conNailMap != null) {
                    conNailMap.close();
                }
                if (conNailMapExclude != null) {
                    conNailMapExclude.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nDONE\nDONE\nDONE\nDONE\nDONE\n");
    }

    public void exportDb2CSV(String db, String csv) {
        Connection conNailMap = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            FileWriter csvFile = new FileWriter(csv);
            Class.forName("org.sqlite.JDBC");
            conNailMap = DriverManager.getConnection("jdbc:sqlite:" + db);
            statement = conNailMap.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM COMPANY where command is null");
            String line;
            String company, address, city, state, zip;
            while (resultSet.next()) {
                
                company = String.format("%s",resultSet.getString("company"));
                address = String.format("%s",resultSet.getString("address"));
                city = String.format("%s",resultSet.getString("city"));
                state = String.format("%s",resultSet.getString("state"));
                zip = String.format("%s",resultSet.getString("zip"));

                line = String.format("%s, %s, %s, %s, %s\n", 
                        company.replaceAll(",",""), 
                        address.replaceAll(",",""), 
                        city.replaceAll(",",""), 
                        state.replaceAll(",",""), 
                        zip.replaceAll(",",""));
                csvFile.write(line);
                
            }
            csvFile.close();
            resultSet.close();
            conNailMap.close();
            statement.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            
            
        }
    }
}
