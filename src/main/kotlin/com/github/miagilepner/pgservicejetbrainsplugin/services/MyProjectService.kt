package com.github.miagilepner.pgservicejetbrainsplugin.services

import com.intellij.openapi.project.Project
import com.github.miagilepner.pgservicejetbrainsplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
