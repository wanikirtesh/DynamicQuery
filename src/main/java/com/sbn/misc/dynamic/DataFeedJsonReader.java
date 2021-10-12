package com.sbn.misc.dynamic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by idnkiw on 3/1/2017.
 */
public class DataFeedJsonReader {
    public static void main(String[] args) {

        String line;
        try (
                InputStream fis = new FileInputStream("json.txt");
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                readJson(line);
            }
        }catch (Exception e){

        }


    }

    private static void readJson(String line) {
        System.out.println(line);



    }
}
