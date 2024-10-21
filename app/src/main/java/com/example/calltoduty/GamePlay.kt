package com.example.calltoduty


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calltoduty.MusicManager.stopSound


@Suppress("DEPRECATION")
class GamePlay : AppCompatActivity(), FailedFragment.FailedFragmentListener {

    private lateinit var timer: CountDownTimer
    private lateinit var timerTextView: TextView
    private val timeLimit: Long = 15000 // 15 seconds per question
    //private lateinit var messageTextView: TextView

    private lateinit var gameProgressManager: GameProgressManager
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var optionButton1: Button
    private lateinit var optionButton2: Button
    private lateinit var optionButton3: Button

    private lateinit var optionImage1: ImageView
    private lateinit var optionImage2: ImageView
    private lateinit var optionImage3: ImageView

    private var score: Int = 0
    private var wrongChoices: Int = 0
    private val maxWrongChoices = 1

    private val previousResponses: MutableList<Pair<Boolean, String>> = mutableListOf()

    private var chosenEmergencyScenario: EmergencyScenario? = null
    private var currentStep = 0

    // flag to track if the game was just restarted
    private var gameJustRestarted = false

    private var scenarioIndex: Int = 0 // Store scenario index

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_play)

        gameProgressManager = GameProgressManager(this)
        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView)
        optionButton1 = findViewById(R.id.optionButton1)
        optionButton2 = findViewById(R.id.optionButton2)
        optionButton3 = findViewById(R.id.optionButton3)
        optionImage1 = findViewById(R.id.optionImage1)
        optionImage2 = findViewById(R.id.optionImage2)
        optionImage3 = findViewById(R.id.optionImage3)
        timerTextView = findViewById(R.id.timerTextView)


        // Set up RecyclerView with ResponseAdapter
        responseAdapter = ResponseAdapter(previousResponses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = responseAdapter

        // Retrieve the scenario index and selected scenario from the Intent
        scenarioIndex = intent.getIntExtra("scenarioIndex", 0)
        chosenEmergencyScenario = intent.getParcelableExtra("selectedScenario")

        // Start the game with the selected scenario
        chosenEmergencyScenario?.let {
            startGame(it)
        }

        // Set up option buttons click listeners
        optionButton1.setOnClickListener { handleChoice(0) }
        optionButton2.setOnClickListener { handleChoice(1) }
        optionButton3.setOnClickListener { handleChoice(2) }

        optionImage1.setOnClickListener { handleChoice(0) }
        optionImage2.setOnClickListener { handleChoice(1) }
        optionImage3.setOnClickListener { handleChoice(2) }

        // Initialize and start the game-specific sound
        MusicManager.initialize(this, "gameplay_sound", R.raw.game_bgm, loop = true, volume = 100.0f)
        MusicManager.startSound("gameplay_sound")
    }

    override fun onPause() {
        super.onPause()
        stopSound("gameplay_music")
    }

    override fun onResume() {
        super.onResume()
        MusicManager.startSound("gameplay_sound")
    }

    override fun onStop() {
        super.onStop()
        stopSound("gameplay_sound")  // Stop the gameplay sound when the activity is no longer visible
    }


    // Initialize game state
    private fun startGame(scenario: EmergencyScenario) {
        score = 0
        wrongChoices = 0
        chosenEmergencyScenario = scenario
        currentStep = 0
        previousResponses.clear()
        responseAdapter.notifyDataSetChanged()
        showScenario()
        startTimer()
        stopSound("bg_music")
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Time left: $secondsRemaining s"
            }

            override fun onFinish() {
                // Handle timer finish (timeout)
                if (!gameJustRestarted) {
                    previousResponses.add(Pair(true, "Hello, is anyone there?"))

                    // Notify the adapter about the new message
                    responseAdapter.notifyItemInserted(previousResponses.size - 1)

                    // Optionally scroll to the bottom of the RecyclerView to show the new message
                    recyclerView.scrollToPosition(previousResponses.size - 1)
                }
                // Reset the flag after the first step
                gameJustRestarted = false
            }
        }.start()
    }

    private fun resetTimer() {
        timer.cancel()
        startTimer()
    }


    private fun showScenario() {
        chosenEmergencyScenario?.let { scenario ->
            if (currentStep < scenario.steps.size) {
                val currentDialogue = scenario.steps[currentStep]

                // Add the current dialogue message to previous responses
                previousResponses.add(Pair(true, currentDialogue.message))
                responseAdapter.notifyItemInserted(previousResponses.size - 1)
                recyclerView.post {
                    recyclerView.scrollToPosition(previousResponses.size - 1)
                }

                // Check if this step uses text or image options
                currentDialogue.textOptions?.let { textOptions ->
                    if (textOptions.size >= 3) {
                        // Set the options for the buttons (text options)
                        optionButton1.text = textOptions[0]
                        optionButton2.text = textOptions[1]
                        optionButton3.text = textOptions[2]

                        // Ensure buttons are visible and images hidden
                        setVisibilityForButtons(View.VISIBLE)
                        setVisibilityForImages(View.GONE)
                    } else {
                        showMessage("Error: Not enough text options provided.")
                    }
                } ?: currentDialogue.imageOptions?.let { imageOptions ->
                    if (imageOptions.size >= 3) {
                        // Set the options for the image buttons (image options)
                        optionImage1.setImageResource(imageOptions[0])
                        optionImage2.setImageResource(imageOptions[1])
                        optionImage3.setImageResource(imageOptions[2])

                        // Ensure images are visible and buttons hidden
                        setVisibilityForButtons(View.GONE)
                        setVisibilityForImages(View.VISIBLE)
                    } else {
                        showMessage("Error: Not enough image options provided.")
                    }
                } ?: showMessage("Error: No options provided for this dialogue step.")
            } else {
                showMessage("No more steps in this scenario.")
                endGame(success = true)
            }
        }
    }

    private fun setVisibilityForButtons(visibility: Int) {
        optionButton1.visibility = visibility
        optionButton2.visibility = visibility
        optionButton3.visibility = visibility
    }

    private fun setVisibilityForImages(visibility: Int) {
        optionImage1.visibility = visibility
        optionImage2.visibility = visibility
        optionImage3.visibility = visibility
    }

    fun loadNextScenario() {
        // Check if there are more scenarios left in the list
        if (scenarioIndex + 1 < emergencyScenarios.size) {
            scenarioIndex++ // Move to the next scenario
            chosenEmergencyScenario = emergencyScenarios[scenarioIndex]
            gameJustRestarted = true
            resetTimer()
            startGame(chosenEmergencyScenario!!) // Start the next scenario
        } else {
            showMessage("No more scenarios left.")
            // Optionally handle what happens if there are no more scenarios
        }
    }



    private fun handleChoice(choice: Int) {
        resetTimer() // Reset timer when the user makes a choice
        val scenario = chosenEmergencyScenario ?: return
        val currentDialogue = scenario.steps.getOrNull(currentStep) ?: return

        // Check if the current step has text or image options
        val chosenOptionText = currentDialogue.textOptions?.getOrNull(choice)
        val chosenOptionImageRes = currentDialogue.imageOptions?.getOrNull(choice)

        when {
            chosenOptionText != null -> processChoice(currentDialogue, choice, chosenOptionText)
            chosenOptionImageRes != null -> processChoice(currentDialogue, choice, "Image option $choice selected")
            else -> showMessage("Invalid choice!")
        }
    }

    private fun processChoice(currentDialogue: Dialogue, choice: Int, chosenOptionText: String) {

        // Add the user's choice to previous responses
        previousResponses.add(Pair(false, chosenOptionText))
        responseAdapter.notifyItemInserted(previousResponses.size - 1)

        if (currentDialogue.correctOption != null) {
            if (currentDialogue.correctOption.contains(choice)) {
                score++
                showMessage("Correct! You've handled it well.")
            } else {
                wrongChoices++
                showMessage("Incorrect! The correct response was one of: ${currentDialogue.correctOption.map { it + 1 }}")
            }
        } else {
            // Handle text-based options where no correctOption is defined
            showMessage("No correct answer for this dialogue step.")
        }

        if (wrongChoices >= maxWrongChoices) {
            endGame(success = false)
        } else {
            currentStep++  // Increment before showing the next scenario
            if (currentStep < (chosenEmergencyScenario?.steps?.size ?: 0)) {
                // Prepare the next dialogue based on the user's choice
                chosenEmergencyScenario?.steps?.getOrNull(currentStep)?.let { nextDialogue ->
                    val responseMessage = currentDialogue.responseMessages?.get(choice)
                    val updatedMessage = responseMessage ?: nextDialogue.message
                    val updatedDialogue = nextDialogue.copy(message = updatedMessage)

                    // Replace the current step with the updated dialogue
                    chosenEmergencyScenario?.steps =
                        chosenEmergencyScenario!!.steps.toMutableList().apply {
                            set(currentStep, updatedDialogue)
                        }
                }
                showScenario()  // Show the next step
            } else {
                endGame(success = true)
            }

        }
    }

    // Restart the chosen emergency scenario
    override fun onPlayAgain() {

        chosenEmergencyScenario?.let {
            score = 0
            wrongChoices = 0
            currentStep = 0
            previousResponses.clear()  // Clear the conversation history
            responseAdapter.notifyDataSetChanged() // Notify adapter to reset the conversation
            gameJustRestarted = true
            showScenario()
            resetTimer()
            startGame(it)
        }
    }


    private fun showMessage(message: String) {
        //messageTextView.text = message
    }


    private fun endGame(success: Boolean) {
        if (success) {
            gameProgressManager.markScenarioAsCompleted(chosenEmergencyScenario!!.scenarioName)
            showMessage("You successfully helped the caller. Your score: $score")
            //saveCompletedScenario(chosenEmergencyScenario!!)
            val successFragment = SuccessFragment.newInstance("param1", "param2")
            successFragment.show(supportFragmentManager, "successFragment")
        } else {
            showMessage("Game over - Too many wrong responses. Your score: $score")
            val failedFragment = FailedFragment.newInstance("param1", "param2")
            failedFragment.show(supportFragmentManager, "failedFragment")
        }
    }


}