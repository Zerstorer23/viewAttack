package Markov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

import static Markov.cafePage.stay_cafe;
import static Markov.pageList.pages;
import static Markov.ViewAttack.*;

public class frontPage {
    public static int maxcores = 35;
    public static int cores = 1;
    public static int maxPer = 10;
    private static String url = "?";
    public static int c = 1;
    public static int prev_c = 0;
    public static int interval = 5;
    public static String gallID = "haruhiism";
    public static int threshold = 1000;
    public static boolean maxMode = false;
    public static ArrayList<String> sub_active = new ArrayList<>();
    public static ArrayList<String> main_active = new ArrayList<>();

    public static int updateInterval = 10;
    public static int thbest = 3;
    public static boolean useKeyword_T = false;
    public static boolean useKeyword_N = false;

    public static void main(String[] args) throws IOException, AWTException {
        System.out.println("Start programme");
        read("front.txt");
        if(useKeyword_T||useKeyword_N)readKey("keyword.txt");
        if (maxPer > 10) System.out.println("[WARNING] 10 is appropriate for maxPer");
        if (maxcores > 100) System.out.println("[WARNING] 100 is appropriate for maxcores");
        Thread go = new Thread(() -> countdown());
        go.start();
        if(cafemode){
            stay_cafe();
        }else{
        stay();}
    }

    private static void stay() {
        while (true) {
            scroll();
            cores = calculateCores();
            for (int i = 0; i < pages.size(); i++) {
                if (pages.get(i).isValid(threshold) && pages.get(i).allocatedThreads == 0) {
                    System.out.println(pages.get(i).URL.substring(24, pages.get(i).URL.length() - 1) + "   / Views: " + pages.get(i).views);
                    pages.get(i).allocatedThreads = cores;
                    System.out.print("[Page " + i + "] is online :");
                    for (int c = 0; c < cores; c++) {
                        int finalI = i;
                        Thread go = new Thread(() -> attack(finalI));
                        go.start();
                        pages.get(i).threads.add(go);
                        System.out.print(c + "/ ");
                    }
                    pages.get(i).allocatedThreads = cores;
                    System.out.println(" ");
                }

            }
            try {
                Thread.sleep(updateInterval * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static int calculateCores() {
        int slots = 1;
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).isValid(threshold) && pages.get(i).allocatedThreads == 0) {
                slots++;
            }
        }
     //    System.out.println("SubActive size = "+sub_active.size());
      //   System.out.println("Cores = "+" free core : "+getFreeCores()+" / slots: "+slots);
        int cores = getFreeCores() / slots;
        if (cores > maxPer) cores = maxPer;
        return cores;
    }

    public static int getFreeCores() {
        int slots = maxcores;
        for (Page yuki : pages) {
            if (yuki.isValid(threshold)) {
                slots = slots - yuki.allocatedThreads;
            }
        }
        return slots;
    }

    private static boolean canLoadNew() {
        for (Page haruhi : pages) {
            if (haruhi.isValid(threshold)) {
                return false;
            }
        }
        return true;
    }

    private static void scroll() {
        sub_active = new ArrayList<>();
        String tempPage;
        if (ViewAttack.major) {
            tempPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&list_num=30&sort_type=N&search_head=&page=1";
        } else {
            tempPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID + "&list_num=30&sort_type=N&search_head=&page=1";
        }
        try {
            Document doc = Jsoup.connect(tempPage).get();
            Elements table = doc.select("tbody").select("tr[class=ub-content]");
            System.out.println("[Reloader] " + doc.title());
            ArrayList<Integer> viewList = new ArrayList<>();
            for (int i = 0; i < table.size(); i++) {
                Element subject = table.get(i).select("td[class=gall_subject]").first();
                String indexS;
                if (subject != null) {
                    indexS = subject.text();
                    if (indexS.equals("??????")||indexS.equals("??????")) {
                        System.out.println("[SKIP ]SUBJECT: " + indexS);
                        continue;
                    }
                } else {
                    indexS = table.get(i).select("td[class=gall_num]").first().text();
                    if (indexS.equals("??????") || indexS.equals("??????")) continue;
                }
                String title = table.get(i).select("td[class=gall_tit ub-word]").select("a").first().text();
                String URL = table.get(i).select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                String view = table.get(i).select("td[class=gall_count]").first().text();
                String writer = table.get(i).select("td[class=gall_writer ub-writer]").first().attr("data-nick");
                int viewCount = Integer.parseInt(view);
                URL = "http://gall.dcinside.com" + URL;
                if (!main_active.contains(URL)) {
                    Page haruhi = new Page(title, URL);
                    haruhi.views = Integer.parseInt(view);
                    haruhi.atFront = true;
                    haruhi.writer = writer;
                    System.out.println(i + ". " + haruhi.URL);
                    pages.add(haruhi);
                    main_active.add(URL);
                }
                sub_active.add(URL); //everything
                if (!viewList.contains(viewCount)) viewList.add(viewCount);

            }
            System.out.println("Active page size : " + sub_active.size());
            if (maxMode) {
                Collections.sort(viewList, Collections.reverseOrder());
                int best = thbest;
                while (best >= viewList.size()) best--;
                threshold = viewList.get(best);
                System.out.println("New threshold : " + threshold);
            }
            for (Page haruhi : pages) {
                if (!sub_active.contains(haruhi.URL)) {
                    haruhi.atFront = false;
                    System.out.println(haruhi.URL.substring(24, haruhi.URL.length() - 1) + " is not at front.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //   pList.buildRoot();
    }

    private static void stopAll() {
        for (Page haruhi : pages) {
            haruhi.atFront = false;
        }
    }
    private static void attack(int i) {
        boolean stop = false;
        if (!pages.get(i).isValid(threshold)) {
            stop = true;
        }
        while (!stop) {
            try {
                Document doc = Jsoup.connect(pages.get(i).URL).get();
                String vvv = doc.select("div[class=gallview_head clear ub-content]").first().select("span[class=gall_count]").first().text();
                String token[] = vvv.split(" ");
                vvv = token[1];
                pages.get(i).updateView(Integer.parseInt(vvv));
                c++;
                if (!pages.get(i).isValid(threshold)) {
                    stop = true;
                    System.out.println("[Core " + i + "] is terminated. View = "+vvv);
                }
            } catch (org.jsoup.HttpStatusException e) {
                System.out.println(e.getUrl() + "??? ?????????.");
                stop = true;
            }catch (SocketTimeoutException e){
                //  System.out.println("Timeout Exception");
            } catch (SocketException e){
                //   System.out.println("Connection Reset");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        pages.get(i).allocatedThreads = 0;
        pages.get(i).threads = new ArrayList<>();
        //   pList.buildRoot();
    }

    private static void read(String fileName) {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = null;
            sc = new BufferedReader(new FileReader(fileName));
            String temp;
            while ((temp = sc.readLine()) != null) {
                String[] entry = temp.split(",");
                String head = entry[0];
                String data = entry[1];
                if (head.equals("maxcore")) {
                    maxcores = Integer.parseInt(data);
                    System.out.println("?????? ?????? ???: "+maxcores);
                } else if (head.equals("gallID")) {
                    gallID = data;
                    System.out.println("?????? ?????????: "+gallID);
                } else if (head.equals("major")) {
                    ViewAttack.major = Boolean.parseBoolean(data);
                    System.out.println("??????????: "+ViewAttack.major);
                } else if (head.equals("count")) {
                    interval = Integer.parseInt(data);
                    System.out.println("?????? ?????? ??????: "+interval+" ???");
                } else if (head.equals("threshold")) {
                    threshold = Integer.parseInt(data);
                    System.out.println("?????? ?????????: "+threshold);
                    if (threshold < 0) {
                        maxMode = true;
                        System.out.println("?????? ???????????? ????????????");
                    }
                } else if (head.equals("maxPer")) {
                    maxPer = Integer.parseInt(data);
                    System.out.println("????????? ?????? ?????? ?????????: "+maxPer);
                } else if (head.equals("updateInterval")) {
                    updateInterval = Integer.parseInt(data);
                    System.out.println("1 ????????? ????????????: "+maxPer+" ???");
                } else if (head.equals("thbest")) {
                    thbest = Integer.parseInt(data);
                    System.out.println("?????? ????????? ????????? "+thbest+"?????? ???????????? ????????????");
                }else if (head.equals("cafemode")) {
                    cafemode = Boolean.parseBoolean(data);
                    if(cafemode)System.out.println("?????? ??????");
                }else if (head.equals("useKeyword")) {
                    if(data.contains("N")){
                        useKeyword_N = true;
                       System.out.println("????????? ??????");
                    }
                    if(data.contains("T")){
                        useKeyword_T=true;
                        System.out.println("???????????? ??????");
                    }
                }else{
                    System.out.println("[????????????]"+ head + " : " + data);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> key_nicks = new ArrayList<>();
    public static ArrayList<String> key_titles = new ArrayList<>();
    private static void readKey(String fileName) {
        try {
            BufferedReader sc = null;
            sc = new BufferedReader(new FileReader(fileName));
            String temp;
            boolean nickmode = false;
            while ((temp = sc.readLine()) != null) {
                if(temp.equals("//TITLE")){
                    nickmode = false;
                    System.out.println("===?????? ??????===");
                }else if(temp.equals("//NICK")){
                    nickmode = true;
                    System.out.println("===?????? ??????===");
                }
                if(nickmode){
                    key_nicks.add(temp);
                }else{
                    key_titles.add(temp);
                }
                System.out.println(temp);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void countdown() {
        while (true) {
            try {
                Thread.sleep(interval * 1000);
                System.out.println("Count: " + c + " (+" + (c - prev_c) + ") Free cores: " + getFreeCores());
                prev_c = c;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
