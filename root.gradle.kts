plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.10-fabric"(1_21_10, "yarn") {
        "1.21.9-fabric"(1_21_09, "yarn") {
            "1.21.8-fabric"(1_21_08, "yarn") {
                "1.21.7-fabric"(1_21_07, "yarn") {
                    "1.21.6-fabric"(1_21_06, "yarn") {
                        "1.21.5-fabric"(1_21_05, "yarn")
                    }
                }
            }
        }
    }
    strictExtraMappings.set(true)
}