package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class MyController implements Initializable {

    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Text path1;
    @FXML
    private Text path2;
    @FXML
    private TextField textfield;
    @FXML
    private Button submit;
    @FXML
    private Text waiting;
    private XWPFDocument xdoc1;
    private XWPFDocument xdoc2;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    public void button1click(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Word files (*.docx)", "*.docx"));
            File file = fileChooser.showOpenDialog(null);
            String path = file.getAbsolutePath();
            xdoc1 = new XWPFDocument(new FileInputStream(path));
            path1.setText(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void button2click(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Word files (*.docx)", "*.docx"));
            File file = fileChooser.showOpenDialog(null);
            String path = file.getAbsolutePath();
            xdoc2 = new XWPFDocument(new FileInputStream(path));
            path2.setText(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean containswords(XWPFParagraph par) {
        String regex = "[a-zA-Z0-9\u4E00-\\u9FA5]*";
        String result = "";
        Matcher matcher = Pattern.compile(regex).matcher(par.getText());
        while (matcher.find()) {
            result += matcher.group(0);
        }
        if (result.equals(""))
            return false;
        else
            return true;
    }

    public static double similarity(XWPFParagraph par1, XWPFParagraph par2) {
        String str1 = par1.getText();
        String str2 = par2.getText();
        int Kq = 5;
        int Kr = 1;
        int Ks = 1;
        char[] ss = str1.toCharArray();
        char[] st = str2.toCharArray();
        int q = 0;
        ArrayList<Character> list1 = new ArrayList<Character>();
        for (char i : ss) {
            list1.add(i);
        }

        ArrayList<Character> list2 = new ArrayList<Character>();
        for (char i : st) {
            if (list1.contains(i)) {
                list2.add(i);
                list1.remove(list1.indexOf(i));
            }
        }
        q = list2.size();
        int s = ss.length - q;
        int r = st.length - q;
        return ((double) Kq * q / (Kq * q + Kr * r + Ks * s));
    }

    public void start(ActionEvent event) {
        double sensitivity = Double.parseDouble(textfield.getText());
        List<XWPFParagraph> list1 = new ArrayList<XWPFParagraph>();
        for (XWPFParagraph par : xdoc1.getParagraphs()) {
            if(containswords(par)) list1.add(par);
        }
        List<XWPFParagraph> list2 = new ArrayList<XWPFParagraph>();
        for (XWPFParagraph par : xdoc2.getParagraphs()) {
            if(containswords(par)) list2.add(par);
        }
        
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("Txt files (*.txt)", "*.txt"));
        File output = fileChooser1.showSaveDialog(null);
        BufferedWriter out = null;   
        try {   
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false)));   
            for (XWPFParagraph par1 : list1) {
                for (XWPFParagraph par2 : list2) {
                    if (similarity(par1, par2) >= sensitivity) {
                        out.write("File1: " + par1.getText() + "\r\n");
                        out.write("File2: " + par2.getText() + "\r\n");
                        out.write("Similarity: "+ similarity(par1,par2) + "\r\n" 
                            + "---------------------------------------------------\r\n");
                    }
                }
            }
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
            if(out != null){
            out.close();   
                }
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        } 
        waiting.setText("Output path: " + output);


        
    }

}
