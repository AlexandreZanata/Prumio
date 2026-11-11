pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "prumio"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Module catalog: docs/architecture/modules.md. Adding a module? Follow docs/ai/recipes/add-feature-module.md.
include(":app:android")
include(":app:shell")

include(":core:common")
include(":core:data")
include(":core:database")
include(":core:database-testing")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:navigation")
include(":core:notifications")
include(":core:screenshot-testing")
include(":core:testing")
include(":core:ui")

include(":feature:agenda")
include(":feature:capture")
include(":feature:inbox")
include(":feature:matters")
include(":feature:search")
include(":feature:settings")
include(":feature:tasks")
include(":feature:today")

include(":tooling:architecture-tests")
