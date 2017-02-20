package com.pablisco.gradle.rambuild;

import org.gradle.api.*
import java.io.File

class RamBuildPlugin : Plugin<Project> {

    fun String.execute(or : (String) -> Unit) : String? {
        Runtime.getRuntime().exec(this).run {
            if (waitFor() != 0) {
                or.invoke(errorStream.bufferedReader().readText())
                return null
            } else {
                return inputStream.bufferedReader().readText()
            }
        }
    }

    fun String.asSize() : Int = when {
        endsWith("k", true) -> removeSuffix("k").toInt()
        endsWith("m", true) -> removeSuffix("m").toInt() * 1024
        endsWith("g", true) -> removeSuffix("g").toInt() * 1024 * 1024
        else -> "512m".asSize()
    } * 2

    override fun apply(project : Project) {
        val extension = RamBuildExtension()
        project.extensions.add("ramBuild", extension)
        project.afterEvaluate {

            val diskSize = extension.size.asSize()
            val diskName = extension.name
            val volume = File("/Volumes/$diskName")

            if (!volume.exists()) {
                project.logger.lifecycle("Creating ramDisk $diskName")
                // TODO: implement for linux and Windows
                val mountedVolumePath = "hdiutil attach -nomount ram://$diskSize".execute(or = { project.logger.error("Error creating ram disk: $it") })
                "diskutil erasevolume HFS+ $diskName $mountedVolumePath".execute(or = { project.logger.error("Error mounting volume: $it") })
            }

            if (volume.exists()) {
                project.rootProject.allprojects {
                    it.setBuildDir("/Volumes/$diskName/gradle/${project.rootProject.name}/${project.name}")
                }
            } else {
                project.logger.error("Volume not created.")
            }

        }

    }

    class RamBuildExtension(
        var size : String = "512m",
        var name : String = "build"
    )

}
