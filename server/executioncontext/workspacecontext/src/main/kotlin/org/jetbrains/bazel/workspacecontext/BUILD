load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "workspacecontext",
    srcs = glob(["*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        "//rules_intellij/intellij_platform_sdk:plugin_api",
        "//server/executioncontext/api",
        "//server/server/src/main/kotlin/org/jetbrains/bazel/server/label",
        "@maven//:commons_io_commons_io",
        "@maven//:org_apache_logging_log4j_log4j_api",
        "@maven//:org_apache_logging_log4j_log4j_core",
    ],
)
