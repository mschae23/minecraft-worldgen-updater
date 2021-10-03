val scala3Version = "3.0.1"

lazy val root = project
    .in(file("."))
    .settings(
        name := "minecraft-worldgen-updater",
        organization := "de.martenschaefer",
        version := "1.5.0",
        homepage := Some(url("https://github.com/mschae23/minecraft-worldgen-updater")),

        scalaVersion := scala3Version,

        idePackagePrefix := Some("de.martenschaefer.minecraft.worldgenupdater"),

        libraryDependencies ++= Seq(
            "de.martenschaefer" %% "data-api" % "4.0.2",

            "org.typelevel" %% "cats-core" % "2.6.1"
            // "org.typelevel" %% "cats-effect" % "3.1.1"
        ),

        resolvers ++= Seq(
            "GitHub Package Registry (mschae23)" at "https://maven.pkg.github.com/mschae23/_"
        ),

        publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/mschae23/minecraft-worldgen-updater"),
        scmInfo := Some(ScmInfo(url("https://github.com/mschae23/minecraft-worldgen-updater"), "scm:git@github.com:mschae23/minecraft-worldgen-updater.git")),

        publishMavenStyle := true,
        versionScheme := Some("semver-spec")
    )

credentials += Credentials(Path.userHome / ".github" / ".credentials")

assembly / assemblyMergeStrategy := {
    case PathList("module-info.class", xs @ _*) => MergeStrategy.discard
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}
