package com.omnishore.cvtech.client.ai;

import com.omnishore.cvtech.annotations.Client;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Client
public class AiClient {

    public ResponseEntity<Map> parseCv(File file) {
        // Appel API IA (http://69.62.106.98:6600/cv)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        return new RestTemplate().postForEntity("http://69.62.106.98:6600/cv", request, Map.class);
    }
}
