import java.io.File; 
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument; 
import org.apache.pdfbox.text.PDFTextStripper; 
// import org.apache.pdfbox.text.PDFTextStripperByArea;
// import org.checkerframework.checker.units.qual.m;



// "/Users/ishaansareen/Downloads/PDFServicesSDK-JavaSamples/adobe-dc-pdf-services-sdk-java-samples/src/main/resources/Result_0072T030793_Part1.pdf"
// /Users/ishaansareen/Desktop/Results0022T452889 (dragged) 2.pdf
// /Users/ishaansareen/Desktop/Results0022T452889 (dragged).pdf
public class processor {
    public static void main(String[] args) throws IOException{
        // loads in data
        File file = new File("/Users/ishaansareen/Desktop/Result_0072T030793_Part1 (1).pdf");
        PDDocument document = Loader.loadPDF(file);


        // Data
        JFrame jFrame = new JFrame();

        ArrayList<String> results = new ArrayList<String>(); 
        ArrayList<Double> max = new ArrayList<Double>(); 
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> units = new ArrayList<String>();
        ArrayList<Double> quantity = new ArrayList<Double>();
        ArrayList<Double> min = new ArrayList<Double>();
        ArrayList<String> status = new ArrayList<String>();
        ArrayList<String> comments = new ArrayList<String>();

        Object[][] tableContents = new Object[44][6]; // 44 rows, 6 columns: name, amount, min, max, status, comments //[r][c]

        
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        String[] lines = text.split("\r?\n|\r");
        

        // generate results list
        System.out.println("\n\n\n");
        for (int i = 14; i < lines.length; i++){
            if (    (Character.isDigit(lines[i].charAt(lines[i].length()-1)) &&  !lines[i].substring(0,4).equals("Note") )      || lines[i].charAt(4) == '%'){
                results.add(lines[i]);
            }
        }
    
        
        // generate max
        for (int i = 0; i < results.size(); i++) {
            String s = results.get(i);
            String[] arr = s.split("");
            for (int j = 0; j < arr.length; j++) {
                if (   (arr[j].equals(" ") && arr[j+1].equals("-"))   ||   (arr[j].equals(" ") && arr[j+1].equals("<"))       ){
                    max.add(Double.parseDouble(s.substring(j+2)));
                }
                else if (j+5 < arr.length && (arr[j].equals("U") && arr[j+1].equals("p") && arr[j+2].equals(" ") && arr[j+3].equals("t") && arr[j+4].equals("o"))) {
                    max.add(Double.parseDouble(s.substring(j+6)));
                } 

                if (j + 2 < arr.length){
                    if ( arr[j].equals("a")  && arr[j+1].equals("n")   && arr[j+2].equals("d")      ) {
                        max.add(100.0);
                    }
                }
            }
        }
        
        
        // generate name + quantity
        int endOfName = 0;
        int indexOfSpace = 0;
        int indexOfSpace2 = 0;
        int percentIndex = 0;
        for (int i = 0; i < results.size(); i++) {
            // generate name
            String s = results.get(i);
            String[] arr = s.split("");
            for (int j = 0; j < arr.length; j++) {
                if (Character.isDigit(arr[j].charAt(0))){
                    name.add(results.get(i).substring(0,j));
                    endOfName = j-1;
                    break;
                }
            }
            

            // finds space after name
            for (int k = endOfName+1; k < arr.length; k++) {
                if (arr[k].equals(" ")){
                    indexOfSpace2 = k;
                    break;
                }
            }

             
            // quantity without %
            for (int j = 0; j < arr.length; j++) {
                if (arr[j].equals("%")){
                    endOfName = 0;
                    indexOfSpace2 = 0;
                    break;
                }
                if (j == (arr.length - 1)){
                    if (indexOfSpace2 < endOfName){
                        break;
                    }
                    // System.out.println(/* endOfName + " " + indexOfSpace2 + " " + results.get(i).substring(0,endOfName) */ results.get(i).substring(endOfName+1, indexOfSpace2));
                    quantity.add(Double.parseDouble(s.substring(endOfName+1, indexOfSpace2)));
                }
            }


            // quantity with %
            for (int j = endOfName+1; j < arr.length; j++) {
                if (arr[j].equals("%") && j != 4){
                    for (int k = j+2; k < arr.length; k++) {
                        if (arr[k].equals(" ")){
                            indexOfSpace = k;
                            break;
                        }
                    }
                    if (i < quantity.size()){
                        quantity.add(i, Double.parseDouble(s.substring(j+2,indexOfSpace)));
                    }
                    else {
                        quantity.add(Double.parseDouble(s.substring(j+2,indexOfSpace)));
                    }
                }
            } 
        }


        // generate units
        int spaceIndex1 = 0;
        int spaceIndex2 = 0;
        for (int i = 0; i < results.size(); i++) {
            String s = results.get(i);
            String[] arr = s.split("");
            for (int j = 0; j < arr.length; j++) {
                if (arr[j].equals(" ")){
                    spaceIndex1 = j;
                    for (int k = j+1; k < arr.length; k++) {
                        if (arr[k].equals(" ")){
                            spaceIndex2 = k;
                            break;
                        }
                    }
                }
                if (arr[j].equals("/") || (arr[j].equals("f") && arr[j+1].equals("L"))  ||   (arr[j].equals("p") && arr[j+1].equals("g"))  || (arr[j].equals("%"))){
                    units.add(results.get(i).substring(spaceIndex1, spaceIndex2));
                    break;
                }
            }
        }


        // for min take quantity after units before max
        int spaceIndex3 = 0;
        for (int i = 0; i < results.size(); i++) {
            String s = results.get(i);
            String[] arr = s.split("");
            for (int j = 0; j < arr.length; j++) {
                if (   (arr[j].equals(" ") && arr[j+1].equals("-"))       ){
                    min.add(Double.parseDouble(s.substring(spaceIndex3+1, j)));
                }
                if ( (arr[j].equals(" ") && arr[j+1].equals("<"))) {
                    min.add(0.0);
                }
                if (j + 2 < arr.length){
                    if ( arr[j].equals("a")  && arr[j+1].equals("n")   && arr[j+2].equals("d")      ) {
                        min.add(Double.parseDouble(s.substring(j-3,j-1)));
                    }
                }
                if ( arr[j].equals("U") && arr[j+1].equals("p") ) {
                    min.add(0.0);
                } 
                if (arr[j].equals(" ")) {
                    spaceIndex3 = j;
                }
            }
        }

        /*
        System.out.println("Lines: " + lines.length + " Min: " + min.size() + " Max: " + max.size() + " Result: " + results.size() + " Quantity: " + quantity.size() + " Units: " + units.size());
        for (int i = 0; i < max.size(); i++) {
            System.out.println(max.get(i));
        }
        */
        int p = 0;
        while ( p < quantity.size()){
            if(quantity.get(p) >= min.get(p) && quantity.get(p) <= max.get(p)){
                status.add("Normal");
            }
            else if (quantity.get(p) > max.get(p)) {
                status.add("High");
            }
            else if (quantity.get(p) < min.get(p)) {
                status.add("Low");
            } 
            p++;
        }

        for (int i = 0; i < 44; i++) {
            comments.add("No comment");
        }

        for (int i = 0; i < tableContents.length; i++) {
            tableContents[i][0] = name.get(i); // name
            tableContents[i][1] = quantity.get(i); // amount
            tableContents[i][2] = min.get(i); // min
            tableContents[i][3] = max.get(i); // max
            tableContents[i][4] = status.get(i); // status 
            tableContents[i][5] = comments.get(i); // status 
        }

        String[] tableColumn = {"NAME", "AMOUNT", "MIN", "MAX", "RESULT", "COMMENTS"};
        JTable jTable = new JTable(tableContents, tableColumn);
        jTable.setBounds(30, 40, 230, 280);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jFrame.add(jScrollPane);
        jFrame.setSize(350, 300);
        jFrame.setVisible(true);

        
        document.close();

        /*
        gui g = new gui();
        g.createWindow();
        */
    } 
}
