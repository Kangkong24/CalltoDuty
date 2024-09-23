package com.example.calltoduty

import android.os.Parcel
import android.os.Parcelable
import kotlin.collections.mutableListOf

enum class Difficulty{
    EASY, MEDIUM, HARD
}

@Suppress("DEPRECATION")
data class EmergencyScenario(
    val difficulty: Difficulty,
    val scenarioName: String,
    var steps: MutableList<Dialogue>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        Difficulty.valueOf(parcel.readString()!!),
        parcel.readString()!!,
        mutableListOf<Dialogue>().apply {
            parcel.readList(this, Dialogue::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(difficulty.name)
        parcel.writeString(scenarioName)
        parcel.writeList(steps)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<EmergencyScenario> {
        override fun createFromParcel(parcel: Parcel): EmergencyScenario {
            return EmergencyScenario(parcel)
        }

        override fun newArray(size: Int): Array<EmergencyScenario?> {
            return arrayOfNulls(size)
        }
    }
}

@Suppress("DEPRECATION")
data class Dialogue(
    val message: String,
    val textOptions: List<String>? = null,
    val imageOptions: List<Int>? = null,
    val correctOption: Set<Int>,
    val responseMessages: Map<Int, String>? = null // New field for follow-up messages
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList(),
        parcel.createIntArray()?.toList(),
        mutableSetOf<Int>().apply {
            val list = mutableListOf<Int>()
            parcel.readList(list, Int::class.java.classLoader)
            addAll(list)
        },
        mutableMapOf<Int, String>().apply {
            parcel.readMap(this, Int::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeStringList(textOptions)
        parcel.writeIntArray(imageOptions?.toIntArray())
        parcel.writeList(correctOption.toList())
        parcel.writeMap(responseMessages)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Dialogue> {
        override fun createFromParcel(parcel: Parcel): Dialogue {
            return Dialogue(parcel)
        }

        override fun newArray(size: Int): Array<Dialogue?> {
            return arrayOfNulls(size)
        }
    }
}



// Example dialogue list
val emergencyScenarios: List<EmergencyScenario> = listOf(
    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Intruder in the House",
        steps = mutableListOf(
            // First Dialogue
            Dialogue(
                message = "You receive a call from a distressed female voice.",
                textOptions = listOf("Where are you?", "What happened?", "What is your location?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "I am at home, hiding in my closet.",
                    1 to "Someone broke into my house!",
                    2 to "I'm at 123 Elm Street."
                )
            ),
            // Second Dialogue - Message will depend on the previous choice
            Dialogue(
                message = "", // This will be filled in dynamically
                textOptions = listOf("Stay quiet", "Run", "Scream"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "Yes officer trying my best to be quiet",
                    1 to "I don't think if that's a smart move?",
                    2 to "The caller panics and screams."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you hurt?", "What is your location?", "Stay on the line."),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "No, not yet.. ",
                    1 to "I'm at 123 Elm Street.",
                    2 to "I will, just send the police."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("qeqweqw", "Stay calm", "Okay i'm sending the police"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "No, not yet.. ",
                    1 to "I'm at 123 Elm Street.",
                    2 to "Thank God, hurry please."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Lost Child",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a concerned parent about a missing child.",
                textOptions = listOf("How old is your child?", "When did they go missing?", "What are they wearing?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "They are 5 years old.",
                    1 to "They went missing about an hour ago.",
                    2 to "They are wearing a red jacket and jeans."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Where did you last see them?", "Have you called anyone else?", "Can you describe them?"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "I last saw them near the park.",
                    1 to "No, I haven’t called anyone else yet.",
                    2 to "They have brown hair and are about 4 feet tall."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, we are sending help.", "Keep searching, and we’ll assist.", "Can you wait at the park?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "Okay, I’ll try to stay calm.",
                    1 to "Yes, I'll keep looking for them.",
                    2 to "Yes, I’ll wait at the park."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, we are sending help.", "Keep searching, and we’ll assist.", "Can you wait at the park?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "Okay, I’ll try to stay calm.",
                    1 to "Yes, I'll keep looking for them.",
                    2 to "Yes, I’ll wait at the park."
                )
            ),
            Dialogue(
                message = "image.",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),

    //Medium Difficulty d2
    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "House Fire",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call about a fire breaking out in a house.",
                textOptions = listOf("Where are you?", "Is anyone inside?", "What caused the fire?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "I'm at 456 Maple Street.",
                    1 to "Yes, my family is still inside!",
                    2 to "I don't know! It just started suddenly."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can they get out?", "Stay calm, help is on the way.", "Do you have a fire extinguisher?"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "No, they are trapped on the second floor.",
                    1 to "I’m trying to stay calm, but it’s spreading fast!",
                    2 to "No, I don’t have one!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you see the fire?", "Break a window!", "Is anyone injured?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "The fire is blocking the stairs.",
                    1 to "I can't get close enough to help.",
                    2 to "I think my daughter is hurt!"
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(1)
            )
        )
    ),
    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Bomb Threat at School",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call reporting a bomb threat at a local school.",
                textOptions = listOf("Where is the bomb?", "Who made the threat?", "Is the school being evacuated?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We don't know the exact location!",
                    1 to "We received an anonymous call about the bomb.",
                    2 to "Yes, we're starting to evacuate the students."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are the authorities informed?", "Where are the students being evacuated?", "How long until the bomb goes off?"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "The police and bomb squad are on their way.",
                    1 to "We’re sending them to the football field.",
                    2 to "We don’t have a time estimate yet!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, help is on the way.", "Clear the nearby area!", "Do you have any details about the bomb?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We are trying, but people are panicking.",
                    1 to "We're clearing the area around the school.",
                    2 to "We think the bomb is in a backpack, but nothing confirmed."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you describe the backpack?", "Has anyone suspicious been spotted?", "How many students are left in the building?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "It's black with a logo, but we're not sure.",
                    1 to "Someone saw a person in a hoodie drop something and run.",
                    2 to "We still have about 30 students inside."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),



    //Hard Difficulty here
    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Bomb Threat at School",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call reporting a bomb threat at a local school.",
                textOptions = listOf("Where is the bomb?", "Who made the threat?", "Is the school being evacuated?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We don't know the exact location!",
                    1 to "We received an anonymous call about the bomb.",
                    2 to "Yes, we're starting to evacuate the students."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are the authorities informed?", "Where are the students being evacuated?", "How long until the bomb goes off?"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "The police and bomb squad are on their way.",
                    1 to "We’re sending them to the football field.",
                    2 to "We don’t have a time estimate yet!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, help is on the way.", "Clear the nearby area!", "Do you have any details about the bomb?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We are trying, but people are panicking.",
                    1 to "We're clearing the area around the school.",
                    2 to "We think the bomb is in a backpack, but nothing confirmed."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you describe the backpack?", "Has anyone suspicious been spotted?", "How many students are left in the building?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "It's black with a logo, but we're not sure.",
                    1 to "Someone saw a person in a hoodie drop something and run.",
                    2 to "We still have about 30 students inside."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Bomb Threat at School",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call reporting a bomb threat at a local school.",
                textOptions = listOf("Where is the bomb?", "Who made the threat?", "Is the school being evacuated?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We don't know the exact location!",
                    1 to "We received an anonymous call about the bomb.",
                    2 to "Yes, we're starting to evacuate the students."
                )
            ),
            Dialogue(
                message = "Yes, we're starting to evacuate the students.",
                textOptions = listOf("Are the authorities informed?", "Where are the students being evacuated?", "How long until the bomb goes off?"),
                correctOption = setOf(0),
                responseMessages = mapOf(
                    0 to "The police and bomb squad are on their way.",
                    1 to "We’re sending them to the football field.",
                    2 to "We don’t have a time estimate yet!"
                )
            ),
            Dialogue(
                message = "The police and bomb squad are on their way.",
                textOptions = listOf("Stay calm, help is on the way.", "Clear the nearby area!", "Do you have any details about the bomb?"),
                correctOption = setOf(2),
                responseMessages = mapOf(
                    0 to "We are trying, but people are panicking.",
                    1 to "We're clearing the area around the school.",
                    2 to "We think the bomb is in a backpack, but nothing confirmed."
                )
            ),
            Dialogue(
                message = "We think the bomb is in a backpack, but nothing confirmed.",
                textOptions = listOf("Can you describe the backpack?", "Has anyone suspicious been spotted?", "How many students are left in the building?"),
                correctOption = setOf(1),
                responseMessages = mapOf(
                    0 to "It's black with a logo, but we're not sure.",
                    1 to "Someone saw a person in a hoodie drop something and run.",
                    2 to "We still have about 30 students inside."
                )
            ),
            Dialogue(
                message = "Someone saw a person in a hoodie drop something and run.",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2)
            )
        )
    ),
)






// Define a list of EmergencyScenario
/*val emergencyScenarios: List<EmergencyScenario> = listOf(
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
)*/
