package com.pablisco.gradle.rambuild;

import org.gradle.api.*;

class RamBuildPlugin implements Plugin<Project> {

  def RAM_DISK_SIZE_MB = hasProperty('ramDiskSize') ? ramDiskSize.toInteger() : 512
  def RAM_DISK_NAME = "build"

  def file = { new File(it) }

  def run(String command, onSuccess) {
      logger.info "Running command: $command"
      command.execute(null, rootDir).with {
          waitFor()
          exitValue() ? logger.error("Error: ${err.text}") : onSuccess(getIn().text)
      }
  }

  def void apply(Project project) {

    // TODO: implement for linux and Windows
    if (!file("/Volumes/$RAM_DISK_NAME").exists()) {
        logger.lifecycle "Creating ramDisk $RAM_DISK_NAME"
        run("hdiutil attach -nomount ram://${RAM_DISK_SIZE_MB * 2048}", { path ->
            run("diskutil erasevolume HFS+ $RAM_DISK_NAME $path", {
                logger.info "Shell: $it"
                logger.lifecycle "Success: Mounted at $path"
            })
        })
    }

    if (file("/Volumes/$RAM_DISK_NAME/").exists()) {
        project.rootProject.allprojects {
            buildDir = "/Volumes/$RAM_DISK_NAME/gradle/${rootProject.name}/${project.name}"
        }
    }
  }

}
