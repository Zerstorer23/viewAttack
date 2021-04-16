package Markov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.awt.*;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static Markov.ViewAttack.cafemode;
import static Markov.frontPage.*;
import static Markov.pageList.pages;

public class cafePage {
   // public static WebDriver driver;
    public static void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
   //     ChromeOptions options = new ChromeOptions();
     //   options.setExperimentalOption("mobileEmulation", mobileEmulation);
     //   driver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
   //     driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정
    }
    public static void stay_cafe() {
        initBrowser();
        while (true) {
            scroll();
            cores = calculateCores();
            for (int i = 0; i < pages.size(); i++) {
                if (pages.get(i).isValid(threshold) && pages.get(i).allocatedThreads == 0) {
                    System.out.println(pages.get(i).shortURL + "   / Views: " + pages.get(i).views);
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

    private static void scroll() {
        String url = "https://m.cafe.naver.com/azurlanekorea.cafe";

    //    driver.get(url);
       String HTML = " ";//driver.getPageSource();
        sub_active = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(HTML);
            //.userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36").get();;
      //      System.out.println(doc.html());
            Elements table = doc.select("div[id=articleListArea]").select("li[class=board_box]");
            System.out.println("[Reloader] " + doc.title());
            ArrayList<Integer> viewList = new ArrayList<>();
            for (int i = 0; i < table.size(); i++) {
                String title = table.get(i).select("strong[class=tit]").first().text();
                String URL = table.get(i).select("a").first().attr("data-article-id");
                String view = table.get(i).select("span[class=no]").first().text();
                int viewCount = Integer.parseInt(view);
                URL = "https://m.cafe.naver.com/ArticleRead.nhn?clubid=29280484&articleid=" + URL+"&page=1&boardtype=L";
                if (!main_active.contains(URL)) {
                    Page haruhi = new Page(title, URL);
                    haruhi.views = Integer.parseInt(view);
                    haruhi.atFront = true;
                    haruhi.shortURL=URL;
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
                    System.out.println(haruhi.shortURL+ " is not at front.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //   pList.buildRoot();
    }

    private static void attack(int i) {
        boolean stop = false;
        if (!pages.get(i).isValid(threshold)) {
            stop = true;
        }
        while (!stop) {
            try {
                Document doc = Jsoup.connect(pages.get(i).URL)
                        .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
                        .get();
           //     System.out.println(doc.select("div[class=post_title]").first()
           //             .html());
//               System.out.println(doc.select("div[class=post_title]").first()
//                       .select("div[class=info]").first()
//                       .html());

                String vvv = doc.select("div[class=post_title]").first()
                        .select("div[class=info]")
                        .get(1)
                        .select("span[class=no font_l]")
                        .select("em")
                       .text();
                pages.get(i).views = Integer.parseInt(vvv);
                c++;
                if (!pages.get(i).isValid(threshold)) {
                    stop = true;
                    System.out.println("[Core " + i + "] is terminated.");
                }
            } catch (org.jsoup.HttpStatusException e) {
                System.out.println(e.getUrl() + "는 삭제됨.");
                stop = true;
            }catch (SocketTimeoutException e){
              //  System.out.println("Timeout Exception");
            } catch (SocketException e){
             //   System.out.println("Connection Reset");
            }catch (NullPointerException e){
                System.out.println("CAFE ONLY");
                pages.get(i).atFront = false;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        pages.get(i).allocatedThreads = 0;
        pages.get(i).threads = new ArrayList<>();
        //   pList.buildRoot();
    }

}
