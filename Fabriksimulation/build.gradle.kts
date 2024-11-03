plugins {
    java
    jacoco
    `java-library`
    application
}

allprojects {
    apply(plugin = "jacoco")
    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:1.7.36")
        implementation("ch.qos.logback:logback-core:1.2.11")
        implementation("ch.qos.logback:logback-classic:1.2.11")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testImplementation("org.mockito:mockito-core:4.6.1")
        testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    // project dependencies
    implementation(project(":model"))
    implementation(project(":services"))
    implementation(project(":rest"))
    implementation(project(":config"))
    implementation(project(":report"))
    implementation(project(":comms"))

    // google guava for event handling
    implementation("com.google.guava:guava:31.1-jre")

    // apache commons CLI for command line handling
    implementation("commons-cli:commons-cli:1.5.0")
}

tasks.jacocoTestReport {
    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
}

tasks.jar {
    manifest.attributes["Main-Class"] = "io.nyando.factorix.FactorixMain"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

task("simtimeExec", JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.nyando.factorix.FactorixMain")

    args("-f", "yaml") // config file format
    args("-w", "config/src/test/resources/workplaces.yml") // workplace config file
    args("-p", "config/src/test/resources/products.yml") // product config file
    args("-l", "2000") // simulation time limit
    args("-i", "src/test/resources/initialorders.csv") // initial orders
}

task("realtimeExec", JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.nyando.factorix.FactorixMain")

    args("-f", "yaml") // config file format
    args("-w", "config/src/test/resources/workplaces.yml") // workplace config file
    args("-p", "config/src/test/resources/products.yml") // product config file
    args("-i", "src/test/resources/initialorders.csv") // initial orders
}