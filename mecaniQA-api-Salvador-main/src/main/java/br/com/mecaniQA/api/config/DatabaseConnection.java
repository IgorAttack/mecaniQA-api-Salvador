package br.com.mecaniQA.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/mecaniqa?useTimezone=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Construtor privado para evitar instanciamento (Padrão Singleton/Factory)
    private DatabaseConnection() {}


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("\n[FALHA CRÍTICA] Erro de Autenticação/Conexão com o Banco de Dados.");
            System.err.println("Por favor, verifique se o MySQL está ativo, e se as credenciais estão corretas.");
            throw new RuntimeException("Erro ao conectar no banco de dados MySQL: " + e.getMessage(), e);
        }
    }
}