import org.gradle.api.tasks.TaskDependency
import org.gradle.api.Task

gradle.projectsEvaluated {
    allprojects {
        val taskToInspect = tasks.findByName("assembleDebug")

        if (taskToInspect != null) {
            println("==========================================================")
            println("Dumping details for task: ${taskToInspect.path}")
            println("==========================================================")

            // Print standard task properties
            println("  -> Group: ${taskToInspect.group ?: "n/a"}")
            println("  -> Description: ${taskToInspect.description ?: "n/a"}")
            println("  -> Type: ${taskToInspect::class.java.name}")

            // Get the 'dependsOn' tasks via the dedicated API
            println("  -> Dependencies:")
            val dependsOnTasks: TaskDependency = taskToInspect.taskDependencies
            dependsOnTasks.getDependencies(taskToInspect).forEach { dependentTask: Task ->
                println("    -> ${dependentTask.path}")
            }
            if (dependsOnTasks.getDependencies(taskToInspect).isEmpty()) {
                println("    (No dependencies found via API)")
            }

            // Iterate over inputs and outputs
            println("  -> Inputs (via API):")
            if (taskToInspect.inputs.properties.isEmpty()) {
                println("    (No input properties found via API)")
            } else {
                taskToInspect.inputs.properties.forEach { name, value ->
                    println("    -> Property '$name': $value")
                }
            }

            println("  -> Outputs (via API):")
            if (taskToInspect.outputs.files.isEmpty) {
                println("    (No output files found via API)")
            } else {
                taskToInspect.outputs.files.forEach { file ->
                    println("    -> File: ${file.absolutePath}")
                }
            }

            println("==========================================================")
        }
    }
}
