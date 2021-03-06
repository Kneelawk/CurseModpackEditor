module com.kneelawk.modpackeditor {
    exports com.kneelawk.modpackeditor;
    exports com.kneelawk.modpackeditor.cache;
    exports com.kneelawk.modpackeditor.curse;
    exports com.kneelawk.modpackeditor.data;
    exports com.kneelawk.modpackeditor.data.curseapi;
    exports com.kneelawk.modpackeditor.data.jumploader;
    exports com.kneelawk.modpackeditor.data.manifest;
    exports com.kneelawk.modpackeditor.data.version;
    exports com.kneelawk.modpackeditor.net;
    exports com.kneelawk.modpackeditor.tasks;
    exports com.kneelawk.modpackeditor.tasks.chain;
    exports com.kneelawk.modpackeditor.ui;
    exports com.kneelawk.modpackeditor.ui.dependency;
    exports com.kneelawk.modpackeditor.ui.mods;
    exports com.kneelawk.modpackeditor.ui.update;
    exports com.kneelawk.modpackeditor.ui.util;

    requires kotlin.stdlib;

    requires arrow.core;
    requires arrow.core.data;

    requires com.google.common;
    requires java.json;
    requires org.apache.commons.codec;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    requires tornadofx;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
}
