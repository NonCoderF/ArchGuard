buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }

    val groupProp = gradle.startParameter.projectProperties["group"] ?: "io.github.noncoderf.archguard"
    val versionProp = gradle.startParameter.projectProperties["version"] ?: "0.1.7"
    val groupPath = groupProp.replace('.', '/')
    val userHome = System.getProperty("user.home")
    val m2Jar = java.io.File(userHome, ".m2/repository/$groupPath/archguard-gradle-plugin/$versionProp/archguard-gradle-plugin-$versionProp.jar")

    val userDir = System.getProperty("user.dir")
    val localJar = java.io.File(userDir, "archguard-gradle-plugin/build/libs/archguard-gradle-plugin-0.1.7.jar")

    if (m2Jar.exists()) {
        dependencies {
            classpath("$groupProp:archguard-gradle-plugin:$versionProp")
        }
    } else if (localJar.exists()) {
        dependencies {
            classpath(files(localJar))
            val coreJar = java.io.File(userDir, "archguard-core/build/libs/archguard-core-0.1.7.jar")
            if (coreJar.exists()) {
                classpath(files(coreJar))
            }
        }
    }
}

plugins {
    kotlin("jvm")
}

val isPluginAvailable = try {
    Class.forName("com.archguard.gradle.ArchGuardPlugin")
    true
} catch (e: ClassNotFoundException) {
    false
}

if (isPluginAvailable) {
    apply(plugin = "io.github.noncoderf.archguard.gradle")

    val archGuardExt = extensions.getByName("archGuard")
    val actionClass = org.gradle.api.Action::class.java

    val reportsMethod = archGuardExt.javaClass.getMethod("reports", actionClass)
    val configureReports = object : org.gradle.api.Action<Any> {
        override fun execute(dsl: Any) {
            val htmlMethod = dsl.javaClass.getMethod("html", actionClass)
            val configureHtml = object : org.gradle.api.Action<Any> {
                override fun execute(htmlDsl: Any) {
                    htmlDsl.javaClass.getMethod("setEnabled", Boolean::class.javaPrimitiveType).invoke(htmlDsl, true)
                    htmlDsl.javaClass.getMethod("setOutputPath", String::class.java).invoke(htmlDsl, "archguard-report.html")
                }
            }
            htmlMethod.invoke(dsl, configureHtml)
        }
    }
    reportsMethod.invoke(archGuardExt, configureReports)

    val architectureMethod = archGuardExt.javaClass.getMethod("architecture", actionClass)
    val configureArchitecture = object : org.gradle.api.Action<Any> {
        override fun execute(dsl: Any) {
            dsl.javaClass.getMethod("setFeatureRoot", String::class.java).invoke(dsl, "feature")

            val layerMethod = dsl.javaClass.getMethod("layer", String::class.java, actionClass)
            val configureLayer = object : org.gradle.api.Action<Any> {
                override fun execute(layerDsl: Any) {
                    layerDsl.javaClass.getMethod("setRequired", Boolean::class.javaPrimitiveType).invoke(layerDsl, true)
                }
            }
            layerMethod.invoke(dsl, "presentation", configureLayer)
            layerMethod.invoke(dsl, "domain", configureLayer)
            layerMethod.invoke(dsl, "data", configureLayer)

            val forbidMethod = dsl.javaClass.getMethod("forbid", Array<String>::class.java)
            forbidMethod.invoke(dsl, arrayOf("helper", "util", "manager") as Any)
        }
    }
    architectureMethod.invoke(archGuardExt, configureArchitecture)
} else {
    logger.warn("ArchGuard plugin is not available on classpath. Skipping configuration.")
}

if (isPluginAvailable) {
    tasks.register("archGuardCheck") {
        group = "verification"
        description = "Runs the ArchGuard architecture check for the sample project."
        dependsOn("architectureCheck")
    }
} else {
    tasks.register("archGuardCheck") {
        group = "verification"
        description = "Dummy ArchGuard check task when plugin is not available."
        doLast {
            logger.warn("ArchGuardCheck skipped because the ArchGuard plugin is not available.")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
