buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }

    val groupProp = gradle.startParameter.projectProperties["group"] ?: "io.github.noncoderf.archguard"
    val versionProp = gradle.startParameter.projectProperties["version"] ?: "0.1.0"
    val groupPath = groupProp.replace('.', '/')
    val userHome = System.getProperty("user.home")
    val m2Jar = java.io.File(userHome, ".m2/repository/$groupPath/archguard-gradle-plugin/$versionProp/archguard-gradle-plugin-$versionProp.jar")
    
    val userDir = System.getProperty("user.dir")
    val localJar = java.io.File(userDir, "archguard-gradle-plugin/build/libs/archguard-gradle-plugin-0.1.0.jar")

    if (m2Jar.exists()) {
        dependencies {
            classpath("$groupProp:archguard-gradle-plugin:$versionProp")
        }
    } else if (localJar.exists()) {
        dependencies {
            classpath(files(localJar))
            val coreJar = java.io.File(userDir, "archguard-core/build/libs/archguard-core-0.1.3.jar")
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
    val function1Class = Class.forName("kotlin.jvm.functions.Function1")

    // configure reports
    val reportsMethod = archGuardExt.javaClass.getMethod("reports", function1Class)
    val configureReports = { dsl: Any ->
        val htmlMethod = dsl.javaClass.getMethod("html", function1Class)
        val configureHtml = { htmlDsl: Any ->
            htmlDsl.javaClass.getMethod("setEnabled", Boolean::class.javaPrimitiveType).invoke(htmlDsl, true)
            htmlDsl.javaClass.getMethod("setOutputPath", String::class.java).invoke(htmlDsl, "archguard-report.html")
            Unit
        }
        htmlMethod.invoke(dsl, configureHtml)
        Unit
    }
    reportsMethod.invoke(archGuardExt, configureReports)

    // configure architecture
    val architectureMethod = archGuardExt.javaClass.getMethod("architecture", function1Class)
    val configureArchitecture = { dsl: Any ->
        dsl.javaClass.getMethod("setFeatureRoot", String::class.java).invoke(dsl, "feature")

        val layerMethod = dsl.javaClass.getMethod("layer", String::class.java, function1Class)
        val configureLayer = { layerDsl: Any ->
            layerDsl.javaClass.getMethod("setRequired", Boolean::class.javaPrimitiveType).invoke(layerDsl, true)
            Unit
        }
        layerMethod.invoke(dsl, "presentation", configureLayer)
        layerMethod.invoke(dsl, "domain", configureLayer)
        layerMethod.invoke(dsl, "data", configureLayer)

        val forbidMethod = dsl.javaClass.getMethod("forbid", Array<String>::class.java)
        forbidMethod.invoke(dsl, arrayOf("helper", "util", "manager") as Any)
        Unit
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
