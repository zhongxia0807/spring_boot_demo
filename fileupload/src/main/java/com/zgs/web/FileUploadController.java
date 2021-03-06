package com.zgs.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zgs
 */
@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private final static Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final static String FILE_BASE_PATH = "E:\\upload\\springBoot";

    @GetMapping
    public String index() {
        return "index";
    }

    /**
     * 单文件上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/singleUpload")
    @ResponseBody
    public Map<String, String> singleUpload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("[文件类型] - [{}]", file.getContentType());
        log.info("[文件名称] - [{}]", file.getOriginalFilename());
        log.info("[文件大小] - [{}]", file.getSize());

        // 将文件写入到指定目录（具体开发中有可能是将文件写入到云存储/或者指定目录通过 Nginx 进行 gzip 压缩和反向代理，此处只是为了演示故将地址写成本地电脑指定目录）
        File file1 = new File(FILE_BASE_PATH);
        if (!file1.exists()) {
            file1.mkdir();
        }

        file.transferTo(new File(FILE_BASE_PATH + "\\" + file.getOriginalFilename()));
        Map<String, String> result = new HashMap<>(16);
        result.put("contentType", file.getContentType());
        result.put("fileName", file.getOriginalFilename());
        result.put("fileSize", file.getSize() + "");
        return result;
    }

    /**
     * 批量上传
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("/batchUpload")
    @ResponseBody
    public List<Map<String, String>> batchUpload(@RequestParam("file") MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return null;
        }

        File file1 = new File(FILE_BASE_PATH);
        if (!file1.exists()) {
            file1.mkdir();
        }

        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            // Spring Mvc 提供的写入方式
            file.transferTo(new File(FILE_BASE_PATH + "\\" + file.getOriginalFilename()));
            Map<String, String> map = new HashMap<>(16);
            map.put("contentType", file.getContentType());
            map.put("fileName", file.getOriginalFilename());
            map.put("fileSize", file.getSize() + "");
            results.add(map);
        }
        return results;
    }

    /**
     * base64上传
     * @param base64
     * @throws IOException
     */
    @PostMapping("/base64Upload")
    @ResponseBody
    public void base64Upload(String base64) throws IOException {

        File file1 = new File(FILE_BASE_PATH);
        if (!file1.exists()) {
            file1.mkdir();
        }

        // BASE64 方式的 格式和名字需要自己控制（如 png 图片编码后前缀就会是 data:image/png;base64,）
        final File tempFile = new File(FILE_BASE_PATH + "\\test.jpg");
        // 防止有的传了 data:image/png;base64, 有的没传的情况
        String[] d = base64.split("base64,");
        final byte[] bytes = Base64Utils.decodeFromString(d.length > 1 ? d[1] : d[0]);
        FileCopyUtils.copy(bytes, tempFile);
    }
}
