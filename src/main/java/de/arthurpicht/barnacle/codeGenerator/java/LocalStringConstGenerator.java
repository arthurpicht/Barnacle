package de.arthurpicht.barnacle.codeGenerator.java;

import java.util.ArrayList;
import java.util.List;

public class LocalStringConstGenerator {

    private static class NameValuePair {
        private final String name;
        private final String value;

        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private final List<NameValuePair> nameValuePairList;

    public LocalStringConstGenerator() {
        this.nameValuePairList = new ArrayList<>();
    }

    public void add(String name, String value) {
        NameValuePair nameValuePair = new NameValuePair(name, value);
        this.nameValuePairList.add(nameValuePair);
    }

    public void generate(SourceCache sourceCache) {
        for (NameValuePair nameValuePair : this.nameValuePairList) {
            sourceCache.addLine("private static final String "
                    + nameValuePair.getName() + " = "
                    + "\"" + nameValuePair.getValue() + "\";");
        }
        if (!this.nameValuePairList.isEmpty())
            sourceCache.addLine();
    }

}
