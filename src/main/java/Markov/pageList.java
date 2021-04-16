package Markov;

import java.util.ArrayList;


public class pageList {
    public static ArrayList<Page> pages = new ArrayList<>();

    public static String removeChars(String title) {
        title = title.replace(".", " ");
        title = title.replace("(", " ");
        title = title.replace("[", " ");
        title = title.replace(")", " ");
        title = title.replace("]", " ");
        title = title.replace(",", " ");
        title = title.replace("/", " ");
//		title = title.replace("!", " ");
        //	title = title.replace("?", " ");
        title = title.replace("+", " ");
        title = title.replace("\"", " ");
        title = title.replace("\'", " ");
        //	title = title.replace("-", " ");
        title = title.replace("_", " ");
        return title;
    }

    public static void printList() {
        for (int i = 0; i < pages.size(); i++) {
            System.out.println(pages.get(i).toString());
        }
    }

    public boolean contains(String url) {
        for (Page haruhi : pages) {
            if (haruhi.URL.equals(url)) return true;
        }
        return false;
    }

}

