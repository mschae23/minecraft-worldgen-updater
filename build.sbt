val scala3Version = "3.0.0"

credentials +=
    Credentials(
        "GitHub Package Registry",
        "maven.pkg.github.com",
        sys.env("GITHUB_USERNAME"),
        sys.env("GITHUB_TOKEN")
    )

lazy val root = project
    .in(file("."))
    .settings(
        name := "minecraft-worldgen-updater",
        organization := "de.martenschaefer",
        version := "1.0.0",
        // homepage := Some(url("https://github.com/mschae23/minecraft-worldgen-updater")),

        scalaVersion := scala3Version,

        idePackagePrefix := Some("de.martenschaefer.minecraft.worldgenupdater"),

        libraryDependencies ++= Seq(
            "de.martenschaefer" %% "data-api" % "3.0.0",

            "org.typelevel" %% "cats-core" % "2.6.1"
            // "org.typelevel" %% "cats-effect" % "3.1.1"
        ),

        resolvers ++= Seq(
            "GitHub Package Registry (mschae23)" at "https://maven.pkg.github.com/mschae23/_"
        ),

        // publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/mschae23/minecraft-worldgen-updater"),
        // scmInfo := Some(ScmInfo(url("https://github.com/mschae23/minecraft-worldgen-updater"), "scm:git@github.com:mschae23/minecraft-worldgen-updater.git")),

        publishMavenStyle := true,
        versionScheme := Some("semver-spec")
    )
