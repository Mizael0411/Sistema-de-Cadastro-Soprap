module com.abrigo {
    requires jakarta.persistence;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.abrigo to javafx.fxml;
    opens com.abrigo.sistema to javafx.fxml;
    opens com.abrigo.view to javafx.fxml;
    opens com.abrigo.model to jakarta.persistence;
    exports com.abrigo.model;
    exports com.abrigo;
    exports com.abrigo.sistema;
    exports com.abrigo.view;
}