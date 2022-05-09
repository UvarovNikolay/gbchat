module ru.gb.gbchat {
    requires javafx.controls;
    requires javafx.fxml;

    exports ru.gb.gbchat.client;
    opens ru.gb.gbchat.client to javafx.fxml;
}