plugins {
    kotlin("jvm")
    id("com.archguard.plugin")
}

archGuard {
    reports {
        html {
            enabled = true
            outputPath = "archguard-report.html"
        }
    }

    architecture {
        featureRoot = "feature"

        layer("presentation") {
            required = true
        }

        layer("domain") {
            required = true
        }

        layer("data") {
            required = true
        }

        forbid(
            "helper",
            "util",
            "manager"
        )
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.register("archGuardCheck") {
    group = "verification"
    description = "Runs the ArchGuard architecture check for the sample project."
    dependsOn("architectureCheck")
}

tasks.test {
    useJUnitPlatform()
}
