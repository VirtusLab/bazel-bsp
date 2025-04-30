load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")
load("@rules_jvm_external//:kt_defs.bzl", "kt_jvm_export")

kt_jvm_library(
    name = "install",
    srcs = glob(["*.kt"]),
    resource_strip_prefix = "server/resources",
    resources = ["//server/resources/aspects"],
    visibility = ["//visibility:public"],
    deps = [
        "@bsp4j//:ch_epfl_scala_bsp4j",
        "@maven//:com_google_code_gson_gson",
        "//commons/src/main/kotlin/org/jetbrains/bazel/commons/constants",
        "//commons/src/main/kotlin/org/jetbrains/bazel/commons/gson",
        "//protocol",
        "//server/executioncontext/projectview:generator",
        "//server/executioncontext/projectview:parser",
        "//server/install/src/main/kotlin/org/jetbrains/bazel/install/cli",
        "//server/install/src/main/kotlin/org/jetbrains/bazel/install/installationcontext",
        "//server/server/src/main/kotlin/org/jetbrains/bazel/server/bsp/utils:fileutils",
    ],
)

# Invocation cmd:
# bazel run --define "maven_repo=file://$HOME/.m2/repository" //server/install/src/main/kotlin/org/jetbrains/bazel/install:install_exported.publish
kt_jvm_export(
    name = "install_exported",
    maven_coordinates = "com.virtuslab:bazel-bsp:3.2.3",
    srcs = glob(["*.kt"]),
    resource_strip_prefix = "server/resources",
    resources = ["//server/resources/aspects"],
    visibility = ["//visibility:public"],
    deps = [
        "@bsp4j//:ch_epfl_scala_bsp4j",
        "@maven//:com_google_code_gson_gson",
        "//commons/src/main/kotlin/org/jetbrains/bazel/commons/constants",
        "//commons/src/main/kotlin/org/jetbrains/bazel/commons/gson",
        "//protocol",
        "//server/executioncontext/projectview:generator",
        "//server/executioncontext/projectview:parser",
        "//server/install/src/main/kotlin/org/jetbrains/bazel/install/cli",
        "//server/install/src/main/kotlin/org/jetbrains/bazel/install/installationcontext",
        "//server/server/src/main/kotlin/org/jetbrains/bazel/server/bsp/utils:fileutils",
    ],
    tags = ["no-javadocs"], # rules_jvm_external#1351
)
