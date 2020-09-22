package com.lexor.sqlite.nailmap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author VinhE7440
 */
public class MainClass {
    public static void main(String args[]) {
        /*
        resule.db dbexclude.db, excel1.xlsx, excelExclude.xlsx
        */
        String db1="naimap.db";
        String dbexclude="naimap-exclude.db", excel1="Nail Salon Addresses - Cleaned June 11, 2020.xlsx", excelExclude="export-customer.xlsx";
        String csv = "export.csv";
        
        NailmapDao dao = new NailmapDao();
        
//        dao.createDatabase(db1);
//        dao.createDatabase(dbexclude);
//        
//        dao.doImport(db1, excel1);
//        dao.doImport(dbexclude, excelExclude);
//        
//        dao.doExclude(db1, dbexclude);
        
        dao.exportDb2CSV(db1, csv);
    }
}
