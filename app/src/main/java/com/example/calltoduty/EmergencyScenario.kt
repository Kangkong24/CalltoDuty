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
    var steps: MutableList<Dialogue>,
    var isCompleted: Boolean = false,
    var isUnlocked: Boolean = false
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
    val correctOption: Set<Int>? = null,
    val responseMessages: Map<Int, String>? = null // New field for follow-up messages
) : Parcelable {
    init {
        // Validation to ensure that correctOption is only provided when imageOptions is not null
        if (correctOption != null && imageOptions == null) {
            throw IllegalArgumentException("correctOption can only be set when imageOptions are provided.")
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList(),
        parcel.createIntArray()?.toList(),
        parcel.createIntArray()?.toSet(),
        mutableMapOf<Int, String>().apply {
            parcel.readMap(this, Int::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeStringList(textOptions)
        parcel.writeIntArray(imageOptions?.toIntArray())
        parcel.writeIntArray(correctOption?.toIntArray())
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
                responseMessages = mapOf(
                    0 to "Yes officer trying my best to be quiet",
                    1 to "I don't think if that's a smart move?",
                    2 to "The caller panics and screams."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you hurt?", "What is your location?", "Stay on the line."),
                responseMessages = mapOf(
                    0 to "No, not yet.. ",
                    1 to "I'm at 123 Elm Street.",
                    2 to "I will, just send the police."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you alone?", "Stay calm", "Okay i'm sending the police"),
                responseMessages = mapOf(
                    0 to "Yes, i'm alone",
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
        scenarioName = "Prank Call",
        steps = mutableListOf(
            Dialogue(
                message = "Help! My friend just exploded!",
                textOptions = listOf("Can you tell me what happened?", "Are you in danger?", "Where are you located?"),
                responseMessages = mapOf(
                    0 to "They just… exploded! Like a balloon!",
                    1 to "I think we’re okay… for now.",
                    2 to "Um… we’re at our school’s playground."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("How many people are there with you?", "Is anyone hurt?", "Stay calm. Help is on the way."),
                responseMessages = mapOf(
                    0 to "There are three of us here.",
                    1 to "Nope! Just… pieces everywhere! Haha!",
                    2 to "Oh, I’m super calm. Don’t worry!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you sure this isn’t a joke?", "Is this a real emergency?", "What’s your name?"),
                responseMessages = mapOf(
                    0 to "Uh… well… maybe it’s a little joke.",
                    1 to "Okay, you got us! We’re just messing around.",
                    2 to "Oh, I probably shouldn't have called. Sorry!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you sure this is real?", "Do you need medical help?", "Is there anyone else who can confirm this?"),
                responseMessages = mapOf(
                    0 to "haha, Yeah it was just a prank call. Sorry!",
                    1 to "haha, Yeah it was just a prank call. Sorry!",
                    2 to "haha, Yeah it was just a prank call. Sorry!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Prank calls waste valuable resources.", "Please don’t call 911 unless it’s an emergency.", "Goodbye."),
                responseMessages = mapOf(
                    0 to "Sorry! I won’t do it again.",
                    1 to "Okay, I understand. It was a dumb idea.",
                    2 to "Bye!"
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.endcall_btn),
                correctOption = setOf(2)
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Baby Locked Inside a Car",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a distressed person saying, 'There’s a baby locked in the car!'",
                textOptions = listOf("Where are you located?", "Is the baby showing any signs of distress?", "How long has the baby been inside?"),
                responseMessages = mapOf(
                    0 to "I’m in the grocery store parking lot, near 12th Avenue.",
                    1 to "The baby looks flushed and is crying!",
                    2 to "It’s been about 10 minutes since they got locked in."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Is the car in direct sunlight?", "Do you have anything to break the window?", "Is anyone else nearby to help?"),
                responseMessages = mapOf(
                    0 to "Yes, the car is right in the sun, and it’s really hot out!",
                    1 to "No, I don’t have anything to break it safely.",
                    2 to "There are a few people around, but no one knows what to do."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Try to keep the baby calm from outside.", "Stand by while I send help.", "Is the car unlocked from any door or window?"),
                responseMessages = mapOf(
                    0 to "I’m trying, but the baby is crying harder.",
                    1 to "Please hurry. The baby looks uncomfortable!",
                    2 to "No, all doors and windows are locked!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("I’m dispatching emergency services now.", "Do you see any emergency buttons in the car?", "Stay with the car until help arrives."),
                responseMessages = mapOf(
                    0 to "Thank you. Please hurry!",
                    1 to "No, it’s an older model, so I don’t think it has any.",
                    2 to "I’ll stay right here."
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
        difficulty = Difficulty.EASY,
        scenarioName = "Lost Child",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a concerned parent about a missing child.",
                textOptions = listOf("How old is your child?", "When did they go missing?", "What are they wearing?"),
                responseMessages = mapOf(
                    0 to "My child is 5 years old.",
                    1 to "They went missing about an hour ago.",
                    2 to "They are wearing a red jacket and jeans."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Where did you last see them?", "Have you called anyone else?", "Can you describe them?"),
                responseMessages = mapOf(
                    0 to "I last saw them near the park.",
                    1 to "No, I haven’t called anyone else yet.",
                    2 to "They have brown hair and are about 4 feet tall."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, we are sending help.", "Keep searching, and we’ll assist.", "Can you wait at the park?"),
                responseMessages = mapOf(
                    0 to "Okay, I’ll try to stay calm.",
                    1 to "Yes, I'll keep looking for them.",
                    2 to "Yes, I’ll wait at the park."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, we are sending help.", "Keep searching, and we’ll assist.", "Can you wait at the park?"),
                responseMessages = mapOf(
                    0 to "Okay, I’ll try to stay calm.",
                    1 to "Yes, I'll keep looking for them.",
                    2 to "Yes, I’ll wait at the park."
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
        scenarioName = "Car Accident",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call reporting a car accident on a busy highway.",
                textOptions = listOf("Is anyone injured?", "Where exactly did it happen?", "How many cars are involved?"),
                responseMessages = mapOf(
                    0 to "Yes, someone is unconscious and bleeding.",
                    1 to "It happened near Exit 23 on the highway.",
                    2 to "There are two cars involved."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you describe the vehicles?", "Is anyone else hurt?", "Have the authorities been notified?"),
                responseMessages = mapOf(
                    0 to "One is a black sedan and the other is a red SUV.",
                    1 to "Yes, another person has minor injuries but is conscious.",
                    2 to "No, not yet."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("We are dispatching emergency services.", "Can you try to move the injured?", "Stay calm, help is on the way."),
                responseMessages = mapOf(
                    0 to "Thank you, I'll wait for them.",
                    1 to "I'll try, but it’s difficult.",
                    2 to "Okay, I'll stay calm."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Please ensure everyone stays away from traffic.", "Can you wave down passing cars for help?", "Check the injured person’s breathing."),
                responseMessages = mapOf(
                    0 to "Yes, I'll keep them away from traffic.",
                    1 to "I'll try waving for help.",
                    2 to "They are breathing, but it's shallow."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(0) // Sending an ambulance is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Suspicious Car Parked",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a passerby reporting a suspicious car parked outside a local store.",
                textOptions = listOf("What does the car look like?", "Where is it parked?", "Have you seen anyone near it?"),
                responseMessages = mapOf(
                    0 to "It's a red sedan with tinted windows.",
                    1 to "It's parked in the lot right in front of the store.",
                    2 to "No, I haven’t seen anyone get in or out."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("How long has it been there?", "Are the doors locked?", "Is it running?"),
                responseMessages = mapOf(
                    0 to "It’s been there for about an hour.",
                    1 to "I can’t tell, but it doesn't look like anyone is inside.",
                    2 to "No, it’s turned off."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you take a picture of the car?", "Stay near the store in case someone comes back.", "What makes you think it’s suspicious?"),
                responseMessages = mapOf(
                    0 to "I can try, but I don’t want to get too close.",
                    1 to "I’ll stay close but out of sight.",
                    2 to "It looks out of place; no one seems to know it."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("I will send the a officer to check it.", "Wait a bit longer to see if someone arrives.", "Try to find the owner nearby."),
                responseMessages = mapOf(
                    0 to "Okay, Thank you.",
                    1 to "I don’t want to take any chances.",
                    2 to "I don’t see anyone that looks like they belong to it."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.EASY,
        scenarioName = "Suspicious Person Lurking",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a resident who reports seeing a suspicious person lurking around their neighborhood.",
                textOptions = listOf("What is the person doing?", "Can you describe the person?", "Have they approached anyone?"),
                responseMessages = mapOf(
                    0 to "They’re just walking around, looking at houses.",
                    1 to "The person is wearing a hoodie and has a backpack.",
                    2 to "No, they haven't talked to anyone yet."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Where are you right now?", "How long has the person been there?", "Do they seem dangerous?"),
                responseMessages = mapOf(
                    0 to "I’m inside my house, watching from the window.",
                    1 to "They’ve been around for about 10 minutes.",
                    2 to "No, they just look suspicious, but not threatening."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Do you see any police nearby?", "Is the person moving closer to your home?", "Can you stay inside and keep an eye on them?"),
                responseMessages = mapOf(
                    0 to "No, I don’t see any police.",
                    1 to "No, they’re staying on the street.",
                    2 to "Yes, I’m staying inside and watching."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Keep watching from a safe distance.", "Try to talk to the person.", "Can you check with your neighbors?"),
                responseMessages = mapOf(
                    0 to "I’ll keep watching, but it’s making me nervous.",
                    1 to "I don’t think I should talk to them.",
                    2 to "I haven’t reached out to my neighbors yet."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("We are sending someone to check it out.", "Stay calm, and don’t approach the person.", "Make sure all your doors are locked."),
                responseMessages = mapOf(
                    0 to "Okay, I’ll wait for the police to come.",
                    1 to "I’ll stay calm and stay inside.",
                    2 to "I’ve already locked everything."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),


    //Medium Difficulty d2
    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "House Fire",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a frantic call from a resident reporting a fire in their house. The caller sounds terrified.",
                textOptions = listOf("Where are you right now?", "Is anyone still inside?", "What do you see around you?"),
                responseMessages = mapOf(
                    0 to "I'm at 456 Maple Street, the fire is huge!",
                    1 to "Yes, my whole family is trapped on the second floor! Please help!",
                    2 to "I see flames coming out of the windows and thick smoke!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can they escape through a window?", "Stay calm, help is on the way.", "Do you have any means to put out the fire?"),
                responseMessages = mapOf(
                    0 to "No, they are trapped! The fire is blocking the stairs!",
                    1 to "I’m trying to stay calm, but it’s spreading so quickly!",
                    2 to "I don’t have anything to stop it! It’s too big!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you see the fire clearly?", "Try to get to a window and signal for help!", "Is anyone hurt or in danger?"),
                responseMessages = mapOf(
                    0 to "Yes, the flames are raging in the living room, and smoke is filling the house!",
                    1 to "I can’t reach the window; it’s too dangerous!",
                    2 to "I think my daughter is hurt! She might have fallen!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Tell your family to stay low to the ground!", "Keep talking to me, help is on the way!", "Try to find a place to escape!"),
                responseMessages = mapOf(
                    0 to "They’re trying, but the smoke is too much!",
                    1 to "I can hear the sirens in the distance, but it feels like forever!",
                    2 to "I can’t leave them! What should I do?"
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
        scenarioName = "Dog Attack",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a panicked resident who says, 'There’s a dog going crazy in the neighborhood, and it’s attacking anyone it sees!'",
                textOptions = listOf("Where are you right now?", "Can you describe the dog?", "Has anyone been injured?"),
                responseMessages = mapOf(
                    0 to "I’m hiding inside my house, but it’s right outside!",
                    1 to "It’s a big dog, looks like it has foam around its mouth.",
                    2 to "Yes, my neighbor got bit pretty badly."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Is the dog still outside?", "How many people are nearby?", "Has animal control been called?"),
                responseMessages = mapOf(
                    0 to "Yes, it’s pacing back and forth.",
                    1 to "There are a few people trying to hide or run away.",
                    2 to "No, I called you first!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you stay inside?", "Are there any children or elderly around?", "Is anyone trying to contain the dog?"),
                responseMessages = mapOf(
                    0 to "Yes, I’m staying inside and locking the doors.",
                    1 to "Yes, some children were playing outside but are hiding now.",
                    2 to "No, no one wants to get close to it!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Please don’t go outside.", "Keep watching from a safe distance.", "Try to call the dog over."),
                responseMessages = mapOf(
                    0 to "I’ll stay inside, don’t worry.",
                    1 to "Okay, I’m watching from the window.",
                    2 to "I don’t think that’s a good idea!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("We’re sending help, stay calm.", "Do you need medical assistance for the bitten neighbor?", "Can you describe any other unusual behavior from the dog?"),
                responseMessages = mapOf(
                    0 to "Thank you, I’m so worried.",
                    1 to "Yes, the bite looks serious.",
                    2 to "It’s just acting wild, barking and attacking."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.animalcontrol_btn),// need to create a animal control option
                correctOption = setOf(2)
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Family Murder Confession",
        steps = mutableListOf(
            Dialogue(
                message = "I... I killed my family. ",
                textOptions = listOf("Can you tell me what happened?", "Where are you right now?", "Is anyone else in danger?"),
                responseMessages = mapOf(
                    0 to "I… I lost control. They wouldn't stop screaming. I didn't mean for it to go this far.",
                    1 to "I’m at home, in the living room. The knife… it’s still here with me.",
                    2 to "No, no… there’s no one left. Just me."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you hurt?", "Can you put the knife down for me?", "Stay on the line with me, okay?"),
                responseMessages = mapOf(
                    0 to "No… it’s not me. It’s them.",
                    1 to "I’ll try, but it feels like it’s stuck to my hand.",
                    2 to "Okay… I’ll stay, but I don’t know what’s going to happen."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Take a deep breath and stay calm.", "Do you want to tell me more about what happened?", "Are you in a safe place right now?"),
                responseMessages = mapOf(
                    0 to "I can’t calm down… I can still see their faces.",
                    1 to "It’s… it’s all a blur. They were shouting at me. I lost control.",
                    2 to "I’m in the living room. I don’t feel safe anywhere anymore."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Help is on the way. Just stay where you are.", "Can you place the knife down on the floor?", "Would you like to talk about what’s on your mind?"),
                responseMessages = mapOf(
                    0 to "I’ll stay here. Just… please don’t take too long.",
                    1 to "Okay… it’s down, but it feels like it’s still in my hand.",
                    2 to "There’s so much in my head… I don’t even know where to start."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending police is the correct action to secure the scene and provide mental health support.
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
                responseMessages = mapOf(
                    0 to "We don't know the exact location!",
                    1 to "We received an anonymous call about the bomb.",
                    2 to "Yes, we're starting to evacuate the students."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are the authorities informed?", "Where are the students being evacuated?", "How long until the bomb goes off?"),
                responseMessages = mapOf(
                    0 to "The police and bomb squad are on their way.",
                    1 to "We’re sending them to the football field.",
                    2 to "We don’t have a time estimate yet!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, help is on the way.", "Clear the nearby area!", "Do you have any details about the bomb?"),
                responseMessages = mapOf(
                    0 to "We are trying, but people are panicking.",
                    1 to "We're clearing the area around the school.",
                    2 to "We think the bomb is in a backpack, but nothing confirmed."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you describe the backpack?", "Has anyone suspicious been spotted?", "How many students are left in the building?"),
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
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Wildfire Approaching",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a resident reporting a wildfire approaching their neighborhood.",
                textOptions = listOf("How close is the fire?", "What do you see around you?", "Have you evacuated anyone yet?"),
                responseMessages = mapOf(
                    0 to "It looks like it’s about a mile away, and the smoke is getting thicker.",
                    1 to "I see flames in the distance and lots of smoke.",
                    2 to "No, I haven’t evacuated anyone yet."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Do you have a safe route to evacuate?", "Are there any animals that need to be evacuated?", "Have you contacted your neighbors?"),
                responseMessages = mapOf(
                    0 to "Yes, there’s a road to the east that should be safe.",
                    1 to "I have a dog, but I can’t find him.",
                    2 to "I haven’t had a chance to reach out yet."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Gather important items and leave now.", "Call your neighbors to warn them.", "Try to get your dog before leaving."),
                responseMessages = mapOf(
                    0 to "I’ll start packing important things.",
                    1 to "I need to warn them about the fire.",
                    2 to "I’ll try to find my dog quickly."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Is the fire spreading quickly?", "Can you see any emergency vehicles?", "Have you checked on your family?"),
                responseMessages = mapOf(
                    0 to "Yes, it seems to be moving towards the neighborhood.",
                    1 to "No, I haven’t seen any help yet.",
                    2 to "They’re all at home, and I haven’t been able to reach them."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm, help is on the way.", "Keep your doors and windows closed.", "Make sure you have your phone charged."),
                responseMessages = mapOf(
                    0 to "I’m trying to stay calm, but I’m really scared.",
                    1 to "I’ll close everything tight.",
                    2 to "I’ll charge my phone now."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(1) // Sending the fire department is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Accidental Stabbing",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a panicked person who has accidentally stabbed themselves while cooking.",
                textOptions = listOf("Where did you stab yourself?", "How deep is the wound?", "Are you able to apply pressure?"),
                responseMessages = mapOf(
                    0 to "I stabbed my hand with a kitchen knife.",
                    1 to "It’s bleeding a lot, but I think it’s not too deep.",
                    2 to "I’m trying, but it hurts so much."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you see any bone or muscle?", "How long ago did this happen?", "Are you feeling dizzy or faint?"),
                responseMessages = mapOf(
                    0 to "No, I can’t see anything like that.",
                    1 to "It happened about 10 minutes ago.",
                    2 to "I feel a bit lightheaded, but I’m okay for now."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Keep applying pressure to the wound.", "Try to elevate your hand above your heart.", "Are you alone at home?"),
                responseMessages = mapOf(
                    0 to "I’m doing that, but the bleeding won’t stop.",
                    1 to "Yes, I’m by myself.",
                    2 to "I should try to get to my phone."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Do you have a clean cloth or bandage?", "Have you called anyone for help?", "What’s your address?"),
                responseMessages = mapOf(
                    0 to "I have a dish towel I can use.",
                    1 to "No, I just called you.",
                    2 to "I live at 123 Maple Street."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay calm; help is on the way.", "Keep pressure on the wound until help arrives.", "Don't remove the knife if it’s still in. Is it?"),
                responseMessages = mapOf(
                    0 to "I’m trying to stay calm, but it’s hard.",
                    1 to "The knife is out; I’m just pressing on the cut.",
                    2 to "Yes, it’s out now."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(0) // Sending the ambulance is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.MEDIUM,
        scenarioName = "Domestic Violence Incident",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a person whispering, saying they are in danger from a violent partner.",
                textOptions = listOf("Are you safe at the moment?", "Can you leave the house?", "Where is your partner now?"),
                responseMessages = mapOf(
                    0 to "I’m hiding in the bathroom, but I don’t feel safe.",
                    1 to "No, I can’t leave. I’m scared they will find me.",
                    2 to "They are in the living room, but they could come looking for me."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay quiet and keep the door locked.", "Can you call for help?", "Can you describe your partner?"),
                responseMessages = mapOf(
                    0 to "I’ve locked the door, but I’m not sure how long I can stay here.",
                    1 to "I don’t want to call anyone; they might hear.",
                    2 to "They are tall, with dark hair, and wearing a black hoodie."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you find a way to leave safely?", "Have they hurt you before?", "Can you stay hidden until help arrives?"),
                responseMessages = mapOf(
                    0 to "I don’t think I can get out without being seen.",
                    1 to "Yes, this has happened before.",
                    2 to "I’ll try to stay hidden, but I’m really scared."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay hidden and don’t make any noise.", "Can you barricade the door?", "Do you have anyone nearby you can contact?"),
                responseMessages = mapOf(
                    0 to "I’ll stay as quiet as I can.",
                    1 to "I’ve pushed something against the door.",
                    2 to "No, I’m afraid to reach out to anyone right now."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("We are sending help right away.", "Stay calm, the police are on their way.", "Try to stay on the line until help arrives."),
                responseMessages = mapOf(
                    0 to "Thank you, I’m so scared.",
                    1 to "I’ll stay quiet and wait for them.",
                    2 to "Okay, I’ll stay on the line."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),


    //Hard Difficulty here

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Pizza Order",
        steps = mutableListOf(
            Dialogue(
                message = "Hi, I’d like to order a pizza for delivery.",
                textOptions = listOf("This is 911. Do you need help?", "Ma'am, this is an emergency line.", "What toppings would you like?"),
                responseMessages = mapOf(
                    0 to "Yes, please. I need a large pizza.",
                    1 to "Yes, a pizza, please.",
                    2 to "Anything, just make it quick."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Is there someone with you?", "Are you in danger?", "How soon do you need it?"),
                responseMessages = mapOf(
                    0 to "Yes, he’s right here.",
                    1 to "Yes, I am.",
                    2 to "As soon as possible, please."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you stay on the line?", "Is there a safe place you can go?", "Do you need medical assistance?"),
                responseMessages = mapOf(
                    0 to "No, I don’t think I can stay long.",
                    1 to "No, not right now.",
                    2 to "Maybe, I’m not sure."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you confirm your address for delivery?", "Do you want us to bring extra help?", "How many people are there with you?"),
                responseMessages = mapOf(
                    0 to "Yes, it’s 123 Main Street.",
                    1 to "Yes, extra cheese would be good.",
                    2 to "Just one, please."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Help is on the way, stay safe.", "We’ll be there soon with your order.", "Try to stay calm and act normal."),
                responseMessages = mapOf(
                    0 to "Thank you.",
                    1 to "Okay, thank you.",
                    2 to "I will, thank you."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action, as this is a domestic violence emergency.
            )
        )
    ),



    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Armed Robbery in Progress",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a terrified store clerk reporting an armed robbery in progress.",
                textOptions = listOf("Where are you right now?", "Can you describe the robber?", "Have they hurt anyone?"),
                responseMessages = mapOf(
                    0 to "I'm hiding in the back room, I can hear them in the store.",
                    1 to "They’re wearing a black hoodie and have a gun.",
                    2 to "Not yet, but they’re yelling at the cashier."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay hidden and keep quiet.", "Can you see how many robbers there are?", "Have you locked the door?"),
                responseMessages = mapOf(
                    0 to "Okay, I’ll stay as quiet as possible.",
                    1 to "There’s just one person, I think.",
                    2 to "Yes, I’ve locked the door."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you give me the store’s address?", "Try to watch what's happening.", "Stay calm, help is on the way."),
                responseMessages = mapOf(
                    0 to "The address is 456 Main Street.",
                    1 to "I can’t see anything from here.",
                    2 to "I’m trying to stay calm."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are there any security cameras?", "Is the robber still in the store?", "Can you leave the back room?"),
                responseMessages = mapOf(
                    0 to "Yes, but I don't know how to access them.",
                    1 to "I can still hear them, so yes, they’re still here.",
                    2 to "No, I’m too scared to move."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay where you are until the police arrive.", "Can you text someone for help?", "Try to create a distraction."),
                responseMessages = mapOf(
                    0 to "Okay, I’ll stay hidden until they get here.",
                    1 to "I’ve already texted my boss, but they haven’t replied.",
                    2 to "I don’t want to risk making things worse."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),


    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Family Trapped Inside a Tanker",
        steps = mutableListOf(
            Dialogue(
                message = "We're stuck inside a tanker! Please help! ",
                textOptions = listOf("Where is the tanker located?", "How many people are with you?", "What happened? How did you get stuck?"),
                responseMessages = mapOf(
                    0 to "We're on the highway, near Exit 14. I don’t know exactly where!",
                    1 to "It’s me, my husband, and our two kids. We can’t get out!",
                    2 to "The door got jammed. We tried everything, but it won't budge!"
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Is there any fuel leaking?", "Are you injured?", "Is there anyone outside who can help?"),
                responseMessages = mapOf(
                    0 to "I don’t see anything leaking, but I smell something strange!",
                    1 to "No injuries, but we’re having trouble breathing!",
                    2 to "No, there’s no one around. We’re on a deserted road."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you open a window or vent?", "Stay calm, I’m sending help.", "Is the smell getting stronger?"),
                responseMessages = mapOf(
                    0 to "We tried, but nothing opens from inside!",
                    1 to "I’m trying to stay calm, but my kids are scared!",
                    2 to "Yes, it’s getting worse. We’re all feeling dizzy."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Move to the back of the tanker, away from the smell.", "Try banging on the door or windows.", "Do you see any emergency release or latch?"),
                responseMessages = mapOf(
                    0 to "The smell is everywhere. I don’t think we can avoid it.",
                    1 to "We’ve been banging, but no one’s stopping!",
                    2 to "I don’t see any latch. It’s pitch black in here."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Keep talking to me, help is on the way.", "Do you hear any sounds outside?", "Can you feel any movement in the tanker?"),
                responseMessages = mapOf(
                    0 to "I will, but please hurry. I’m really scared.",
                    1 to "No, it’s completely silent outside.",
                    2 to "No, the tanker’s not moving, but it feels hot inside."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(1) // Fire department is the best option for rescue in a hazardous environment.
            )
        )
    ),



    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Hostage Situation",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a panicked person reporting that they are being held hostage.",
                textOptions = listOf("How many hostages are there?", "Can you describe the captors?", "Where are you located?"),
                responseMessages = mapOf(
                    0 to "There are three of us being held.",
                    1 to "There are two men, both armed with guns.",
                    2 to "We’re at 789 Pine Street, in an office building."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Are you in immediate danger?", "Can you stay on the phone?", "What do they want?"),
                responseMessages = mapOf(
                    0 to "Yes, they’re threatening to hurt us if their demands aren’t met.",
                    1 to "I don’t think I can stay on the phone for long.",
                    2 to "They want money and a getaway vehicle."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Try to stay calm, help is on the way.", "Can you hide or escape?", "Can you negotiate with them?"),
                responseMessages = mapOf(
                    0 to "I’ll try, but I’m really scared.",
                    1 to "There’s nowhere to hide, and I can’t escape.",
                    2 to "They’re not listening to us at all."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you describe the room you're in?", "Do they know you have a phone?", "Are the other hostages okay?"),
                responseMessages = mapOf(
                    0 to "We’re in a small office room with no windows.",
                    1 to "No, they don’t know I’m on the phone. I’m hiding it.",
                    2 to "They’re scared but unhurt so far."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay hidden and keep the phone silent.", "Try to talk to the captors.", "Can you signal anyone outside?"),
                responseMessages = mapOf(
                    0 to "I’ll stay quiet and keep the phone on silent.",
                    1 to "I don’t want to risk it, they’re very aggressive.",
                    2 to "There’s no way I can signal anyone from inside."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Stalker Outside the House",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from someone reporting that they believe a stalker is outside their home.",
                textOptions = listOf("Are you sure it's a stalker?", "Can you see them now?", "Where are you located?"),
                responseMessages = mapOf(
                    0 to "Yes, they’ve been following me for weeks.",
                    1 to "Yes, they’re standing across the street, just watching.",
                    2 to "I’m at 214 Maple Drive, in the suburbs."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay inside and lock all doors.", "Confront them to see what they want.", "Can you take a picture for evidence?"),
                responseMessages = mapOf(
                    0 to "I’ve already locked everything, I’m scared.",
                    1 to "No, I’m too terrified to confront them.",
                    2 to "I’m afraid they might notice if I take a picture."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Have they tried to enter your home?", "Have you seen them before?", "Can you call a neighbor for help?"),
                responseMessages = mapOf(
                    0 to "No, but they’re always around, watching me.",
                    1 to "Yes, I’ve seen them around my work and neighborhood before.",
                    2 to "No, I don’t want to make too much noise."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Turn off all the lights and stay hidden.", "Can you go to a safer room?", "Yell to scare them off."),
                responseMessages = mapOf(
                    0 to "I already have the lights off, they don’t know I’m home.",
                    1 to "Okay, I’m moving to my bedroom and locking the door.",
                    2 to "I don’t think yelling would help."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Wait for the police to arrive.", "Can you call someone to stay with you?", "Keep watching the stalker’s movements."),
                responseMessages = mapOf(
                    0 to "I’m waiting, please send help quickly!",
                    1 to "I’ll call a friend now.",
                    2 to "I’ll try to keep an eye on them, but I’m scared."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "School Shooting Incident",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a terrified student reporting a shooting at their school.",
                textOptions = listOf("Are you safe right now?", "How many people are involved?", "Where are you in the school?"),
                responseMessages = mapOf(
                    0 to "I’m hiding in a classroom, but I can hear shots.",
                    1 to "I think there are at least two shooters.",
                    2 to "I’m on the second floor, near the science lab."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you lock the classroom door?", "Are there any teachers with you?", "Have you called anyone else?"),
                responseMessages = mapOf(
                    0 to "Yes, we locked the door as soon as we heard.",
                    1 to "Yes, my teacher is with us, trying to keep everyone calm.",
                    2 to "No, I haven’t called anyone else; I’m too scared."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay quiet and keep your phone on silent.", "Can you find a safe exit?", "Is there a way to barricade the door?"),
                responseMessages = mapOf(
                    0 to "We’re all trying to stay quiet.",
                    1 to "I don’t want to move; it feels too dangerous.",
                    2 to "We could use some desks to block the door."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Have you heard any updates from outside?", "Are there any windows you can look out of?", "Can you describe what you hear?"),
                responseMessages = mapOf(
                    0 to "No, we can’t hear anything outside.",
                    1 to "There’s a window, but I’m too scared to look.",
                    2 to "It sounds chaotic; I hear shouting and more shots."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Stay on the line with me until help arrives.", "Can you text someone for help?", "Try to stay calm; help is coming."),
                responseMessages = mapOf(
                    0 to "I’ll stay on the line. Please hurry!",
                    1 to "I’ll try, but my hands are shaking.",
                    2 to "I’m trying to stay calm; it’s really hard."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(2) // Sending the police is the correct action
            )
        )
    ),

    EmergencyScenario(
        difficulty = Difficulty.HARD,
        scenarioName = "Truck Accident on Highway",
        steps = mutableListOf(
            Dialogue(
                message = "You receive a call from a bystander reporting a serious truck accident on the highway.",
                textOptions = listOf("Are there any injuries?", "How many vehicles are involved?", "Where are you located?"),
                responseMessages = mapOf(
                    0 to "Yes, there are people injured, and one person is trapped.",
                    1 to "It looks like three vehicles are involved, including the truck.",
                    2 to "I’m on the shoulder of the northbound lane, just past mile marker 12."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Can you see any flames or smoke?", "Is there anyone helping the injured?", "Are there any signs of a fuel leak?"),
                responseMessages = mapOf(
                    0 to "Yes, there’s a small fire starting near the truck.",
                    1 to "No, people are just standing around, unsure of what to do.",
                    2 to "I can't tell from here, but it looks dangerous."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Move to a safer location if possible.", "Is the driver of the truck responsive?", "Can you get a closer look at the injured?"),
                responseMessages = mapOf(
                    0 to "I’ll move further away from the road.",
                    1 to "I can’t tell; they seem unconscious.",
                    2 to "I don’t want to get too close until help arrives."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Have you called 911?", "What injuries do you see?", "Can you help the trapped person?"),
                responseMessages = mapOf(
                    0 to "Yes, I called them right after I saw the accident.",
                    1 to "There’s blood, and one person is clearly in shock.",
                    2 to "I can’t help, I’m afraid to move them."
                )
            ),
            Dialogue(
                message = "",
                textOptions = listOf("Keep your distance until professionals arrive.", "Try to calm the other bystanders.", "Gather any information about the accident."),
                responseMessages = mapOf(
                    0 to "I’ll stay back and wait for help.",
                    1 to "No one seems to be listening to me.",
                    2 to "I’ll take notes on what I see."
                )
            ),
            Dialogue(
                message = "",
                imageOptions = listOf(R.drawable.ambulance_btn, R.drawable.firetruck_btn, R.drawable.police_btn),
                correctOption = setOf(0) // Sending the ambulance is the correct action
            )
        )
    ),

)




