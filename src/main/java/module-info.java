module com.kneelawk.modpackeditor {
    exports com.kneelawk.modpackeditor;
    exports com.kneelawk.modpackeditor.curse;
    exports com.kneelawk.modpackeditor.data;
    exports com.kneelawk.modpackeditor.data.curseapi;
    exports com.kneelawk.modpackeditor.data.jumploader;
    exports com.kneelawk.modpackeditor.data.manifest;
    exports com.kneelawk.modpackeditor.net;
    exports com.kneelawk.modpackeditor.ui;

    requires kotlin.stdlib;

    requires com.google.common;
    requires java.json;
    requires org.apache.commons.codec;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    requires tornadofx;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
}
