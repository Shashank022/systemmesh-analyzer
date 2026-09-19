package io.systemmesh.engine;

import java.nio.file.Path;
import java.util.List;

public record SourceFile(Path path, String content, List<String> lines) {
}
