package com.polimerconsumer.app

import javafx.stage.Stage
import tornadofx.App
import tornadofx.Stylesheet
import tornadofx.launch
import tornadofx.px

class GitApp : App(AppView::class, Styles::class) {
    init {
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.apply {
            width = 800.0
            height = 600.0
        }
    }
}

class Styles : Stylesheet() {
    init {
        root {
            prefWidth = 800.px
            prefHeight = 600.px
        }
    }
}

fun main(args: Array<String>) {
    launch<GitApp>(args)
}
