module com.abrigo {
    // Módulos do JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Módulos do Banco de Dados / JPA / Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    // Outras dependências do projeto
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    // Permite que o JavaFX acesse os FXMLs e Controllers
    opens com.abrigo to javafx.fxml;
    opens com.abrigo.sistema to javafx.fxml;
    opens com.abrigo.view to javafx.fxml;

    // LIBERA O ACESSO DO HIBERNATE ÀS SUAS ENTIDADES (Essencial!)
    opens com.abrigo.model to jakarta.persistence, org.hibernate.orm.core;
    opens com.abrigo.database to jakarta.persistence;

    // Exporta os pacotes para o projeto
    exports com.abrigo;
    exports com.abrigo.sistema;
    exports com.abrigo.view;
    exports com.abrigo.model;
    exports com.abrigo.database;
}