package in.andonsystem.v2.restcontroller;

import in.andonsystem.util.MiscUtil;
import in.andonsystem.v2.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Created by mdzahidraza on 20/06/17.
 */
@Controller
@RequestMapping(ApiUrls.APK_DOWNLOAD_URL)
public class DownloadController {
    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @GetMapping("/apk")
    public ResponseEntity<Resource> downloadApk(){
        logger.debug("downloadApk");
        String filename = MiscUtil.getAndonHome() + File.separator + "apk" + File.separator + "AndonSystem.apk";
        Resource resource = null;
        try {
            Path file = new File(filename).toPath();
            resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.info("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.info("Could not read file: " + filename, e);
            return null;
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.android.package-archive")
                .body(resource);
    }

    @GetMapping
    public String downloadPage() {
        return "download";
    }
}
