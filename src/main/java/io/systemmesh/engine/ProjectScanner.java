package io.systemmesh.engine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ProjectScanner {
    private static final Set<String> SKIP_DIRS = Set.of(".git", "target", "build", ".gradle", "node_modules", ".idea");

    public ProjectContext scan(Path root) throws IOException {
        List<SourceFile> files = new ArrayList<>();
        try (var stream = Files.walk(root)) {
            for (Path path : stream.filter(Files::isRegularFile).toList()) {
                if (shouldSkip(root, path) || !supported(path)) {
                    continue;
                }
                String content = Files.readString(path, StandardCharsets.UTF_8);
                files.add(new SourceFile(root.relativize(path), content, content.lines().toList()));
            }
        }
        return new ProjectContext(root, List.copyOf(files));
    }

    private boolean supported(Path path) {
        String p = path.toString();
        return p.endsWith(".java") || p.endsWith(".yml") || p.endsWith(".yaml") || p.endsWith(".properties");
    }

    private boolean shouldSkip(Path root, Path path) {
        Path relative = root.relativize(path);
        for (Path part : relative) {
            if (SKIP_DIRS.contains(part.toString())) {
                return true;
            }
        }
        return false;
    }
}
