pluginManagement {
    includeBuild("archguard-gradle-plugin") {
        name = "archguard-plugin-build"
    }
}

rootProject.name = "archguard"

include(":archguard-core")
include(":archguard-gradle-plugin")
include(":sample-project")
