package com.rache.racheserver.music;

import com.rache.racheserver.entity.MusicConfigEntity;
import com.rache.racheserver.mapper.MusicMapper;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@RequestMapping("/music")
public class MusicController {

    @Resource
    private MusicMapper musicMapper;

    @Resource
    private MusicProperties musicProperties;

    private String getMD5(byte []bytes){
        MessageDigest generator;

        try{
            generator = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        generator.update(bytes);
        byte[] md5 = generator.digest();
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : md5) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }

    private @NotNull String getMD5(InputStream stream) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        byte [] buffer = new byte[8192];
        int count;
        try {
            while ((count = stream.read(buffer)) != -1) {
                digest.update(buffer, 0, count);
            }
            byte[] md5 = digest.digest();
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : md5) {
                hexStringBuilder.append(String.format("%02x", b));
            }
            return hexStringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/config")
    private ResponseEntity<Map<String, Object>> postMusicConfig(PostConfig config) {
        int rowAffected = musicMapper.insertConfig(config.getName(), config.getSinger(), config.getAlbum());
        if (rowAffected == 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Failed to add config.");
            return ResponseEntity.internalServerError().body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK);
        response.put("message", "success");
        response.put("id", musicMapper.getLastInsertID());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/config")
    private ResponseEntity<Map<String, Object>> getMusicConfig(@RequestParam(value = "id") int id) {
        List<MusicConfigEntity> configList = musicMapper.selectById(id);

        Map<String, Object> response = new HashMap<>();
        String message;
        HttpStatus responseStatus;
        if (configList.isEmpty()) {
            responseStatus = HttpStatus.NOT_FOUND;
            message = "Config not found";
        } else {
            responseStatus = HttpStatus.OK;
            message = "success";
            MusicConfigEntity config = configList.get(0);
            GetConfig responseConfig = new GetConfig();
            responseConfig.setId(config.getId());
            responseConfig.setName(config.getName());
            responseConfig.setSinger(config.getSinger());
            responseConfig.setAlbum(config.getAlbum());
            response.put("config", responseConfig);
        }

        response.put("id", id);
        response.put("code", responseStatus.value());
        response.put("message", message);
        return ResponseEntity.status(responseStatus).body(response);
    }

    @PostMapping("/lyrics")
    private ResponseEntity<Map<String, Object>> postMusicLyrics(PostLyrics lyrics) {
        int rowAffected = musicMapper.updateLyrics(lyrics.getId(), lyrics.getLyrics());
        if (rowAffected == 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("id", lyrics.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.OK);
            response.put("id", lyrics.getId());
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/lyrics")
    private ResponseEntity<Map<String, Object>> getMusicLyrics(@RequestParam("id") int id) {
        List<MusicConfigEntity> configList = musicMapper.selectById(id);
        Map<String, Object> response = new HashMap<>();
        HttpStatus responseStatus;
        String message;
        if (configList.isEmpty()) {
            responseStatus = HttpStatus.NOT_FOUND;
            message = "Id not found.";
        } else {
            responseStatus = HttpStatus.OK;
            message = "success";
            response.put("lyrics", configList.get(0).getLyrics());
        }
        response.put("code", responseStatus);
        response.put("message", message);
        response.put("id", id);
        return ResponseEntity.status(responseStatus).body(response);
    }

    @PostMapping("/audio")
    private ResponseEntity<Map<String, Object>> postAudioFile(
            @RequestParam("audio") MultipartFile file,
            @RequestParam(value = "id") int id
    ) throws IOException {
        String md5 = getMD5(file.getInputStream());
        if (md5.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("id", id);
            response.put("message", "Error when upload audio files.");
            return ResponseEntity.badRequest().body(response);
        }
        int rowAffected = musicMapper.updateAudio(id, md5);
        Map<String, Object> response = new HashMap<>();
        HttpStatus responseStatus;
        String message;

        response.put("message", "Error when upload audio files");
        if (rowAffected == 0) {
            responseStatus = HttpStatus.NOT_FOUND;
            message = "Id not found.";
        } else {
            responseStatus = HttpStatus.OK;
            message = "success";
            File audioDest = new File(musicProperties.getAudioDir() + "//" + md5);
            file.transferTo(audioDest);
        }

        response.put("code", responseStatus);
        response.put("id", id);
        response.put("message", message);
        return ResponseEntity.status(responseStatus).body(response);
    }

    @GetMapping("/audio")
    private ResponseEntity<?> getAudioFile(@RequestParam("id") int id) {
        List<MusicConfigEntity> configList = musicMapper.selectAudioById(id);
        if (configList.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("id", id);
            response.put("message", "Id not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        String filename = configList.get(0).getAudio();
        File audioFile = new File(musicProperties.getAudioDir() + "//" + filename);
        org.springframework.core.io.Resource resource = new FileSystemResource(audioFile);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".mp3");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(audioFile.length())
                .body(resource);
    }

    @PostMapping("/cover")
    private ResponseEntity<Map<String, Object>> postCoverImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "id") int id
    ) throws IOException {
        String md5 = getMD5(file.getInputStream());
        if (md5.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("id", id);
            response.put("message", "Error when upload audio files.");
            return ResponseEntity.badRequest().body(response);
        }
        int rowAffected = musicMapper.updateCover(id, md5);
        Map<String, Object> response = new HashMap<>();
        HttpStatus responseStatus;
        String message;

        response.put("message", "Error when upload cover image file.");
        if (rowAffected == 0) {
            responseStatus = HttpStatus.NOT_FOUND;
            message = "Id not found.";
        } else {
            responseStatus = HttpStatus.OK;
            message = "success";
            File audioDest = new File(musicProperties.getCoverDir() + "//" + md5);
            file.transferTo(audioDest);
        }

        response.put("code", responseStatus);
        response.put("id", id);
        response.put("message", message);
        return ResponseEntity.status(responseStatus).body(response);
    }

    @GetMapping("/cover")
    private ResponseEntity<?> getCoverImage(@RequestParam("id") int id) {
        List<MusicConfigEntity> configList = musicMapper.selectCoverById(id);
        if (configList.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("id", id);
            response.put("message", "Id not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        String filename = configList.get(0).getCover();
        File coverImage = new File(musicProperties.getCoverDir() + "//" + filename);
        org.springframework.core.io.Resource resource = new FileSystemResource(coverImage);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".png");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(coverImage.length())
                .body(resource);
    }
}
