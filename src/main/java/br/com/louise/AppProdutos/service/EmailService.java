package br.com.louise.AppProdutos.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
}