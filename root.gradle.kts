plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.10-fabric"(1_21_10, "yarn") {
        "1.21.8-fabric"(1_21_08, "yarn") {
            "1.21.5-fabric"(1_21_05, "yarn")
        }
    }
    strictExtraMappings.set(true)
}