package id.yukngoding.explore_firebase_database.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.yukngoding.explore_firebase_database.R
import id.yukngoding.explore_firebase_database.databinding.ActivityFoodBinding
import id.yukngoding.explore_firebase_database.databinding.ActivityMainBinding

/**
 * Access with object
 *
 * Add food
 * Read foods
 * Set food
 * Read foods with pagging
 * */

class FoodActivity : AppCompatActivity() {

    private var TAG: String = "FoodActivity"
    private lateinit var binding: ActivityFoodBinding

    private val db = Firebase.firestore

    private val dbCollection = "foods"
    private val dbDoc = "foodDoc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            addFood()
        }
        binding.btnRead.setOnClickListener {
            readFoods()
        }
        binding.btnSet.setOnClickListener {
            setFood()
        }
        binding.btnReadPagging.setOnClickListener {
            readFoodPagging()
        }
    }

    private fun addFood() {
        Log.e(TAG, "addFood")
        // Create a new user with a first and last name
        val food = hashMapOf(
            "name" to "Nasi Goreng",
            "description" to "nasi yang digoreng",
            "ingredients" to hashMapOf(
                "1" to "Nasi",
                "2" to "Minyak",
            ),
            "cook_time_in_s" to 160
        )
        //        "stringExample" to "Hello world!",
        //        "booleanExample" to true,
        //        "numberExample" to 3.14159265,
        //        "dateExample" to Timestamp(Date()),
        //        "listExample" to arrayListOf(1, 2, 3),
        //        "nullExample" to null

        // Add a new document with a generated ID
        db.collection(dbCollection)
            .add(food)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun readFoods() {
        Log.e(TAG, "readFoods")
        db.collection(dbCollection)
            // .whereEqualTo("cook_time_in_s", 160)
            .orderBy("cook_time_in_s", Query.Direction.DESCENDING)
            // .limit(2)
            // without startAfter
            .get()
            .addOnSuccessListener { result ->
                Log.e(TAG, "Get success result.size: ${result.size()}")
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(TAG, "${document.id} => ${document.data["name"]}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }
    }

    private fun setFood() {
        Log.e(TAG, "setFood")
        // Update one field, creating the document if it does not already exist.
        val food = hashMapOf(
            "name" to "Nasi Goreng Bakso",
            "description" to "nasi yang digoreng dengan bakso",
            "ingredients" to hashMapOf(
                "1" to "Nasi Perak",
                "2" to "Minyak",
            ),
            "cook_time_in_s" to 320
        )

        // Add a new document with a generated ID
        db.collection(dbCollection).document(dbDoc)
            // When you use set() to create a document, you must specify an ID for the document to create.
            // if the data not exist, firebase will create it,
            // else firebase will update it
            .set(food)
            //  If you're not sure whether the document exists, pass the option to merge the new
            //  data with any existing document to avoid overwriting entire documents
            //  .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun readFoodPagging() {
        // Construct query for first 2 origins, ordered by population
        val first = db.collection(dbCollection)
            .orderBy("cook_time_in_s")
            .limit(2)

        first.get()
            .addOnSuccessListener { documentSnapshots ->
                Log.e(TAG, "First Get success result.size: ${documentSnapshots.size()}")
                for (document in documentSnapshots) {
                    Log.d(TAG, "First Get ${document.id} => ${document.data}")
                    Log.d(TAG, "First Get ${document.id} => ${document.data["name"]}")
                }

                // Get the last visible document
                val lastVisible: DocumentSnapshot =
                    documentSnapshots.documents[documentSnapshots.size() - 1]

                // Construct a new query starting at this document,
                // get the next 2 origins.
                val next = db.collection(dbCollection)
                    .orderBy("cook_time_in_s")
                    .startAfter(lastVisible)
                    .limit(2)

                next.get().addOnSuccessListener { secondResult ->
                    Log.e(TAG, "Second Get success result.size: ${secondResult.size()}")
                    for (document in secondResult) {
                        Log.d(TAG, "Second Get ${document.id} => ${document.data}")
                        Log.d(TAG, "Second Get ${document.id} => ${document.data["name"]}")
                    }
                }

                // Use the query for pagination
                // ...
            }
    }

}