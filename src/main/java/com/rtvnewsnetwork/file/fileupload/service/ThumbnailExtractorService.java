package com.rtvnewsnetwork.file.fileupload.service;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThumbnailExtractorService {

    /* ============================================================
       FACEBOOK THUMBNAIL EXTRACTOR
       ============================================================ */
    public String extractFacebookThumbnail(String iframeHtml) throws Exception {

        // 1. Extract FB video URL from iframe embed
        String videoUrl = extractFbVideoUrl(iframeHtml);
        if (videoUrl == null) {
            return null;
        }

        // 2. Fetch video page HTML
        String html = Jsoup.connect(videoUrl)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get()
                .html();

        Document doc = Jsoup.parse(html);

        // 3. Extract og:image
        String ogImage = doc.select("meta[property=og:image]").attr("content");

        if (ogImage == null || ogImage.isEmpty()) {
            return null;
        }

        // 4. HTML-decode (&amp; → &)
        // Jsoup handles HTML entity decoding safely
        String decoded = Jsoup.parse(ogImage).text();

        return decoded;
    }


    /* Extract the video URL from Facebook iframe */
    private String extractFbVideoUrl(String iframe) {
        try {
            Pattern p = Pattern.compile("href=([^&]+)");
            Matcher m = p.matcher(iframe);

            if (!m.find()) return null;

            String encodedUrl = m.group(1)
                    .replace("href=", "")
                    .replace("\"", "");

            return java.net.URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }


    /* ============================================================
       TWITTER / X THUMBNAIL EXTRACTOR
       ============================================================ */
    public String extractTwitterThumbnail(String tweetUrl) throws Exception {

        // Ensure it's a URL, not HTML
        if (!tweetUrl.startsWith("http")) {
            tweetUrl = extractTweetUrl(tweetUrl);
        }
        if (tweetUrl == null) return null;
        tweetUrl = tweetUrl.replace("x.com", "twitter.com");

        String oembedUrl = "https://publish.twitter.com/oembed?url=" + tweetUrl;

        String json = Jsoup.connect(oembedUrl)
                .ignoreContentType(true)
                .timeout(10_000)
                .userAgent("Mozilla/5.0")
                .get()
                .text();

        JSONObject obj = new JSONObject(json);
        String html = obj.getString("html");

        // Extract image from thumbnail inside oEmbed HTML
        Pattern p = Pattern.compile("https?://pbs\\.twimg\\.com[^\"']+");
        Matcher m = p.matcher(html);

        if (m.find()) return m.group();

        return null;
    }



    /* Extract tweet URL from embed iframe or blockquote */
    private String extractTweetUrl(String html) {
        try {
            Pattern p = Pattern.compile("https?://(www\\.)?(twitter|x)\\.com/[^\"\\s]+");
            Matcher m = p.matcher(html);

            if (m.find()) return m.group();

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
