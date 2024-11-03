package com.example.calltoduty


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calltoduty.MusicManager.stopSound
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Suppress("DEPRECATION")
class GamePlay : AppCompatActivity(), FailedFragment.FailedFragmentListener {

    // Variables to manage game state and UI components
    private lateinit var apiService: ApiService // API service for making network requests
    private lateinit var timer: CountDownTimer // Timer to track time for each question
    private lateinit var timerTextView: TextView // TextView to display timer
    private val timeLimit: Long = 15000 // Limit for each question: 15 seconds
    //private lateinit var messageTextView: TextView
    private var lastClickTime: Long = 0 // To prevent spam clicks
    private var clickCount: Int = 0 // Counts rapid clicks
    private val spamClickThreshold = 3 // Max clicks allowed in rapid succession
    private val clickInterval = 500L // 500 milliseconds threshold for rapid clicks


    private lateinit var gameProgressManager: GameProgressManager // Manages game progress
    private lateinit var responseAdapter: ResponseAdapter // Adapter for RecyclerView
    private lateinit var recyclerView: RecyclerView // Displays user responses
    private lateinit var optionButton1: Button // Button for option 1
    private lateinit var optionButton2: Button // Button for option 2
    private lateinit var optionButton3: Button // Button for option 3

    private lateinit var optionImage1: ImageView // Image view for option 1
    private lateinit var optionImage2: ImageView // Image view for option 2
    private lateinit var optionImage3: ImageView // Image view for option 3

    private var score: Int = 0  // Tracks the user's score
    private var wrongChoices: Int = 0 // Tracks incorrect choices
    private val maxWrongChoices = 1 // Max allowed wrong choices before game ends

    private val previousResponses: MutableList<Pair<Boolean, String>> = mutableListOf()  // Stores previous user responses

    private var chosenEmergencyScenario: EmergencyScenario? = null // Selected scenario for the game
    private var currentStep = 0 // Current step in the scenario

    // flag to track if the game was just restarted
    private var gameJustRestarted = false

    private var scenarioIndex: Int = 0 // Index for the scenario

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge layout
        setContentView(R.layout.activity_game_play) // Sets the layout for the activity

        // Initializes Retrofit for network requests
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL for API calls
            .addConverterFactory(GsonConverterFactory.create(gson)) // Adds Gson converter for JSON
            .build()

        // Create an instance of ApiService
        apiService = retrofit.create(ApiService::class.java)

        // Initialize GameProgressManager with the ApiService
        gameProgressManager = GameProgressManager(this, apiService)
        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView)
        optionButton1 = findViewById(R.id.optionButton1)
        optionButton2 = findViewById(R.id.optionButton2)
        optionButton3 = findViewById(R.id.optionButton3)
        optionImage1 = findViewById(R.id.optionImage1)
        optionImage2 = findViewById(R.id.optionImage2)
        optionImage3 = findViewById(R.id.optionImage3)
        //timerTextView = findViewById(R.id.timerTextView)


        // Set up RecyclerView with ResponseAdapter
        responseAdapter = ResponseAdapter(previousResponses)
        recyclerView.layoutManager = LinearLayoutManager(this) // Set layout manager for RecyclerView
        recyclerView.adapter = responseAdapter // Set adapter for RecyclerView

        // Retrieve the scenario index and selected scenario from the Intent
        scenarioIndex = intent.getIntExtra("scenarioIndex", 0) // Get index
        chosenEmergencyScenario = intent.getParcelableExtra("selectedScenario") // Get selected scenario

        // Start the game with the selected scenario
        chosenEmergencyScenario?.let {
            startGame(it) // Call startGame with the selected scenario
        }

        // Set up option buttons click listeners
        optionButton1.setOnClickListener { handleChoice(0) }
        optionButton2.setOnClickListener { handleChoice(1) }
        optionButton3.setOnClickListener { handleChoice(2) }

        // Set up image click listeners
        optionImage1.setOnClickListener { handleChoice(0) }
        optionImage2.setOnClickListener { handleChoice(1) }
        optionImage3.setOnClickListener { handleChoice(2) }

        // Initialize and start the game-specific sound
        MusicManager.initialize(this, "gameplay_sound", R.raw.game_bgm, loop = true, volume = 100.0f)
        MusicManager.startSound("gameplay_sound") // Play background music
    }

    // Called when the activity is paused
    override fun onPause() {
        super.onPause()
        stopSound("gameplay_music") // Stop music when paused
        timer.cancel() // Cancel the timer
    }

    // Called when the activity is resumed
    override fun onResume() {
        super.onResume()
        MusicManager.startSound("gameplay_sound") // Resume music
    }

    // Called when the activity is stopped
    override fun onStop() {
        super.onStop()
        stopSound("gameplay_sound")  // Stop the gameplay sound when the activity is no longer visible
    }


    // Initialize game state
    private fun startGame(scenario: EmergencyScenario) {
        score = 0 // Reset score
        wrongChoices = 0 // Reset wrong choices
        chosenEmergencyScenario = scenario  // Set chosen scenario
        currentStep = 0 // Reset current step
        previousResponses.clear() // Clear previous responses
        responseAdapter.notifyDataSetChanged() // Notify adapter of data change
        showScenario() // Display the first scenario
        startTimer() // Start the timer
        stopSound("bg_music") // Stop background music
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLimit, 1000) { // Count down from timeLimit to 0, tick every second
            override fun onTick(millisUntilFinished: Long) {
                //val secondsRemaining = millisUntilFinished / 1000
                //timerTextView.text = "Time left: $secondsRemaining s"
            }

            override fun onFinish() {
                // Handle timer finish (timeout)
                if (!gameJustRestarted) {
                    previousResponses.add(Pair(true, "Hello, is anyone there?")) // Add timeout response

                    // Notify the adapter about the new message
                    responseAdapter.notifyItemInserted(previousResponses.size - 1) // Update adapter

                    // Optionally scroll to the bottom of the RecyclerView to show the new message
                    recyclerView.scrollToPosition(previousResponses.size - 1)  // Scroll to the new message
                }
                // Reset the flag after the first step
                gameJustRestarted = false
            }
        }.start() // Start the timer
    }

    private fun resetTimer() {
        timer.cancel() // Cancel existing timer
        startTimer() // Start a new timer
    }

    // Show the current scenario dialogue
    private fun showScenario() {
        chosenEmergencyScenario?.let { scenario ->
            if (currentStep < scenario.steps.size) {  // Check if there are more steps
                val currentDialogue = scenario.steps[currentStep] // Get current dialogue

                // Add the current dialogue message to previous responses
                previousResponses.add(Pair(true, currentDialogue.message))
                responseAdapter.notifyItemInserted(previousResponses.size - 1) // Update adapter
                recyclerView.post {
                    recyclerView.scrollToPosition(previousResponses.size - 1) // Scroll to the new message
                }

                // Check if this step uses text or image options
                currentDialogue.textOptions?.let { textOptions -> // If there are text options
                    if (textOptions.size >= 3) { // Ensure there are at least 3 options
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
                } ?: currentDialogue.imageOptions?.let { imageOptions -> // If there are image option
                    if (imageOptions.size >= 3) { // Ensure there are at least 3 options
                        // Set the options for the image buttons (image options)
                        optionImage1.setImageResource(imageOptions[0]) // Set image for option 1
                        optionImage2.setImageResource(imageOptions[1]) // Set image for option 2
                        optionImage3.setImageResource(imageOptions[2]) // Set image for option 3

                        // Ensure buttons are hidden and images are visible
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


    // Handle user's choice
    private fun handleChoice(choice: Int) {
        // Prevents rapid clicks
        val currentTime = System.currentTimeMillis()

        // Check if the click interval is within the rapid click threshold
        if (currentTime - lastClickTime < clickInterval) {
            clickCount++
        } else {
            clickCount = 1 // Reset click count if interval is larger than the threshold
        }

        lastClickTime = currentTime

        // Show WarningFragment if the user spams clicks
        if (clickCount >= spamClickThreshold) {
            clickCount = 0 // Reset click count after showing warning

            // Show WarningFragment
            val warningFragment = WarningFragment.newInstance("Too Many Clicks", "Please slow down!")
            warningFragment.show(supportFragmentManager, "warningFragment")
            return // Exit the method to prevent further processing of this click
        }



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
            score = 0 // Reset score
            wrongChoices = 0 // Reset wrong choices
            currentStep = 0 // Reset to the first step
            previousResponses.clear()  // Clear the conversation history
            responseAdapter.notifyDataSetChanged() // Notify adapter to reset the conversation
            gameJustRestarted = true // Flag that the game has restarted
            //showScenario()
            resetTimer() // Reset timer
            startGame(it) // Start the game again with the same scenario
        }
    }


    private fun showMessage(message: String) {
        //messageTextView.text = message
    }


    // Handle end of game logic
    private fun endGame(success: Boolean) {
        chosenEmergencyScenario?.let { scenario ->
            val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
            val nickname = sharedPreferences.getString("nickname", "") ?: ""
            Log.d("retrieveNickname", "Nickname retrieved: $nickname")
            // Handle success or failure of the game
            if (nickname.isNotEmpty()) {
                if (success) {
                    gameProgressManager.markScenarioAsCompleted(nickname, scenario.scenarioName)
                    val successFragment = SuccessFragment.newInstance("param1", "param2")
                    successFragment.show(supportFragmentManager, "successFragment")
                    Log.d("endGame", "Showing SuccessFragment")
                } else {
                    val failedFragment = FailedFragment.newInstance("param1", "param2")
                    failedFragment.show(supportFragmentManager, "failedFragment")
                    Log.d("endGame", "Showing FailedFragment")
                }
            } else {
                Log.d("endGame", "Nickname is empty")
            }
        } ?: run {
            Log.d("endGame", "Scenario not selected")
        }
    }

}