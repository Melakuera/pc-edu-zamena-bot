module com.example.zamenabotgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires json.simple;
	requires org.apache.httpcomponents.core5.httpcore5;


    opens io.melakuera.zamenabotgui to javafx.fxml;
    exports io.melakuera.zamenabotgui;
}