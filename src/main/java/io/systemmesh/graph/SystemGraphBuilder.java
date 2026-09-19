package io.systemmesh.graph;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.SourceFile;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SystemGraphBuilder {
    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern TYPE = Pattern.compile("\\b(class|interface|record|enum)\\s+([A-Za-z_$][A-Za-z0-9_$]*)");
    private static final Pattern KAFKA_SEND = Pattern.compile("\\.send\\(\\s*\"([^\"]+)\"");
    private static final Pattern KAFKA_LISTENER = Pattern.compile("topics\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z0-9_.-]+)(?::[^}]*)?}");
    private static final Pattern FEIGN_CLIENT = Pattern.compile("@FeignClient\\s*\\([^)]*?(?:name|value)\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern DB_ACCESS = Pattern.compile("\\b(repository|repo|dao|jdbcTemplate|entityManager)\\b", Pattern.CASE_INSENSITIVE);

    public SystemGraph build(ProjectContext context) {
        Map<String, GraphNode> nodes = new LinkedHashMap<>();
        Set<EdgeKey> edgeKeys = new LinkedHashSet<>();
        List<GraphEdge> edges = new ArrayList<>();

        String serviceName = resolveServiceName(context);
        String serviceId = "service:" + serviceName;
        addNode(nodes, new GraphNode(serviceId, NodeType.SERVICE, serviceName,
                Map.of("root", context.root().toString())));

        for (SourceFile file : context.javaFiles()) {
            JavaIdentity identity = javaIdentity(file);
            String classId = "class:" + identity.qualifiedName();
            addNode(nodes, new GraphNode(classId, NodeType.JAVA_CLASS, identity.qualifiedName(),
                    Map.of("file", file.path().toString())));
            addEdge(edges, edgeKeys, serviceId, classId, EdgeType.CONTAINS, Map.of());

            Matcher send = KAFKA_SEND.matcher(file.content());
            while (send.find()) {
                String topic = send.group(1);
                String topicId = "topic:" + topic;
                addNode(nodes, new GraphNode(topicId, NodeType.KAFKA_TOPIC, topic, Map.of()));
                addEdge(edges, edgeKeys, classId, topicId, EdgeType.PRODUCES, Map.of());
            }

            Matcher listener = KAFKA_LISTENER.matcher(file.content());
            while (listener.find()) {
                String topic = listener.group(1);
                String topicId = "topic:" + topic;
                addNode(nodes, new GraphNode(topicId, NodeType.KAFKA_TOPIC, topic, Map.of()));
                addEdge(edges, edgeKeys, classId, topicId, EdgeType.CONSUMES, Map.of());
            }

            Matcher placeholder = PLACEHOLDER.matcher(file.content());
            while (placeholder.find()) {
                String key = placeholder.group(1);
                String keyId = "config:" + key;
                addNode(nodes, new GraphNode(keyId, NodeType.CONFIG_KEY, key, Map.of()));
                addEdge(edges, edgeKeys, classId, keyId, EdgeType.READS_CONFIG, Map.of());
            }

            Matcher feign = FEIGN_CLIENT.matcher(file.content());
            while (feign.find()) {
                String downstream = feign.group(1);
                String downstreamId = "service:" + downstream;
                addNode(nodes, new GraphNode(downstreamId, NodeType.SERVICE, downstream,
                        Map.of("external", "true")));
                addEdge(edges, edgeKeys, classId, downstreamId, EdgeType.CALLS, Map.of("via", "FeignClient"));
            }

            if (DB_ACCESS.matcher(file.content()).find()) {
                String dbId = "data:database";
                addNode(nodes, new GraphNode(dbId, NodeType.DATA_STORE, "database", Map.of()));
                addEdge(edges, edgeKeys, classId, dbId, EdgeType.ACCESSES, Map.of());
            }
        }

        for (SourceFile file : context.configFiles()) {
            Matcher placeholder = PLACEHOLDER.matcher(file.content());
            while (placeholder.find()) {
                String key = placeholder.group(1);
                String keyId = "config:" + key;
                addNode(nodes, new GraphNode(keyId, NodeType.CONFIG_KEY, key, Map.of()));
                addEdge(edges, edgeKeys, serviceId, keyId, EdgeType.READS_CONFIG,
                        Map.of("file", file.path().toString()));
            }
        }

        return new SystemGraph(List.copyOf(nodes.values()), List.copyOf(edges));
    }

    private String resolveServiceName(ProjectContext context) {
        for (SourceFile file : context.configFiles()) {
            for (String line : file.content().split("\\R")) {
                String trimmed = line.trim();
                if (trimmed.startsWith("spring.application.name=")) {
                    return sanitize(trimmed.substring("spring.application.name=".length()));
                }
            }
        }
        Path name = context.root().getFileName();
        return sanitize(name == null ? "application" : name.toString());
    }

    private JavaIdentity javaIdentity(SourceFile file) {
        Matcher type = TYPE.matcher(file.content());
        String simple = type.find() ? type.group(2) : stripExtension(file.path().getFileName().toString());
        Matcher pkg = PACKAGE.matcher(file.content());
        String packageName = pkg.find() ? pkg.group(1) : "";
        return new JavaIdentity(packageName.isBlank() ? simple : packageName + "." + simple);
    }

    private String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private String sanitize(String value) {
        String result = value.trim().replace("\\\"", "").replace("'", "");
        return result.isBlank() ? "application" : result;
    }

    private void addNode(Map<String, GraphNode> nodes, GraphNode node) {
        nodes.putIfAbsent(node.id(), node);
    }

    private void addEdge(List<GraphEdge> edges, Set<EdgeKey> keys,
                         String from, String to, EdgeType type, Map<String, String> attributes) {
        EdgeKey key = new EdgeKey(from, to, type);
        if (keys.add(key)) {
            edges.add(new GraphEdge(from, to, type, attributes));
        }
    }

    private record JavaIdentity(String qualifiedName) {}
    private record EdgeKey(String from, String to, EdgeType type) {}
}
