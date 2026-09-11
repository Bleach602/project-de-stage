package IFFPO_Web_Platform.controller;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SitemapController {

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String generateSitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Pages statiques
        addUrl(xml, "/", "daily", "1.0");
        addUrl(xml, "/#filiere", "weekly", "0.9");
        addUrl(xml, "/#equipe", "weekly", "0.8");
        addUrl(xml, "/#actu", "weekly", "0.7");
        addUrl(xml, "/#contact", "monthly", "0.7");
        addUrl(xml, "/login", "weekly", "0.6");
        addUrl(xml, "/register", "weekly", "0.6");

        xml.append("</urlset>");
        return xml.toString();
    }

    private void addUrl(StringBuilder xml, String path, String freq, String priority) {
        xml.append("  <url>\n");
        xml.append("    <loc>https://ifp-perle.com").append(path).append("</loc>\n");
        xml.append("    <changefreq>").append(freq).append("</changefreq>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("  </url>\n");
    }
}