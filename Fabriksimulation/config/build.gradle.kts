plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":services"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.dom4j:dom4j:2.1.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}