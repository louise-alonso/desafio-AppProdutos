package br.com.louise.AppProdutos.config;

public final class Constants {

    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    // Access Token: 1 hora
    public static final int TOKEN_EXPIRATION = 3_600_000;

    // Refresh Token: 30 dias (em milissegundos)
    public static final long REFRESH_EXPIRATION = 2592000000L;

    public static final String TOKEN_PASSWORD = "b62e569f-3bfe-44f0-9de5-c5eb3a20298b";

    public static final String HEADER_ATTRIBUTE = "Authorization";
    public static final String PREFIX_ATTRIBUTE = "Bearer ";
}