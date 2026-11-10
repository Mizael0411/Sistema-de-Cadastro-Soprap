module com.abrigo {

    requires javafx.controls;
    requires javafx.fxml;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires java.sql;
    requires java.desktop;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires org.kordamp.bootstrapfx.core;

    requires bcrypt;

    requires com.github.librepdf.openpdf;

    exports com.abrigo;
    exports com.abrigo.sistema;
    exports com.abrigo.controller;
    exports com.abrigo.model;
    exports com.abrigo.database;
    exports com.abrigo.dto;
    exports com.abrigo.service;

    opens com.abrigo to javafx.fxml;
    opens com.abrigo.sistema to javafx.fxml;
    opens com.abrigo.controller to javafx.fxml;

    opens com.abrigo.model to
            jakarta.persistence,
            org.hibernate.orm.core;

    opens com.abrigo.database to
            jakarta.persistence;

    opens com.abrigo.dto to
            org.hibernate.orm.core;
}
