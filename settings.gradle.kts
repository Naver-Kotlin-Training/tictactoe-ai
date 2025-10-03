pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // For KSP & ROOM
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // For KSP & ROOM
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

rootProject.name = "TicTactoeAI"
include(":app")
