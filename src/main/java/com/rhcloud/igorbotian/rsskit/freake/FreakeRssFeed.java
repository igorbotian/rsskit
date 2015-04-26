package com.rhcloud.igorbotian.rsskit.freake;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FreakeRssFeed {

    private static final int NUMBER_OF_TOP_RELEASES = 7; // 7 top releases are shown on the main page

    private final Freake freake;
    private final Feed dayFeed;
    private final Feed topReleasesFeed;

    public FreakeRssFeed(Freake freake) throws IOException {
        this.freake = freake;

        dayFeed = new Feed(downloadFeed());
        topReleasesFeed = new Feed(downloadFeed());
        composeTopReleasesOfTheDay();

        TaskScheduler scheduler = new TaskScheduler();
        scheduler.executeEveryNMinutes(1, new Runnable() {

            @Override
            public void run() {
                try {
                    update();
                } catch (IOException e) {
                    e.printStackTrace(); // TODO
                }
            }
        });

        scheduler.executeEveryDayAt(22, new Runnable() {

            @Override
            public void run() {
                try {
                    composeTopReleasesOfTheDay();
                } catch (IOException e) {
                    e.printStackTrace(); // TODO
                }
            }
        });
    }

    private SyndFeed downloadFeed() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) freake.rssURL().openConnection();

        try (InputStream is = conn.getInputStream()) {
            try {
                return new SyndFeedInput().build(new XmlReader(is));
            } catch (FeedException e) {
                throw new IOException("Failed to retrieve freake.ru RSS feed", e);
            }
        }
    }

    public SyndFeed get() {
        return topReleasesFeed.get();
    }

    private void update() throws IOException {
        SyndFeed snapshot = downloadFeed();
        dayFeed.mergeWith(snapshot.getEntries());
    }

    //-------------------------------------------------------------------------

    private void composeTopReleasesOfTheDay() throws IOException {
        topReleasesFeed.setEntries(identifyTopReleasesOfTheDay());
        dayFeed.setEntries(Collections.<SyndEntry>emptyList());
    }

    private List<SyndEntry> identifyTopReleasesOfTheDay() throws IOException {
        String mainPage = downloadMainPage();
        TreeMap<Integer, SyndEntry> positionsOnThePage = findPositionsOnThePage(dayFeed.get().getEntries(), mainPage);
        return getTopReleases(positionsOnThePage);
    }

    private TreeMap<Integer, SyndEntry> findPositionsOnThePage(List<SyndEntry> entries, String page) {
        assert entries != null;
        assert page != null;

        TreeMap<Integer, SyndEntry> positions = new TreeMap<>(new Comparator<Integer>() {

            @Override
            public int compare(Integer first, Integer second) {
                return Integer.compare(first, second);
            }
        });

        for(SyndEntry entry : entries) {
            int pos = page.indexOf(entry.getTitle());

            if(pos >= 0) {
                positions.put(pos, entry);
            }
        }

        return positions;
    }

    private List<SyndEntry> getTopReleases(TreeMap<Integer, SyndEntry> entries) {
        assert entries != null;

        List<SyndEntry> top = new ArrayList<>(NUMBER_OF_TOP_RELEASES);
        int counter = 0;

        for(Map.Entry<Integer, SyndEntry> entry : entries.entrySet()) {
            top.add(entry.getValue());
            counter++;

            if(counter > NUMBER_OF_TOP_RELEASES) {
                break;
            }
        }

        return top;
    }

    private String downloadMainPage() throws IOException {
        return IOUtils.toString(freake.url());
    }

    //-------------------------------------------------------------------------

    private static class Feed {

        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final SyndFeed feed;

        public Feed(SyndFeed feed) {
            this.feed = Objects.requireNonNull(feed);
        }

        public void mergeWith(List<SyndEntry> entries) {
            assert entries != null;

            try {
                lock.writeLock().lock();
                List<SyndEntry> newEntries = new ArrayList<>(feed.getEntries());

                for (SyndEntry entry : entries) {
                    if (!hasEntry(newEntries, entry)) {
                        newEntries.add(entry);
                    }
                }

                feed.setEntries(newEntries);
            } finally {
                lock.writeLock().unlock();
            }
        }

        private boolean hasEntry(List<SyndEntry> entries, SyndEntry entry) {
            assert entries != null;
            assert entry != null;

            for (SyndEntry e : entries) {
                if(e.getTitle().equals(entry.getTitle())) {
                    return true;
                }
            }

            return false;
        }

        public void setEntries(List<SyndEntry> entries) {
            assert entries != null;

            try {
                lock.writeLock().lock();
                feed.setEntries(entries);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public SyndFeed get() {
            try {
                lock.readLock().lock();
                return this.feed;
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
