load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")
load("//rules_intellij/testing:test_defs_kt.bzl", "intellij_integration_test_suite")

exports_files(glob([
    "main/jps-resources/**/*",
]))

kt_jvm_library(
    name = "optional-xmls",
    resource_strip_prefix = "plugin-bazel/src/main/jps-resources",
    resources = glob(
        [
            "main/jps-resources/**",
        ],
        exclude = [
            "main/jps-resources/META-INF/plugin.xml",
        ],
    ),
    visibility = ["//visibility:public"],
)

kt_jvm_library(
    name = "test_fixtures",
    testonly = 1,
    srcs = glob([
        "testFixtures/kotlin/**/*.kt",
    ]),
    visibility = ["//visibility:public"],
    deps = [
        ":plugin-bazel-lib",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/coroutines",
        "//protocol",
        "//rules_intellij/testing:lib",
        "@maven//:io_kotest_kotest_assertions_api_jvm",
        "@maven//:io_kotest_kotest_assertions_core_jvm",
        "@maven//:io_kotest_kotest_assertions_shared_jvm",
        "@maven//:io_kotest_kotest_common_jvm",
        "@maven//:org_junit_jupiter_junit_jupiter",
        "@server_maven//:org_jetbrains_kotlinx_kotlinx_coroutines_test",
    ],
)

kt_jvm_library(
    name = "plugin-bazel-lib",
    resources = glob([
        "main/resources/**",
    ]),
    # Do not depend on this target because it exports the Bazel BSP server dependency!
    # If you want to share code with the server, use //commons.
    # Otherwise depend on a more specific subpackage of //plugin-bazel
    visibility = [
        "//plugin-bazel:__pkg__",
        "//plugin-bazel/src:__pkg__",
        "//plugin-bazel/src/test:__subpackages__",
        "//server/bspcli:__pkg__",
    ],
    exports = [
        ":optional-xmls",
        "//commons/src/main/kotlin/org/jetbrains/bazel/performance",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/action",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/action/registered",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/android",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/android/run",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/annotations",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/assets",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/buildTask",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/config",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/coroutines",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/debug",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/extensionPoints",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/flow",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/flow/modify/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/flow/open",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/golang/debug",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/golang/sync",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/golang/ui/gutters",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/hotswap",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/hotswap/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/intellij",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/java",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/jpsCompilation/utils",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/jvm/junit",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/jvm/run",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/jvm/sync",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/jvm/ui/gutters",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/kotlin",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/languages",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/magicmetamodel",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/magicmetamodel/impl",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/magicmetamodel/impl/workspacemodel",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/magicmetamodel/impl/workspacemodel/impl",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/magicmetamodel/impl/workspacemodel/impl/updaters",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/nonmodule/sync",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/nonmodule/sync/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/performanceImpl",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/projectAware",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/python",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/python/sync",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/run",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/run/coverage/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/runnerAction",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/scala",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/server/client",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/server/connection",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/server/tasks",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/services",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/settings",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/startup",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/action",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/projectStructure",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/projectStructure/workspaceModel",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/scope",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/status",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/sync/task",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/target",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/taskEvents",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/console",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/console/bazel",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/console/filter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/console/ids",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/gutters",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/notifications",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/projectTree",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/projectTree/action",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/settings",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/widgets",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/widgets/fileTargets",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/utils",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/workspace",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/workspace/ideStarter",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/workspacemodel/entities",
        "//server/server/src/main/kotlin/org/jetbrains/bazel:bsp_export",
    ],
)

kt_jvm_library(
    name = "plugin-bazel-resources",
    resources = glob([
        "main/resources/**",
    ]),
    visibility = ["//visibility:public"],
)

intellij_integration_test_suite(
    name = "plugin_tests",
    srcs = glob([
        "test/kotlin/org/jetbrains/**/*.kt",
    ]),
    associates = [":plugin-bazel-lib"],
    test_package_root = "org.jetbrains",
    runtime_deps = [
        ":plugin-bazel-lib",
    ],
    deps = [
        "//plugin-bazel/src:test_fixtures",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/extensionPoints/buildTargetClassifier",
        "//plugin-bazel/src/main/kotlin/org/jetbrains/bazel/ui/settings",
        "//sdkcompat",
        "@maven//:io_kotest_kotest_assertions_api_jvm",
        "@maven//:io_kotest_kotest_assertions_core_jvm",
        "@maven//:io_kotest_kotest_assertions_shared_jvm",
        "@maven//:io_kotest_kotest_common_jvm",
    ],
)
