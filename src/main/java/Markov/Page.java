package Markov;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static Markov.frontPage.*;

public class Page {
    String title;
    int hits;
    int views;
    boolean atFront = false;
    int allocatedThreads = 0;
    ArrayList<Thread> threads = new ArrayList<>();
    String writer;
    String URL;
    String shortURL;
    public Semaphore semaphore = new Semaphore(1);

    public Page(String title, String URL) {
        this.title = title;
        this.URL = URL;
        this.shortURL = URL;
    }

    public Page() {

    }

    public void increment() {
        this.hits++;
    }

    public void updateView(int views) {
        try {
        //    System.out.println(semaphore.availablePermits());
            semaphore.acquire();
        //    System.out.println(semaphore.availablePermits());
            this.views = views;
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String a = title + " by " + writer + " with " + hits + " at " + URL;
        return a;
    }

    public boolean isValid(int threshold) {
        try {
            semaphore.acquire();
        if (this.views < threshold && this.atFront) {
                if (useKeyword_N) {
                    if (key_nicks.contains(writer)) {
                        semaphore.release();
                        return true;
                    } else {
                        semaphore.release();
                        return false;
                    }
                } else if (useKeyword_T) {
                    for (String kTitle : key_titles) {
                        if (title.contains(kTitle)) {
                            semaphore.release();
                            return true;
                        }
                    }
                    semaphore.release();
                    return false;
                } else {
                    semaphore.release();
                    return true;
                }
            } else {
                semaphore.release();
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
