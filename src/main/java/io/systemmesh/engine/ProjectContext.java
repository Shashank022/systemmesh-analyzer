package io.systemmesh.engine;

import java.nio.file.Path;
import java.util.List;

public record ProjectContext(Path root, List<SourceFile> files) {
    public List<SourceFile> javaFiles() {
        return files.stream().filter(f -> f.path().toString().endsWith(".java")).toList();
    }

    public List<SourceFile> configFiles() {
        return files.stream()
                .filter(f -> {
                    String p = f.path().toString();
                    return p.endsWith(".yml") || p.endsWith(".yaml") || p.endsWith(".properties");
                })
                .toList();
    }
}
