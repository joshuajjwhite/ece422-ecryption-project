package com.company;
//import org.apache.commons.io.FileUtils;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by joshua on 21/02/17.
 */
public class FileHandler {

    private String fileName;

    public FileHandler(String fileName) {
        setFileName(fileName);
    }

    //Credit to Steven
    //http://stackoverflow.com/questions/5820508/writing-an-array-to-a-file-in-java

    protected void writeToFile(String string) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFileName(), true));

            writer.write(string);
            writer.write("\n");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String[] readByLine() throws IOException {
        String file = readFromFile();
        if (file == null) {
            System.out.println("File still exist");
            return null;
        }
        String[] fileLines = file.split("\n");
        return fileLines;
    }

    protected String readFromFile() {
        String str = null;

        File file = new File(getFileName());

        if(!file.canRead()){
            System.out.println("File doesnt exist");
            return str;
        }

        try {
            str = FileUtils.readFileToString(new File(getFileName()), "utf-8");
        } catch (IOException e) {
            System.out.println("File not found");
        }
        return str;
    }

    protected void deleteFile() {
        File file = new File(getFileName());
        file.delete();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
