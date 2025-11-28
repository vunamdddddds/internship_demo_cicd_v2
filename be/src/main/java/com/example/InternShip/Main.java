package com.example.InternShip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main {

    public static void main(String[] args) {
        // CẤU HÌNH QUAN TRỌNG:
        // .ignoreIfMissing() giúp app KHÔNG bị crash khi chạy trong Docker (nơi không có file .env)
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMissing() 
                              .load();
        
        // Nếu tìm thấy file .env (môi trường Local), nạp biến vào System Properties
        // Nếu không thấy file (môi trường Docker), lệnh này sẽ bỏ qua và không làm gì cả
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // Chạy Spring Boot
        // Lưu ý: Trong Docker, Spring Boot sẽ tự động đọc biến môi trường từ OS (do docker-compose truyền vào)
        SpringApplication.run(Main.class, args);
    }
}