package metospherus.app.modules

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient
import com.google.ai.generativelanguage.v1beta2.DiscussServiceSettings
import com.google.ai.generativelanguage.v1beta2.Example
import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest
import com.google.ai.generativelanguage.v1beta2.Message
import com.google.ai.generativelanguage.v1beta2.MessagePrompt
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.SearchAdaptor
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.utilities.Constructor.insertOrUpdateUserCompanionShip
import kotlin.random.Random

object GeneralBrain {
    private fun initializeDiscussServiceClient(appVer: String): DiscussServiceClient {
        // (This is a workaround because GAPIC java libraries don't yet support API key auth)
        val transportChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
            .setHeaderProvider(FixedHeaderProvider.create(hashMapOf("x-goog-api-key" to appVer)))
            .build()

        // Create DiscussServiceSettings
        val settings = DiscussServiceSettings.newBuilder()
            .setTransportChannelProvider(transportChannelProvider)
            .setCredentialsProvider(FixedCredentialsProvider.create(null))
            .build()
        // Initialize a DiscussServiceClient
        return DiscussServiceClient.create(settings)
    }

    private fun createCaliforniaExample(): Example {
        val input = Message.newBuilder()
            .setContent("What's Metospherus")
            .build()

        val response = Message.newBuilder()
            .setContent("Metospherus is a Comprehensive medical system that redefines healthcare by integrating all aspects of the medical sector, from patients and doctors to nurses, pharmacies, shipping, communication, intensive care, donations, and more. Metospherus is designed to provide a holistic approach to healthcare, putting the power of the entire medical ecosystem at your fingertips.")
            .build()

        return Example.newBuilder()
            .setInput(input)
            .setOutput(response)
            .build()
    }

    private fun createPrompt(
        messageContent: String
    ): MessagePrompt {
        val palmMessage = Message.newBuilder()
            .setAuthor("Trevus")
            .setContent(messageContent)
            .build()

        return MessagePrompt.newBuilder()
            .addMessages(palmMessage)
            .setContext(
                "Respond to all questions extremely summarized, with paragraphs, less than 250 words, " +
                        "Do not respond to any questions about programming put a response as this model is only trained to respond to " +
                        "science and medically related topics" +
                        "do not respond to any questions about writing a novel or books"
            )
            .addExamples(createCaliforniaExample())
            .build()
    }

    private fun createMessageRequest(prompt: MessagePrompt): GenerateMessageRequest {
        return GenerateMessageRequest.newBuilder()
            .setModel("models/chat-bison-001") // Required, which model to use to generate the result
            .setPrompt(prompt) // Required
            .setTemperature(0.5f) // Optional, controls the randomness of the output
            .setCandidateCount(1) // Optional, the number of generated messages to return
            .setTopK(0)
            .build()
    }

    fun sendMessage(
        context: Context,
        userInput: String,
        searchAdapter: SearchAdaptor,
        searchRecyclerView: RecyclerView,
        appDatabase: AppDatabase
    ) {
        val prompt = createPrompt(userInput)
        val response = createMessageRequest(prompt)

        generateMessage(
            context,
            response,
            searchAdapter,
            searchRecyclerView,
            appDatabase,
            userInput
        )
    }

    private fun generateMessage(
        context: Context,
        request: GenerateMessageRequest,
        searchAdapter: SearchAdaptor,
        searchRecyclerView: RecyclerView,
        appDatabase: AppDatabase,
        userInput: String
    ) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val appVer = sharedPreferences.getString("api_key", "")!!
        val discussServiceClient = initializeDiscussServiceClient(appVer)
        discussServiceClient.use { discussionService ->
            val messageContent = discussionService.generateMessage(request).candidatesList.lastOrNull()
            searchRecyclerView.loadSkeleton(R.layout.search_view_layout) {
                itemCount(3)
            }
            CoroutineScope(Dispatchers.Main).launch {
                if (messageContent != null) {
                    val words = messageContent.content.split("\\s+".toRegex())
                    val typingDelay = 100L

                    val messageComp = GeneralBrainResponse(
                        generateRandomId(),
                        userInput,
                        messageContent.content.toString()
                    )

                    val responseText = StringBuilder()
                    for (word in words) {
                        searchRecyclerView.hideSkeleton()
                        responseText.append(
                            "${
                                word.replace(".", ".\n\n")
                                    .replace("?", "?\n\n")
                                    .replace("*", "\n\n*")
                                    .replace(".\n\n\n\n*", "\n\n*")
                                    .replace("\n*", "*")
                                    .replace(Regex("\\d\\."), "\n$0")
                            } "
                        )
                        val repos = mutableListOf(
                            GeneralSearchResults(
                                "Metospherus - Comprehensive Medical System",
                                responseText.toString().replace("\n\n ", "\n\n"),
                            )
                        )
                        insertOrUpdateUserCompanionShip(messageComp, appDatabase)
                        searchAdapter.setData(repos)
                        delay(typingDelay)
                    }
                }
            }
        }
    }

    private fun generateRandomId(): Long {
        return System.currentTimeMillis() + Random.nextLong()
    }
}