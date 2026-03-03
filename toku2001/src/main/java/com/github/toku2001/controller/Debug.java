package com.github.toku2001.controller;

import java.util.Enumeration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/debug")
public class Debug {

    @PostMapping("/health-auto-exports")
    public ResponseEntity<String> dump(HttpServletRequest request, @RequestBody(required = false) String body) throws Exception {

        // 1. ヘッダ出力
        System.out.println("=== HEADERS ===");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + " = " + request.getHeader(name));
        }

        // 2. Content-Type
        System.out.println("Content-Type: " + request.getContentType());

        // 3. Body（CSV / raw）
        System.out.println("=== BODY ===");
        if (body != null) {
            System.out.println(body);
        }

        return ResponseEntity.ok("ok");
    }
}
