package com.example.calltoduty



enum class Difficulty{
    EASY, MEDIUM, HARD
}


data class EmergencyScenario(

    val difficulty: Difficulty, // difficulty level
    val scenarioName: String, // Give each scenario a name
    val steps: List<Dialogue> // List of Emergencies (steps)


)
data class Dialogue(
    val message: String,
    val textOptions: List<String>? = null,
    val imageOptions: List<Int>? = null,
    //val options: List<String>,
    val correctOption: Set<Int>
    //val hint: String? = null,  // Optional hint
    //val timeLimit: Int? = null // Optional time limit in seconds

)

// Define a list of EmergencyScenario
val emergencyScenarios: List<EmergencyScenario> = listOf(
    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Intruder in the House",
        steps = listOf(
            Dialogue(
                message = "You receive a call from a distressed female voice.",
                textOptions = listOf("Where are you?", "What happened?", "What is your location?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "I think someone broke into my house.",
                textOptions = listOf("Are you hurt?", "Can you hide?", "Stay on the line."),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "I'm hiding in a closet.",
                textOptions = listOf("Stay quiet.", "Is the door locked?", "What do they look like?"),
                correctOption = setOf(0)
            ),
            Dialogue(
                message = "Yes! it is locked, send a police please",
                textOptions = listOf("Ok Ma'am calm down", "Wait and stay quiet.", "I will send the help."),
                correctOption = setOf(2)
            ),
            Dialogue(
                message = "Ok.. i'll wait",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )

        )
    ),

    // New easy scenario
    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Lost Child",
        steps = listOf(
            Dialogue(
                message = "You receive a call from a concerned parent about a missing child.",
                textOptions = listOf("How old is your child?", "When did they go missing?", "What are they wearing?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "They went missing about an hour ago.",
                textOptions = listOf("Where did you last see them?", "Have you called anyone else?", "Can you describe them?"),
                correctOption = setOf(0)
            ),
            Dialogue(
                message = "I last saw them near the park.",
                textOptions = listOf("Stay calm, we are sending help.", "Keep searching, and we’ll assist.", "Can you wait at the park?"),
                correctOption = setOf(2)
            ),
            Dialogue(
                message = "image.",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            ),
        )
    ),


    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Home Invasion",
        steps = listOf(
            Dialogue(
                message = "medium lang ito.",
                textOptions = listOf("Where are you?", "What happened?", "What is your location?"),
                correctOption = setOf(1)

            ),
            Dialogue(
                message = "I think someone broke into my house.",
                textOptions = listOf("Are you hurt?", "Can you hide?", "Stay on the line."),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "I'm hiding in a closet.",
                textOptions = listOf("Stay quiet.", "Is the door locked?", "What do they look like?"),
                correctOption = setOf(0)
            ),
            Dialogue(
                message = "Yes! it is locked, send a police please",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(1)
            )
        )
    ),
    // New easy scenario
    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Lost Child",
        steps = listOf(
            Dialogue(
                message = "Medium num 2",
                textOptions = listOf("How old is your child?", "When did they go missing?", "What are they wearing?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "They went missing about an hour ago.",
                textOptions = listOf("Where did you last see them?", "Have you called anyone else?", "Can you describe them?"),
                correctOption = setOf(0)
            ),
            Dialogue(
                message = "I last saw them near the park.",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Eme Eme pa lang",
        steps = listOf(
            Dialogue(
                message = "Hard ito.",
                textOptions = listOf("Where are you?", "What happened?", "What is your location?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "I think someone broke into my house.",
                textOptions = listOf("Are you hurt?", "Can you hide?", "Stay on the line."),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "I'm hiding in a closet.",
                textOptions = listOf("Stay quiet.", "Is the door locked?", "What do they look like?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "Yes! it is locked, send a police please",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),
    // New easy scenario
    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Lost Child",
        steps = listOf(
            Dialogue(
                message = "Hard num 2",
                textOptions= listOf("How old is your child?", "When did they go missing?", "What are they wearing?"),
                correctOption = setOf(1)
            ),
            Dialogue(
                message = "They went missing about an hour ago.",
                textOptions = listOf("Where did you last see them?", "Have you called anyone else?", "Can you describe them?"),
                correctOption = setOf(0)
            ),
            Dialogue(
                message = "I last saw them near the park.",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),
    // Add more scenarios here
)
