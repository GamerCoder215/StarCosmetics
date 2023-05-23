val mcVersion = "1.17.1"

dependencies {
    api(project(":starcosmetics-abstraction"))
    api(project(":starcosmetics-api"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}