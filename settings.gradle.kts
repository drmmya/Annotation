// settings.gradle.kts (project root)
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")        // ← make sure this is here
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")        // ← and also here
    }
}

rootProject.name = "Annotation"
include(":app")
include(":material")
