module com.gestionale {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens com.gestionale to javafx.fxml;
    exports com.gestionale;
}
