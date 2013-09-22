package com.movie.rating;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
  Created with IntelliJ IDEA.
  User: cchen
  Date: 9/21/13
  Time: 5:59 PM
  To change this template use File | Settings | File Templates.
 */
public class ReadCSV_general {

    private String fileName;
    private ArrayList<String> content;


    public ReadCSV_general (String fileName){
        this.setFileName(fileName);
    }

    public ArrayList<String> loadCSV() {

        BufferedReader br = null;
        String line;
        ArrayList<String> allLines = new ArrayList<String>();

        try {

            br = new BufferedReader(new FileReader(this.fileName));

            while ((line = br.readLine()) != null) allLines.add(line);

            br.close();

            System.out.println("File Loaded");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return(allLines);
    }

//    public String getFileName() {
//        return fileName;
//    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setContent(this.loadCSV());
    }


    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }
}
