package id.yukngoding.explore_firebase_database.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.yukngoding.explore_firebase_database.Origin
import id.yukngoding.explore_firebase_database.databinding.ActivityOriginBinding

/**
 * Access with path
 * db.document("collection_name/document_name")
 * db.document("collection_name/document_name/collection_name/document_name")
 *
 * Set origin
 * Read origins
 * Update origin
 * Transaction
 * Batch
 *
 * Todo rule:
 * https://firebase.google.com/docs/firestore/manage-data/transactions?authuser=0#data_validation_for_atomic_operations
 *
 * */

class OriginActivity : AppCompatActivity() {

    private var TAG: String = "OriginActivity"
    private lateinit var binding: ActivityOriginBinding

    private val db = Firebase.firestore

    private val dbCollection = "origins"
    private val dbDoc = "originDoc"
    private val dbDoc2 = "originDoc2"
    private val dbDoc3 = "originDoc3"
    private val dbDoc4 = "originDoc4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOriginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSet.setOnClickListener {
            setOrigin()
        }
        binding.btnRead.setOnClickListener {
            readOrigins()
        }
        binding.btnUpdate.setOnClickListener {
            updateOrigins()
        }
        binding.btnAddPopulation.setOnClickListener {
            addPopulation()
        }
        binding.btnAddPopulationResult.setOnClickListener {
            addPopulationWithResult()
        }
        binding.btnBatchSet.setOnClickListener {
            batchSetOrigins()
        }
        binding.btnBatchUpdate.setOnClickListener {
            batchUpdateOrigins()
        }
    }

    //  Added with object value
    private fun setOrigin() {
        Log.e(TAG, "setOrigin")

        val origin = Origin(
            "Los Angeles", "CA", "USA",
            false, 5000000L, listOf("west_coast", "socal")
        )
        db.document("$dbCollection/$dbDoc").set(origin)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun readOrigins() {
        Log.e(TAG, "readOrigins")

        db.collection(dbCollection)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(TAG, "${document.id} => ${document.data["name"]}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun updateOrigins() {
        Log.e(TAG, "updateOrigins")

        /// Case 1
        val washingtonRef = db.document("$dbCollection/$dbDoc")

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
            .update("isCapital", true)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        /// Case 2 - Server Timestamp
        // If you're using custom Kotlin objects in Android, add an @ServerTimestamp
        // annotation to a Date field for your custom object classes. This indicates
        // that the Date field should be treated as a server timestamp by the object mapper.
        // val docRef = db.collection("objects").document("some-id")

        // Update the timestamp field with the value from the server
        // FieldValue.arrayUnion()
        // FieldValue.arrayRemove()
        // FieldValue.increment()
        // FieldValue.delete() for delete field
        // val updates = hashMapOf<String, Any>(
        //    "timestamp" to FieldValue.serverTimestamp()
        // )

        // docRef.update(updates).addOnCompleteListener { }

        /// Case 3 - Update fields in nested objects
        // Assume the document contains:
        // {
        //   name: "Frank",
        //   favorites: { food: "Pizza", color: "Blue", subject: "recess" }
        //   age: 12
        // }
        //
        // To update age and favorite color:
        //        db.collection("users").document("frank")
        //            .update(mapOf(
        //                "age" to 13,
        //                "favorites.color" to "Red"
        //            ))
    }

    // a transaction is a set of read and write operations on one or more documents.
    // Read operations must always come before any write operations.
    private fun addPopulation() {
        Log.e(TAG, "addPopulation")

        val docPath = db.document("$dbCollection/$dbDoc")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docPath)

            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            val newPopulation = snapshot.getDouble("population")!! + 1
            transaction.update(docPath, "population", newPopulation)

            // Success
            null
        }.addOnSuccessListener { Log.d(TAG, "Transaction success!") }
            .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
    }

    private fun addPopulationWithResult() {
        Log.e(TAG, "addPopulationWithResult")

        val docPath = db.document("$dbCollection/$dbDoc")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docPath)

            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            val newPopulation = snapshot.getDouble("population")!! + 1
            transaction.update(docPath, "population", newPopulation)

            // Success
            newPopulation
        }.addOnSuccessListener { result ->
            Log.d(
                TAG,
                "Transaction success! newPopulation: $result"
            )
        }.addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
    }

    // If you do not need to read any documents in your operation set, you can execute multiple
    // write operations as a single batch that contains any combination of
    // set(), update(), or delete() operations.
    private fun batchSetOrigins() {
        Log.e(TAG, "batchSetOrigins")

        val docPath2 = db.document("$dbCollection/$dbDoc2")
        val docPath3 = db.document("$dbCollection/$dbDoc3")
        val docPath4 = db.document("$dbCollection/$dbDoc4")

        // Get a new write batch and commit all write operations
        db.runBatch { batch ->
            // Set values
            batch.set(
                docPath2, Origin(
                    "Indonesia", "JKT", "ID",
                    true, 5000000L, listOf("jaksel", "jakut")
                )
            )
            batch.set(
                docPath3, Origin(
                    "Indonesia", "PDG", "ID",
                    false, 200000L, listOf("sumbar", "sumut")
                )
            )
            batch.set(
                docPath4, Origin(
                    "Indonesia", "KLT", "ID",
                    false, 30000L, listOf("kaltim", "kalbar")
                )
            )
        }.addOnSuccessListener {
            Log.d(TAG, "Batch set success!")
        }.addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
    }

    private fun batchUpdateOrigins() {
        Log.e(TAG, "batchUpdateOrigins")

        val docPath2 = db.document("$dbCollection/$dbDoc2")
        val docPath3 = db.document("$dbCollection/$dbDoc3")
        val docPath4 = db.document("$dbCollection/$dbDoc4")

        // Get a new write batch and commit all write operations
        db.runBatch { batch ->
            // Update the capital
            batch.update(docPath2, "isCapital", false)
            batch.update(docPath4, mapOf("isCapital" to true, "population" to 3000000L))

            // Delete
            batch.delete(docPath3)
        }.addOnSuccessListener {
            Log.d(TAG, "Batch update success!")
        }.addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
    }

}