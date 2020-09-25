package org.fouryouandme.researchkit.task

import org.fouryouandme.researchkit.step.Step

abstract class Task(val identifier: String) {

    abstract val steps: List<Step>

}